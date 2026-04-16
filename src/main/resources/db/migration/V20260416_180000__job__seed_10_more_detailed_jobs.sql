-- Migration: job - seed 10 more detailed jobs (đa ngành nghề)
-- Created: Wed Apr 16 2026

-- =============================================
-- 1. ADD img_url COLUMN IF NOT EXISTS
-- =============================================
ALTER TABLE job ADD COLUMN IF NOT EXISTS img_url TEXT;

-- =============================================
-- 2. SEED 10 NEW JOBS (đa ngành: IT, Marketing, Bếp, Kế toán, Nhân sự, Logistics...)
-- =============================================
INSERT INTO job (
    job_id, company_id, title, description, requirement, img_url, major_preffered,
    level, job_type, province, commune, salary_min, salary_max,
    experience_min_year, application_deadline, application_count, view_count,
    status, created_by, created_at, updated_at
) VALUES

-- 1. Marketing Manager
(gen_random_uuid(),
 (SELECT company_id FROM company WHERE company_name = 'Tiki Corporation' LIMIT 1),
 'Marketing Manager - Digital Marketing & Brand Strategy',
 'Mô tả công việc:
- Xây dựng và triển khai chiến lược Marketing tổng thể cho nền tảng thương mại điện tử.
- Quản lý ngân sách marketing (Paid Ads, SEO, Content, KOL/Influencer), tối ưu ROI.
- Lên kế hoạch và điều phối các chiến dịch lớn: Flash Sale, Mega Sale, Black Friday, 11/11, 12/12.
- Phân tích dữ liệu người dùng (Google Analytics, Mixpanel) để đưa ra quyết định marketing.
- Quản lý team 5-8 người (Content Writer, Graphic Designer, Performance Marketer).
- Hợp tác với team Product, Sales để align chiến lược tăng trưởng.
- Xây dựng brand identity, brand guideline và quản lý hình ảnh thương hiệu.',
 'Yêu cầu ứng viên:
- Từ 4 năm kinh nghiệm Digital Marketing, ít nhất 2 năm ở vị trí quản lý.
- Có kinh nghiệm chạy quảng cáo Facebook Ads, Google Ads, TikTok Ads với ngân sách lớn (>100M/tháng).
- Thành thạo Google Analytics, Data Studio, SEO tools (Ahrefs, SEMrush).
- Có kinh nghiệm ngành e-commerce, retail hoặc FMCG là lợi thế lớn.
- Kỹ năng quản lý team, lãnh đạo, ra quyết định dựa trên data.
- Tư duy sáng tạo, nắm bắt xu hướng thị trường nhanh.
- Tiếng Anh giao tiếp tốt.',
 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776052464/companies/avatars/1692104431985_uik5mj.jpg',
 'Marketing', 'Senior', 'Full-time', 'Hồ Chí Minh', 'Quận 1',
 1500.00, 2500.00, 4.0, '2026-06-30', 0, 0, 'active',
 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '2 days', NOW()),

