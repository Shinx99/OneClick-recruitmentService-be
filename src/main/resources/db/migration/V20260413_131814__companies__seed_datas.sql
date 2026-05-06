-- Migration: companies - seed_datas
-- Created: Mon Apr 13 01:18:14 PM +07 2026
-- Author: hoangvuongbui

-- Add your SQL statements below:
-- Migration: company__update_logo_background_urls
-- Created: Fri Apr 11 2026
-- Description: Update company logo_url and background_url with real image URLs for display

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778090574/companies/covers/fpt_software_cover_jiziit.jpg'
WHERE company_name = 'FPT Software';

UPDATE company SET background_url= 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776062108/companies/covers/vng_corporation_cover_yk6ecy.jpg'
WHERE company_name = 'VNG Corporation';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776061989/companies/covers/tiki_vn_cover_nr6yts.jpg'
WHERE company_name = 'Tiki Corporation';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776062067/companies/covers/vinai_research_cover_a7rnrz.jpg'
WHERE company_name = 'VinAI Research';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776061817/companies/covers/cmc_global_company_limited_cover_cxjaor.jpg'
WHERE company_name = 'CMC Global';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776061935/companies/covers/saigon_technology_cover_jfvszv.jpg'
WHERE company_name = 'Saigon Technology';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776061900/companies/covers/kms_technology_cover_qfd7qo.jpg'
WHERE company_name = 'KMS Technology';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776062029/companies/covers/tma_solutions_cover_a4cnsq.jpg'
WHERE company_name = 'TMA Solutions';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776061777/companies/covers/axon_protect_life_cover_qeuaxa.jpg'
WHERE company_name = 'Axon Active';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776062405/companies/covers/rikkeisoft_cover_ugiz98.jpg'
WHERE company_name = 'Rikkeisoft';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776063209/companies/covers/elsa_corp_cover_mzlfav.jpg'
WHERE company_name = 'ELSA Corp';

UPDATE company SET background_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776063075/companies/covers/Untitled_xf8qev.png'
WHERE company_name = 'Giao Hàng Tiết Kiệm';

