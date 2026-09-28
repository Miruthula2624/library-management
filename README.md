# College Library Management System

A full-stack, web-based College Library Management System designed for academic institutions to manage book inventories, student registrations, borrowing transactions, return workflows with automatic overdue fine calculation, and live dashboard analytics.

---

## Project Description

The **College Library Management System** is developed as a complete academic project (e.g., for Naan Mudhalvan submission). It replaces traditional manual register management with a robust, relational database-backed system. Librarians can track books in real time, monitor student loans, compute overdue penalties dynamically, and maintain complete transactional safety.

---

## Features

- **Dashboard Analytics**: Real-time KPI cards displaying Total Books, Registered Students, Available Copies, Currently Issued Books, Overdue Books, and Total Collected Fines.
- **Book Inventory Management**: Full CRUD (Add, View, Update, Delete) with multi-criteria search (by Title, Author, ISBN, Category).
- **Stock Tracking**: Dynamic availability status (`Available` vs `Not Available`) with atomic copy decrement/increment protection.
- **Student Management**: Student records management with department, college year, phone, and unique email validation.
- **Issue Book Transaction**: Safe issue transaction checking student eligibility, book existence, and non-zero available copy count.
- **Return Book Workflow**: Return processing with automated overdue fine calculation (₹5 per overdue day beyond the due date).
- **Overdue Tracking**: Dynamic identification of unreturned books past their due date with accumulated fine calculation.
- **Modern Responsive UI**: Clean sidebar navigation, Bootstrap 5 styling, mobile responsiveness, and intuitive alerts.

---

## Technologies Used

- **Frontend**: HTML5, CSS3, Vanilla JavaScript (ES6+), Bootstrap 5.3, Bootstrap Icons
- **Backend**: Java (JDK 21/26), Spring Boot 3.3.4, Spring Data JPA, Hibernate, REST APIs
- **Database**: MySQL Server 8.0
- **Build Tool**: Apache Maven 3.9.6

---

## System Requirements

- **Operating System**: Windows 10/11, Linux, or macOS
- **Java Development Kit (JDK)**: JDK 17, 21 (LTS recommended), or 26
- **Build Tool**: Apache Maven 3.8+
- **Database**: MySQL Server 8.0+
- **Web Browser**: Google Chrome, Mozilla Firefox, Microsoft Edge, or any modern browser

---

## Installation

1. Clone or extract the project into your local workspace directory:
   ```bash
   d:\Library database\
   ```

2. Verify that Java and Maven are available:
   ```bash
   java -version
   mvn -version
   ```

---

## MySQL Setup

1. Ensure MySQL Server is running on your machine on port `3306`.
2. Log in to MySQL Workbench or MySQL CLI:
   ```bash
   mysql -u root -p
   ```
3. Open and execute the provided SQL script:
   ```bash
   database/library_management.sql
   ```
4. This will create the `library_management` database with tables and sample academic data.

---

## Backend Setup

1. Open the backend configuration file:
   ```text
   backend/src/main/resources/application.properties
   ```
2. Locate the database password setting and replace `YOUR_PASSWORD` with your actual MySQL `root` password:
   ```properties
   spring.datasource.password=YOUR_ACTUAL_PASSWORD
   ```
3. Navigate to the `backend` folder and compile the application:
   ```bash
   cd backend
   mvn clean compile
   ```

---

## Frontend Setup

The frontend consists of static HTML5, CSS3, and modern Vanilla JavaScript files located in the `frontend/` directory.

No Node.js or external package installation is required. You can serve the frontend using any local static web server, such as:
- VS Code Live Server extension (port `5500` - `http://127.0.0.1:5500/frontend/index.html`)
- Python HTTP server:
  ```bash
  cd frontend
  python -m http.server 5500
  ```
- Or open `dashboard.html` in your browser.

---

## How to Run

### Step 1: Start MySQL Server
Ensure MySQL service is active.

### Step 2: Start Spring Boot Backend
From the `backend/` directory, run:
```bash
mvn spring-boot:run
```
The backend REST APIs will start on `http://localhost:8080`.

### Step 3: Launch Frontend
Open `frontend/index.html` via Live Server or browser:
```text
http://127.0.0.1:5500/frontend/dashboard.html
```

---

## API Endpoints

### Books
- `GET /api/books` - Retrieve all books (or search with query params)
- `GET /api/books/{id}` - Retrieve a single book by ID
- `POST /api/books` - Add a new book
- `PUT /api/books/{id}` - Update book details
- `DELETE /api/books/{id}` - Remove a book

### Students
- `GET /api/students` - Retrieve all students
- `GET /api/students/{id}` - Retrieve student by ID
- `POST /api/students` - Register a student
- `PUT /api/students/{id}` - Update student record
- `DELETE /api/students/{id}` - Delete student record

### Issues
- `GET /api/issues` - List all book issues
- `POST /api/issues` - Issue a book to a student

### Returns
- `GET /api/returns` - List all returned book transactions
- `POST /api/returns` - Record a book return with fine calculation

### Overdue
- `GET /api/overdue` - List currently overdue, unreturned books

### Dashboard
- `GET /api/dashboard` - Get aggregated library metrics

---

## Database Structure

The MySQL database `library_management` consists of:

- **`books`**: `book_id` (PK), `title`, `author`, `publisher`, `category`, `isbn` (UNIQUE), `total_copies`, `available_copies`
- **`students`**: `student_id` (PK), `name`, `department`, `year`, `email`, `phone`
- **`issues`**: `issue_id` (PK), `book_id` (FK), `student_id` (FK), `issue_date`, `due_date`
- **`returns`**: `return_id` (PK), `issue_id` (FK, UNIQUE), `return_date`, `fine`

---

## Project Structure

```text
College-Library-Management-System/
│
├── backend/
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/
│           │       └── library/
│           │           └── management/
│           │               ├── config/
│           │               │   └── CorsConfig.java
│           │               └── CollegeLibraryManagementApplication.java
│           └── resources/
│               └── application.properties
│
├── frontend/
│   ├── index.html
│   ├── dashboard.html
│   ├── books.html
│   ├── students.html
│   ├── issue-book.html
│   ├── returns.html
│   ├── overdue.html
│   ├── css/
│   │   └── style.css
│   └── js/
│       ├── common.js
│       ├── dashboard.js
│       ├── books.js
│       ├── students.js
│       ├── issues.js
│       ├── returns.js
│       └── overdue.js
│
├── database/
│   └── library_management.sql
│
└── README.md
```

---

## Testing

1. **Backend Compilation**:
   ```bash
   mvn clean compile
   ```
2. **REST API Testing**:
   Use Postman, curl, or browser network inspector to verify endpoints against `http://localhost:8080/api/*`.
3. **Transaction Edge Cases**:
   - Zero available copies prevents checkout with a descriptive message.
   - Duplicate return of an already returned issue is rejected.
   - Fine computed as ₹5 × overdue days if return date is greater than due date.

---

## Future Enhancements

- Email notification reminders to students 2 days prior to due date.
- Barcode / QR-code scanning for rapid book checkout.
- Role-based authentication (Admin, Librarian, Student portal).
- Digital PDF / E-book previews.
