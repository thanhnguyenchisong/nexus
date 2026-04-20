-- Seed AI sources
INSERT INTO source (name, url, feed_url, type, domain, language) VALUES
('OpenAI Blog',       'https://openai.com',          'https://openai.com/blog/rss.xml',                          'RSS', 'AI', 'en'),
('Google AI Blog',    'https://blog.google',         'https://blog.google/technology/ai/rss/',                    'RSS', 'AI', 'en'),
('DeepMind Blog',     'https://deepmind.google',     'https://deepmind.google/blog/rss.xml',                     'RSS', 'AI', 'en'),
('MIT Tech Review',   'https://technologyreview.com','https://www.technologyreview.com/feed/',                    'RSS', 'AI', 'en'),
('Hugging Face Blog', 'https://huggingface.co',      'https://huggingface.co/blog/feed.xml',                     'RSS', 'AI', 'en'),
('arXiv cs.AI',       'https://arxiv.org',           'http://arxiv.org/rss/cs.AI',                               'RSS', 'AI', 'en'),
('VentureBeat AI',    'https://venturebeat.com',     'https://venturebeat.com/category/ai/feed/',                'RSS', 'AI', 'en'),
('MarkTechPost',      'https://marktechpost.com',    'https://www.marktechpost.com/feed/',                       'RSS', 'AI', 'en'),
('The Verge AI',      'https://theverge.com',        'https://www.theverge.com/rss/ai-artificial-intelligence/index.xml','RSS','AI','en');

-- Seed Finance (International) sources
INSERT INTO source (name, url, feed_url, type, domain, language) VALUES
('Reuters Business',  'https://reuters.com',         'https://www.reuters.com/business/rss',                      'RSS', 'FINANCE', 'en'),
('MarketWatch',       'https://marketwatch.com',     'https://feeds.marketwatch.com/marketwatch/topstories/',      'RSS', 'FINANCE', 'en'),
('CNBC',              'https://cnbc.com',            'https://www.cnbc.com/id/100003114/device/rss/rss.html',      'RSS', 'FINANCE', 'en'),
('Seeking Alpha',     'https://seekingalpha.com',    'https://seekingalpha.com/market_currents.xml',               'RSS', 'FINANCE', 'en'),
('Investopedia',      'https://investopedia.com',    'https://www.investopedia.com/feedbuilder/feed/getfeed/?feedName=rss_articles','RSS','FINANCE','en'),
('CoinDesk',          'https://coindesk.com',        'https://www.coindesk.com/arc/outboundfeeds/rss/',            'RSS', 'FINANCE', 'en'),
('Yahoo Finance',     'https://finance.yahoo.com',   'https://finance.yahoo.com/news/rssindex',                    'RSS', 'FINANCE', 'en');

-- Seed Finance (Vietnamese) sources
INSERT INTO source (name, url, feed_url, type, domain, language) VALUES
('CafeF',             'https://cafef.vn',            'https://cafef.vn/rss/trang-chu.rss',                        'RSS', 'FINANCE', 'vi'),
('VnExpress Kinh Doanh','https://vnexpress.net',     'https://vnexpress.net/rss/kinh-doanh.rss',                  'RSS', 'FINANCE', 'vi'),
('VnExpress Tài Chính','https://vnexpress.net',      'https://vnexpress.net/rss/tai-chinh.rss',                   'RSS', 'FINANCE', 'vi'),
('VietStock',         'https://vietstock.vn',        'https://vietstock.vn/rss/tai-chinh.rss',                    'RSS', 'FINANCE', 'vi'),
('Thanh Niên Kinh Tế','https://thanhnien.vn',        'https://thanhnien.vn/rss/kinh-te.rss',                      'RSS', 'FINANCE', 'vi'),
('Tuổi Trẻ Kinh Doanh','https://tuoitre.vn',        'https://tuoitre.vn/rss/kinh-doanh.rss',                     'RSS', 'FINANCE', 'vi');
