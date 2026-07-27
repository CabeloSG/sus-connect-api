# 🏥 SUS Connect AI

> **Tecnologia para otimizar agendamentos, reduzir vagas ociosas e apoiar a gestão do Sistema Único de Saúde.**

Backend desenvolvido para o **Hackathon FIAP – Pós-Graduação em Arquitetura e Desenvolvimento Java**, aplicando conceitos de arquitetura de software, sistemas distribuídos, processamento orientado a eventos e Machine Learning ao problema de faltas e ociosidade em agendamentos do SUS.

---

## 📌 Sobre o projeto

O **SUS Connect AI** é uma plataforma backend criada para apoiar unidades de saúde na gestão do ciclo de vida dos agendamentos.

A solução automatiza a comunicação com o paciente antes da consulta, registra confirmações e recusas, identifica vagas liberadas, permite localizar novas disponibilidades e utiliza **Machine Learning para estimar o risco de ausência (no-show)**.

O projeto foi desenvolvido como MVP backend, com demonstração das funcionalidades por meio do **Swagger / OpenAPI**.

---

## 🎯 Problema

Faltas em consultas e procedimentos podem gerar vagas ociosas, desperdício de capacidade operacional e aumento do tempo de espera de outros pacientes.

Além da ausência propriamente dita, a falta de confirmação antecipada dificulta que unidades de saúde saibam, com antecedência, quais horários poderão ser reaproveitados.

O SUS Connect AI atua nesse fluxo permitindo:

- comunicação automatizada com o paciente;
- confirmação ou recusa antecipada;
- identificação de vagas liberadas;
- localização de próxima disponibilidade;
- acompanhamento de indicadores;
- registro do comparecimento real;
- predição de risco de ausência por Machine Learning.

---

## 💡 Solução

Quando um agendamento é criado, o SUS Connect AI gera automaticamente uma comunicação vinculada ao atendimento.

São preparados três canais:

- WhatsApp;
- SMS;
- E-mail.

O paciente pode responder por qualquer um deles.

A primeira resposta válida é considerada definitiva e os demais canais são invalidados, evitando respostas conflitantes.

```text
Paciente
   │
   ▼
Agendamento
   │
   ▼
Communication
   │
   ├── WhatsApp
   ├── SMS
   └── E-mail
          │
          ▼
       YES / NO
       /      \
      ▼        ▼
 CONFIRMED   CANCELLED
                │
                ▼
          Vaga liberada
                │
                ▼
              Kafka
```

---

# 🔄 Ciclo do agendamento

## 1. Criação

Um novo agendamento é associado a um paciente e contém informações como:

- data e horário;
- tipo do atendimento;
- especialidade;
- profissional;
- unidade de saúde;
- prazo de confirmação.

A comunicação é gerada automaticamente após a criação do agendamento.

---

## 2. Comunicação multicanal

Cada comunicação pode possuir entregas por:

```text
WHATSAPP
SMS
EMAIL
```

Cada entrega possui um token próprio para identificação da resposta.

---

## 3. Confirmação — YES

Quando o paciente responde `YES`:

```text
Communication → CONFIRMED
Appointment   → CONFIRMED
Canal usado   → RESPONDED
Outros canais → INVALIDATED
```

A consulta permanece agendada.

---

## 4. Recusa — NO

Quando o paciente responde `NO`:

```text
Communication → DECLINED
Appointment   → CANCELLED
Canal usado   → RESPONDED
Outros canais → INVALIDATED
```

O horário cancelado pode ser transformado em uma vaga disponível para reaproveitamento.

A aplicação também pode localizar uma próxima disponibilidade compatível e publicar o evento correspondente para processamento assíncrono.

---

## 5. Ausência de resposta

Caso o paciente não responda dentro do prazo, a comunicação pode expirar.

**A expiração da comunicação não cancela automaticamente o agendamento.**

A ausência de resposta e a ausência física do paciente são situações diferentes.

