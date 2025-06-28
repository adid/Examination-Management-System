# Examination Management System

## Overview
This is a Java-based Examination Management System using PostgreSQL for database management. Core functionalities include course registration, exam scheduling, marks entry, result calculation, and viewing exam schedules or grades. This project mainly focuses on the implementation of SQL and PL/SQL in Applications.

---

## Features

### For Students
- View registered courses
- Register for new courses (with credit limits and seat availability)
- View exam schedules
- View detailed marks for quizzes, midterms, and finals
- View overall results and calculate CGPA

### For Teachers
- View assigned courses and enrolled students
- Schedule exams with specific types and timings
- Evaluate and enter marks for exams

### User Authentication
- Role-based login system (Students, Teachers, Admin)

---

## Technologies Used

- Java (JDK 11+)
- PostgreSQL
- JDBC for database connectivity
- SQL stored procedures and functions for marks retrieval and CGPA calculation

---

## Project Structure

- `StudentCourseService`: Manages course registration and viewing for students
- `StudentService`: Manages exam schedules, marks, and result display for students
- `TeacherService`: Handles course assignments, exam scheduling, and marks evaluation for teachers
- `UserLoginService`: Validates user login credentials and role

---

## Prerequisites

- Java JDK 11 or higher installed
- PostgreSQL installed and running
- Database schema and necessary tables created (refer to provided SQL scripts if any)
- JDBC driver for PostgreSQL (`postgresql-<version>.jar`) included in the classpath

---

## Setup and Running

1. Clone the repository:
   ```bash
   git clone https://github.com/adid/Examination-Management-System.git
   cd Examination-Management-System
2. Configure PostgreSQL database connection parameters (url, user, password) in service classes or ideally in a configuration file.

3. Compile Java classes:
   ```bash
   javac *.java

4. Run your main program:
   ```bash
   java Main

## Future Enhancements
- Implement a GUI frontend for better user interaction
- Use password hashing for secure authentication
- Introduce admin modules for managing users and courses
- Use ORM frameworks such as Hibernate for database abstraction

## License
This project is licensed under the MIT License - see the LICENSE file for details.

## Contact
For any queries or contributions, please open an issue or contact:

Adid Al Mahamud Shazid

Email: adidalmahamud@iut-dhaka.edu
