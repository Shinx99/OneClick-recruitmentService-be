-- Migration: job - seed_sample_data
-- Created: Wed Apr 01 2026
-- =====================================================================
-- 1. EMPLOYER
-- =====================================================================
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1',
    (SELECT company_id FROM company WHERE company_name = 'FPT Software' LIMIT 1),
    'fpt@example.com', 'Admin', 'Recruiter', 'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;


-- =====================================================================
-- 2. JOBS (tất cả company_id và created_by đều là FPT)
-- =====================================================================
INSERT INTO job (
    job_id, company_id, title, description, requirement, img_url, level, job_type,
    province, salary_min, salary_max, experience_min_year, status, created_by, created_at
) VALUES
(
    gen_random_uuid(),
    (SELECT company_id FROM company WHERE company_name = 'FPT Software' LIMIT 1),
    'Senior Java Backend Developer (Spring Boot) - Upto $2500',
    E'- Tham gia phát triển, thiết kế và triển khai các dự án phần mềm quy mô lớn cho khách hàng Nhật Bản, Mỹ và Châu Âu trong lĩnh vực Tài chính - Ngân hàng.\n- Phân tích yêu cầu nghiệp vụ, thiết kế kiến trúc hệ thống Microservices, xây dựng RESTful API phục vụ hàng triệu người dùng.\n- Phát triển backend sử dụng Java 11/17, Spring Boot, Spring Cloud, Hibernate/JPA, kết hợp với Kafka, Redis, Elasticsearch.\n- Tối ưu hóa hiệu năng hệ thống, xử lý các vấn đề về concurrency, caching, database indexing trên PostgreSQL/Oracle.\n- Thực hiện code review, mentor các thành viên Junior/Middle, đảm bảo chất lượng source code theo chuẩn Clean Code và SOLID.\n- Tham gia vào quy trình CI/CD, triển khai ứng dụng trên môi trường Docker, Kubernetes, AWS.\n- Làm việc trực tiếp với khách hàng nước ngoài (Onsite ngắn hạn/dài hạn) để nhận yêu cầu và trình bày giải pháp kỹ thuật.',
    E'- Tối thiểu 3 năm kinh nghiệm phát triển backend với Java và Spring Framework (Spring Boot, Spring Security, Spring Data JPA).\n- Thành thạo thiết kế RESTful API, hiểu rõ kiến trúc Microservices, Event-driven Architecture.\n- Có kinh nghiệm với các hệ quản trị CSDL: PostgreSQL, MySQL, Oracle; có khả năng viết và tối ưu câu lệnh SQL phức tạp.\n- Hiểu biết về message queue (Kafka, RabbitMQ), caching (Redis, Memcached).\n- Có kinh nghiệm triển khai ứng dụng với Docker, Kubernetes, Jenkins, Git.\n- Khả năng đọc hiểu tài liệu tiếng Anh kỹ thuật tốt; giao tiếp tiếng Anh hoặc tiếng Nhật là lợi thế lớn.\n- Có tư duy logic tốt, khả năng giải quyết vấn đề độc lập và làm việc nhóm hiệu quả.\n- Ưu tiên ứng viên có kinh nghiệm làm việc trong lĩnh vực Banking, Fintech, Insurance.',
    'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776047417/jobs/covers/fpt_software_logo_bwsxp9.jpg',
    'Senior', 'Full-time', 'Hồ Chí Minh', 1500.00, 2500.00, 3.0, 'active',
    'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '1 hours'
),
(
    gen_random_uuid(),
    (SELECT company_id FROM company WHERE company_name = 'FPT Software' LIMIT 1),
    'Frontend Engineer (ReactJS/NextJS) - ZaloPay Team',
    E'- Trực tiếp phát triển các tính năng mới cho sản phẩm ZaloPay - ví điện tử hàng đầu Việt Nam với hơn 10 triệu người dùng.\n- Xây dựng giao diện người dùng (UI) hiện đại, responsive trên cả Web và Mobile Web bằng ReactJS, NextJS, TypeScript.\n- Phối hợp chặt chẽ với đội ngũ UI/UX Designer để chuyển đổi thiết kế Figma thành sản phẩm thực tế, đảm bảo pixel-perfect.\n- Tối ưu hóa hiệu năng ứng dụng: lazy loading, code splitting, SSR/SSG, Core Web Vitals.\n- Tích hợp RESTful API, GraphQL với đội ngũ Backend; xử lý state management bằng Redux Toolkit, Zustand hoặc React Query.\n- Viết Unit Test (Jest, React Testing Library) và tham gia quy trình CI/CD để đảm bảo chất lượng release.\n- Nghiên cứu, đề xuất các công nghệ frontend mới để cải thiện trải nghiệm người dùng.',
    E'- Có từ 2 năm kinh nghiệm phát triển web với ReactJS, NextJS; ưu tiên ứng viên sử dụng thành thạo TypeScript.\n- Nắm vững HTML5, CSS3, JavaScript (ES6+), responsive design, cross-browser compatibility.\n- Có kinh nghiệm làm việc với các thư viện state management (Redux, Zustand, Recoil) và data fetching (React Query, SWR).\n- Hiểu rõ về hiệu năng web, SEO, accessibility và các nguyên tắc UX cơ bản.\n- Thành thạo Git, quy trình code review và làm việc theo Agile/Scrum.\n- Có kinh nghiệm viết Unit Test, Integration Test là một lợi thế.\n- Đam mê công nghệ, chủ động học hỏi và có tinh thần ownership cao đối với sản phẩm.',
    'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776047417/jobs/covers/fpt_software_logo_bwsxp9.jpg',
    'Middle', 'Full-time', 'Hồ Chí Minh', 1200.00, 2000.00, 2.0, 'active',
    'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '5 hours'
),
(
    gen_random_uuid(),
    (SELECT company_id FROM company WHERE company_name = 'FPT Software' LIMIT 1),
    'Backend Engineer (Golang/Microservices) - E-commerce',
    E'- Tham gia xây dựng hệ thống backend cho nền tảng thương mại điện tử Tiki phục vụ hàng chục triệu giao dịch mỗi tháng.\n- Thiết kế và triển khai các service theo kiến trúc Microservices sử dụng Golang, gRPC, Protocol Buffers.\n- Xây dựng các module nghiệp vụ cốt lõi: Order, Payment, Inventory, Promotion, Search với khả năng mở rộng cao (scalability).\n- Làm việc với các hệ thống message queue như Kafka, NATS để xử lý bất đồng bộ hàng triệu event/ngày.\n- Tối ưu truy vấn cơ sở dữ liệu MySQL, PostgreSQL, MongoDB; sử dụng Redis, Elasticsearch cho caching và search.\n- Theo dõi, giám sát và xử lý sự cố production với Grafana, Prometheus, ELK Stack.\n- Tham gia vào các buổi Tech Talk, chia sẻ kiến thức và đóng góp cho văn hóa kỹ thuật của team.',
    E'- Có kinh nghiệm với Golang từ 2 năm trở lên; nếu đến từ background Java/NodeJS nhưng yêu thích Go cũng được xem xét.\n- Hiểu sâu về kiến trúc Microservices, Distributed Systems, CAP theorem, Eventual Consistency.\n- Thành thạo thiết kế RESTful API, gRPC; có kinh nghiệm với API Gateway, Service Mesh (Istio, Linkerd) là lợi thế.\n- Có kiến thức vững về Data Structures, Algorithms và Database Design.\n- Kinh nghiệm làm việc với Docker, Kubernetes, CI/CD (GitLab CI, ArgoCD).\n- Có khả năng debug, profile và tối ưu hiệu năng ứng dụng backend.\n- Ưu tiên ứng viên đã từng xây dựng hệ thống high-traffic trong lĩnh vực E-commerce, Logistics, Fintech.',
    'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776047417/jobs/covers/fpt_software_logo_bwsxp9.jpg',
    'Middle', 'Full-time', 'Hà Nội', 1500.00, 2800.00, 2.0, 'active',
    'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '1 days'
),
(
    gen_random_uuid(),
    (SELECT company_id FROM company WHERE company_name = 'FPT Software' LIMIT 1),
    'Business Analyst (IT/Phần mềm) - Khối Tài Chính',
    E'- Trực tiếp làm việc với khách hàng trong lĩnh vực Tài chính - Ngân hàng - Bảo hiểm để thu thập, phân tích và làm rõ yêu cầu nghiệp vụ.\n- Xây dựng các tài liệu đặc tả yêu cầu: BRD, SRS, FRD, Use Case, User Story, Wireframe, Mockup.\n- Phân tích quy trình nghiệp vụ AS-IS, đề xuất quy trình TO-BE và giải pháp phần mềm phù hợp.\n- Làm cầu nối giữa khách hàng và đội ngũ Dev/QC, hỗ trợ team kỹ thuật hiểu rõ yêu cầu trong suốt vòng đời dự án.\n- Tham gia các buổi Demo, UAT (User Acceptance Test) với khách hàng; hỗ trợ đào tạo người dùng cuối.\n- Quản lý thay đổi yêu cầu (Change Request), đảm bảo tiến độ và chất lượng bàn giao.\n- Tham gia các dự án Digital Transformation cho các ngân hàng lớn tại Việt Nam và nước ngoài.',
    E'- Có 1.5 - 3 năm kinh nghiệm ở vị trí Business Analyst trong lĩnh vực phần mềm, ưu tiên mảng Banking/Finance.\n- Thành thạo các kỹ thuật phân tích: UML, BPMN, User Story Mapping, Prototyping.\n- Sử dụng tốt các công cụ: Jira, Confluence, Figma, Axure, Draw.io, Microsoft Visio.\n- Tiếng Anh giao tiếp tốt (tương đương TOEIC 700+), có thể làm việc trực tiếp với khách hàng nước ngoài.\n- Kỹ năng giao tiếp, thuyết trình, thương lượng và giải quyết vấn đề tốt.\n- Có kiến thức về SDLC, Agile/Scrum, Waterfall.\n- Ưu tiên ứng viên tốt nghiệp các trường top về CNTT, Hệ thống thông tin quản lý, Kinh tế - Tài chính.',
    'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776047417/jobs/covers/fpt_software_logo_bwsxp9.jpg',
    'Junior', 'Full-time', 'Đà Nẵng', 800.00, 1500.00, 1.5, 'active',
    'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '2 days'
),
(
    gen_random_uuid(),
    (SELECT company_id FROM company WHERE company_name = 'FPT Software' LIMIT 1),
    'Data Engineer (Big Data / Spark / Hadoop)',
    E'- Thiết kế, xây dựng và vận hành Data Pipeline quy mô lớn xử lý hàng TB dữ liệu mỗi ngày.\n- Phát triển các ETL/ELT job sử dụng Apache Spark, Apache Airflow, Kafka, Flink.\n- Xây dựng và bảo trì Data Warehouse, Data Lake trên nền tảng Hadoop, Hive, Presto, BigQuery.\n- Hợp tác với đội ngũ Data Scientist, Data Analyst để chuẩn bị dữ liệu phục vụ Machine Learning, Reporting, Analytics.\n- Đảm bảo chất lượng dữ liệu (Data Quality), giám sát pipeline, xử lý sự cố và tối ưu chi phí lưu trữ/tính toán.\n- Thiết kế Data Model (Star Schema, Snowflake Schema) cho các bài toán nghiệp vụ cụ thể.\n- Nghiên cứu và áp dụng các công nghệ Big Data mới: Delta Lake, Apache Iceberg, Databricks.',
    E'- Có 3+ năm kinh nghiệm ở vị trí Data Engineer hoặc tương đương.\n- Thành thạo Python hoặc Scala/Java trong lập trình xử lý dữ liệu.\n- Hiểu sâu về hệ sinh thái Hadoop: HDFS, YARN, Hive, HBase và đặc biệt là Apache Spark (Spark SQL, Spark Streaming).\n- Kinh nghiệm với các công cụ orchestration: Apache Airflow, Luigi, Dagster.\n- Thành thạo SQL, tối ưu query trên các hệ thống OLAP (Presto, ClickHouse, BigQuery, Snowflake).\n- Có kinh nghiệm với streaming data: Kafka, Kinesis, Flink.\n- Hiểu biết về Cloud Platform: AWS (EMR, Glue, Redshift), GCP (Dataflow, Dataproc) là lợi thế.\n- Tư duy tốt về Data Modeling, Data Governance và Data Security.',
    'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776047417/jobs/covers/fpt_software_logo_bwsxp9.jpg',
    'Senior', 'Full-time', 'Hồ Chí Minh', 2000.00, 3500.00, 3.0, 'active',
    'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '3 days'
),
(
    gen_random_uuid(),
    (SELECT company_id FROM company WHERE company_name = 'FPT Software' LIMIT 1),
    'DevOps Engineer (AWS/Kubernetes) - Lương hấp dẫn',
    E'- Xây dựng và vận hành hạ tầng Cloud trên AWS phục vụ hệ thống với hàng trăm microservices.\n- Thiết kế, triển khai và quản lý Kubernetes cluster (EKS) ở quy mô production với hàng nghìn pod.\n- Phát triển và duy trì hệ thống CI/CD tự động hóa cho hàng trăm service (Jenkins, GitLab CI, ArgoCD, Spinnaker).\n- Xây dựng hệ thống giám sát (Monitoring), cảnh báo (Alerting) và logging tập trung (Prometheus, Grafana, Loki, ELK).\n- Áp dụng Infrastructure as Code với Terraform, Ansible, Helm Chart để quản lý hạ tầng.\n- Đảm bảo tính sẵn sàng cao (HA), khả năng mở rộng (Scalability) và bảo mật (Security) cho toàn bộ hệ thống.\n- Hỗ trợ developer khắc phục sự cố, tối ưu hiệu năng và chi phí vận hành Cloud.\n- Tham gia On-call rotation để xử lý sự cố production.',
    E'- Có 4+ năm kinh nghiệm DevOps/SRE, thành thạo hệ điều hành Linux (Ubuntu, CentOS).\n- Kinh nghiệm sâu với Docker, Kubernetes (ưu tiên có chứng chỉ CKA/CKAD).\n- Thành thạo AWS (EC2, VPC, EKS, RDS, S3, CloudWatch, IAM, Lambda); có thể thiết kế hạ tầng multi-region.\n- Có kinh nghiệm với Infrastructure as Code: Terraform, CloudFormation, Pulumi.\n- Thành thạo scripting: Bash, Python, Go.\n- Hiểu rõ về Networking, Security (WAF, IDS/IPS, SSL/TLS, Zero Trust).\n- Kinh nghiệm xây dựng hệ thống observability: Prometheus, Grafana, Jaeger, OpenTelemetry.\n- Tư duy automation, có trách nhiệm cao và khả năng làm việc dưới áp lực sự cố.',
    'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776047417/jobs/covers/fpt_software_logo_bwsxp9.jpg',
    'Senior', 'Full-time', 'Hồ Chí Minh', 1800.00, 3200.00, 4.0, 'active',
    'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '4 days'
),
(
    gen_random_uuid(),
    (SELECT company_id FROM company WHERE company_name = 'FPT Software' LIMIT 1),
    'Thực Tập Sinh / Fresher Java (Có Phụ Cấp)',
    E'- Tham gia chương trình đào tạo chuyên sâu FPT Software Academy kéo dài 2-3 tháng về Java, Spring Boot, SQL, Git và Agile/Scrum.\n- Được mentor 1-1 bởi các Senior Developer có 5+ năm kinh nghiệm trong ngành.\n- Tham gia vào các dự án thực tế (real project) của khách hàng Nhật Bản, Mỹ, Châu Âu ngay sau khi tốt nghiệp khóa đào tạo.\n- Thực hiện các task lập trình: viết code, fix bug, viết Unit Test dưới sự hướng dẫn của Team Leader.\n- Tham gia các buổi Daily Meeting, Sprint Planning, Retrospective theo quy trình Scrum.\n- Học hỏi và rèn luyện kỹ năng làm việc nhóm, giao tiếp và quản lý thời gian trong môi trường doanh nghiệp.\n- Có cơ hội trở thành nhân viên chính thức sau 3-6 tháng với mức lương hấp dẫn (10-15 triệu/tháng).',
    E'- Sinh viên năm cuối hoặc mới tốt nghiệp chuyên ngành Công nghệ thông tin, Khoa học máy tính, Kỹ thuật phần mềm.\n- Có kiến thức cơ bản về lập trình hướng đối tượng (OOP) với Java.\n- Hiểu biết cơ bản về SQL và cơ sở dữ liệu quan hệ (MySQL, SQL Server).\n- Đã từng làm đồ án/project với Spring Boot là một lợi thế lớn.\n- Có khả năng đọc hiểu tài liệu tiếng Anh kỹ thuật; giao tiếp tiếng Anh cơ bản.\n- Tinh thần ham học hỏi, chịu khó, trung thực và có trách nhiệm.\n- Có thể làm việc full-time tại văn phòng Hà Nội tối thiểu 3 tháng.',
    'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776047417/jobs/covers/fpt_software_logo_bwsxp9.jpg',
    'Fresher', 'Internship', 'Hà Nội', 200.00, 400.00, 0.0, 'active',
    'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '5 days'
),
(
    gen_random_uuid(),
    (SELECT company_id FROM company WHERE company_name = 'FPT Software' LIMIT 1),
    'Mobile Developer (Flutter / iOS / Android)',
    E'- Phát triển các ứng dụng mobile cho các dự án outsourcing của FPT Software cho khách hàng nước ngoài.\n- Xây dựng tính năng mới bằng Flutter (Dart) cho cả iOS và Android, đảm bảo performance và trải nghiệm mượt mà.\n- Tích hợp các SDK thanh toán, bản đồ, push notification (Firebase, OneSignal), analytics (Firebase Analytics, Amplitude).\n- Làm việc với RESTful API, GraphQL, WebSocket để tương tác với backend.\n- Tối ưu hiệu năng ứng dụng: giảm kích thước APK/IPA, tối ưu khởi động app, xử lý memory leak.\n- Viết Unit Test, Widget Test, Integration Test cho các module quan trọng.\n- Phối hợp với QC để phát hiện và sửa lỗi trước khi release lên CH Play / App Store.\n- Theo dõi crash report (Crashlytics) và xử lý các lỗi phát sinh trong production.',
    E'- Ít nhất 1.5 năm kinh nghiệm phát triển ứng dụng mobile với Flutter; hoặc có kinh nghiệm native iOS (Swift) / Android (Kotlin) và sẵn sàng chuyển sang Flutter.\n- Nắm vững Dart, State Management (Bloc, Riverpod, Provider, GetX).\n- Hiểu về kiến trúc Clean Architecture, MVVM, MVC trong mobile development.\n- Có kinh nghiệm publish app lên CH Play và App Store; hiểu quy trình review của Apple.\n- Thành thạo Git, làm việc với Gitflow và quy trình Code Review.\n- Có kiến thức về CI/CD cho mobile (Fastlane, Codemagic, Bitrise) là lợi thế.\n- Yêu thích các sản phẩm mobile, có tư duy UX/UI tốt.\n- Ưu tiên ứng viên có portfolio các app đã publish trên store.',
    'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776047417/jobs/covers/fpt_software_logo_bwsxp9.jpg',
    'Junior', 'Full-time', 'Hồ Chí Minh', 1000.00, 1800.00, 1.5, 'active',
    'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '6 days'
),
(
    gen_random_uuid(),
    (SELECT company_id FROM company WHERE company_name = 'FPT Software' LIMIT 1),
    'QA/QC Engineer (Automation Test)',
    E'- Lên kế hoạch kiểm thử (Test Plan), thiết kế và viết Test Case/Test Scenario cho các tính năng mới của hệ thống.\n- Xây dựng và phát triển framework Automation Test cho Web (Selenium, Cypress, Playwright) và Mobile (Appium).\n- Viết và duy trì các bộ Test Script tự động cho Regression Test, Smoke Test, E2E Test.\n- Thực hiện API Testing với Postman, RestAssured, Karate Framework.\n- Tích hợp Automation Test vào pipeline CI/CD (Jenkins, GitLab CI) để chạy tự động sau mỗi lần build.\n- Phân tích lỗi, báo cáo bug trên Jira và phối hợp với Developer để xử lý triệt để.\n- Thực hiện Performance Test với JMeter, K6 cho các service quan trọng.\n- Đề xuất cải tiến quy trình QA, nâng cao chất lượng sản phẩm.',
    E'- Có 2+ năm kinh nghiệm Automation Test với các công cụ như Selenium WebDriver, Cypress, Playwright hoặc Appium.\n- Thành thạo ít nhất 1 ngôn ngữ lập trình: Java, JavaScript/TypeScript, Python.\n- Có kinh nghiệm thiết kế Test Framework theo mô hình Page Object Model (POM), Data-Driven, Keyword-Driven.\n- Hiểu rõ quy trình phát triển phần mềm (SDLC), Agile/Scrum và các loại test (Functional, Non-functional, Regression, E2E).\n- Có kinh nghiệm API Testing với Postman, Swagger, RestAssured.\n- Kiến thức cơ bản về SQL để kiểm tra dữ liệu trên database.\n- Quen với các công cụ CI/CD (Jenkins, GitLab CI) và quản lý source code (Git).\n- Ưu tiên ứng viên có kinh nghiệm Performance Test (JMeter, K6, Gatling) hoặc Security Test.',
    'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776047417/jobs/covers/fpt_software_logo_bwsxp9.jpg',
    'Middle', 'Full-time', 'Hà Nội', 900.00, 1600.00, 2.0, 'active',
    'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '7 days'
),
(
    gen_random_uuid(),
    (SELECT company_id FROM company WHERE company_name = 'FPT Software' LIMIT 1),
    'Chuyên Viên Quản Trị Hệ Thống (System Admin)',
    E'- Quản trị và vận hành hệ thống mạng, máy chủ (Windows Server, Linux) của FPT Software tại Đà Nẵng với hơn 5000 nhân viên.\n- Cài đặt, cấu hình và quản lý Active Directory, DNS, DHCP, File Server, Print Server, Mail Server (Exchange, Office 365).\n- Giám sát hiệu năng hệ thống 24/7, phát hiện và xử lý sự cố kịp thời để đảm bảo uptime 99.9%.\n- Triển khai và quản lý hệ thống ảo hóa VMware vSphere, Hyper-V.\n- Quản lý hệ thống backup, đảm bảo an toàn và khôi phục dữ liệu theo chính sách Disaster Recovery.\n- Thực hiện các chính sách bảo mật, vá lỗi hệ thống định kỳ, quản lý antivirus doanh nghiệp.\n- Hỗ trợ kỹ thuật cho người dùng nội bộ (Level 2/3): xử lý các vấn đề về mạng, máy tính, phần mềm.\n- Tài liệu hóa quy trình vận hành, báo cáo tình trạng hệ thống cho quản lý.',
    E'- Tốt nghiệp chuyên ngành Mạng máy tính, An toàn thông tin, Công nghệ thông tin.\n- Có ít nhất 1 năm kinh nghiệm quản trị hệ thống Windows Server và Linux.\n- Nắm vững kiến thức về TCP/IP, VLAN, Routing, Switching, Firewall.\n- Có kinh nghiệm quản trị Active Directory, Group Policy, Exchange/Office 365.\n- Hiểu biết về ảo hóa (VMware, Hyper-V) và điện toán đám mây (Azure, AWS) là lợi thế.\n- Có kỹ năng scripting cơ bản: PowerShell, Bash để tự động hóa công việc.\n- Ưu tiên ứng viên có chứng chỉ MCSA, MCSE, CCNA, VCP, RHCSA.\n- Tinh thần trách nhiệm cao, có thể trực ca đêm/cuối tuần khi có sự cố.',
    'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776047417/jobs/covers/fpt_software_logo_bwsxp9.jpg',
    'Junior', 'Full-time', 'Đà Nẵng', 500.00, 900.00, 1.0, 'active',
    'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '8 days'
);


