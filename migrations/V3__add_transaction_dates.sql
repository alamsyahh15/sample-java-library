-- V3: Add transaction date columns

ALTER TABLE transactions
ADD COLUMN loan_date DATETIME NOT NULL AFTER transaction_date,
ADD COLUMN due_date DATETIME NOT NULL AFTER loan_date,
ADD COLUMN return_date DATETIME NULL AFTER due_date;

-- Migrate existing transaction_date data to loan_date
UPDATE transactions SET loan_date = transaction_date;

-- Set initial due_dates (14 days from loan_date as default)
UPDATE transactions SET due_date = DATE_ADD(loan_date, INTERVAL 14 DAY);

-- Drop the old transaction_date column
ALTER TABLE transactions DROP COLUMN transaction_date;