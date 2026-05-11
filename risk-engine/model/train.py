"""Train credit scoring model using logistic regression.

Generates synthetic training data mimicking UCI Credit dataset structure,
trains a logistic regression classifier, and saves the model as pickle.
"""

import os
import pickle
import numpy as np
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline
from sklearn.metrics import accuracy_score, f1_score, roc_auc_score


def generate_synthetic_data(n_samples=5000):
    """Generate synthetic credit data with 7 features."""
    np.random.seed(42)

    registered_capital = np.random.uniform(50, 5000, n_samples)
    employee_count = np.random.randint(5, 500, n_samples)
    annual_revenue = np.random.uniform(100, 10000, n_samples)
    establish_years = np.random.uniform(1, 30, n_samples)
    previous_loans = np.random.poisson(3, n_samples)
    previous_overdues = np.random.poisson(0.5, n_samples)
    debt_ratio = np.random.uniform(0.05, 0.8, n_samples)

    X = np.column_stack([
        registered_capital, employee_count, annual_revenue,
        establish_years, previous_loans, previous_overdues, debt_ratio
    ])

    # Generate label: higher score = better credit
    score = (
        registered_capital / 5000 * 25 +
        employee_count / 500 * 20 +
        annual_revenue / 10000 * 25 +
        establish_years / 30 * 15 +
        np.clip(1 - previous_overdues / 5, 0, 1) * 15
    )
    noise = np.random.normal(0, 5, n_samples)
    score = np.clip(score + noise, 0, 100)

    y = (score > 60).astype(int)

    print(f"Generated {n_samples} samples")
    print(f"  Good credit (score>60): {y.sum()} ({y.mean()*100:.1f}%)")
    return X, y


def train():
    print("Generating training data...")
    X, y = generate_synthetic_data(5000)

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    pipeline = Pipeline([
        ("scaler", StandardScaler()),
        ("classifier", LogisticRegression(max_iter=1000, random_state=42))
    ])

    print("Training logistic regression model...")
    pipeline.fit(X_train, y_train)

    y_pred = pipeline.predict(X_test)
    y_prob = pipeline.predict_proba(X_test)[:, 1]

    acc = accuracy_score(y_test, y_pred)
    f1 = f1_score(y_test, y_pred)
    auc = roc_auc_score(y_test, y_prob)

    print(f"Test results:")
    print(f"  Accuracy:  {acc:.4f}")
    print(f"  F1 Score:  {f1:.4f}")
    print(f"  AUC-ROC:   {auc:.4f}")

    model_path = os.path.join(os.path.dirname(__file__), "credit_model.pkl")
    with open(model_path, "wb") as f:
        pickle.dump(pipeline, f)
    print(f"Model saved to {model_path}")


if __name__ == "__main__":
    train()
