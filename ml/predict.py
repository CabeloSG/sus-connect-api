from pathlib import Path
import sys

import joblib
import pandas as pd


BASE_DIR = Path(__file__).resolve().parent
MODEL_PATH = BASE_DIR / "model" / "no-show-model.joblib"

LOW_THRESHOLD = 0.40
HIGH_THRESHOLD = 0.70


def load_model():
    """
    Carrega o pipeline treinado para previsão
    de risco de no-show.
    """
    if not MODEL_PATH.exists():
        raise FileNotFoundError(
            f"Modelo não encontrado: {MODEL_PATH}"
        )

    return joblib.load(MODEL_PATH)


def classify_risk(probability: float) -> str:
    """
    Converte a probabilidade de no-show
    em uma classificação operacional.
    """
    if probability < LOW_THRESHOLD:
        return "LOW"

    if probability < HIGH_THRESHOLD:
        return "MEDIUM"

    return "HIGH"


def predict_no_show(model, patient_data: dict) -> dict:
    """
    Executa a previsão de risco de ausência
    utilizando o pipeline treinado.
    """
    dataframe = pd.DataFrame([patient_data])

    probability = float(
        model.predict_proba(dataframe)[0][1]
    )

    risk_level = classify_risk(probability)

    return {
        "noShowProbability": round(
            probability * 100,
            2
        ),
        "riskLevel": risk_level,
    }


def main() -> None:
    try:
        model = load_model()

        scenarios = [
            {
                "name": "CENARIO A - MENOR RISCO",
                "data": {
                    "patientAge": 45,
                    "dayOfWeek": 3,
                    "hourOfDay": 10,
                    "daysInAdvance": 7,
                    "appointmentType": "CONSULTATION",
                    "medicalSpecialty": "CLINICO_GERAL",
                    "historicalAppointments": 15,
                    "historicalNoShows": 0,
                    "historicalCompleted": 15,
                    "historicalNoShowRate": 0.0,
                    "confirmationStatus": "CONFIRMED",
                },
            },
            {
                "name": "CENARIO B - RISCO INTERMEDIARIO",
                "data": {
                    "patientAge": 38,
                    "dayOfWeek": 4,
                    "hourOfDay": 14,
                    "daysInAdvance": 35,
                    "appointmentType": "RETURN",
                    "medicalSpecialty": "ORTOPEDIA",
                    "historicalAppointments": 10,
                    "historicalNoShows": 3,
                    "historicalCompleted": 7,
                    "historicalNoShowRate": 0.30,
                    "confirmationStatus": "PENDING",
                },
            },
            {
                "name": "CENARIO C - MAIOR RISCO",
                "data": {
                    "patientAge": 63,
                    "dayOfWeek": 2,
                    "hourOfDay": 9,
                    "daysInAdvance": 75,
                    "appointmentType": "CONSULTATION",
                    "medicalSpecialty": "CARDIOLOGIA",
                    "historicalAppointments": 12,
                    "historicalNoShows": 7,
                    "historicalCompleted": 5,
                    "historicalNoShowRate": 0.5833,
                    "confirmationStatus": "PENDING",
                },
            },
        ]

        print(
            "=============================================="
        )
        print(
            "SUS CONNECT - VALIDACAO DO MODELO DE NO-SHOW"
        )
        print(
            "=============================================="
        )

        for scenario in scenarios:
            result = predict_no_show(
                model,
                scenario["data"],
            )

            print(f"\n{scenario['name']}")
            print(
                f"Probabilidade de ausência: "
                f"{result['noShowProbability']:.2f}%"
            )
            print(
                f"Classificação: "
                f"{result['riskLevel']}"
            )

        print(
            "\n=============================================="
        )

    except Exception as exception:
        print(
            f"Erro ao realizar previsão: {exception}",
            file=sys.stderr,
        )
        sys.exit(1)


if __name__ == "__main__":
    main()