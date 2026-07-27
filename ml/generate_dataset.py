import csv
import math
import random
from pathlib import Path

SEED = 42
DATASET_SIZE = 5000

random.seed(SEED)

APPOINTMENT_TYPES = [
    "CONSULTATION",
    "EXAM",
    "RETURN",
]

MEDICAL_SPECIALTIES = [
    "CLINICO_GERAL",
    "CARDIOLOGIA",
    "ORTOPEDIA",
    "PEDIATRIA",
    "GINECOLOGIA",
    "DERMATOLOGIA",
    "OFTALMOLOGIA",
    "PSIQUIATRIA",
    "ODONTOLOGIA",
]

CONFIRMATION_STATUSES = [
    "PENDING",
    "CONFIRMED",
]


def sigmoid(value: float) -> float:
    return 1.0 / (1.0 + math.exp(-value))


def generate_row() -> dict:
    patient_age = random.randint(18, 90)

    # Python: 1 = segunda-feira e 7 = domingo.
    day_of_week = random.randint(1, 7)

    hour_of_day = random.randint(7, 17)
    days_in_advance = random.randint(1, 90)

    appointment_type = random.choice(APPOINTMENT_TYPES)
    medical_specialty = random.choice(MEDICAL_SPECIALTIES)

    historical_appointments = random.randint(0, 20)

    if historical_appointments == 0:
        historical_no_shows = 0
        historical_completed = 0
    else:
        historical_no_shows = random.randint(
            0,
            historical_appointments
        )

        historical_completed = (
            historical_appointments - historical_no_shows
        )

    concluded_history = (
        historical_no_shows + historical_completed
    )

    historical_no_show_rate = (
        historical_no_shows / concluded_history
        if concluded_history > 0
        else 0.0
    )

    confirmation_status = random.choices(
        CONFIRMATION_STATUSES,
        weights=[35, 65],
        k=1
    )[0]

    # Relação sintética criada exclusivamente para o MVP.
    #
    # Maior histórico de faltas, maior antecedência e
    # ausência de confirmação aumentam a probabilidade
    # simulada de no-show.
    score = -2.2

    score += historical_no_show_rate * 3.2
    score += min(days_in_advance, 90) * 0.012

    if confirmation_status == "PENDING":
        score += 0.9
    else:
        score -= 0.8

    if day_of_week in (1, 5):
        score += 0.15

    if hour_of_day <= 8:
        score += 0.10

    # Pequeno ruído aleatório evita um dataset
    # excessivamente determinístico.
    score += random.gauss(0, 0.45)

    no_show_probability = sigmoid(score)

    no_show = (
        1
        if random.random() < no_show_probability
        else 0
    )

    return {
        "patientAge": patient_age,
        "dayOfWeek": day_of_week,
        "hourOfDay": hour_of_day,
        "daysInAdvance": days_in_advance,
        "appointmentType": appointment_type,
        "medicalSpecialty": medical_specialty,
        "historicalAppointments": historical_appointments,
        "historicalNoShows": historical_no_shows,
        "historicalCompleted": historical_completed,
        "historicalNoShowRate": round(
            historical_no_show_rate,
            4
        ),
        "confirmationStatus": confirmation_status,
        "noShow": no_show,
    }


def main() -> None:
    output_directory = Path(__file__).parent / "data"

    output_directory.mkdir(
        parents=True,
        exist_ok=True
    )

    output_file = (
        output_directory /
        "no-show-training-data.csv"
    )

    fieldnames = [
        "patientAge",
        "dayOfWeek",
        "hourOfDay",
        "daysInAdvance",
        "appointmentType",
        "medicalSpecialty",
        "historicalAppointments",
        "historicalNoShows",
        "historicalCompleted",
        "historicalNoShowRate",
        "confirmationStatus",
        "noShow",
    ]

    rows = [
        generate_row()
        for _ in range(DATASET_SIZE)
    ]

    with output_file.open(
        "w",
        newline="",
        encoding="utf-8"
    ) as csv_file:

        writer = csv.DictWriter(
            csv_file,
            fieldnames=fieldnames
        )

        writer.writeheader()
        writer.writerows(rows)

    no_show_count = sum(
        row["noShow"]
        for row in rows
    )

    attendance_count = (
        DATASET_SIZE - no_show_count
    )

    print("Dataset sintético gerado com sucesso.")
    print(f"Arquivo: {output_file}")
    print(f"Registros: {DATASET_SIZE}")
    print(f"Comparecimento: {attendance_count}")
    print(f"No-show: {no_show_count}")
    print(
        "Taxa de no-show: "
        f"{(no_show_count / DATASET_SIZE) * 100:.2f}%"
    )


if __name__ == "__main__":
    main()