-- =====================================================================
-- 3. SKILLS (không thay đổi)
-- =====================================================================
INSERT INTO skills (skills_id, skills_name) VALUES
    (gen_random_uuid(), 'java'),
    (gen_random_uuid(), 'java spring boot'),
    (gen_random_uuid(), 'microservices'),
    (gen_random_uuid(), 'restful api'),
    (gen_random_uuid(), 'postgresql'),
    (gen_random_uuid(), 'mysql'),
    (gen_random_uuid(), 'mongodb'),
    (gen_random_uuid(), 'oracle'),
    (gen_random_uuid(), 'redis'),
    (gen_random_uuid(), 'elasticsearch'),
    (gen_random_uuid(), 'apache kafka'),
    (gen_random_uuid(), 'docker'),
    (gen_random_uuid(), 'kubernetes'),
    (gen_random_uuid(), 'aws'),
    (gen_random_uuid(), 'gcp'),
    (gen_random_uuid(), 'azure'),
    (gen_random_uuid(), 'jenkins'),
    (gen_random_uuid(), 'gitlab ci'),
    (gen_random_uuid(), 'terraform'),
    (gen_random_uuid(), 'ansible'),
    (gen_random_uuid(), 'linux'),
    (gen_random_uuid(), 'windows server'),
    (gen_random_uuid(), 'vmware'),
    (gen_random_uuid(), 'active directory'),
    (gen_random_uuid(), 'networking (tcp/ip)'),
    (gen_random_uuid(), 'powershell'),
    (gen_random_uuid(), 'bash'),
    (gen_random_uuid(), 'prometheus'),
    (gen_random_uuid(), 'grafana'),
    (gen_random_uuid(), 'elk stack'),
    (gen_random_uuid(), 'react.js'),
    (gen_random_uuid(), 'next.js'),
    (gen_random_uuid(), 'typescript'),
    (gen_random_uuid(), 'javascript'),
    (gen_random_uuid(), 'html5/css3'),
    (gen_random_uuid(), 'redux'),
    (gen_random_uuid(), 'golang'),
    (gen_random_uuid(), 'grpc'),
    (gen_random_uuid(), 'python'),
    (gen_random_uuid(), 'scala'),
    (gen_random_uuid(), 'apache spark'),
    (gen_random_uuid(), 'hadoop'),
    (gen_random_uuid(), 'hive'),
    (gen_random_uuid(), 'apache airflow'),
    (gen_random_uuid(), 'sql'),
    (gen_random_uuid(), 'data warehouse'),
    (gen_random_uuid(), 'flutter'),
    (gen_random_uuid(), 'dart'),
    (gen_random_uuid(), 'ios (swift)'),
    (gen_random_uuid(), 'android (kotlin)'),
    (gen_random_uuid(), 'firebase'),
    (gen_random_uuid(), 'selenium'),
    (gen_random_uuid(), 'cypress'),
    (gen_random_uuid(), 'playwright'),
    (gen_random_uuid(), 'appium'),
    (gen_random_uuid(), 'postman'),
    (gen_random_uuid(), 'jmeter'),
    (gen_random_uuid(), 'automation testing'),
    (gen_random_uuid(), 'git'),
    (gen_random_uuid(), 'agile/scrum'),
    (gen_random_uuid(), 'business analysis'),
    (gen_random_uuid(), 'uml'),
    (gen_random_uuid(), 'bpmn'),
    (gen_random_uuid(), 'jira & confluence'),
    (gen_random_uuid(), 'figma'),
    (gen_random_uuid(), 'english communication')
