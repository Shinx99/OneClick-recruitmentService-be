```bash
recruitmentService-be/
│
├── pom.xml                                          # Maven dependencies (Spring Boot 3.5.9, PostgreSQL, Redis, Flyway)
├── docker-compose.yml                               # PostgreSQL 16 + Redis 7 + MinIO (S3)
├── Dockerfile
├── .env.example                                     # DB_URL, JWT_SECRET, MAIL_PASSWORD, S3_ENDPOINT
├── README.md
├── .gitignore
│
├── docs/                                            # 📚 Documentation
│   ├── API-DOCUMENTATION.md                         # Swagger + Postman collection
│   ├── DATABASE-SCHEMA.md                           # ERD (Employer-Company-Job-Candidate-Resume)
│   ├── ARCHITECTURE.md                              # Modular feature + shared persistence
│   ├── DEPLOYMENT.md                                # Docker/K8s + CI/CD GitHub Actions
│   ├── SECURITY.md                                  # JWT roles, input validation, file upload security
│   └── TESTING.md                                   # Unit/Integration/E2E strategy
│
├── scripts/
│   ├── migrate-db.sh                                # Flyway migrate
│   ├── start-dev.sh                                 # Docker compose + hot reload
│   ├── seed-data.sh                                 # Insert test employers/companies/jobs
│   └── run-tests.sh                                 # mvn test + coverage
│
└── src/
    ├── main/
    │   ├── java/com/onceClick/recruitmentService/
    │   │   │
    │   │   ├── RecruitmentServiceApplication.java  # 🚀 Main app + @EnableJpaRepositories + @ComponentScan
    │   │   │
    │   │   │
    │   │   ├── shared/                              # ════════ SHARED INFRA ════════
    │   │   │   │
    │   │   │   ├── config/                          # 🔧 Global configs
    │   │   │   │   ├── SecurityConfig.java          # JWT + OAuth2 resource server + role-based access
    │   │   │   │   ├── RedisConfig.java             # CacheManager + RedisTemplate
    │   │   │   │   ├── CorsConfig.java              # Allowed origins từ yml
    │   │   │   │   ├── OpenApiConfig.java           # Swagger groups by feature
    │   │   │   │   ├── AsyncConfig.java             # ThreadPool cho email/file processing
    │   │   │   │   ├── S3Config.java                # MinIO/S3 client cho resume storage
    │   │   │   │   └── JpaConfig.java               # AuditEntity, querydsl (optional)
    │   │   │   │
    │   │   │   ├── security/                        # 🔐 JWT + Auth infrastructure
    │   │   │   │   ├── JwtAuthenticationFilter.java # Extract JWT → @CurrentUser(Employer/Candidate)
    │   │   │   │   ├── CurrentUser.java             # @CurrentUser annotation
    │   │   │   │   ├── RolePermission.java          # @RequireRole("EMPLOYER"), @RequireVerified
    │   │   │   │   └── AuthClientService.java       # Feign/WebClient call auth-service /api/profile
    │   │   │   │
    │   │   │   ├── notification/                    # 📧 Email templates (Thymeleaf)
    │   │   │   │   ├── EmailService.java            # Interface
    │   │   │   │   ├── EmailServiceImpl.java        # JavaMailSender + Thymeleaf
    │   │   │   │   ├── EmailTemplateService.java    # Interface (6 methods)
    │   │   │   │   └── EmailTemplateServiceImpl.java # ThymeleafContext builder
    │   │   │   │
    │   │   │   ├── exception/                       # ⚠️ Error handling
    │   │   │   │   ├── GlobalExceptionHandler.java  # @ControllerAdvice → ApiResponse<Error>
    │   │   │   │   ├── BusinessException.java       # Base + ErrorCode enum
    │   │   │   │   ├── ResourceNotFoundException.java
    │   │   │   │   ├── UnverifiedCompanyException.java
    │   │   │   │   └── FileUploadException.java
    │   │   │   │
    │   │   │   ├── dto/                             # 📦 Response wrappers
    │   │   │   │   ├── ApiResponse.java             # data, message, timestamp
    │   │   │   │   ├── PageResponse.java            # content, totalPages, currentPage
    │   │   │   │   └── FileUploadResponse.java      # fileUrl, fileSize, checksum
    │   │   │   │
    │   │   │   ├── persistence/                     # 🗄️ ALL ENTITIES + REPOS (shared)
    │   │   │   │   ├── entity/
    │   │   │   │   │   ├── BaseEntity.java          # id(Long), createdAt/updatedAt
    │   │   │   │   │   ├── Employer.java            # email, phone, verificationLevel(PENDING/VERIFIED)
    │   │   │   │   │   ├── Company.java             # name, industry, size, logoUrl, @OneToOne Employer
    │   │   │   │   │   ├── Job.java                 # title, salaryRange, skills(JSONB), @ManyToOne Company
    │   │   │   │   │   ├── Candidate.java           # email, skills(JSONB)
    │   │   │   │   │   ├── Resume.java              # fileUrl(S3), parsedSkills, @ManyToOne Candidate
    │   │   │   │   │   ├── CandidateStatisticDaily.java # date, views, applies
    │   │   │   │   │   ├── JobStatisticDaily.java
    │   │   │   │   │   └── ResumeStatisticDaily.java
    │   │   │   │   │
    │   │   │   │   └── repository/
    │   │   │   │       ├── EmployerRepository.java  # findByEmail, existsByPhone
    │   │   │   │       ├── CompanyRepository.java   # findVerifiedByEmployerId
    │   │   │   │       ├── JobRepository.java       # findActiveByCompanyId, searchNative(title,skills)
    │   │   │   │       ├── CandidateRepository.java
    │   │   │   │       ├── ResumeRepository.java    # findByCandidateIdOrderByCreatedAtDesc
    │   │   │   │       └── StatisticRepository.java # groupByDate native queries
    │   │   │   │
    │   │   │   └── util/                            # 🛠️ Helpers
    │   │   │       ├── DateTimeUtil.java            # VN timezone, expiry calc
    │   │   │       ├── FileUtil.java                # Virus scan, PDF extract
    │   │   │       └── ValidationUtil.java          # Salary range, skills format
    │   │   │
    │   │   │
    │   │   └── features/                            # ════════ FEATURES (9 total) ════════
    │   │       │
    │   │       ├── employer/                        # 🎯 FEATURE 1: Employer Management
    │   │       │   │                                # Dev: Dev1 | Tables: employer, company
    │   │       │   │                                # Endpoints: POST /api/employers/register
    │   │       │   │
    │   │       │   ├── RegisterRequest.java         /**
    │   │       │   │    * email @Email, phone VN-format, password @Size(8)
    │   │       │   │    */
    │   │       │   ├── RegisterResponse.java        /**
    │   │       │   │    * employerId, verificationUrl, message
    │   │       │   │    */
    │   │       │   ├── EmployerHandler.java         /**
    │   │       │   │    * @Service Logic:
    │   │       │   │    * 1. Check email/phone unique (call auth?)
    │   │       │   │    * 2. Create Employer(status=PENDING)
    │   │       │   │    * 3. Send verification email (w/ docs upload link)
    │   │       │   │    * 4. EmployerCreatedEvent (async)
    │   │       │   │    */
    │   │       │   ├── EmployerController.java      /**
    │   │       │   │    * POST /api/employers/register @PublicEndpoint
    │   │       │   │    */
    │   │       │   ├── EmployerValidator.java       /**
    │   │       │   │    * @Validator: phone unique check repo
    │   │       │   │    */
    │   │       │   │
    │   │       │   ├── VerifyRequest.java           /**
    │   │       │   │    * businessLicenseUrl(S3), taxCode
    │   │       │   │    */
    │   │       │   ├── VerifyHandler.java           /**
    │   │       │   │    * @Service: Validate docs, update verificationLevel=VERIFIED
    │   │       │   │    * Publish CompanyVerifiedEvent
    │   │       │   │    */
    │   │       │   └── VerifyController.java        /**
    │   │       │   │    * POST /api/employers/{id}/verify @RequireRole("EMPLOYER")
    │   │       │   │    */
    │   │       │
    │   │       │
    │   │       ├── company/                         # 🎯 FEATURE 2: Company Profile
    │   │       │   │                                # Dev: Dev1 | Tables: company
    │   │       │   │                                # Endpoints: POST /api/companies
    │   │       │   │
    │   │       │   ├── CompanyRequest.java          /**
    │   │       │   │    * name, industry(enum), size(1-50), address
    │   │       │   │    */
    │   │       │   ├── CompanyResponse.java         /**
    │   │       │   │    * companyId, logoUrl, jobCount
    │   │       │   │    */
    │   │       │   ├── CompanyHandler.java          /**
    │   │       │   │    * @Service: Link employer→company, upload logo(S3)
    │   │       │   │    * CompanyCreatedEvent → statistic init
    │   │       │   │    */
    │   │       │   ├── CompanyController.java       /**
    │   │       │   │    * POST/PUT/GET /api/companies @RequireVerifiedEmployer
    │   │       │   │    */
    │   │       │   └── CompanyCreatedEvent.java     /**
    │   │       │   │    * Kafka event: init daily stats
    │   │       │   │    */
    │   │       │
    │   │       │
    │   │       ├── job/                             # 🎯 FEATURE 3: Job CRUD + Search
    │   │       │   │                                # Dev: Dev2 | Tables: job
    │   │       │   │                                # Endpoints: POST /api/jobs, GET /api/jobs/search
    │   │       │   │
    │   │       │   ├── JobRequest.java              /**
    │   │       │   │    * title, description, salaryMin/Max, skills(List)
    │   │       │   │    */
    │   │       │   ├── JobSearchCriteria.java       /**
    │   │       │   │    * keyword, location, salaryMin, skills
    │   │       │   │    */
    │   │       │   ├── JobResponse.java             /**
    │   │       │   │    * + company info, applyCount, viewCount
    │   │       │   │    */
    │   │       │   ├── JobHandler.java              /**
    │   │       │   │    * @Service: CRUD, validate company verified
    │   │       │   │    * Native search PostgreSQL full-text + JSONB skills
    │   │       │   │    * JobPublishedEvent (increment statistic)
    │   │       │   │    */
    │   │       │   ├── JobController.java           /**
    │   │       │   │    * POST /jobs, GET /jobs/my-jobs, GET /search @Public
    │   │       │   │    */
    │   │       │   └── JobSpecification.java        /**
    │   │       │   │    * CriteriaBuilder: dynamic filter active/verified
    │   │       │   │    */
    │   │       │
    │   │       │
    │   │       ├── candidate/                       # 🎯 FEATURE 4: Candidate Register
    │   │       │   │                                # Dev: Dev2 | Tables: candidate
    │   │       │   │                                # Endpoints: POST /api/candidates/register
    │   │       │   │
    │   │       │   ├── CandidateRequest.java        /**
    │   │       │   │    * email, skills(List), location
    │   │       │   │    */
    │   │       │   ├── CandidateHandler.java        /**
    │   │       │   │    * @Service: Create candidate, send welcome email
    │   │       │   │    */
    │   │       │   └── CandidateController.java     /**
    │   │       │   │    * POST /candidates/register @PublicEndpoint
    │   │       │   │    */
    │   │       │
    │   │       │
    │   │       ├── resume/                          # 🎯 FEATURE 5: Resume Upload/Parse
    │   │       │   │                                # Dev: Dev3 | Tables: resume
    │   │       │   │                                # Endpoints: POST /api/resumes/upload
    │   │       │   │
    │   │       │   ├── ResumeUploadRequest.java     /**
    │   │       │   │    * MultipartFile resumeFile (PDF<=10MB)
    │   │       │   │    */
    │   │       │   ├── ResumeHandler.java           /**
    │   │       │   │    * @Service: Virus scan, upload S3, extract text(PDFBox)
    │   │       │   │    * Parse skills (NLP/keyword), save parsedSkills JSONB
    │   │       │   │    * ResumeUploadedEvent
    │   │       │   │    */
    │   │       │   ├── ResumeStorageService.java    /**
    │   │       │   │    * S3 putObject, generate presigned URL
    │   │       │   │    */
    │   │       │   └── ResumeController.java        /**
    │   │       │   │    * POST /resumes/upload @RequireRole("CANDIDATE")
    │   │       │   │    */
    │   │       │
    │   │       │
    │   │       └── statistic/                       # 🎯 FEATURE 6-8: Daily Statistics
    │   │           │                                # Dev: Dev3 | Tables: *StatisticDaily
    │   │           │                                # Endpoints: GET /api/stats/jobs/daily
    │   │           │
    │   │           ├── JobStatisticHandler.java      /**
    │   │           │    * @Service: Native query GROUP BY date, SUM(views/applies)
    │   │           │    * Cron job daily aggregate @Scheduled
    │   │           │    */
    │   │           ├── JobStatisticController.java   /**
    │   │           │    * GET /stats/jobs/daily?from&to @Public
    │   │           │    */
    │   │           ├── CandidateStatisticHandler.java
    │   │           ├── ResumeStatisticHandler.java
    │   │           └── StatisticResponse.java       /**
    │   │                   │    * date, views, applies, avgSalary
    │   │                   │    */
    │   │
    │   │
    │   └── resources/
    │       ├── application.yml                      # Server, datasource, redis, mail, jwt
    │       ├── application-dev.yml                  # show-sql=true, cache=false
    │       ├── db/migration/                        # Flyway
    │       │   ├── V1__init_schema.sql              # CREATE TABLE employer, company...
    │       │   └── V2__add_indexes.sql              # idx_company_verified, idx_job_skills_gin
    │       └── templates/email/                     # Thymeleaf
    │           ├── employer-verification.html
    │           ├── company-verified.html
    │           └── job-applied.html
    │
    │
    └── test/
        └── java/com/onceClick/recruitmentService/
            │
            ├── shared/
            │   └── config/TestContainersConfig.java   # @Testcontainers PostgreSQL/Redis
            │
            └── features/
                ├── employer/
                │   ├── EmployerHandlerTest.java      # Mock repo, assert event
                │   └── EmployerControllerTest.java   # MockMvc + @Sql
                │
                ├── job/
                │   ├── JobHandlerTest.java
                │   └── JobSearchIntegrationTest.java
                │
                └── resume/
                    └── ResumeUploadE2ETest.java     # Testcontainers + MinIO
```

