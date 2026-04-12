-- Migration: company__update_logo_background_urls
-- Created: Fri Apr 11 2026
-- Description: Update company logo_url and background_url with real image URLs for display

UPDATE company SET logo_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/11/FPT_logo_2010.svg/1200px-FPT_logo_2010.svg.png'
WHERE company_name = 'FPT Software';

UPDATE company SET logo_url = 'https://cdn.haitrieu.com/wp-content/uploads/2022/01/Logo-VNG-Corporation.png'
WHERE company_name = 'VNG Corporation';

UPDATE company SET logo_url = 'https://cdn.haitrieu.com/wp-content/uploads/2022/01/Logo-Tiki.png'
WHERE company_name = 'Tiki Corporation';

UPDATE company SET logo_url = 'https://cdn.haitrieu.com/wp-content/uploads/2022/02/Logo-Vingroup.png'
WHERE company_name = 'VinAI Research';

UPDATE company SET logo_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/Microsoft_logo.svg/200px-Microsoft_logo.svg.png'
WHERE company_name = 'CMC Global';

UPDATE company SET logo_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Google_2015_logo.svg/200px-Google_2015_logo.svg.png'
WHERE company_name = 'Saigon Technology';

UPDATE company SET logo_url = 'https://cdn.haitrieu.com/wp-content/uploads/2022/01/Logo-KMS-Technology.png'
WHERE company_name = 'KMS Technology';

UPDATE company SET logo_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Amazon_logo.svg/200px-Amazon_logo.svg.png'
WHERE company_name = 'TMA Solutions';

UPDATE company SET logo_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Facebook_Logo_%282019%29.png/200px-Facebook_Logo_%282019%29.png'
WHERE company_name = 'Axon Active';

UPDATE company SET logo_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/6f/Logo_of_Twitter.svg/200px-Logo_of_Twitter.svg.png'
WHERE company_name = 'Rikkeisoft';
