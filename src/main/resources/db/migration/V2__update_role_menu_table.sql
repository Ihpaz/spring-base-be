ALTER TABLE role_menu
ADD COLUMN uuid VARCHAR(255) NOT NULL,
ADD COLUMN is_created BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN is_updated BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN is_show BOOLEAN NOT NULL DEFAULT FALSE;
