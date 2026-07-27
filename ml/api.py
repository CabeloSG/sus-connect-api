from contextlib import asynccontextmanager
from pathlib import Path

import joblib
import pandas as pd
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field


BASE_DIR = Path(__file__).resolve().parent
MODEL_PATH = BASE_DIR / "model" / "no-show-model.joblib"

LOW_THRESHOLD = 0.40
HIGH_THRESHOLD = 0.70

model = None


class PredictionRequest(BaseModel):
    """
    Dados utilizados pelo modelo para calcular
    o risco de ausência do paciente.
    """

    patientAge: int = Field(ge=0, le=120)
    dayOfWeek: int = Field(ge=1, le=7)
    hourOfDay: int = Field(ge=0, le=23)
    daysInAdvance: int = Field(ge=0)

    appointmentType: str
    medicalSpecialty: str

    historicalAppointments: int = Field(ge=0)
    historicalNoShows: int = Field(ge=0)
    historicalCompleted: int = Field(ge=0)

    historicalNoShowRate: float = Field(ge=0.0, le=1.0)

    confirmationStatus: str


class PredictionResponse(BaseModel):
    """
    Resultado produzido pelo modelo de Machine Learning.
    """

    noShowProbability: float
    riskLevel: str


def classify_risk(probability: float) -> str:
    """
    Classifica a probabilidade calculada pelo modelo.
    """

    if probability < LOW_THRESHOLD:
        return "LOW"

    if probability < HIGH_THRESHOLD:
        return "MEDIUM"

    return "HIGH"


def load_ml_model():
    """
    Carrega o modelo treinado na inicialização da API.
    """

    if not MODEL_PATH.exists():
        raise RuntimeError(
            f"Modelo não encontrado: {MODEL_PATH}"
        )

    return joblib.load(MODEL_PATH)


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Gerencia o ciclo de vida da aplicação.
    """

    global model

    model = load_ml_model()

    print("Modelo de no-show carregado com sucesso.")

    yield

    model = None


app = FastAPI(
    title="SUS Connect - No-Show ML Service",
    description=(
        "Serviço responsável pela predição "
        "do risco de ausência em agendamentos."
    ),
    version="1.0.0",
    lifespan=lifespan,
)


@app.get("/health")
def health():
    """
    Verifica se o serviço e o modelo estão disponíveis.
    """

    return {
        "status": "UP",
        "modelLoaded": model is not None,
    }


@app.post(
    "/predict",
    response_model=PredictionResponse,
)
def predict(request: PredictionRequest):
    """
    Calcula a probabilidade de no-show.
    """

    if model is None:
        raise HTTPException(
            status_code=503,
            detail="Modelo de Machine Learning indisponível.",
        )

    try:
        dataframe = pd.DataFrame(
            [request.model_dump()]
        )

        probability = float(
            model.predict_proba(dataframe)[0][1]
        )

        return PredictionResponse(
            noShowProbability=round(
                probability * 100,
                2,
                ),
            riskLevel=classify_risk(probability),
        )

    except Exception as exception:
        raise HTTPException(
            status_code=500,
            detail="Erro ao realizar predição de no-show.",
        ) from exception