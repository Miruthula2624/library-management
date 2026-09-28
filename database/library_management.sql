-- ===================================================================
-- College Library Management System
-- Database Schema Script & Sample Data
-- Database: library_management
-- ===================================================================

-- 1. Create and Use Database
CREATE DATABASE IF NOT EXISTS library_management;
USE library_management;

-- 2. Drop Tables if already existing (in reverse dependency order)
DROP TABLE IF EXISTS `returns`;
DROP TABLE IF EXISTS `issues`;
DROP TABLE IF EXISTS `students`;
DROP TABLE IF EXISTS `books`;

-- ===================================================================
-- 3. Table: books
-- ===================================================================
CREATE TABLE `books` (
    `book_id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(255) NOT NULL,
    `author` VARCHAR(255) NOT NULL,
    `publisher` VARCHAR(255) DEFAULT NULL,
    `category` VARCHAR(100) DEFAULT NULL,
    `isbn` VARCHAR(50) NOT NULL,
    `total_copies` INT NOT NULL,
    `available_copies` INT NOT NULL,
    PRIMARY KEY (`book_id`),
    UNIQUE KEY `uk_books_isbn` (`isbn`),
    CONSTRAINT `chk_total_copies_non_negative` CHECK (`total_copies` >= 0),
    CONSTRAINT `chk_available_copies_non_negative` CHECK (`available_copies` >= 0),
    CONSTRAINT `chk_available_le_total` CHECK (`available_copies` <= `total_copies`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
-- 4. Table: students
-- ===================================================================
CREATE TABLE `students` (
    `student_id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `department` VARCHAR(100) NOT NULL,
    `year` VARCHAR(50) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `phone` VARCHAR(30) DEFAULT NULL,
    PRIMARY KEY (`student_id`),
    UNIQUE KEY `uk_students_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
-- 5. Table: issues
-- ===================================================================
CREATE TABLE `issues` (
    `issue_id` BIGINT NOT NULL AUTO_INCREMENT,
    `book_id` BIGINT NOT NULL,
    `student_id` BIGINT NOT NULL,
    `issue_date` DATE NOT NULL,
    `due_date` DATE NOT NULL,
    PRIMARY KEY (`issue_id`),
    KEY `idx_issues_book` (`book_id`),
    KEY `idx_issues_student` (`student_id`),
    CONSTRAINT `fk_issues_book` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_issues_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
-- 6. Table: returns
-- ===================================================================
CREATE TABLE `returns` (
    `return_id` BIGINT NOT NULL AUTO_INCREMENT,
    `issue_id` BIGINT NOT NULL,
    `return_date` DATE NOT NULL,
    `fine` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (`return_id`),
    UNIQUE KEY `uk_returns_issue` (`issue_id`),
    CONSTRAINT `fk_returns_issue` FOREIGN KEY (`issue_id`) REFERENCES `issues` (`issue_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
-- 7. Sample Data: books (12 Books across various disciplines)
-- ===================================================================
INSERT INTO `books` (`book_id`, `title`, `author`, `publisher`, `category`, `isbn`, `total_copies`, `available_copies`) VALUES
(1, 'Introduction to Algorithms', 'Thomas H. Cormen', 'MIT Press', 'Computer Science', '978-0262033848', 5, 4),
(2, 'Clean Code: A Handbook of Agile Software Craftsmanship', 'Robert C. Martin', 'Prentice Hall', 'Programming', '978-0132350884', 4, 3),
(3, 'Database System Concepts', 'Abraham Silberschatz', 'McGraw-Hill', 'Database', '978-0073523323', 6, 5),
(4, 'Artificial Intelligence: A Modern Approach', 'Stuart Russell', 'Pearson', 'Artificial Intelligence', '978-0136042594', 3, 2),
(5, 'Data Structures and Algorithms in Java', 'Robert Lafore', 'Sams Publishing', 'Data Structures', '978-0672324536', 4, 4),
(6, 'Operating System Concepts', 'Abraham Silberschatz', 'Wiley', 'Computer Science', '978-1118063330', 5, 5),
(7, 'Discrete Mathematics and Its Applications', 'Kenneth Rosen', 'McGraw-Hill', 'Mathematics', '978-0073383095', 4, 3),
(8, 'University Physics with Modern Physics', 'Hugh Young', 'Pearson', 'Physics', '978-0321973610', 3, 2),
(9, 'Computer Networks', 'Andrew Tanenbaum', 'Pearson', 'Computer Science', '978-0132126953', 4, 4),
(10, 'To Kill a Mockingbird', 'Harper Lee', 'J.B. Lippincott & Co.', 'Literature', '978-0060935467', 3, 3),
(11, 'Design Patterns: Elements of Reusable Object-Oriented Software', 'Erich Gamma', 'Addison-Wesley', 'Programming', '978-0201633610', 5, 5),
(12, 'Deep Learning', 'Ian Goodfellow', 'MIT Press', 'Artificial Intelligence', '978-0262035613', 4, 4);

-- ===================================================================
-- 8. Sample Data: students (10 Students across diverse departments)
-- ===================================================================
INSERT INTO `students` (`student_id`, `name`, `department`, `year`, `email`, `phone`) VALUES
(1, 'Aarav Sharma', 'Computer Science', '3rd Year', 'aarav.sharma@college.edu', '9876543210'),
(2, 'Priya Patel', 'AI & Data Science', '2nd Year', 'priya.patel@college.edu', '9876543211'),
(3, 'Karthik Raja', 'Information Technology', '4th Year', 'karthik.raja@college.edu', '9876543212'),
(4, 'Sneha Reddy', 'Computer Science', '1st Year', 'sneha.reddy@college.edu', '9876543213'),
(5, 'Rohan Verma', 'Mathematics', '3rd Year', 'rohan.verma@college.edu', '9876543214'),
(6, 'Ananya Iyer', 'Physics', '2nd Year', 'ananya.iyer@college.edu', '9876543215'),
(7, 'Vikram Sundaram', 'Commerce', '3rd Year', 'vikram.s@college.edu', '9876543216'),
(8, 'Divya Nair', 'AI & Data Science', '4th Year', 'divya.nair@college.edu', '9876543217'),
(9, 'Mohammad Farooq', 'Information Technology', '2nd Year', 'm.farooq@college.edu', '9876543218'),
(10, 'Kavya Murugan', 'Computer Science', '4th Year', 'kavya.m@college.edu', '9876543219');

-- ===================================================================
-- 9. Sample Data: issues (Past returned, currently active, and overdue)
-- ===================================================================
-- Issue 1: Returned on time (Book 1, Student 1)
INSERT INTO `issues` (`issue_id`, `book_id`, `student_id`, `issue_date`, `due_date`) VALUES
(1, 1, 1, '2026-09-01', '2026-09-15');

-- Issue 2: Returned late with fine (Book 5, Student 2)
INSERT INTO `issues` (`issue_id`, `book_id`, `student_id`, `issue_date`, `due_date`) VALUES
(2, 5, 2, '2026-08-10', '2026-08-24');

-- Issue 3: Returned on time (Book 10, Student 7)
INSERT INTO `issues` (`issue_id`, `book_id`, `student_id`, `issue_date`, `due_date`) VALUES
(3, 10, 7, '2026-08-01', '2026-08-15');

-- Issue 4: Currently Issued (Book 1, Student 3) - Due: 2026-09-29 (Active)
INSERT INTO `issues` (`issue_id`, `book_id`, `student_id`, `issue_date`, `due_date`) VALUES
(4, 1, 3, '2026-09-15', '2026-09-29');

-- Issue 5: Currently Issued (Book 2, Student 4) - Due: 2026-09-30 (Active)
INSERT INTO `issues` (`issue_id`, `book_id`, `student_id`, `issue_date`, `due_date`) VALUES
(5, 2, 4, '2026-09-16', '2026-09-30');

-- Issue 6: Currently Issued (Book 3, Student 5) - Due: 2026-10-02 (Active)
INSERT INTO `issues` (`issue_id`, `book_id`, `student_id`, `issue_date`, `due_date`) VALUES
(6, 3, 5, '2026-09-18', '2026-10-02');

-- Issue 7: Currently Issued (Book 4, Student 8) - Due: 2026-10-01 (Active)
INSERT INTO `issues` (`issue_id`, `book_id`, `student_id`, `issue_date`, `due_date`) VALUES
(7, 4, 8, '2026-09-17', '2026-10-01');

-- Issue 8: Currently Issued & OVERDUE (Book 7, Student 9) - Due: 2026-09-03
INSERT INTO `issues` (`issue_id`, `book_id`, `student_id`, `issue_date`, `due_date`) VALUES
(8, 7, 9, '2026-08-20', '2026-09-03');

-- Issue 9: Currently Issued & OVERDUE (Book 8, Student 6) - Due: 2026-09-08
INSERT INTO `issues` (`issue_id`, `book_id`, `student_id`, `issue_date`, `due_date`) VALUES
(9, 8, 6, '2026-08-25', '2026-09-08');

-- ===================================================================
-- 10. Sample Data: returns (Corresponding to returned issues 1, 2, 3)
-- ===================================================================
-- Return for Issue 1 (Returned on 2026-09-14, before due date 2026-09-15 => Fine: 0.00)
INSERT INTO `returns` (`return_id`, `issue_id`, `return_date`, `fine`) VALUES
(1, 1, '2026-09-14', 0.00);

-- Return for Issue 2 (Returned on 2026-08-27, 3 days after due date 2026-08-24 => 3 * 5 = Fine: 15.00)
INSERT INTO `returns` (`return_id`, `issue_id`, `return_date`, `fine`) VALUES
(2, 2, '2026-08-27', 15.00);

-- Return for Issue 3 (Returned on 2026-08-15, on due date 2026-08-15 => Fine: 0.00)
INSERT INTO `returns` (`return_id`, `issue_id`, `return_date`, `fine`) VALUES
(3, 3, '2026-08-15', 0.00);
