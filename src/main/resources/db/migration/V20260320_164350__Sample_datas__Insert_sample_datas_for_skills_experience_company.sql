-- Migration: Sample_datas - Insert_sample_datas_for_skills_experience_company
-- Created: Fri Mar 20 04:43:50 PM +07 2026
-- Author: hoangvuongbui

-- Add your SQL statements below:
-- ========================================================
-- 1. COMPANY (10 records - Professional Vietnamese IT companies)
-- ========================================================
INSERT INTO company (company_id, company_name, tax_code, size_range, overview, industry, province_code, address, logo_url, website_url) VALUES
(gen_random_uuid(), 'FPT Software', '0100109106', '30000+', 'Vietnam''s largest IT exporter, global digital transformation leader', 'Công Nghệ Thông Tin', '700000', 'Phu My Hung, District 7, HCMC', 'fpt-logo.png', 'fpt-software.com'),
(gen_random_uuid(), 'VNG Corporation', '0302266895', '8000+', 'Zalo messaging, gaming & cloud platform pioneer', 'Công Nghệ Thông Tin', '700000', '9 Dinh Tien Hoang, District 1, HCMC', 'vng-logo.png', 'vng.com.vn'),
(gen_random_uuid(), 'Tiki Corporation', '0103692061', '3000+', 'Vietnam''s leading e-commerce & digital marketplace', 'Sale Bán Hàng', '700000', 'Lach Tray, District 3, HCMC', 'tiki-logo.png', 'tiki.vn'),
(gen_random_uuid(), 'VinAI Research', '0108441481', '200+', 'VinGroup AI research lab, computer vision leader', 'Công Nghệ Thông Tin', '100000', 'Vincom Mega Mall Royal City, Hanoi', 'vinai-logo.png', 'vinai.io'),
(gen_random_uuid(), 'CMC Global', '0101777852', '5000+', 'Digital transformation & software outsourcing', 'Công Nghệ Thông Tin', '100000', 'Duy Tan, Cau Giay, Hanoi', 'cmc-logo.png', 'cmcglobal.com.vn'),
(gen_random_uuid(), 'Saigon Technology', '0311978999', '400+', 'Custom software for US/EU enterprises', 'Công Nghệ Thông Tin', '700000', 'District 1, HCMC', 'saigon-tech-logo.png', 'saigontechnology.com'),
(gen_random_uuid(), 'KMS Technology', '0304837555', '1500+', 'Google Cloud partner, QA & automation leader', 'Công Nghệ Thông Tin', '700000', 'Tan Thuan Export Processing Zone, HCMC', 'kms-logo.png', 'kms-technology.com'),
(gen_random_uuid(), 'TMA Solutions', '0302892381', '5000+', 'Largest software outsourcing company in VN', 'Công Nghệ Thông Tin', '700000', 'Tan Thuan EPZ, District 7, HCMC', 'tma-logo.png', 'tmasolutions.com'),
(gen_random_uuid(), 'Axon Active', '0313788899', '1000+', 'Offshore development for EU/Swiss markets', 'Công Nghệ Thông Tin', '500000', 'Chai Tower, Hai Chau, Da Nang', 'axon-logo.png', 'axonactive.com'),
(gen_random_uuid(), 'Giao Hang Tiet Kiem', '0106181234', '20000+', 'Top-tier logistics tech company in Vietnam', 'Logistics', '100000', 'Pham Hung, Nam Tu Liem, Hanoi', 'ghtk-logo.png', 'giaohangtietkiem.vn'),
(gen_random_uuid(), 'ELSA Corp', '0314123987', '150+', 'AI-powered English pronunciation assistant', 'Giáo Dục', '700000', 'District 1, HCMC', 'elsa-logo.png', 'elsaspeak.com'),
(gen_random_uuid(), 'Rikkeisoft', '0106728999', '1500+', 'Japan-focused offshore software development', 'Công Nghệ Thông Tin', '100000', 'Ba Dinh District, Hanoi', 'rikkeisoft-logo.png', 'rikkeisoft.com');

-- ========================================================
-- 2. SKILLS (10 records - Professional tech stack)
-- ========================================================
INSERT INTO skills (skills_name) VALUES
('Java Spring Boot'),
('React.js'),
('Node.js'),
('Python Django'),
('Docker Kubernetes'),
('PostgreSQL'),
('AWS Cloud'),
('Microservices'),
('CI/CD Jenkins'),
('GitOps ArgoCD');

