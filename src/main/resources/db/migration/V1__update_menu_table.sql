ALTER TABLE menu
    ADD COLUMN parent_id BIGINT NULL,
    ADD COLUMN path VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN icon VARCHAR(255) NULL,
    ADD COLUMN prioritize INT NOT NULL DEFAULT 0;