# 📊 Phân chia công việc cho Team (9 Features)

| Developer | Features | Số lượng | Trách nhiệm |
|-----------|----------|----------|-------------|
| **Dev 1** | `employer`, `company` | **2** | Employer/Company CRUD + Verify |
| **Dev 2** | `job`, `candidate` | **2** | Job posting + Candidate basic |
| **Dev 3** | `resume`, `statistic` (3 endpoints) | **4** | File upload + Analytics |

**Total: 8 features, 9 endpoints chính**
# 🗄️ Database Tables (8 bảng chính)
```bash

employer - Nhà tuyển dụng (email, phone, verificationLevel)

company - Công ty (name, industry, employer FK, verified)

job - Việc làm (title, salary, skills JSONB, company FK)

candidate - Ứng viên (email, skills JSONB)

resume - CV (fileUrl S3, parsedSkills JSONB, candidate FK)

candidate_statistic_daily - Thống kê hàng ngày

job_statistic_daily

resume_statistic_daily
````
# 🏗️ Architecture Pattern
## Vertical Slice + Shared Persistence


### Mỗi feature self-contained:
```text
✓ Controller - HTTP layer + @Validated

✓ Handler - Business logic + transaction

✓ DTO - Request/Response specific

✓ Events - Async Kafka/Spring Events

✓ Util - File/S3/Date helpers shared

![ERD Recruitment](erd-recruitment.png)
```