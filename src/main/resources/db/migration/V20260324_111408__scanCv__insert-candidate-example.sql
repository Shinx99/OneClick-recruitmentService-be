-- Migration: scanCv - insert-candidate-example
-- Created: Tue Mar 24 11:14:08 AM +07 2026
-- Author: mango

-- Add your SQL statements below:

INSERT INTO candidate (
  candidate_id,
  email,
  about,
  surname,
  name,
  birthday,
  province,
  commune,
  gender,
  avatar_url,
  background_url,
  reference_link,
  consent_data_at,
  consent_version,
  cccd,
  cccd_verified_at,
  verification_level,
  status,
  created_at,
  updated_at
) VALUES

  -- candidate0@example.com
  ('949270cd-1fa2-4ad1-9a15-a7d5c10aa755'::uuid,
   'candidate0@example.com',
   'Full-stack developer 5+ years experience. React, Node.js, PostgreSQL expert.',
   'Nguyen', 'Van Hung', '1995-08-15'::date,
   'Ho Chi Minh', 'Phuong 1, Quan 1', true,
   'https://example.com/avatar/hung-nguyen.jpg',
   'https://example.com/bg/fullstack.jpg',
   'https://linkedin.com/in/hungnguyen-dev',
   now() - INTERVAL '25 days', 'v1.0', '012345678901',
   now() - INTERVAL '10 days', 'lv3', 'active',
   now() - INTERVAL '25 days', now()),

  -- candidate1@example.com
  ('306f0290-32e4-432f-a76c-8da640ae83d0'::uuid,
   'candidate1@example.com',
   'Senior Frontend Engineer - UI/UX specialist. React, Vue.js, Figma.',
   'Tran', 'Thi Lan', '1993-03-22'::date,
   'Ho Chi Minh', 'Phuong 5, Quan 3', false,
   'https://example.com/avatar/lan-tran.jpg',
   'https://example.com/bg/frontend.jpg',
   'https://github.com/lantran-frontend',
   now() - INTERVAL '20 days', 'v1.0', '098765432102',
   NULL, 'lv2', 'active',
   now() - INTERVAL '20 days', now()),

  -- candidate2@example.com
  ('6d66756e-9257-47a8-903f-4175b8e9530f'::uuid,
   'candidate2@example.com',
   'Junior Backend Developer - Java Spring Boot, Microservices.',
   'Le', 'Minh Tuan', '2000-11-10'::date,
   'Ha Noi', 'Phuong 10, Ba Dinh', true,
   'https://example.com/avatar/tuan-le.jpg',
   'https://example.com/bg/backend.jpg',
   'https://topcv.vn/tuanle-backend',
   now() - INTERVAL '15 days', 'v1.0', '112233445566',
   now() - INTERVAL '5 days', 'lv3', 'active',
   now() - INTERVAL '15 days', now()),

  -- candidate3@example.com
  ('d5601db5-cf93-4d5b-ba85-0696ea08fba8'::uuid,
   'candidate3@example.com',
   'Data Analyst - Python, SQL, Tableau, Power BI.',
   'Pham', 'Thi Mai', '1997-07-05'::date,
   'Da Nang', 'Hai Chau 1', false,
   'https://example.com/avatar/mai-pham.jpg',
   'https://example.com/bg/data-analyst.jpg',
   NULL,
   now() - INTERVAL '30 days', 'v1.0', '778899001122',
   NULL, 'lv1', 'inactive',
   now() - INTERVAL '30 days', now() - INTERVAL '1 week')

ON CONFLICT (candidate_id) DO UPDATE SET
  updated_at = now(),
  status = EXCLUDED.status;