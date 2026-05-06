-- Migration: company - add_logo
-- Created: Wed May  6 11:40:07 PM +07 2026
-- Author: hoangvuongbui

-- Add your SQL statements below:
UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778086707/companies/avatars/viettel_logo_wsel0i.jpg'
WHERE company_name = 'Viettel Group';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778086699/companies/avatars/momo_logo_xob4ch.jpg'
WHERE company_name = 'MoMo (M_Service)';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778086705/companies/avatars/shopee_logo_gdzael.jpg'
WHERE company_name = 'Shopee Vietnam';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778086709/companies/avatars/vnpay_logo_faq8mx.jpg'
WHERE company_name = 'VNPay';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778086682/companies/avatars/amanotes_logo_hpatqt.jpg'
WHERE company_name = 'Amanotes';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778086700/companies/avatars/nashtech_logo_rejtzs.jpg'
WHERE company_name = 'NashTech Vietnam';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778086682/companies/avatars/base_vn_logo_vyptjr.jpg'
WHERE company_name = 'Base.vn';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778086684/companies/avatars/coccoc_logo_i6bxtq.jpg'
WHERE company_name = 'Cốc Cốc';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778086682/companies/avatars/be_group_logo_bvtnot.jpg'
WHERE company_name = 'Be Group';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778086703/companies/avatars/sendo_logo_z3aqnr.jpg'
WHERE company_name = 'Sendo';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778086686/companies/avatars/gotit_logo_fawoon.jpg'
WHERE company_name = 'Got It';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778086702/companies/avatars/orient_software_logo_kpklfu.jpg'
WHERE company_name = 'Orient Software';
