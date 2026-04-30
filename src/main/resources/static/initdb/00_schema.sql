CREATE TABLE IF NOT EXISTS sectors (
    id CHAR(36) NOT NULL,
    sector_name VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS industries (
    id CHAR(36) NOT NULL,
    industries_name VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS stocks (
    id CHAR(36) NOT NULL,
    stock_name VARCHAR(100) NOT NULL,
    stock_name_kr VARCHAR(100) NOT NULL,
    stock_symbol VARCHAR(10) NOT NULL,
    stock_cik VARCHAR(20),
    stock_excd VARCHAR(20) NOT NULL,
    stock_rank INT NOT NULL,
    stock_logo_img VARCHAR(255),
    sector_id CHAR(36) NOT NULL,
    industry_id CHAR(36) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (stock_symbol),
    CONSTRAINT fk_stock_sector FOREIGN KEY (sector_id) REFERENCES sectors (id),
    CONSTRAINT fk_stock_industry FOREIGN KEY (industry_id) REFERENCES industries (id)
);
