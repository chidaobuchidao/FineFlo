package com.inclusivefinance.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class RiskEngineClient {

    private final RestTemplate restTemplate;
    private final String riskEngineUrl;

    public RiskEngineClient(@Value("${risk-engine.url:http://localhost:5000}") String riskEngineUrl) {
        this.restTemplate = new RestTemplate();
        this.riskEngineUrl = riskEngineUrl;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> predict(Long enterpriseId, Map<String, Object> features) {
        try {
            Map<String, Object> request = Map.of(
                    "enterpriseId", enterpriseId,
                    "features", features
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    riskEngineUrl + "/api/risk/predict", entity, Map.class);

            if (response.getBody() != null && response.getBody().get("data") != null) {
                return (Map<String, Object>) response.getBody().get("data");
            }
        } catch (Exception e) {
            // Fallback: return null, caller handles gracefully
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> evaluate(Long enterpriseId, Map<String, Object> features) {
        try {
            Map<String, Object> request = Map.of(
                    "enterpriseId", enterpriseId,
                    "features", features
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    riskEngineUrl + "/api/risk/evaluate", entity, Map.class);

            if (response.getBody() != null && response.getBody().get("data") != null) {
                return (Map<String, Object>) response.getBody().get("data");
            }
        } catch (Exception e) {
            // Fallback
        }
        return null;
    }
}