ON CONFLICT (skills_name) DO NOTHING;


-- =====================================================================
-- 4. LINK JOBS TO SKILLS (không thay đổi)
-- =====================================================================
INSERT INTO job_skills (job_id, skills_id)
SELECT j.job_id, s.skills_id
FROM job j
CROSS JOIN skills s
WHERE
    (j.title ILIKE '%Senior Java Backend%' AND s.skills_name IN (
        'java', 'java spring boot', 'microservices', 'restful api',
        'postgresql', 'oracle', 'redis', 'apache kafka',
        'docker', 'kubernetes', 'aws', 'jenkins', 'git', 'agile/scrum'
    ))
    OR (j.title ILIKE '%Frontend Engineer%' AND s.skills_name IN (
        'react.js', 'next.js', 'typescript', 'javascript',
        'html5/css3', 'redux', 'restful api', 'git', 'agile/scrum'
    ))
    OR (j.title ILIKE '%Backend Engineer (Golang%' AND s.skills_name IN (
        'golang', 'microservices', 'grpc', 'restful api',
        'mysql', 'postgresql', 'mongodb', 'redis', 'elasticsearch',
        'apache kafka', 'docker', 'kubernetes', 'gitlab ci', 'git'
    ))
    OR (j.title ILIKE '%Business Analyst%' AND s.skills_name IN (
        'business analysis', 'uml', 'bpmn', 'jira & confluence',
        'figma', 'sql', 'agile/scrum', 'english communication'
    ))
    OR (j.title ILIKE '%Data Engineer%' AND s.skills_name IN (
        'python', 'scala', 'apache spark', 'hadoop', 'hive',
        'apache airflow', 'apache kafka', 'sql', 'data warehouse',
        'aws', 'gcp'
    ))
    OR (j.title ILIKE '%DevOps Engineer%' AND s.skills_name IN (
        'aws', 'kubernetes', 'docker', 'terraform', 'ansible',
        'jenkins', 'gitlab ci', 'linux', 'bash', 'python',
        'prometheus', 'grafana', 'elk stack'
    ))
    OR (j.title ILIKE '%Fresher Java%' AND s.skills_name IN (
        'java', 'java spring boot', 'sql', 'mysql',
        'git', 'agile/scrum', 'english communication'
    ))
    OR (j.title ILIKE '%Mobile Developer%' AND s.skills_name IN (
        'flutter', 'dart', 'ios (swift)', 'android (kotlin)',
        'firebase', 'restful api', 'git', 'agile/scrum'
    ))
    OR (j.title ILIKE '%QA/QC Engineer%' AND s.skills_name IN (
        'automation testing', 'selenium', 'cypress', 'playwright',
        'appium', 'postman', 'jmeter', 'java', 'python',
        'javascript', 'sql', 'jenkins', 'git', 'agile/scrum'
    ))
    OR (j.title ILIKE '%Quản Trị Hệ Thống%' AND s.skills_name IN (
        'linux', 'windows server', 'active directory',
        'networking (tcp/ip)', 'vmware', 'powershell', 'bash',
        'azure', 'aws'
    ))
ON CONFLICT DO NOTHING;