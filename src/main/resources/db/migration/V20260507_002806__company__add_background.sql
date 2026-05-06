-- Migration: company - add_background
-- Created: Thu May  7 12:28:06 AM +07 2026
-- Author: hoangvuongbui

-- Add your SQL statements below:

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778089250/companies/covers/viettel_group_cover_rbwmmz.jpg'
WHERE company_name = 'Viettel Group';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778089236/companies/covers/momo_mservice_cover_ugqm8g.jpg'
WHERE company_name = 'MoMo (M_Service)';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778089247/companies/covers/shopee_cover_yglkop.jpg'
WHERE company_name = 'Shopee Vietnam';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778089252/companies/covers/vnpay_cover_vsgn1d.jpg'
WHERE company_name = 'VNPay';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778089224/companies/covers/amanotes_cover_hxf4lg.jpg'
WHERE company_name = 'Amanotes';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778089239/companies/covers/nashtech_global_cover_kljwxu.jpg'
WHERE company_name = 'NashTech Vietnam';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778089225/companies/covers/base_cover_vfowvc.jpg'
WHERE company_name = 'Base.vn';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778089231/companies/covers/coccoc_cover_a7lvof.jpg'
WHERE company_name = 'Cốc Cốc';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778089228/companies/covers/be_groupjsc_cover_c8gdb5.jpg'
WHERE company_name = 'Be Group';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778089244/companies/covers/sendovn_cover_ukqh9q.jpg'
WHERE company_name = 'Sendo';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778089233/companies/covers/gotit_ab_cover_mh5dli.jpg'
WHERE company_name = 'Got It';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778089241/companies/covers/orient_insurance_cover_ncjrgj.jpg'
WHERE company_name = 'Orient Software';