-- 2. Bếp trưởng (Head Chef)
(gen_random_uuid(),
 (SELECT company_id FROM company WHERE company_name = 'Saigon Technology' LIMIT 1),
 'Bếp Trưởng (Head Chef) - Nhà Hàng Ẩm Thực Á Âu',
 'Mô tả công việc:
- Quản lý toàn bộ hoạt động bếp: lên menu, kiểm soát chất lượng món ăn, quản lý nguyên vật liệu.
- Sáng tạo và phát triển menu mới theo mùa, kết hợp ẩm thực Á - Âu hiện đại.
- Đảm bảo tiêu chuẩn vệ sinh an toàn thực phẩm (HACCP, ISO 22000).
- Quản lý đội ngũ bếp 10-15 người, phân chia ca làm việc, đào tạo nhân viên mới.
- Kiểm soát food cost, giảm thiểu lãng phí, tối ưu nguyên vật liệu.
- Phối hợp với bộ phận F&B, Marketing để lên concept menu phù hợp đối tượng khách hàng.
- Đảm bảo tốc độ ra món nhanh, đồng đều chất lượng giữa các ca.',
 'Yêu cầu ứng viên:
- Tối thiểu 5 năm kinh nghiệm làm bếp, trong đó ít nhất 2 năm vị trí Bếp trưởng/Bếp phó.
- Tốt nghiệp Trường Cao đẳng/Đại học chuyên ngành Nấu ăn, Quản trị Nhà hàng Khách sạn.
- Thành thạo ẩm thực Việt Nam, có kiến thức về ẩm thực Âu, Nhật, Hàn.
- Có chứng chỉ vệ sinh an toàn thực phẩm.
- Kỹ năng quản lý team, chịu được áp lực cao trong giờ cao điểm.
- Sáng tạo, đam mê ẩm thực, luôn cập nhật xu hướng food & beverage.
- Sức khỏe tốt, có thể làm việc theo ca (bao gồm cuối tuần, lễ tết).',
 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776052512/companies/avatars/1773284290624_kgwmgs.jpg',
 'Nhà hàng - Khách sạn', 'Senior', 'Full-time', 'Hồ Chí Minh', 'Quận 3',
 800.00, 1500.00, 5.0, '2026-07-15', 0, 0, 'active',
 'e2e2e2e2-e2e2-e2e2-e2e2-e2e2e2e2e2e2', NOW() - INTERVAL '3 days', NOW()),

-- 3. Kế toán trưởng
(gen_random_uuid(),
 (SELECT company_id FROM company WHERE company_name = 'FPT Software' LIMIT 1),
 'Kế Toán Trưởng (Chief Accountant) - Công ty Công nghệ',
 'Mô tả công việc:
- Chịu trách nhiệm toàn bộ công tác kế toán, tài chính của công ty (doanh thu > 500 tỷ/năm).
- Lập báo cáo tài chính quý/năm theo chuẩn VAS và IFRS.
- Quản lý thuế (GTGT, TNDN, TNCN), kê khai và quyết toán thuế đúng hạn.
- Xây dựng quy trình kiểm soát nội bộ, quản lý dòng tiền, lập ngân sách.
- Phối hợp với kiểm toán độc lập, cơ quan thuế khi có yêu cầu.
- Quản lý team kế toán 5-8 người (kế toán thanh toán, kế toán kho, kế toán lương).
- Tư vấn cho Ban Giám đốc về các vấn đề tài chính, thuế, tối ưu chi phí.',
 'Yêu cầu ứng viên:
- Từ 5 năm kinh nghiệm kế toán, ít nhất 2 năm vị trí Kế toán trưởng.
- Tốt nghiệp Đại học chuyên ngành Kế toán, Tài chính.
- Có chứng chỉ Kế toán trưởng (bắt buộc theo quy định).
- Thành thạo phần mềm kế toán (MISA, SAP, Fast) và Excel nâng cao.
- Am hiểu Luật Thuế, Luật Kế toán Việt Nam, chuẩn mực VAS.
- Có kinh nghiệm ngành IT/Phần mềm là lợi thế.
- Tỉ mỉ, cẩn thận, có trách nhiệm cao, giữ bí mật thông tin tài chính.',
 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776053365/companies/avatars/fpt_software_logo_ibo1d7.jpg',
 'Kế toán - Tài chính', 'Senior', 'Full-time', 'Hà Nội', 'Cầu Giấy',
 1200.00, 2000.00, 5.0, '2026-06-30', 0, 0, 'active',
 'e3e3e3e3-e3e3-e3e3-e3e3-e3e3e3e3e3e3', NOW() - INTERVAL '4 days', NOW()),

