from pathlib import Path

import joblib
import pandas as pd
from sklearn.compose import ColumnTransformer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import (
    accuracy_score,
    classification_report,
    confusion_matrix,
    f1_score,
    precision_score,
    recall_score,
    roc_auc_score,
)
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder, StandardScaler


SEED = 42
TEST_SIZE = 0.20

BASE_DIR = Path(__file__).resolve().parent
DATASET_PATH = BASE_DIR / "data" / "no-show-training-data.csv"
MODEL_DIR = BASE_DIR / "model"
MODEL_PATH = MODEL_DIR / "no-show-model.joblib"

TARGET_COLUMN = "noShow"


def load_dataset() -> pd.DataFrame:
    """
    Carrega o dataset utilizado para treinamento do modelo.
    """
    if not DATASET_PATH.exists():
        raise FileNotFoundError(
            f"Dataset não encontrado: {DATASET_PATH}"
        )

    dataframe = pd.read_csv(DATASET_PATH)

    if dataframe.empty:
        raise ValueError("O dataset está vazio.")

    if TARGET_COLUMN not in dataframe.columns:
        raise ValueError(
            f"Coluna alvo '{TARGET_COLUMN}' não encontrada."
        )

    return dataframe


def prepare_features(
        dataframe: pd.DataFrame,
) -> tuple[pd.DataFrame, pd.Series]:
    """
    Separa as variáveis utilizadas pelo modelo e a variável alvo.
    """
    x = dataframe.drop(columns=[TARGET_COLUMN])
    y = dataframe[TARGET_COLUMN]

    return x, y


def build_pipeline(x: pd.DataFrame) -> Pipeline:
    """
    Constrói o pipeline de pré-processamento e classificação.
    """

    categorical_columns = list(
        x.select_dtypes(
            include=["object", "string", "category"]
        ).columns
    )

    numerical_columns = list(
        x.select_dtypes(include=["number", "bool"]).columns
    )

    preprocessor = ColumnTransformer(
        transformers=[
            (
                "categorical",
                OneHotEncoder(
                    handle_unknown="ignore"
                ),
                categorical_columns,
            ),
            (
                "numerical",
                StandardScaler(),
                numerical_columns,
            ),
        ]
    )

    classifier = LogisticRegression(
        max_iter=1000,
        random_state=SEED,
        class_weight="balanced",
    )

    return Pipeline(
        steps=[
            ("preprocessor", preprocessor),
            ("classifier", classifier),
        ]
    )


def evaluate_model(
        pipeline: Pipeline,
        x_test: pd.DataFrame,
        y_test: pd.Series,
) -> None:
    """
    Avalia o modelo utilizando métricas de classificação.
    """

    predictions = pipeline.predict(x_test)
    probabilities = pipeline.predict_proba(x_test)[:, 1]

    accuracy = accuracy_score(y_test, predictions)
    precision = precision_score(
        y_test,
        predictions,
        zero_division=0,
    )
    recall = recall_score(
        y_test,
        predictions,
        zero_division=0,
    )
    f1 = f1_score(
        y_test,
        predictions,
        zero_division=0,
    )
    roc_auc = roc_auc_score(y_test, probabilities)

    matrix = confusion_matrix(y_test, predictions)

    print("\n==============================================")
    print("SUS CONNECT - MODELO DE RISCO DE NO-SHOW")
    print("==============================================")

    print(f"\nRegistros de teste: {len(y_test)}")

    print("\nMétricas:")
    print(f"Accuracy : {accuracy:.4f}")
    print(f"Precision: {precision:.4f}")
    print(f"Recall   : {recall:.4f}")
    print(f"F1-score : {f1:.4f}")
    print(f"ROC-AUC  : {roc_auc:.4f}")

    print("\nMatriz de confusão:")
    print(matrix)

    print("\nRelatório de classificação:")
    print(
        classification_report(
            y_test,
            predictions,
            target_names=[
                "COMPARECIMENTO",
                "NO_SHOW",
            ],
            zero_division=0,
        )
    )


def save_model(pipeline: Pipeline) -> None:
    """
    Persiste o pipeline treinado para utilização posterior.
    """

    MODEL_DIR.mkdir(
        parents=True,
        exist_ok=True,
    )

    joblib.dump(
        pipeline,
        MODEL_PATH,
    )

    print(f"\nModelo salvo em:")
    print(MODEL_PATH)


def main() -> None:
    """
    Executa o treinamento completo do modelo.
    """

    print("Carregando dataset...")

    dataframe = load_dataset()

    print(f"Registros encontrados: {len(dataframe)}")

    x, y = prepare_features(dataframe)

    print(f"Variáveis utilizadas: {len(x.columns)}")
    print(f"Taxa de no-show: {y.mean() * 100:.2f}%")

    x_train, x_test, y_train, y_test = train_test_split(
        x,
        y,
        test_size=TEST_SIZE,
        random_state=SEED,
        stratify=y,
    )

    print(f"Registros de treinamento: {len(x_train)}")
    print(f"Registros de teste: {len(x_test)}")

    pipeline = build_pipeline(x)

    print("\nTreinando modelo...")

    pipeline.fit(
        x_train,
        y_train,
    )

    print("Treinamento concluído.")

    evaluate_model(
        pipeline,
        x_test,
        y_test,
    )

    save_model(pipeline)

    print("\nProcesso finalizado com sucesso.")


if __name__ == "__main__":
    main()