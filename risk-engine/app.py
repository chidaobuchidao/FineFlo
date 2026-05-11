"""Risk Engine — Hybrid credit scoring: local ML + optional cloud LLM.

Primary: local LogisticRegression model (trainable via train.py).
Secondary: DeepSeek LLM provides qualitative risk analysis (optional).

Request/response contract unchanged — loan-api needs zero modifications.
"""

import os
import json
import pickle
import logging
import requests
import numpy as np
from datetime import datetime
from flask import Flask, request, jsonify

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)

# ── Local model ─────────────────────────────────────────────────
MODEL_PATH = os.path.join(os.path.dirname(__file__), "model", "credit_model.pkl")
try:
    with open(MODEL_PATH, "rb") as f:
        local_model = pickle.load(f)
    logger.info("Local model loaded: %s", MODEL_PATH)
except FileNotFoundError:
    local_model = None
    logger.warning("No local model at %s — will use rule-based scoring", MODEL_PATH)

# ── DeepSeek (optional cloud LLM for qualitative analysis) ──────
DEEPSEEK_API_KEY = os.getenv("DEEPSEEK_API_KEY", "")
DEEPSEEK_BASE_URL = os.getenv("DEEPSEEK_BASE_URL", "https://api.deepseek.com")
DEEPSEEK_MODEL = os.getenv("DEEPSEEK_MODEL", "deepseek-v4-flash")

# ── Eureka ──────────────────────────────────────────────────────
EUREKA_SERVER = os.getenv("EUREKA_SERVER", "http://localhost:8761/eureka/")
SERVICE_HOST = os.getenv("SERVICE_HOST", "localhost")
SERVICE_PORT = int(os.getenv("SERVICE_PORT", "5000"))


# =================================================================
#  SCORING ENGINE: local model > fallback rules
# =================================================================

def extract_features(features):
    """Extract 7-dimensional feature vector from features dict."""
    return np.array([
        float(features.get("registeredCapital", 0)),
        float(features.get("employeeCount", 0)),
        float(features.get("annualRevenue", 0)),
        float(features.get("establishYears", 1)),
        float(features.get("previousLoans", 0)),
        float(features.get("previousOverdues", 0)),
        float(features.get("debtRatio", 0.5)),
    ]).reshape(1, -1)


def predict_with_model(features_data):
    """Score using the trained LogisticRegression pipeline."""
    X = extract_features(features_data)
    proba = local_model.predict_proba(X)[0]
    score = round(float(proba[1]) * 100, 1)
    confidence = round(float(max(proba)), 2)
    return score, confidence


def predict_with_rules(features):
    """Rule-based fallback scoring — no model needed."""
    score = 60.0
    rc = features.get("registeredCapital", 0)
    ec = features.get("employeeCount", 0)
    ar = features.get("annualRevenue", 0)
    ey = features.get("establishYears", 1)
    po = features.get("previousOverdues", 0)
    dr = features.get("debtRatio", 0.5)

    if rc > 1000:   score += 8
    elif rc > 500:  score += 4
    if ec > 200:    score += 6
    elif ec > 50:   score += 3
    if ar > 5000:   score += 8
    elif ar > 1000: score += 4
    if ey > 10:     score += 5
    elif ey > 5:    score += 2
    if po == 0:     score += 5
    elif po < 3:    score += 2
    if dr > 0.6:    score -= 10
    elif dr > 0.4:  score -= 3

    return max(0, min(100, score)), 0.85


def classify_risk(score):
    if score >= 80: return "LOW"
    if score >= 60: return "MEDIUM"
    return "HIGH"


# =================================================================
#  DEEPSEEK: qualitative analysis (optional, non-blocking)
# =================================================================

