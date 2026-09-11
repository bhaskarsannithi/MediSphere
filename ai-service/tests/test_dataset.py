from data.generate_dataset import generate_dataset


def test_synthetic_data_is_reproducible_and_has_targets():
    first = generate_dataset(40, 7)
    second = generate_dataset(40, 7)
    assert first.equals(second)
    assert {"cvd_target", "diabetes_target", "hba1c", "systolic_bp"}.issubset(first.columns)
