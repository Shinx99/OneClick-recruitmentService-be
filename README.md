# 🧭 OneClick Recruitment Service
## Recruitment Service (CV + Job) — Job Recruitment Platform
>Microservice phụ trách quản lý CV, Job postings, và Matching cho hệ thống tuyển dụng.
Xử lý CV ứng viên (parsing, OCR), quản lý tin tuyển dụng, và cung cấp thuật toán matching thông minh giữa ứng viên và cơ hội việc làm.


[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📋 Table of Contents

- [About the Project](#about-the-project)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Running with Docker](#running-with-docker)
- [Database Migrations](#database-migrations)
- [API Documentation](#api-documentation)
- [Development](#development)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

---

## 📖 About the Project
>Recruitment Service là core microservice xử lý toàn bộ quy trình tuyển dụng:

- CV parsing/upload

- Job posting/management

- Candidate–Job matching

- Service hỗ trợ OCR cho CV PDF, AI matching, và tích hợp với Auth Service.

### Chức năng chính gồm:

- CV upload & parsing (OCR, JSON extraction)

- Job posting & management cho Recruiter

- Intelligent matching (skills, experience, location)

- Candidate profile với CV history

- Job recommendation engine

- Search & filtering (skills, salary, location)
---
## ✨ Features
- ✅ CV Processing: Upload PDF/DOC → OCR → JSON skills extraction
- ✅ Job Management: CRUD jobs, categories, requirements
- ✅ Matching Engine: AI-based candidate-job matching
- ✅ Search & Filter: Elasticsearch-like full-text search
- ✅ Hot Reload: Docker Compose dev + Spring DevTools
- ✅ Multi-tenant: Candidate/Recruiter separation
- ✅ Database Migration: Flyway auto-migrate
- ✅ Caching: Redis cho sessions/matching results
- ✅ Docker Optimized: Maven cache, delegated volumes
- ✅ RESTful API + Swagger: Auto-generated docs
- ✅ Health Monitoring: Actuator endpoints

---

## 🛠 Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Programming Language |
| Spring Boot | 3.5.9 | Backend Framework |
| PostgreSQL | Latest | Database |
| Redis | Latest | Caching |
| Flyway | Latest | Database Migration |
| Docker | Latest | Containerization |
| Maven | 3.8+ | Build Tool |



---
## 📁 Project Structure
```text
recruitmentService/  # hoặc cvservice-be
├── src/
│   ├── main/
│   │   ├── java/com/oneClick/recruitmentService/
│   │   │   ├── RecruitmentServiceApplication.java
│   │   │   ├── config/           # HikariCP, Redis, Security
│   │   │   ├── controller/       # CVController, JobController, MatchController
│   │   │   ├── dto/              # CvDto, JobDto, MatchDto
│   │   │   ├── domain/           # CvEntity, JobEntity, CandidateProfile
│   │   │   ├── repository/       # JpaRepository interfaces
│   │   │   ├── service/          # CvParsingService, JobMatchingService
│   │   │   └── ocr/              # OCR processing (Tesseract/Google Vision)
│   │   └── resources/
│   │       ├── db/migration/     # V1__create_cv_job_tables.sql
│   │       └── application.yml   # Multi-profile config
│   └── test/
├── compose.yml                    # Docker Compose (Postgres + Redis + App)
├── Dockerfile                     # Hot reload for Maven dev
├── .env.example                   # Environment variables
├── pom.xml                        # Spring Boot + OCR libs
└── README.md                      # This file
```

## 🚀 Getting Started
### Prerequisites
- Docker (Recommended)

- Docker 20.10+ & Docker Compose 2.0+

### Optional Local Setup

- JDK 17+

- Maven 3.9+

- PostgreSQL, Redis

### Installation
```bash
git clone <repo-url>
cd recruitmentService
cp .env.example .env  # Update POSTGRES_PASSWORD
```
### .env.example content:
```bash
POSTGRES_DB=recruitment_db
POSTGRES_USER=postgres  
POSTGRES_PASSWORD=your_secure_password
APP_NAME=cvService-be
SPRING_PROFILES_ACTIVE=dev
```
### Running with Docker 🚀

```bash
# Start full stack (DB + Redis + cvService)
DOCKER_BUILDKIT=1 docker compose up --build
```
### Development workflow:

```bash
docker compose up --build     # Hot rebuild (~20s)
docker compose logs -f app    # View logs
docker compose down           # Stop
```
### Endpoints:

```text
http://localhost:8082/actuator/health    # Health check
http://localhost:8082/swagger-ui.html    # API Docs
http://localhost:8082/api-docs           # OpenAPI JSON
http://localhost:5433                    # PostgreSQL
http://localhost:6380                    # Redis
```

## 🗄 Database Migrations
### Flyway auto-migrate khi start:

```text
V1__create_cv_and_job_tables.sql
V2__add_candidate_profile.sql  
V3__job_requirements_index.sql
```
### Create new migration:

```bash
docker compose exec app mvn flyway:migrate -Dflyway.locations=filesystem:./src/main/resources/db/migration
```
## 📚 API Documentation
>### Swagger UI: http://localhost:8082/swagger-ui.html

### Endpoints chính:

```text
POST /api/cv/upload          # CV PDF → JSON skills
GET  /api/cv/{id}            # Get parsed CV
POST /api/jobs               # Create job posting
GET  /api/matching/{cvId}    # Find matching jobs
GET  /api/jobs/search        # Job search & filter
```

## 💻 Development
### Hot Reload Workflow
- Code → Save Java file

- Rebuild: docker compose up --build (~20s)

- Auto restart: Spring DevTools (~3s)

- Browser refresh: LiveReload port 35730

### Debug Mode
```text
IntelliJ IDEA → Run → Edit Configurations → Remote JVM Debug  
Host: localhost, Port: 5006
```
### Performance Tips
```bash
# Super fast rebuild (deps cached)
DOCKER_BUILDKIT=1 docker compose up --build

# Skip tests (dev)
docker compose build --no-cache --build-arg SKIP_TESTS=true
```

## 🧪 Testing
```bash
# Unit tests
docker compose exec app mvn test

# Integration tests
docker compose exec app mvn test -Dspring.profiles.active=test
```

## 🤝 Contributing


## 📄 License
>This project is licensed under the MIT License — xem chi tiết trong file LICENSE.

## 👨‍💼 Developed with ❤️ for Job Recruitment Platform
### Ports Used:

- API: 8082
- PostgreSQL: 5433
- Redis: 6380

