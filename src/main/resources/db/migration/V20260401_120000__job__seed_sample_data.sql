-- Migration: job - seed_sample_data
-- Created: Wed Apr 01 2026

-- 1. TẠO EMPLOYER TRƯỚC (Đây là mấu chốt để không bị lỗi Foreign Key)
INSERT INTO employer (
    employer_id,
    company_id,
    email,
    name,
    surname,
    status,
    verification_level,
    level
) VALUES (
             'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1',
             (SELECT company_id FROM company WHERE company_name = 'FPT Software' LIMIT 1),
    'hr.manager@fpt.com',
    'Admin',
    'Recruiter',
    'active',
    'lv3',
    'level1'
    ) ON CONFLICT (employer_id) DO NOTHING;

-- 2. TẠO 10 JOBS (Sử dụng ID của Employer vừa tạo ở trên)
INSERT INTO job (
    job_id, company_id, title, description, requirement, level, job_type,
    province, salary_min, salary_max, experience_min_year, status, created_by, created_at
) VALUES
      (
          gen_random_uuid(), (SELECT company_id FROM company WHERE company_name = 'FPT Software' LIMIT 1),
    'Senior Java Backend Developer (Spring Boot) - Upto $2500', 'Mô tả công việc:\n- Tham gia phát triển các dự án...', 'Yêu cầu ứng viên:\n- Tối thiểu 3 năm kinh nghiệm...', 'Senior', 'Full-time', 'Hồ Chí Minh', 1500.00, 2500.00, 3.0, 'active', 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '1 hours'
    ),
    (
    gen_random_uuid(), (SELECT company_id FROM company WHERE company_name = 'VNG Corporation' LIMIT 1),
    'Frontend Engineer (ReactJS/NextJS) - ZaloPay Team', 'Mô tả công việc:\n- Trực tiếp phát triển các tính năng...', 'Yêu cầu ứng viên:\n- Có từ 2 năm kinh nghiệm...', 'Middle', 'Full-time', 'Hồ Chí Minh', 1200.00, 2000.00, 2.0, 'active', 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '5 hours'
    ),
    (
    gen_random_uuid(), (SELECT company_id FROM company WHERE company_name = 'Tiki Corporation' LIMIT 1),
    'Backend Engineer (Golang/Microservices) - E-commerce', 'Mô tả công việc:\n- Tham gia xây dựng hệ thống...', 'Yêu cầu ứng viên:\n- Có kinh nghiệm với Golang...', 'Middle', 'Full-time', 'Hà Nội', 1500.00, 2800.00, 2.0, 'active', 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '1 days'
    ),
    (
    gen_random_uuid(), (SELECT company_id FROM company WHERE company_name = 'FPT Software' LIMIT 1),
    'Business Analyst (IT/Phần mềm) - Khối Tài Chính', 'Mô tả công việc:\n- Trực tiếp làm việc với khách hàng...', 'Yêu cầu ứng viên:\n- Có 1.5 - 3 năm kinh nghiệm...', 'Junior', 'Full-time', 'Đà Nẵng', 800.00, 1500.00, 1.5, 'active', 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '2 days'
    ),
    (
    gen_random_uuid(), (SELECT company_id FROM company WHERE company_name = 'VNG Corporation' LIMIT 1),
    'Data Engineer (Big Data / Spark / Hadoop)', 'Mô tả công việc:\n- Quản trị Data Pipeline...', 'Yêu cầu ứng viên:\n- 3+ năm kinh nghiệm Data Engineer...', 'Senior', 'Full-time', 'Hồ Chí Minh', 2000.00, 3500.00, 3.0, 'active', 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '3 days'
    ),
    (
    gen_random_uuid(), (SELECT company_id FROM company WHERE company_name = 'Tiki Corporation' LIMIT 1),
    'DevOps Engineer (AWS/Kubernetes) - Lương hấp dẫn', 'Mô tả công việc:\n- Xây dựng hạ tầng Cloud...', 'Yêu cầu ứng viên:\n- Kinh nghiệm Linux, Docker, AWS...', 'Senior', 'Full-time', 'Hồ Chí Minh', 1800.00, 3200.00, 4.0, 'active', 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '4 days'
    ),
    (
    gen_random_uuid(), (SELECT company_id FROM company WHERE company_name = 'FPT Software' LIMIT 1),
    'Thực Tập Sinh / Fresher Java (Có Phụ Cấp)', 'Mô tả công việc:\n- Tham gia khóa đào tạo chuyên sâu...', 'Yêu cầu ứng viên:\n- Sinh viên năm cuối CNTT...', 'Fresher', 'Internship', 'Hà Nội', 200.00, 400.00, 0.0, 'active', 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '5 days'
    ),
    (
    gen_random_uuid(), (SELECT company_id FROM company WHERE company_name = 'VNG Corporation' LIMIT 1),
    'Mobile Developer (Flutter / iOS / Android)', 'Mô tả công việc:\n- Phát triển các ứng dụng Mobile...', 'Yêu cầu ứng viên:\n- Ít nhất 1.5 năm kinh nghiệm Flutter...', 'Junior', 'Full-time', 'Hồ Chí Minh', 1000.00, 1800.00, 1.5, 'active', 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '6 days'
    ),
    (
    gen_random_uuid(), (SELECT company_id FROM company WHERE company_name = 'Tiki Corporation' LIMIT 1),
    'QA/QC Engineer (Automation Test)', 'Mô tả công việc:\n- Lên kế hoạch kiểm thử, viết Test Case...', 'Yêu cầu ứng viên:\n- 2+ năm kinh nghiệm Automation Test...', 'Middle', 'Full-time', 'Hà Nội', 900.00, 1600.00, 2.0, 'active', 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '7 days'
    ),
    (
    gen_random_uuid(), (SELECT company_id FROM company WHERE company_name = 'FPT Software' LIMIT 1),
    'Chuyên Viên Quản Trị Hệ Thống (System Admin)', 'Mô tả công việc:\n- Quản trị hệ thống mạng, máy chủ...', 'Yêu cầu ứng viên:\n- Tốt nghiệp chuyên ngành Mạng...', 'Junior', 'Full-time', 'Đà Nẵng', 500.00, 900.00, 1.0, 'active', 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '8 days'
    );

-- 3. LINK JOBS TO SKILLS
INSERT INTO job_skills (job_id, skills_id)
SELECT j.job_id, s.skills_id FROM job j, skills s WHERE j.title LIKE '%Java%' AND s.skills_name = 'Java Spring Boot' ON CONFLICT DO NOTHING;

INSERT INTO job_skills (job_id, skills_id)
SELECT j.job_id, s.skills_id FROM job j, skills s WHERE j.title LIKE '%Frontend%' AND s.skills_name = 'React.js' ON CONFLICT DO NOTHING;