-- ========================================================
-- 3. EXPERIENCE (10 records - LinkedIn-style professional)
-- ========================================================

INSERT INTO experience (company_id, headline, employment_type, start_date, end_date, description, location_type, employment_location, employment_industry, is_current) VALUES
-- FPT Software (Senior role)
((SELECT company_id FROM company WHERE company_name='FPT Software'), 'Senior Backend Engineer', 'FULL_TIME', '2023-04-01', NULL, 'Led 5-dev team building microservices with Spring Boot + PostgreSQL. Optimized database queries reducing latency 65%. Dockerized deployments to AWS EKS.', 'HYBRID', 'Ho Chi Minh City', 'IT Services', true),

-- VNG (Frontend)
((SELECT company_id FROM company WHERE company_name='VNG Corporation'), 'React Frontend Developer', 'FULL_TIME', '2022-07-01', '2024-12-31', 'Developed Zalo web dashboard with React/Redux. Handled 2M+ DAU, implemented real-time notifications with WebSockets.', 'REMOTE', 'Ho Chi Minh City', 'Technology', false),

-- Tiki (Fullstack)
((SELECT company_id FROM company WHERE company_name='Tiki Corporation'), 'Fullstack Engineer', 'FULL_TIME', '2021-03-01', '2023-03-31', 'Built e-commerce checkout flow with Node.js + React. Integrated payment gateways (VNPay, Momo), improved conversion rate 25%.', 'ON_SITE', 'Ho Chi Minh City', 'E-commerce', false),

-- VinAI (ML)
((SELECT company_id FROM company WHERE company_name='VinAI Research'), 'ML Engineer', 'FULL_TIME', '2022-09-01', NULL, 'Developed computer vision models for autonomous driving. PyTorch deployment on edge devices, achieved 92% mAP on custom dataset.', 'HYBRID', 'Hanoi', 'AI/ML', true),

-- CMC Global (Tech Lead)
((SELECT company_id FROM company WHERE company_name='CMC Global'), 'Tech Lead', 'FULL_TIME', '2023-01-01', '2025-02-28', 'Led offshore team for Japanese bank digital transformation. Migrated monolith to Kubernetes microservices.', 'HYBRID', 'Hanoi', 'IT Services', false),

-- Saigon Technology (DevOps)
((SELECT company_id FROM company WHERE company_name='Saigon Technology'), 'DevOps Engineer', 'FULL_TIME', '2021-11-01', '2023-10-31', 'Implemented CI/CD pipelines with Jenkins/GitLab. Managed AWS infrastructure for fintech clients, reduced deployment time 80%.', 'REMOTE', 'Ho Chi Minh City', 'Software Development', false),

-- KMS (QA Lead)
((SELECT company_id FROM company WHERE company_name='KMS Technology'), 'QA Automation Lead', 'FULL_TIME', '2020-06-01', '2022-05-31', 'Automated testing frameworks with Selenium/Cypress. Google Cloud integration testing for enterprise clients.', 'ON_SITE', 'Ho Chi Minh City', 'IT Services', false),

-- TMA (Software Engineer)
((SELECT company_id FROM company WHERE company_name='TMA Solutions'), 'Software Engineer II', 'FULL_TIME', '2022-02-01', NULL, 'Java backend for US healthcare SaaS. REST APIs + Oracle DB optimization, HIPAA compliant.', 'HYBRID', 'Ho Chi Minh City', 'IT Outsourcing', true),

-- Axon Active (Offshore Dev)
((SELECT company_id FROM company WHERE company_name='Axon Active'), '.NET Developer', 'FULL_TIME', '2019-09-01', '2021-08-31', 'Offshore projects for Swiss fintech. C# ASP.NET Core + Azure DevOps.', 'ON_SITE', 'Da Nang', 'IT Services', false),

-- Rikkeisoft (Backend)
((SELECT company_id FROM company WHERE company_name='Rikkeisoft'), 'Backend Developer', 'FULL_TIME', '2021-08-01', '2023-07-31', 'Enterprise systems for Japanese clients. Spring Boot + MySQL, handled 10k+ TPS.', 'ON_SITE', 'Hanoi', 'IT Services', false);