```text
Sem resposta
     │
     ▼
Communication → EXPIRED
     │
     ▼
Appointment permanece válido
```

Somente uma decisão explícita de recusa (`NO`) cancela antecipadamente o atendimento nesse fluxo.

---

## 6. Comparecimento

Após o horário da consulta, a unidade pode registrar o desfecho real do atendimento.

```text
attended = true
        ↓
COMPLETED
```

Caso o paciente não compareça:

```text
attended = false
        ↓
NO_SHOW
```

Dessa forma, o sistema diferencia:

- paciente que confirmou;
- paciente que recusou;
- paciente que não respondeu;
- paciente que compareceu;
- paciente que realmente faltou.

---

# 🤖 Machine Learning — Predição de No-Show

O SUS Connect AI possui um serviço independente de **Machine Learning**, desenvolvido em Python e disponibilizado através de uma API FastAPI.

O objetivo é estimar a probabilidade de ausência do paciente em determinado agendamento.

A API Java envia características do atendimento ao serviço de ML, que executa o modelo treinado e retorna:

```json
{
  "noShowProbability": 5.63,
  "riskLevel": "LOW"
}
```

Os níveis utilizados são:

```text
LOW
MEDIUM
HIGH
```

Entre as informações utilizadas pelo modelo estão características temporais, dados do atendimento e histórico relacionado aos agendamentos.

### Arquitetura da predição

```text
Swagger
   │
   ▼
Spring Boot
   │
   ▼
NoShow ML Client
   │ HTTP
   ▼
FastAPI
   │
   ▼
Modelo Machine Learning
   │
   ▼
predict_proba()
   │
   ▼
Probabilidade + nível de risco
```

O modelo treinado é carregado pelo serviço Python durante sua inicialização.

---

# ⚡ Event-Driven Architecture e Apache Kafka

O projeto utiliza **Apache Kafka** para processamento assíncrono de eventos relacionados ao reaproveitamento de vagas.

Entre os eventos implementados estão:

```text
AvailableSlotReleasedEvent
NextAvailabilityFoundEvent
```

Tópicos utilizados:

```text
available-slot-released
next-availability-found
```

Exemplo:

```text
Paciente recusa consulta
        │
        ▼
Appointment CANCELLED
        │
        ▼
AvailableSlot criada
        │
        ▼
AvailableSlotReleasedEvent
        │
        ▼
Apache Kafka
        │
        ▼
Consumer
        │
        ▼
Notificação da unidade
```

A arquitetura orientada a eventos reduz o acoplamento entre os componentes responsáveis pelo fluxo operacional.

---

# 🔎 CQRS

O projeto aplica princípios de **CQRS (Command Query Responsibility Segregation)** em partes da aplicação.

Operações responsáveis por alterar estado são separadas das operações destinadas à consulta.

Exemplo no domínio de comunicação:

```text
communication
└── application
    ├── command
    │   ├── dto
    │   └── usecase
    │
    └── query
        ├── dto
        └── mapper
```

Entre os Commands encontram-se operações como:

```text
GenerateCommunicationUseCase
RegisterPatientResponseUseCase
ExpireCommunicationsUseCase
```

Essa separação melhora a organização das responsabilidades e prepara a arquitetura para futuras evoluções independentes dos modelos de leitura e escrita.

> O MVP utiliza CQRS em nível de organização e responsabilidades da aplicação, sem bancos físicos separados para leitura e escrita.

---

# 🏗️ Arquitetura

O SUS Connect AI utiliza conceitos e padrões como:

- Clean Architecture;
- Domain-Driven Design (DDD);
- CQRS;
- Event-Driven Architecture;
- REST;
- SOLID;
- separação por domínio;
- processamento assíncrono;
- Machine Learning como serviço independente;
- containerização com Docker.

Visão simplificada:

