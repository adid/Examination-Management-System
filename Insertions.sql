INSERT INTO
    Users (User_ID, Password, UserRole)
VALUES
    (1001, '1001', 'Admin');

INSERT INTO
    Users (User_ID, Password, UserRole)
VALUES
    (1002, '1002', 'Admin');

INSERT INTO
    Users (User_ID, Password, UserRole)
VALUES
    (1003, '1003', 'Admin');

-- Inserting Departments
INSERT INTO
    Departments (Dept_ID, Dept_name)
VALUES
    ('CSE', 'Computer Science and Engineering'),
    ('EEE', 'Electrical and Electronics Engineering'),
    ('ME', 'Mechanical Engineering');

-- Inserting Students
INSERT INTO
    Students (
        Student_ID,
        Name,
        Phone,
        Email,
        Department,
        Admission_year
    )
VALUES
    (
        210041101,
        'Adid',
        '01237843261',
        'adid.doe@example.com',
        'CSE',
        2021
    ),
    (
        210041102,
        'Dipto',
        '01234543261',
        'dipto.doe@example.com',
        'CSE',
        2021
    ),
    (
        210041103,
        'Ovi',
        '01237783261',
        'ovi.doe@example.com',
        'CSE',
        2021
    ),
    (
        210021101,
        'Alvi',
        '01223843261',
        'alvi.doe@example.com',
        'EEE',
        2021
    ),
    (
        210021102,
        'Mahin',
        '01298543261',
        'mahin.doe@example.com',
        'EEE',
        2021
    ),
    (
        210021103,
        'Nahiyan',
        '01237716261',
        'nahiyan.doe@example.com',
        'EEE',
        2021
    ),
    (
        210011101,
        'Mehedi',
        '01167843261',
        'mehedi.doe@example.com',
        'ME',
        2021
    ),
    (
        210011102,
        'Nihal',
        '01954543261',
        'nihal.doe@example.com',
        'ME',
        2021
    ),
    (
        210011103,
        'Kanon',
        '01027783261',
        'kanon.doe@example.com',
        'ME',
        2021
    );

-- Inserting Teachers
INSERT INTO
    Teachers (
        Teacher_ID,
        Name,
        Phone,
        Email,
        Department,
        Joining_year
    )
VALUES
    (
        151401,
        'Hasanul Kabir',
        '0172834783',
        'hasan.smith@example.com',
        'CSE',
        2015
    ),
    (
        101201,
        'Azam Khan',
        '017223783',
        'azam.johnson@example.com',
        'EEE',
        2010
    ),
    (
        181101,
        'Rafid Haque',
        '0172830461',
        'rafid.williams@example.com',
        'ME',
        2018
    );