-- 4. HR Business Partner
(gen_random_uuid(),
 (SELECT company_id FROM company WHERE company_name = 'VNG Corporation' LIMIT 1),
 'HR Business Partner (HRBP) - Mảng Công nghệ',
 'Mô tả công việc:
- Là đối tác chiến lược của các bộ phận Engineering, Product trong các vấn đề nhân sự.
- Tư vấn cho Manager/Director về quản lý hiệu suất, phát triển nhân tài, cơ cấu tổ chức.
- Triển khai chương trình đánh giá năng lực (OKR/KPI), review lương thưởng hàng năm.
- Xây dựng và thực hiện kế hoạch tuyển dụng cho các vị trí IT (50+ headcount/quý).
- Quản lý quan hệ lao động, giải quyết xung đột, đảm bảo tuân thủ Luật Lao động.
- Thiết kế chương trình onboarding, retention, employee engagement.
- Phân tích data HR (turnover rate, engagement score, time-to-hire) để đề xuất cải thiện.',
 'Yêu cầu ứng viên:
- Từ 4 năm kinh nghiệm HR, ít nhất 2 năm ở vai trò HRBP hoặc HR Manager.
- Tốt nghiệp Đại học ngành Quản trị Nhân sự, Quản trị Kinh doanh, Luật hoặc liên quan.
- Có kinh nghiệm tuyển dụng và quản lý nhân sự ngành IT/Tech là bắt buộc.
- Am hiểu Luật Lao động Việt Nam, BHXH, BHYT.
- Kỹ năng giao tiếp, đàm phán, xử lý xung đột xuất sắc.
- Thành thạo Excel, có kinh nghiệm với HRIS (SAP HR, BambooHR).
- Tiếng Anh giao tiếp tốt.',
 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776053530/companies/avatars/vng_group_logo_fvdsli.jpg',
 'Nhân sự', 'Senior', 'Full-time', 'Hồ Chí Minh', 'Quận 7',
 1500.00, 2500.00, 4.0, '2026-07-01', 0, 0, 'active',
 'e4e4e4e4-e4e4-e4e4-e4e4-e4e4e4e4e4e4', NOW() - INTERVAL '5 days', NOW()),