```text
                     ┌──────────────────┐
                     │ Swagger / Client │
                     └────────┬─────────┘
                              │
                              ▼
                     ┌──────────────────┐
                     │ SUS Connect API  │
                     │ Spring Boot      │
                     │ Java 21          │
                     └───────┬──────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
              ▼              ▼              ▼
        PostgreSQL         Kafka         FastAPI
          :5432            :9092          :8000
                                             │
                                             ▼
                                      Machine Learning
                                      No-Show Model
```

---

# 🧩 Domínios e módulos

A aplicação está organizada por domínio:

```text
appointment
availability
communication
confirmation
dashboard
ml
notification
patient
common
config
shared
```

### Patient

Gerenciamento dos pacientes.

### Appointment

Criação e consulta de agendamentos e registro do comparecimento.

### Communication

Geração das comunicações, entregas multicanal, respostas e expiração.

### Availability

Gerenciamento e consulta de vagas disponibilizadas para reaproveitamento.

### Confirmation

Recursos relacionados ao ciclo de confirmação dos agendamentos.

### Notification

Simulação dos mecanismos de notificação ao paciente e à unidade.

### Dashboard

Consolidação de indicadores operacionais.

### ML

Integração da aplicação Java com o serviço de predição de no-show.

---

# 📊 Dashboard

O projeto possui recursos para geração de indicadores relacionados à operação do SUS Connect.

Entre os dados disponíveis estão indicadores de:

- agendamentos;
- comunicações;
- vagas disponíveis;
- desempenho operacional.

Essas informações podem apoiar a gestão das unidades e futuras ferramentas de visualização.

---

# 🛠️ Tecnologias

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Hibernate
- Maven
- Lombok
- Swagger / OpenAPI

### Dados e mensageria

- PostgreSQL 16
- Apache Kafka
- Spring Kafka

### Machine Learning

- Python
- FastAPI
- Uvicorn
- pandas
- NumPy
- scikit-learn
- joblib

### Infraestrutura

- Docker
- Docker Compose

### Testes

- JUnit
- Mockito
- Spring Boot Test

---

# 🐳 Docker

O ambiente do projeto pode ser executado integralmente utilizando Docker Compose.

A stack contém:

```text
sus-connect-api         → Spring Boot
sus-connect-postgres    → PostgreSQL
sus-connect-kafka       → Apache Kafka
sus-connect-ml          → FastAPI + Machine Learning
```

Arquitetura:

```text
Docker Compose
│
├── sus-connect-api
│      ├── PostgreSQL
│      ├── Kafka
│      └── ML Service
│
├── sus-connect-postgres
│
├── sus-connect-kafka
│
└── sus-connect-ml
```

---

# 🚀 Executando o projeto

## Pré-requisitos

Para execução através do Docker:

- Git
- Docker
- Docker Compose

Clone o repositório:

```bash
git clone https://github.com/CabeloSG/sus-connect-api.git
```

Entre no diretório:

```bash
cd sus-connect-api
```

Construa e inicie o ambiente:

```bash
docker compose up -d --build
```

Verifique os serviços:

```bash
docker compose ps
```

Os principais serviços deverão estar disponíveis em:

```text
Spring Boot API → porta 8080
PostgreSQL      → porta 5432
Apache Kafka    → porta 9092
ML Service      → porta 8000
```

Para encerrar:

```bash
docker compose down
```

---

# 🧪 Testes automatizados

Para executar os testes:

### Windows

```powershell
.\mvnw.cmd test
```

### Linux / macOS

```bash
./mvnw test
```

Durante o desenvolvimento do MVP, a suíte atingiu:

