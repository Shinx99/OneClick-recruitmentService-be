-- Migration: company__update_logo_background_urls
-- Created: Fri Apr 11 2026
-- Description: Update company logo_url and background_url with real image URLs for display

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776053365/companies/avatars/fpt_software_logo_ibo1d7.jpg'
WHERE company_name = 'FPT Software';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776053530/companies/avatars/vng_group_logo_fvdsli.jpg'
WHERE company_name = 'VNG Corporation';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776052464/companies/avatars/1692104431985_uik5mj.jpg'
WHERE company_name = 'Tiki Corporation';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776053487/companies/avatars/vinai_research_logo_c0d6qz.jpg'
WHERE company_name = 'VinAI Research';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776053319/companies/avatars/cmc_global_company_limited_logo_ghl4pb.jpg'
WHERE company_name = 'CMC Global';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776052512/companies/avatars/1773284290624_kgwmgs.jpg'
WHERE company_name = 'Saigon Technology';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776053403/companies/avatars/kms_technology_logo_nuystc.jpg'
WHERE company_name = 'KMS Technology';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776052420/companies/avatars/1631382132338_jk7uze.jpg'
WHERE company_name = 'TMA Solutions';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776052550/axon_protect_life_logo_tpcm2q.jpg'
WHERE company_name = 'Axon Active';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776053447/companies/avatars/rikkeisoft_logo_ytp2tt.jpg'
WHERE company_name = 'Rikkeisoft';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1776063162/companies/avatars/elsa_corp_logo_tcv10z.jpg'
WHERE company_name = 'ELSA Corp';

UPDATE company SET logo_url = 'https://res.cloudinary.com/dhiz9hdeq/image/upload/v1778088186/companies/avatars/ghtk_logo_wuwjtf.jpg'
WHERE company_name = 'Giao Hàng Tiết Kiệm';
