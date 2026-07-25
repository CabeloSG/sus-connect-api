# 🏥 SUS Connect AI

Backend desenvolvido para o **Hackathon FIAP – Pós-Graduação em Arquitetura e Desenvolvimento Java**, com foco na otimização do processo de confirmação de agendamentos do Sistema Único de Saúde (SUS).

## 📌 Sobre o projeto

O **SUS Connect AI** é uma solução criada para auxiliar unidades de saúde na gestão de agendamentos, reduzindo vagas ociosas causadas por faltas e permitindo melhor aproveitamento da capacidade de atendimento.

Ao criar um agendamento, o sistema gera automaticamente uma comunicação para o paciente por múltiplos canais:

- WhatsApp
- SMS
- E-mail

O paciente pode confirmar ou recusar o atendimento por qualquer um dos canais disponíveis.

Após a primeira resposta válida, os demais canais são automaticamente invalidados, garantindo que apenas uma resposta seja processada.

## 🎯 Objetivo

O projeto busca:

- reduzir faltas em consultas;
- melhorar o aproveitamento das vagas disponíveis;
- automatizar confirmações de agendamentos;
- reduzir processos manuais nas unidades de saúde;
- identificar vagas liberadas por recusas;
- permitir futuramente o reaproveitamento dessas vagas para pacientes em fila de espera;
- fornecer indicadores para apoio à gestão.

## 🔄 Fluxo principal

```text
Paciente cadastrado
        ↓
Agendamento criado
        ↓
Communication criada automaticamente
        ↓
┌────────────┬────────────┬────────────┐
│ WhatsApp   │    SMS     │   E-mail   │
└────────────┴────────────┴────────────┘
        ↓
Paciente responde
        ↓
     YES / NO
      ↙    ↘
CONFIRMED  CANCELLED
     ↓        ↓
Demais canais são invalidados
```

### Confirmação

Quando o paciente responde `YES`:

```text
Communication → CONFIRMED
Appointment   → CONFIRMED
Canal usado   → RESPONDED
Outros canais → INVALIDATED
```

### Recusa

Quando o paciente responde `NO`:

```text
Communication → DECLINED
Appointment   → CANCELLED
Canal usado   → RESPONDED
Outros canais → INVALIDATED
```

A vaga cancelada poderá posteriormente ser disponibilizada para reaproveitamento pela unidade de saúde.

## 🏗️ Arquitetura

O projeto está sendo desenvolvido utilizando conceitos de:

- Clean Architecture
- Domain-Driven Design (DDD)
- REST API
- Microsserviços
- Event-Driven Architecture
- SOLID
- separação por domínio e responsabilidade

A evolução da arquitetura prevê comunicação assíncrona utilizando Apache Kafka.

## 🧩 Domínios

Atualmente o projeto possui módulos relacionados a:

```text
patient
appointment
communication
confirmation
common
config
```

Entre as principais responsabilidades estão cadastro de pacientes, gerenciamento de agendamentos, geração de comunicações, envio simulado por múltiplos canais e processamento das respostas dos pacientes.

## 🛠️ Tecnologias

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- Maven
- Docker
- Swagger / OpenAPI
- Lombok

### Evolução prevista

- Apache Kafka
- Event-Driven Architecture
- Dashboard de indicadores
- reaproveitamento de vagas
- fila de espera inteligente
- integração com serviços externos de comunicação
- Inteligência Artificial para previsão de ausência
- Kubernetes

## 📡 API

A API utiliza endpoints REST e pode ser demonstrada através do Swagger.

Exemplos:

```http
/api/v1/patients
/api/v1/appointments
/api/v1/communications
```

### Resposta do paciente

```http
POST /api/v1/communications/respond
```

Exemplo:

```json
{
  "token": "token-da-notificacao",
  "response": "YES"
}
```

A primeira resposta válida determina o estado do agendamento e invalida os demais canais.

### Disparo das notificações

```http
POST /api/v1/communications/dispatch
```

No MVP, o envio por WhatsApp, SMS e E-mail é simulado.

## 🚀 Executando o projeto

### Pré-requisitos

- Java
- Maven
- Docker
- Docker Compose

Clone o repositório:

```bash
git clone https://github.com/CabeloSG/sus-connect-api.git
```

Entre no projeto:

```bash
cd sus-connect-api
```

Suba a infraestrutura:

```bash
docker compose up -d
```

Execute a aplicação:

```bash
./mvnw spring-boot:run
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Após iniciar a aplicação, utilize o Swagger para explorar e testar os endpoints disponíveis.

## 🗺️ Roadmap

### Concluído

- Cadastro de pacientes
- CRUD inicial de agendamentos
- geração automática de comunicação
- criação das notificações WhatsApp, SMS e E-mail
- simulação de envio
- resposta `YES`
- resposta `NO`
- atualização automática do agendamento
- invalidação dos canais restantes
- bloqueio de múltiplas respostas

### Próximas etapas

- tratamento de vagas liberadas por recusas
- disponibilização das vagas para atendentes
- fila de espera
- Apache Kafka
- eventos de domínio
- dashboard
- indicadores de confirmações e recusas
- observabilidade
- evolução para Kubernetes
- recursos de Inteligência Artificial

## 👥 Equipe

Projeto desenvolvido durante o **Hackathon FIAP – Arquitetura e Desenvolvimento Java**.

- Leandro Gonçalves
- Felipe
- Lucas

## 📚 Contexto acadêmico

**FIAP**  
Pós-Graduação em Arquitetura e Desenvolvimento Java  
Tech Challenge / Hackathon

---

### SUS Connect AI

**Tecnologia para melhorar o aproveitamento dos recursos públicos de saúde.**
