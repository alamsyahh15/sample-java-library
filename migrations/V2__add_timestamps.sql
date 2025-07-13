-- V2: Add timestamp columns for auditing

ALTER TABLE books ADD created_at DATETIME DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE books ADD updated_at DATETIME;

ALTER TABLE members ADD created_at DATETIME DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE members ADD updated_at DATETIME;

ALTER TABLE transactions ADD created_at DATETIME DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE transactions ADD updated_at DATETIME;