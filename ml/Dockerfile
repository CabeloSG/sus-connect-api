# ============================================================
# SUS Connect - Machine Learning Service
# Predição de risco de No-Show
# ============================================================

FROM python:3.13-slim

WORKDIR /app

# Copia as dependências primeiro para aproveitar o cache
COPY requirements.txt .

# Instala as dependências do serviço ML
RUN pip install --no-cache-dir -r requirements.txt

# Copia a API
COPY api.py .

# Copia o modelo treinado
COPY model ./model

# Porta utilizada pelo FastAPI
EXPOSE 8000

# Inicializa o serviço de inferência
CMD ["uvicorn", "api:app", "--host", "0.0.0.0", "--port", "8000"]