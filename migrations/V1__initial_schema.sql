-- V1: Initial schema

DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS members;

CREATE TABLE IF NOT EXISTS books (
    id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    available TINYINT(1) DEFAULT 1
);

CREATE TABLE IF NOT EXISTS members (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id VARCHAR(50) NOT NULL,
    member_id VARCHAR(50) NOT NULL,
    loan_date DATETIME NOT NULL,
    FOREIGN KEY(book_id) REFERENCES books(id),
    FOREIGN KEY(member_id) REFERENCES members(id)
);

-- Insert initial data
INSERT INTO books (id, title, author, available) VALUES
('B001', 'Java Programming', 'John Smith', 1),
('B002', 'Database Design', 'Jane Doe', 1),
('B003', 'Web Development', 'Mike Johnson', 1);

INSERT INTO members (id, name, type) VALUES
('M001', 'Joko', 'Student'),
('M002', 'Susanti', 'Staff'),
('M003', 'Budi', 'Student');