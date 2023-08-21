DROP TABLE IF EXISTS campaigns;
DROP TABLE IF EXISTS vouchers;
DROP TYPE IF EXISTS status;

-- CREATE TABLE campaigns
CREATE TABLE campaigns
(
    id         SERIAL PRIMARY KEY,
    title      VARCHAR(255) NOT NULL UNIQUE,
    start_date DATE         NOT NULL,
    end_date   DATE CHECK (end_date > start_date)
);

-- CREATE TABLE status
CREATE TABLE status
(
    id   SERIAL PRIMARY KEY,
    status_name VARCHAR(255) NOT NULL UNIQUE
);


-- CREATE TABLE vouchers
CREATE TABLE vouchers
(
    id          SERIAL PRIMARY KEY,
    code        UUID   NOT NULL UNIQUE,
    campaign_id BIGINT NOT NULL,
    status_id   BIGINT NOT NULL,
    CONSTRAINT fk_campaign
        FOREIGN KEY (campaign_id)
            REFERENCES campaigns (id),
    CONSTRAINT fk_status
        FOREIGN KEY (status_id)
            REFERENCES status (id)
);