def deepseek_analyze(enterprise_id, features, score, risk_level):
    """Ask DeepSeek for a qualitative risk analysis — bonus, not required."""
    if not DEEPSEEK_API_KEY:
        return None

    prompt = f"""As a credit analyst, give a 2-3 sentence risk assessment for this loan applicant.

Enterprise ID: {enterprise_id}
Quantitative Score: {score}/100 (risk level: {risk_level})
- Registered Capital: {features.get("registeredCapital", 0)} (10k CNY)
- Employees: {features.get("employeeCount", 0)}
- Annual Revenue: {features.get("annualRevenue", 0)} (10k CNY)
- Years Established: {features.get("establishYears", 1)}
- Previous Loans: {features.get("previousLoans", 0)}
- Previous Overdues: {features.get("previousOverdues", 0)}
- Debt Ratio: {features.get("debtRatio", 0.5)}
- Industry: {features.get("industry", "unknown")}

Respond with ONLY a JSON: {{"strengths": ["..."], "risks": ["..."], "verdict": "..."}}"""

    try:
        resp = requests.post(
            f"{DEEPSEEK_BASE_URL}/chat/completions",
            headers={
                "Authorization": f"Bearer {DEEPSEEK_API_KEY}",
                "Content-Type": "application/json",
            },
            json={
                "model": DEEPSEEK_MODEL,
                "messages": [
                    {"role": "system", "content": "You are a credit analyst. Output only valid JSON, no markdown fences."},
                    {"role": "user", "content": prompt},
                ],
                "temperature": 0.3,
                "max_tokens": 4096,
            },
            timeout=15,
        )
        resp.raise_for_status()
        raw = resp.json()["choices"][0]["message"]["content"].strip()
        if raw.startswith("```"):
            raw = raw.split("\n", 1)[-1].rstrip("```")
        return json.loads(raw)
    except Exception as e:
        logger.info("DeepSeek analysis skipped: %s", str(e)[:60])
        return None


# =================================================================
#  API ENDPOINTS
# =================================================================

@app.route("/api/risk/predict", methods=["POST"])
def predict():
    data = request.get_json()
    if not data:
        return jsonify({"code": 400, "message": "Request body required", "data": None}), 400

    enterprise_id = data.get("enterpriseId")
    if not enterprise_id:
        return jsonify({"code": 400, "message": "enterpriseId required", "data": None}), 400

    features = data.get("features", {})

    # 1. Quantitative scoring (always runs)
    if local_model is not None:
        score, confidence = predict_with_model(features)
        engine = f"logistic-regression-v1"
    else:
        score, confidence = predict_with_rules(features)
        engine = "rule-based"

    risk_level = classify_risk(score)

    # 2. LLM qualitative analysis (optional, non-blocking)
    analysis = deepseek_analyze(enterprise_id, features, score, risk_level)
    if analysis:
        engine += "+deepseek-analysis"

    logger.info("Predict enterprise=%s score=%.1f level=%s engine=%s", enterprise_id, score, risk_level, engine)

    return jsonify({"code": 200, "message": "success", "data": {
        "enterpriseId": enterprise_id,
        "creditScore": score,
        "riskLevel": risk_level,
        "confidence": confidence,
        "modelVersion": engine,
        "analysis": analysis,  # None if DeepSeek not configured
    }})


@app.route("/api/risk/evaluate", methods=["POST"])
def evaluate():
    data = request.get_json()
    if not data:
        return jsonify({"code": 400, "message": "Request body required", "data": None}), 400

    enterprise_id = data.get("enterpriseId")
    if not enterprise_id:
        return jsonify({"code": 400, "message": "enterpriseId required", "data": None}), 400

    features = data.get("features", {})
    if local_model is not None:
        score, confidence = predict_with_model(features)
        engine = "logistic-regression-v1"
    else:
        score, confidence = predict_with_rules(features)
        engine = "rule-based"

    return jsonify({"code": 200, "message": "success", "data": {
        "enterpriseId": enterprise_id,
        "creditScore": score,
        "riskLevel": classify_risk(score),
        "confidence": confidence,
        "modelVersion": engine,
        "evaluatedAt": datetime.now().isoformat(),
    }})


@app.route("/health", methods=["GET"])
def health():
    return jsonify({
        "status": "UP",
        "local_model": "logistic-regression" if local_model else "rule-based",
        "deepseek": "configured" if DEEPSEEK_API_KEY else "not-configured",
    })


# =================================================================
#  EUREKA
# =================================================================

def register_with_eureka():
    try:
        resp = requests.post(
            f"{EUREKA_SERVER}apps/RISK-ENGINE",
            json={"instance": {
                "hostName": SERVICE_HOST, "app": "RISK-ENGINE",
                "ipAddr": SERVICE_HOST, "vipAddress": "risk-engine",
                "port": {"$": SERVICE_PORT, "@enabled": "true"},
                "dataCenterInfo": {
                    "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
                    "name": "MyOwn",
                },
                "status": "UP",
            }},
            headers={"Content-Type": "application/json"},
            timeout=5,
        )
        if resp.status_code in (200, 204):
            logger.info("Registered with Eureka")
    except Exception as e:
        logger.warning("Eureka registration failed: %s", e)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=SERVICE_PORT, debug=False)