-- 5. AI/ML Engineer (giữ lại 1 job IT)
(gen_random_uuid(),
 (SELECT company_id FROM company WHERE company_name = 'VinAI Research' LIMIT 1),
 'AI/ML Engineer - Computer Vision (Python/PyTorch)',
 'Mô tả công việc:
- Nghiên cứu và phát triển các mô hình AI/ML cho bài toán Computer Vision.
- Xây dựng pipeline huấn luyện và triển khai mô hình trên môi trường production (AWS SageMaker, TensorRT).
- Hợp tác với team Research để chuyển đổi paper nghiên cứu thành sản phẩm thực tế.
- Tối ưu hóa hiệu suất mô hình (model compression, quantization, pruning).
- Viết tài liệu kỹ thuật và chia sẻ kiến thức với team.',
 'Yêu cầu ứng viên:
- Tốt nghiệp Thạc sĩ trở lên ngành Khoa học Máy tính, Trí tuệ Nhân tạo.
- Tối thiểu 2 năm kinh nghiệm Deep Learning, đặc biệt Computer Vision.
- Thành thạo Python, PyTorch hoặc TensorFlow.
- Có kinh nghiệm với CNN (ResNet, EfficientNet, YOLO, Transformer-based models).
- Có publication tại CVPR, ICCV, NeurIPS là điểm cộng lớn.
- Tiếng Anh giao tiếp tốt.',
 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776053487/companies/avatars/vinai_research_logo_c0d6qz.jpg',
 'Trí tuệ nhân tạo', 'Senior', 'Full-time', 'Hà Nội', 'Cầu Giấy',
 2000.00, 4000.00, 2.0, '2026-06-30', 0, 0, 'active',
 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', NOW() - INTERVAL '6 days', NOW()),

-- 6. Nhân viên Logistics / Quản lý kho
(gen_random_uuid(),
 (SELECT company_id FROM company WHERE company_name = 'Tiki Corporation' LIMIT 1),
 'Quản Lý Kho Vận (Warehouse Supervisor) - Logistics',
 'Mô tả công việc:
- Quản lý hoạt động nhập - xuất - tồn kho cho kho hàng thương mại điện tử (diện tích 5000m²+).
- Điều phối đội ngũ nhân viên kho 30-50 người, phân chia ca làm việc, giám sát năng suất.
- Đảm bảo đơn hàng được pick-pack-ship đúng hạn (SLA giao hàng 24h nội thành).
- Kiểm soát inventory accuracy >= 99.5%, thực hiện kiểm kê định kỳ.
- Vận hành và cải thiện quy trình kho theo tiêu chuẩn 5S, Lean Warehouse.
- Quản lý WMS (Warehouse Management System), phối hợp với team IT để cải tiến hệ thống.
- Báo cáo KPI kho hàng: thời gian xử lý đơn, tỷ lệ lỗi, năng suất nhân viên.',
 'Yêu cầu ứng viên:
- Từ 3 năm kinh nghiệm quản lý kho, ưu tiên ngành thương mại điện tử/logistics.
- Tốt nghiệp Đại học/Cao đẳng ngành Logistics, Quản trị Kinh doanh, Quản lý Công nghiệp.
- Có kinh nghiệm sử dụng WMS (Warehouse Management System).
- Thành thạo Excel (VLOOKUP, Pivot Table, báo cáo).
- Kỹ năng quản lý team lớn, giải quyết vấn đề nhanh.
- Chịu được áp lực cao, đặc biệt trong các đợt sale lớn (11/11, 12/12).
- Sức khỏe tốt, sẵn sàng làm thêm giờ khi cần.',
 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776052464/companies/avatars/1692104431985_uik5mj.jpg',
 'Logistics - Kho vận', 'Middle', 'Full-time', 'Hồ Chí Minh', 'Quận 9',
 700.00, 1200.00, 3.0, '2026-07-15', 0, 0, 'active',
 'e2e2e2e2-e2e2-e2e2-e2e2-e2e2e2e2e2e2', NOW() - INTERVAL '7 days', NOW()),

-- 7. Content Creator / Social Media
(gen_random_uuid(),
 (SELECT company_id FROM company WHERE company_name = 'ELSA Corp' LIMIT 1),
 'Content Creator (TikTok/Reels/YouTube) - EdTech',
 'Mô tả công việc:
- Sáng tạo nội dung video ngắn cho TikTok, Instagram Reels, YouTube Shorts về chủ đề học tiếng Anh.
- Lên ý tưởng, viết kịch bản, quay và edit video (target 3-5 video/tuần).
- Xây dựng và phát triển cộng đồng trên các nền tảng MXH (target 100K followers trong 6 tháng).
- Hợp tác với KOL/Influencer trong lĩnh vực giáo dục.
- Phân tích metrics (views, engagement rate, conversion) để tối ưu nội dung.
- Theo dõi trend, viral content để áp dụng vào chiến lược content.
- Phối hợp với team Marketing, Product để align thông điệp thương hiệu.',
 'Yêu cầu ứng viên:
- Từ 1-3 năm kinh nghiệm Content Creator hoặc Social Media.
- Có kênh TikTok/YouTube cá nhân với lượng theo dõi đáng kể là lợi thế rất lớn.
- Thành thạo CapCut, Premiere Pro hoặc Final Cut Pro.
- Biết chụp ảnh, quay video cơ bản bằng smartphone và camera.
- Có khiếu hài hước, storytelling tốt, không ngại xuất hiện trước camera.
- Am hiểu các thuật toán đề xuất của TikTok, Instagram, YouTube.
- Đam mê giáo dục, tiếng Anh giao tiếp tốt.',
 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776063162/companies/avatars/elsa_corp_logo_tcv10z.jpg',
 'Marketing - Truyền thông', 'Junior', 'Full-time', 'Hồ Chí Minh', 'Quận 2',
 500.00, 1000.00, 1.0, '2026-07-31', 0, 0, 'active',
 'e3e3e3e3-e3e3-e3e3-e3e3-e3e3e3e3e3e3', NOW() - INTERVAL '8 days', NOW()),

-- 8. Nhân viên Kinh doanh B2B
(gen_random_uuid(),
 (SELECT company_id FROM company WHERE company_name = 'CMC Global' LIMIT 1),
 'Nhân Viên Kinh Doanh B2B (IT Solutions / Outsourcing)',
 'Mô tả công việc:
- Tìm kiếm và phát triển khách hàng doanh nghiệp mới cho dịch vụ outsourcing IT, giải pháp phần mềm.
- Tư vấn giải pháp công nghệ phù hợp với nhu cầu khách hàng (Web App, Mobile App, Cloud Migration).
- Quản lý pipeline sales, theo dõi leads từ giai đoạn tiếp cận đến ký hợp đồng (deal size $50K-$500K).
- Chuẩn bị proposal, báo giá, thuyết trình giải pháp cho C-level clients.
- Đạt target doanh số hàng quý/năm (quota $1M+/năm).
- Duy trì quan hệ khách hàng hiện tại, upsell/cross-sell các dịch vụ bổ sung.
- Tham gia các sự kiện, hội thảo công nghệ để networking.',
 'Yêu cầu ứng viên:
- Từ 2 năm kinh nghiệm sales B2B, ưu tiên ngành IT/Phần mềm/Outsourcing.
- Tốt nghiệp Đại học ngành Kinh doanh, Marketing, CNTT hoặc liên quan.
- Hiểu biết cơ bản về công nghệ: Web, Mobile, Cloud, AI để tư vấn khách hàng.
- Kỹ năng thuyết trình, đàm phán, chốt deal xuất sắc.
- Có network trong cộng đồng doanh nghiệp/startup là lợi thế.
- Tiếng Anh hoặc tiếng Nhật giao tiếp tốt (khách hàng quốc tế).
- Năng động, chịu được áp lực target, có tinh thần hunter.',
 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776053319/companies/avatars/cmc_global_company_limited_logo_ghl4pb.jpg',
 'Kinh doanh - Bán hàng', 'Middle', 'Full-time', 'Hà Nội', 'Thanh Xuân',
 800.00, 2000.00, 2.0, '2026-07-15', 0, 0, 'active',
 'e4e4e4e4-e4e4-e4e4-e4e4-e4e4e4e4e4e4', NOW() - INTERVAL '9 days', NOW()),

-- 9. Thực tập sinh Graphic Design
(gen_random_uuid(),
 (SELECT company_id FROM company WHERE company_name = 'VNG Corporation' LIMIT 1),
 'Thực Tập Sinh Graphic Designer - Gaming Division',
 'Mô tả công việc:
- Hỗ trợ thiết kế banner, poster, social media post cho các sản phẩm game (Zalo, ZingMP3).
- Thiết kế UI assets cho game mobile (icons, buttons, splash screens, loading screens).
- Hỗ trợ team Marketing thiết kế ấn phẩm quảng cáo: email template, landing page, in-app banners.
- Tham gia brainstorm ý tưởng sáng tạo cho các chiến dịch marketing game.
- Học và thực hành motion graphics cơ bản (After Effects).
- Được mentor bởi Senior Designer, review portfolio hàng tuần.',
 'Yêu cầu ứng viên:
- Sinh viên năm 3-4 hoặc mới tốt nghiệp ngành Thiết kế Đồ họa, Mỹ thuật Ứng dụng.
- Thành thạo Adobe Photoshop, Illustrator. Biết Figma là điểm cộng.
- Có portfolio thể hiện khả năng sáng tạo (Behance, Dribbble, hoặc PDF).
- Am hiểu về color theory, typography, layout principles.
- Đam mê game, hiểu biết về thị trường game Việt Nam.
- Có thể làm full-time trong thời gian thực tập (3-6 tháng).
- Biết After Effects hoặc motion graphics là lợi thế lớn.',
 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776053530/companies/avatars/vng_group_logo_fvdsli.jpg',
 'Thiết kế đồ họa', 'Fresher', 'Internship', 'Hồ Chí Minh', 'Quận 7',
 200.00, 400.00, 0.0, '2026-08-01', 0, 0, 'active',
 'e5e5e5e5-e5e5-e5e5-e5e5-e5e5e5e5e5e5', NOW() - INTERVAL '10 days', NOW()),

-- 10. Chăm sóc khách hàng (Customer Service)
(gen_random_uuid(),
 (SELECT company_id FROM company WHERE company_name = 'KMS Technology' LIMIT 1),
 'Nhân Viên Chăm Sóc Khách Hàng (Customer Support) - Part-time',
 'Mô tả công việc:
- Tiếp nhận và xử lý yêu cầu, khiếu nại của khách hàng qua điện thoại, email, live chat.
- Hỗ trợ khách hàng sử dụng sản phẩm phần mềm SaaS: hướng dẫn, troubleshooting, escalation.
- Ghi nhận phản hồi khách hàng và chuyển đến bộ phận liên quan (Product, Engineering).
- Đạt KPI: thời gian phản hồi < 2 phút, CSAT >= 4.5/5, resolution rate >= 90%.
- Cập nhật Knowledge Base, FAQ cho khách hàng.
- Phối hợp với team Sales để chăm sóc khách hàng tiềm năng.
- Làm việc theo ca (8h/ngày, linh hoạt chọn ca sáng hoặc chiều).',
 'Yêu cầu ứng viên:
- Không yêu cầu kinh nghiệm (sẽ được đào tạo).
- Tốt nghiệp THPT trở lên, ưu tiên sinh viên đang học muốn làm thêm.
- Giọng nói dễ nghe, thái độ thân thiện, kiên nhẫn.
- Biết sử dụng máy tính cơ bản (Word, Excel, email).
- Kỹ năng lắng nghe và giải quyết vấn đề tốt.
- Tiếng Anh cơ bản (đọc hiểu email) là điểm cộng.
- Có thể làm part-time 4-5 giờ/ngày, phù hợp cho sinh viên.',
 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776053403/companies/avatars/kms_technology_logo_nuystc.jpg',
 'Dịch vụ khách hàng', 'Fresher', 'Part-time', 'Hồ Chí Minh', 'Quận 1',
 200.00, 400.00, 0.0, '2026-07-31', 0, 0, 'active',
 'e5e5e5e5-e5e5-e5e5-e5e5-e5e5e5e5e5e5', NOW() - INTERVAL '11 days', NOW());

-- =============================================
-- 3. LINK JOBS TO SKILLS (chỉ job IT mới cần skills tech)
-- =============================================

-- AI/ML Engineer → Python Django, Docker Kubernetes, AWS Cloud
INSERT INTO job_skills (job_id, skills_id)
SELECT j.job_id, s.skills_id FROM job j, skills s
WHERE j.title = 'AI/ML Engineer - Computer Vision (Python/PyTorch)' AND s.skills_name = 'Python Django'
ON CONFLICT DO NOTHING;
INSERT INTO job_skills (job_id, skills_id)
SELECT j.job_id, s.skills_id FROM job j, skills s
WHERE j.title = 'AI/ML Engineer - Computer Vision (Python/PyTorch)' AND s.skills_name = 'Docker Kubernetes'
ON CONFLICT DO NOTHING;
INSERT INTO job_skills (job_id, skills_id)
SELECT j.job_id, s.skills_id FROM job j, skills s
WHERE j.title = 'AI/ML Engineer - Computer Vision (Python/PyTorch)' AND s.skills_name = 'AWS Cloud'
ON CONFLICT DO NOTHING;

-- =============================================
-- 4. LINK JOBS TO EMPLOYERS (owner)
-- =============================================
INSERT INTO job_employer (job_id, employer_id, access_role, granted_at)
SELECT j.job_id, j.created_by, 'owner', NOW()
FROM job j
WHERE j.created_at > NOW() - INTERVAL '12 days'
  AND NOT EXISTS (
    SELECT 1 FROM job_employer je WHERE je.job_id = j.job_id
  );