```text
Tests run: 87
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

Os testes cobrem casos de uso relacionados a pacientes, agendamentos, comunicação, expiração, disponibilidade, notificações, dashboard e integração com o serviço de Machine Learning.

---

# 📡 Swagger / OpenAPI

Com o ambiente em execução, a documentação interativa da API está disponível em:

```text
http://localhost:8080/swagger
```

Através do Swagger é possível demonstrar o funcionamento do MVP sem necessidade de uma interface frontend.

---

# 📡 Principais recursos da API

A API disponibiliza recursos relacionados a:

```http
/api/v1/patients
/api/v1/appointments
/api/v1/communications
```

Além desses, o Swagger apresenta os endpoints implementados para disponibilidade de vagas, dashboard e predição de risco de no-show.

### Registrar resposta do paciente

Exemplo:

```json
{
  "token": "token-da-notificacao",
  "response": "YES"
}
```

ou:

```json
{
  "token": "token-da-notificacao",
  "response": "NO"
}
```

### Registrar comparecimento

```json
{
  "attended": true
}
```

Resultado:

```text
COMPLETED
```

Para ausência:

```json
{
  "attended": false
}
```

Resultado:

```text
NO_SHOW
```

---

# 🔐 Regras importantes do domínio

O SUS Connect AI implementa algumas regras centrais:

1. Apenas uma resposta é aceita por comunicação.
2. A primeira resposta válida invalida os demais canais.
3. `YES` confirma o agendamento.
4. `NO` cancela o agendamento.
5. A recusa pode liberar a vaga para reaproveitamento.
6. Comunicação expirada não significa cancelamento automático da consulta.
7. `NO_SHOW` representa ausência real após o horário do atendimento.
8. `COMPLETED` representa atendimento realizado.
9. Comparecimento não pode ser registrado antes do horário da consulta.
10. Agendamentos cancelados não podem receber registro de comparecimento.

---

# ✅ Funcionalidades implementadas no MVP

- cadastro e gerenciamento de pacientes;
- criação e consulta de agendamentos;
- comunicação automática vinculada ao agendamento;
- canais WhatsApp, SMS e E-mail simulados;
- tokens individuais por canal;
- confirmação `YES`;
- recusa `NO`;
- invalidação automática dos demais canais;
- expiração das comunicações;
- preservação do agendamento quando não houver resposta;
- liberação de vagas;
- consulta de vagas disponíveis;
- busca da próxima disponibilidade;
- Apache Kafka;
- Event-Driven Architecture;
- notificação assíncrona;
- registro de comparecimento;
- `COMPLETED`;
- `NO_SHOW`;
- dashboard de indicadores;
- CQRS;
- serviço Python/FastAPI;
- modelo de Machine Learning para risco de no-show;
- integração Spring Boot ↔ FastAPI;
- Docker Compose;
- testes automatizados;
- documentação Swagger/OpenAPI.

---

# 🔭 Evoluções futuras

O MVP estabelece uma base arquitetural para futuras evoluções, como:

- integração com sistemas oficiais do SUS;
- integração real com WhatsApp, SMS e E-mail;
- fila de espera inteligente;
- reserva automática ou assistida de vagas liberadas;
- evolução e retreinamento do modelo de Machine Learning com dados reais e devidamente governados;
- autenticação e autorização adequadas ao ambiente produtivo;
- observabilidade distribuída;
- métricas, tracing e logs centralizados;
- Kubernetes;
- escalabilidade independente dos serviços;
- resiliência e tolerância a falhas;
- evolução do CQRS;
- novos eventos de domínio.

---

# 👥 Equipe

Projeto desenvolvido durante o **Hackathon FIAP – Pós-Graduação em Arquitetura e Desenvolvimento Java**.

### Integrantes

- **Leandro da Silva Gonçalves – RM367789**
- **Filipe Gonçalves Ferreira – RM367737**
- **Lucas Santos Escolástico do Nascimento – RM367273**

---

# 🎓 Contexto acadêmico

**FIAP**  
**Pós-Graduação em Arquitetura e Desenvolvimento Java**  
**Tech Challenge – Hackathon**

O projeto demonstra a aplicação prática de conceitos estudados durante a pós-graduação, incluindo arquitetura de software, DDD, Clean Architecture, CQRS, sistemas distribuídos, mensageria, containerização, integração de serviços e Machine Learning.

---

# 🏥 SUS Connect AI

**Tecnologia para melhorar o aproveitamento dos recursos públicos de saúde.**
