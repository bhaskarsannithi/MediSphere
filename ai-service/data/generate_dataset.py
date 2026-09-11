"""Generate reproducible, synthetic and de-identified risk-model training data."""
import numpy as np
import pandas as pd

from config import DATA_DIR, RANDOM_SEED


def generate_dataset(rows: int = 1800, seed: int = RANDOM_SEED) -> pd.DataFrame:
    rng = np.random.default_rng(seed)
    age = rng.integers(25, 86, rows)
    sex = rng.integers(0, 2, rows)
    bmi = np.clip(rng.normal(28, 5.5, rows), 17, 52)
    systolic_bp = np.clip(rng.normal(124 + (age - 50) * 0.42, 16, rows), 85, 210)
    diastolic_bp = np.clip(rng.normal(78 + (age - 50) * 0.12, 10, rows), 48, 125)
    heart_rate = np.clip(rng.normal(75, 12, rows), 45, 150)
    diabetes = rng.binomial(1, np.clip(0.10 + (bmi - 25) * 0.018 + (age - 45) * 0.004, .04, .72))
    hba1c = np.clip(rng.normal(5.5 + diabetes * 2.2, .75, rows), 4.2, 13.5)
    glucose = np.clip(rng.normal(95 + diabetes * 63, 20, rows), 60, 330)
    cholesterol = np.clip(rng.normal(188 + (bmi - 25) * 2.2, 32, rows), 100, 360)
    smoking = rng.binomial(1, .22, rows)
    family_history = rng.binomial(1, .29, rows)
    previous_cvd = rng.binomial(1, np.clip(.025 + (age - 40) * .005, .02, .27))
    diabetes_duration = np.where(diabetes == 1, rng.integers(1, 23, rows), 0)
    kidney_indicator = rng.binomial(1, np.clip(.03 + diabetes * .13 + (age - 50) * .003, .02, .5))

    cvd_logit = (-8.4 + .060 * age + .022 * (systolic_bp - 110) + .013 * (cholesterol - 160)
                 + .75 * smoking + .65 * family_history + 1.35 * previous_cvd + .20 * sex
                 + rng.normal(0, .70, rows))
    diabetes_logit = (-7.5 + .035 * age + .72 * diabetes + .78 * (hba1c - 6)
                      + .012 * (glucose - 100) + .08 * diabetes_duration + 1.1 * kidney_indicator
                      + .022 * (systolic_bp - 110) + rng.normal(0, .75, rows))
    frame = pd.DataFrame(locals())
    frame["cvd_target"] = rng.binomial(1, 1 / (1 + np.exp(-cvd_logit)))
    frame["diabetes_target"] = rng.binomial(1, 1 / (1 + np.exp(-diabetes_logit)))
    return frame.drop(columns=["rng", "rows", "seed", "cvd_logit", "diabetes_logit"])


def create_partitions() -> pd.DataFrame:
    DATA_DIR.mkdir(parents=True, exist_ok=True)
    data = generate_dataset()
    data.to_csv(DATA_DIR / "synthetic_healthcare.csv", index=False)
    for hospital, partition in zip(("hospital_a", "hospital_b", "hospital_c"), np.array_split(data.sample(frac=1, random_state=RANDOM_SEED), 3)):
        partition.to_csv(DATA_DIR / f"{hospital}.csv", index=False)
    return data


if __name__ == "__main__":
    create_partitions()
