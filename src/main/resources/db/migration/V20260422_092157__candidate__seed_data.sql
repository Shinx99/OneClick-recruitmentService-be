-- Migration: candidate - seed_data
-- Created: Wed Apr 22 09:21:57 AM +07 2026
-- Author: hoangvuongbui

-- Add your SQL statements below:
INSERT INTO candidate (
    candidate_id, email, phone, about, surname, name, birthday, province, commune,
    gender, avatar_url, background_url, reference_link, consent_data_at,
    consent_version, cccd, cccd_verified_at, verification_level, status, created_at, updated_at
)
VALUES
(
    'a1a1a1a1-0001-0001-0001-a1a1a1a1a1a1',
    'nguyen.van.an@gmail.com', '0901234561',
    'Detail-oriented System Analyst with 6+ years of experience in evaluating business requirements,
     designing efficient system solutions, and coordinating with technical teams to implement technology
     improvements',
    'Nguyễn', 'Văn An',
    '1998-05-15', 'Hà Nội', 'Phường Cầu Giấy',
    TRUE,
    'https://cdn.example.com/avatars/001.jpg',
    'https://cdn.example.com/backgrounds/001.jpg',
    'https://linkedin.com/in/nguyenvanan',
    '2024-01-10 08:00:00+07', 'v1.0',
    '001098005151', '2024-01-10 08:05:00+07', 'lv3', 'active',
    '2024-01-10 07:50:00+07', '2024-01-10 07:50:00+07'
),
(
    'a1a1a1a1-0002-0002-0002-a1a1a1a1a1a2',
    'tran.thi.bich@gmail.com', '0901234562',
    'Là một chuyên gia tiếp thị với hơn 10 năm kinh nghiệm, chuyên cung cấp các chiến lược dựa trên
     dữ liệu cho các chiến dịch đa kênh',
    'Trần', 'Thị Bích',
    '2000-03-22', 'Hồ Chí Minh', 'Phường Bến Nghé, Quận 1',
    FALSE,
    'https://cdn.example.com/avatars/002.jpg',
    'https://cdn.example.com/backgrounds/002.jpg',
    'https://behance.net/tranthibich',
    '2024-02-15 09:00:00+07', 'v1.0',
    '079200003221', '2024-02-15 09:05:00+07', 'lv3', 'active',
    '2024-02-15 08:45:00+07', '2024-02-15 08:45:00+07'
),
(
    'a1a1a1a1-0003-0003-0003-a1a1a1a1a1a3',
    'le.minh.duc@gmail.com', '0901234563',
    'Tôi là sinh viên tốt nghiệp chuyên ngành Hóa học với mong muốn áp dụng nền tảng học thuật rộng lớn
     và kinh nghiệm phòng thí nghiệm của mình vào một tổ chức có tầm nhìn đổi mới. Tôi mong muốn
     đóng góp vào một đội ngũ năng động và hỗ trợ các nỗ lực nghiên cứu và phát triển.',
    'Lê', 'Minh Đức',
    '2001-11-08', 'Đà Nẵng', 'Phường Hải Châu',
    TRUE,
    'https://cdn.example.com/avatars/003.jpg',
    NULL,
    'https://github.com/leminhduc',
    '2024-03-20 10:00:00+07', 'v1.0',
    '048201011081', '2024-03-20 10:05:00+07', 'lv3', 'active',
    '2024-03-20 09:55:00+07', '2024-03-20 09:55:00+07'
),
(
    'a1a1a1a1-0004-0004-0004-a1a1a1a1a1a4',
    'pham.thi.huong@gmail.com', '0901234564',
    'Strategic and analytical Business Consultant with 7 years of experience in delivering impactful
     solutions and driving organizational change for diverse clients across various industries. Proven
     expertise in identifying complex business challenges, developing data-driven strategies, and
     facilitating successful implementation to achieve measurable improvements in efficiency,
     profitability, and market positioning. Committed to fostering long-term client relationships and
     delivering exceptional value.',
    'Phạm', 'Thị Hương',
    '1997-07-30', 'Hà Nội', 'Phường Đống Đa',
    FALSE,
    'https://cdn.example.com/avatars/004.jpg',
    'https://cdn.example.com/backgrounds/004.jpg',
    'https://linkedin.com/in/phamthihuong',
    '2024-04-05 11:00:00+07', 'v1.0',
    '001097007301', '2024-04-05 11:05:00+07', 'lv3', 'active',
    '2024-04-05 10:50:00+07', '2024-04-05 10:50:00+07'
),
(
    'a1a1a1a1-0005-0005-0005-a1a1a1a1a1a5',
    'hoang.van.khai@gmail.com', '0901234565',
    'Tôi là một chuyên viên thiết kế hệ thống tận tâm và sáng tạo với 10 năm kinh nghiệm trong việc tạo ra,
     triển khai và tối ưu hóa các hệ thống phức tạp.',
    'Hoàng', 'Văn Khải',
    '1999-09-12', 'Hồ Chí Minh', 'Phường Tân Bình',
    TRUE,
    'https://cdn.example.com/avatars/005.jpg',
    NULL,
    NULL,
    '2024-05-12 08:00:00+07', 'v1.0',
    '079199009121', NULL, 'lv3', 'active',
    '2024-05-12 08:00:00+07', '2024-05-12 08:00:00+07'
),
(
    'a1a1a1a1-0006-0006-0006-a1a1a1a1a1a6',
    'vo.thi.lan@gmail.com', '0901234566',
    'Tôi là kỹ sư phần mềm với hơn 5 năm kinh nghiệm làm việc trong
     ngành công nghệ, cung cấp chuyên môn có giá trị cho các công ty
     khởi nghiệp.',
    'Võ', 'Thị Lan',
    '1996-01-25', 'Cần Thơ', 'Phường Ninh Kiều',
    FALSE,
    'https://cdn.example.com/avatars/006.jpg',
    'https://cdn.example.com/backgrounds/006.jpg',
    NULL,
    '2024-06-01 07:30:00+07', 'v1.0',
    '092196001251', '2024-06-01 07:35:00+07', 'lv3', 'active',
    '2024-06-01 07:20:00+07', '2024-06-01 07:20:00+07'
),
(
    'a1a1a1a1-0007-0007-0007-a1a1a1a1a1a7',
    'dang.quoc.minh@gmail.com', '0901234567',
    'Kỹ sư robot định hướng chi tiết với nền tảng vững chắc về trí tuệ nhân tạo, có kinh nghiệm trong việc thiết kế,
     phát triển và triển khai các hệ thống robot tiên tiến.',
    'Đặng', 'Quốc Minh',
    '1995-04-18', 'Hà Nội', 'Phường Hoàng Mai',
    TRUE,
    'https://cdn.example.com/avatars/007.jpg',
    'https://cdn.example.com/backgrounds/007.jpg',
    'https://linkedin.com/in/dangquocminh',
    '2024-07-18 14:00:00+07', 'v1.1',
    '001095004181', '2024-07-18 14:05:00+07', 'lv3', 'active',
    '2024-07-18 13:55:00+07', '2024-07-18 13:55:00+07'
),
(
    'a1a1a1a1-0008-0008-0008-a1a1a1a1a1a8',
    'bui.thi.ngoc@gmail.com', '0901234568',
    'Chiến lược gia nội dung tiếp thị sáng tạo và
     quyết đoán với năm năm kinh nghiệm trong việc
     phát triển và thực hiện các chiến lược nội dung
     thúc đẩy sự tương tác và tăng trưởng.',
    'Bùi', 'Thị Ngọc',
    '1998-12-03', 'Hồ Chí Minh', 'Phường Thủ Đức',
    FALSE,
    'https://cdn.example.com/avatars/008.jpg',
    NULL,
    'https://linkedin.com/in/buithingoc',
    '2024-08-22 09:00:00+07', 'v1.1',
    '079198012031', '2024-08-22 09:05:00+07', 'lv3', 'active',
    '2024-08-22 08:50:00+07', '2024-09-01 10:00:00+07'
),
(
    'a1a1a1a1-0009-0009-0009-a1a1a1a1a1a9',
    'nguyen.hoang.phuc@gmail.com', '0901234569',
    'Tôi là một cử nhân mới tốt nghiệp, có chứng chỉ sư phạm, với thế mạnh
     cung cấp nền giáo dục tiếng Anh chất lượng cao cho học sinh trung học.',
    'Nguyễn', 'Hoàng Phúc',
    '1994-06-27', 'Hà Nội', 'Phường Tây Hồ',
    TRUE,
    'https://cdn.example.com/avatars/009.jpg',
    'https://cdn.example.com/backgrounds/009.jpg',
    'https://linkedin.com/in/nguyenhoangphuc',
    '2024-09-30 15:00:00+07', 'v1.1',
    '001094006271', '2024-09-30 15:05:00+07', 'lv3', 'active',
    '2024-09-30 14:50:00+07', '2024-09-30 14:50:00+07'
),
(
    'a1a1a1a1-0010-0010-0010-a1a1a1a1a1a0',
    'tran.van.quang@gmail.com', '0901234570',
    'Marketing Manager',
    'Trần', 'Văn Quang',
    '1997-02-14', 'Hồ Chí Minh', 'Phường Bình Thạnh',
    TRUE,
    'https://cdn.example.com/avatars/010.jpg',
    'https://cdn.example.com/backgrounds/010.jpg',
    'https://github.com/tranvanquang',
    '2024-10-11 08:00:00+07', 'v1.1',
    '079197002141', '2024-10-11 08:05:00+07', 'lv3', 'active',
    '2024-10-11 07:55:00+07', '2024-10-11 07:55:00+07'
),
(
    'a1a1a1a1-0011-0011-0011-a1a1a1a1a1b1',
    'ly.thi.sau@gmail.com', '0901234571',
    'Graphics Designer',
    'Lý', 'Thị Sáu',
    '2000-08-19', 'Hải Phòng', 'Phường Lê Chân',
    FALSE,
    'https://cdn.example.com/avatars/011.jpg',
    NULL,
    NULL,
    '2024-10-20 09:00:00+07', 'v1.1',
    '031200008191', '2024-10-20 09:05:00+07', 'lv3', 'active',
    '2024-10-20 08:50:00+07', '2024-10-20 08:50:00+07'
),
(
    'a1a1a1a1-0012-0012-0012-a1a1a1a1a1b2',
    'dinh.van.tam@gmail.com', '0901234572',
    'Results-driven Administrative Manager with 8+ years of experience leading office operations, HR support,
     budgeting, procurement, and cross-functional coordination. Proven ability to optimize administrative processes,
     reduce operational costs, and support senior leadership with high-level reporting and organizational planning.
     Skilled in team leadership, policy development, compliance systems, and project execution.',
    'Đinh', 'Văn Tám',
    '1999-10-05', 'Đà Nẵng', 'Phường Sơn Trà',
    TRUE,
    'https://cdn.example.com/avatars/012.jpg',
    'https://cdn.example.com/backgrounds/012.jpg',
    'https://linkedin.com/in/dinhvantam',
    '2024-11-01 10:00:00+07', 'v1.1',
    '048199010051', '2024-11-01 10:05:00+07', 'lv3', 'active',
    '2024-11-01 09:55:00+07', '2024-11-01 09:55:00+07'
),
(
    'a1a1a1a1-0013-0013-0013-a1a1a1a1a1b3',
    'ngo.thi.uyen@gmail.com', '0901234573',
    'Kỹ sư Cơ khí',
    'Ngô', 'Thị Uyên',
    '2002-05-30', 'Hồ Chí Minh', 'Phường Gò Vấp',
    FALSE,
    'https://cdn.example.com/avatars/013.jpg',
    NULL,
    'https://github.com/ngothiuyen',
    '2024-11-10 08:00:00+07', 'v1.1',
    '079202005301', NULL, 'lv1', 'active',
    '2024-11-10 08:00:00+07', '2024-11-10 08:00:00+07'
),
(
    'a1a1a1a1-0014-0014-0014-a1a1a1a1a1b4',
    'truong.van.vinh@gmail.com', '0901234574',
    'MARKETING MANAGER',
    'Trương', 'Văn Vĩnh',
    '1993-03-11', 'Hà Nội', 'Phường Nam Từ Liêm',
    TRUE,
    'https://cdn.example.com/avatars/014.jpg',
    'https://cdn.example.com/backgrounds/014.jpg',
    'https://linkedin.com/in/truongvanvinh',
    '2024-11-15 11:00:00+07', 'v1.1',
    '001093003111', '2024-11-15 11:05:00+07', 'lv3', 'active',
    '2024-11-15 10:55:00+07', '2024-11-15 10:55:00+07'
),
(
    'a1a1a1a1-0015-0015-0015-a1a1a1a1a1b5',
    'mai.thi.xuan@gmail.com', '0901234575',
    'Tôi là một kế toán viên có chứng chỉ hành nghề và kinh nghiệm lập ngân sách chi phí cho các công ty đa quốc gia.
     Tôi hiện tìm kiếm cơ hội việc làm trong vai trò quản lý.',
    'Mai', 'Thị Xuân',
    '1998-07-07', 'Hồ Chí Minh', 'Phường Phú Nhuận',
    FALSE,
    'https://cdn.example.com/avatars/015.jpg',
    'https://cdn.example.com/backgrounds/015.jpg',
    NULL,
    '2024-11-22 08:30:00+07', 'v1.1',
    '079198007071', '2024-11-22 08:35:00+07', 'lv3', 'active',
    '2024-11-22 08:20:00+07', '2024-11-22 08:20:00+07'
),
(
    'a1a1a1a1-0016-0016-0016-a1a1a1a1a1b6',
    'cao.van.yen@gmail.com', '0901234576',
    'Graphics Designer',
    'Cao', 'Văn Yên',
    '1996-12-20', 'Hà Nội', 'Phường Thanh Xuân',
    TRUE,
    'https://cdn.example.com/avatars/016.jpg',
    'https://cdn.example.com/backgrounds/016.jpg',
    'https://linkedin.com/in/caovanyen',
    '2024-12-01 09:00:00+07', 'v1.2',
    '001096012201', '2024-12-01 09:05:00+07', 'lv3', 'active',
    '2024-12-01 08:50:00+07', '2024-12-01 08:50:00+07'
),
(
    'a1a1a1a1-0017-0017-0017-a1a1a1a1a1b7',
    'luu.thi.zung@gmail.com', '0901234577',
    'Business Consultant',
    'Lưu', 'Thị Dung',
    '2001-04-16', 'Đà Nẵng', 'Phường Thanh Khê',
    FALSE,
    'https://cdn.example.com/avatars/017.jpg',
    NULL,
    'https://behance.net/luuthidung',
    '2024-12-10 10:00:00+07', 'v1.2',
    '048201004161', '2024-12-10 10:05:00+07', 'lv3', 'active',
    '2024-12-10 09:55:00+07', '2024-12-10 09:55:00+07'
),
(
    'a1a1a1a1-0018-0018-0018-a1a1a1a1a1b8',
    'ha.van.bach@gmail.com', '0901234578',
    'I am a qualified and professional web development with five years of
     experience in database administration and website design. Strong
     Bachelor of IT, Major in Technologycreative and analytical skills. Team player with an eye for detail',
    'Hà', 'Văn Bạch',
    '1995-09-09', 'Hà Nội', 'Phường Long Biên',
    TRUE,
    'https://cdn.example.com/avatars/018.jpg',
    'https://cdn.example.com/backgrounds/018.jpg',
    'https://github.com/havanbach',
    '2024-12-15 09:00:00+07', 'v1.2',
    '001095009091', '2024-12-15 09:05:00+07', 'lv3', 'active',
    '2024-12-15 08:50:00+07', '2025-01-05 10:00:00+07'
),
(
    'a1a1a1a1-0019-0019-0019-a1a1a1a1a1b9',
    'phan.thi.cam@gmail.com', '0901234579',
    'I am a Sales Representative is a professional who initializes and
     manages relationships with customers. They serve as their point of
     contact and lead from initial outreach through the making of the final
     purchase by them or someone in their household.',
    'Phan', 'Thị Cẩm',
    '2000-02-28', 'Cần Thơ', 'Phường Bình Thủy',
    FALSE,
    'https://cdn.example.com/avatars/019.jpg',
    NULL,
    NULL,
    '2025-01-05 08:00:00+07', 'v1.2',
    '092200002281', NULL, 'lv3', 'active',
    '2025-01-05 07:50:00+07', '2025-01-05 07:50:00+07'
),
(
    'a1a1a1a1-0020-0020-0020-a1a1a1a1a1c0',
    'kieu.van.dong@gmail.com', '0901234580',
    'Results-driven Marketing Area Manager with 7+ years of experience
     leading regional marketing operations, coordinating cross-functional
     sales teams, and executing strategic growth campaigns. Proven
     success in increasing market share, improving brand positioning, and
     optimizing marketing performance through data-driven decisions.
     Strong leadership and communication skills with a track record of
     driving sustainable growth across multiple territories.',
    'Kiều', 'Văn Đông',
    '1996-06-13', 'Hồ Chí Minh', 'Phường Bình Dương',
    TRUE,
    'https://cdn.example.com/avatars/020.jpg',
    'https://cdn.example.com/backgrounds/020.jpg',
    'https://github.com/kieuvandong',
    '2025-01-15 09:00:00+07', 'v1.2',
    '079196006131', '2025-01-15 09:05:00+07', 'lv3', 'active',
    '2025-01-15 08:55:00+07', '2025-01-15 08:55:00+07'
)
ON CONFLICT (candidate_id) DO NOTHING;
