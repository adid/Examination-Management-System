DROP DATABASE dbms;

CREATE DATABASE dbms;

CREATE TABLE
    Users (
        User_ID NUMERIC PRIMARY KEY,
        Password VARCHAR(100) NOT NULL,
        UserRole VARCHAR(20) CHECK (UserRole IN ('Admin', 'Student', 'Teacher'))
    );

CREATE TABLE
    Departments (
        Dept_ID VARCHAR(10) PRIMARY KEY,
        Dept_name VARCHAR(100) NOT NULL
    );

CREATE TABLE
    Students (
        Student_ID NUMERIC PRIMARY KEY,
        Name VARCHAR NOT NULL,
        Phone VARCHAR(20),
        Email VARCHAR(100) UNIQUE,
        Department VARCHAR(10) REFERENCES Departments (Dept_ID),
        Admission_year INTEGER
    );

CREATE TABLE
    Teachers (
        Teacher_ID NUMERIC PRIMARY KEY,
        Name VARCHAR(100),
        Phone VARCHAR(20),
        Email VARCHAR(100),
        Department VARCHAR(10) REFERENCES Departments (Dept_ID),
        Joining_year INTEGER
    );

CREATE TABLE
    Courses (
        Course_ID VARCHAR(10) PRIMARY KEY,
        Course_code NUMERIC(4),
        Course_title VARCHAR(50),
        Department VARCHAR(10) REFERENCES Departments (Dept_ID),
        Instructor_ID NUMERIC REFERENCES Teachers (Teacher_ID),
        credit INTEGER CHECK (
            credit > 0
            AND credit <= 6
        )
    );

CREATE TABLE
    Takes (
        Student_ID INTEGER REFERENCES Students (Student_ID),
        Course_ID VARCHAR(15) REFERENCES Courses (Course_ID),
        PRIMARY KEY (Student_ID, Course_ID)
    );

CREATE TABLE
    Exams (
        Exam_ID NUMERIC PRIMARY KEY,
        Course_ID VARCHAR(10) REFERENCES Courses (Course_ID),
        Academic_year VARCHAR(10) NOT NULL,
        Semester VARCHAR(10) CHECK (Semester IN ('Summer', 'Winter')),
        Exam_type VARCHAR(10) CHECK (
            Exam_type IN ('Quiz1', 'Quiz2', 'Quiz3', 'Mid', 'Final')
        ),
        Exam_date DATE NOT NULL,
        StartTime TIME NOT NULL,
        EndTime TIME NOT NULL CHECK (EndTime > StartTime)
    );

CREATE TABLE
    Marks (
        Exam_ID NUMERIC REFERENCES Exams (Exam_ID),
        Student_ID NUMERIC REFERENCES Students (Student_ID),
        Marks_obtained NUMERIC CHECK (Marks_obtained >= 0),
        PRIMARY KEY (Exam_ID, Student_ID)
    );

CREATE TABLE
    Result (
        Result_ID NUMERIC PRIMARY KEY,
        Student_ID INTEGER REFERENCES Students (Student_ID),
        Gpa Double
    );