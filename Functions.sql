-- Function to add user from teacher
CREATE OR REPLACE FUNCTION insert_user_from_teacher() 
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO Users (User_ID, Password, UserRole)
    VALUES (NEW.Teacher_ID, NEW.Teacher_ID::VARCHAR, 'Teacher');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger for teacher insert
CREATE TRIGGER after_teacher_insert
AFTER INSERT ON Teachers
FOR EACH ROW
EXECUTE FUNCTION insert_user_from_teacher();

-- Function to add user from student
CREATE OR REPLACE FUNCTION insert_user_from_student() 
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO Users (User_ID, Password, UserRole)
    VALUES (NEW.Student_ID, NEW.Student_ID::VARCHAR, 'Student');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger for student insert
CREATE TRIGGER after_student_insert
AFTER INSERT ON Students
FOR EACH ROW
EXECUTE FUNCTION insert_user_from_student();


-- Create function to generate Course_ID
CREATE OR REPLACE FUNCTION generate_course_id(dept_id VARCHAR, course_code NUMERIC) RETURNS VARCHAR AS $$
BEGIN
    RETURN dept_id || ' ' || LPAD(course_code::VARCHAR, 4, '0');
END;
$$ LANGUAGE plpgsql;

-- Create trigger function to assign Course_ID
CREATE OR REPLACE FUNCTION assign_course_id() RETURNS TRIGGER AS $$
BEGIN
    NEW.Course_ID := generate_course_id(NEW.Department, NEW.Course_code);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Ensure the generate_exam_id function returns a numeric value
CREATE OR REPLACE FUNCTION generate_exam_id(academic_year VARCHAR, course_code NUMERIC, exam_type VARCHAR) RETURNS NUMERIC AS $$
DECLARE
    exam_id NUMERIC;
BEGIN
    exam_id := CAST(SUBSTRING(academic_year, 1, 4) || LPAD(course_code::VARCHAR, 4, '0') || 
                (CASE exam_type
                    WHEN 'Quiz1' THEN '11'
                    WHEN 'Quiz2' THEN '12'
                    WHEN 'Quiz3' THEN '13'
                    WHEN 'Mid' THEN '30'
                    WHEN 'Final' THEN '50'
                    ELSE '00'
                END) AS NUMERIC);
    RETURN exam_id;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION assign_exam_id() RETURNS TRIGGER AS $$
BEGIN
    NEW.Exam_ID := generate_exam_id(NEW.Academic_year, (SELECT Course_code FROM Courses WHERE Course_ID = NEW.Course_ID), NEW.Exam_type);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER assign_exam_id_trigger
BEFORE INSERT ON Exams
FOR EACH ROW
EXECUTE FUNCTION assign_exam_id();


CREATE OR REPLACE FUNCTION validate_marks(p_exam_id numeric, p_student_id numeric, p_marks_obtained numeric)
RETURNS BOOLEAN AS $$
DECLARE
    max_marks numeric;
BEGIN
    -- Fetch the maximum allowed marks for the exam
    SELECT CASE 
        WHEN e.Exam_type IN ('Quiz1', 'Quiz2', 'Quiz3') THEN 5 * c.credit
        WHEN e.Exam_type = 'Mid' THEN 25 * c.credit
        WHEN e.Exam_type = 'Final' THEN 60 * c.credit
        ELSE 0
    END INTO max_marks
    FROM Exams e
    JOIN Courses c ON e.Course_ID = c.Course_ID
    WHERE e.Exam_ID = p_exam_id; -- Use parameter alias

    -- Check if the marks obtained exceed the maximum allowed marks
    IF p_marks_obtained > max_marks THEN
        RAISE EXCEPTION 'Marks obtained exceed the maximum allowed marks: %', max_marks;
    END IF;

    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION trigger_validate_marks()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM validate_marks(NEW.Exam_ID, NEW.Student_ID, NEW.Marks_obtained);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Drop the existing trigger if it exists
DROP TRIGGER IF EXISTS validate_marks_trigger ON Marks;

-- Create a new trigger
CREATE TRIGGER validate_marks_trigger
BEFORE INSERT OR UPDATE ON Marks
FOR EACH ROW EXECUTE FUNCTION trigger_validate_marks();


-- Function to get student Marks
CREATE OR REPLACE FUNCTION get_student_marks(student_id NUMERIC)
RETURNS TABLE (
    course_id VARCHAR,
    course_title VARCHAR,
    quiz1 NUMERIC,
    quiz2 NUMERIC,
    quiz3 NUMERIC,
    mid NUMERIC,
    final NUMERIC
) AS $$
DECLARE
    course_rec RECORD;
    quiz1_marks NUMERIC;
    quiz2_marks NUMERIC;
    quiz3_marks NUMERIC;
    mid_marks NUMERIC;
    final_marks NUMERIC;
BEGIN
    FOR course_rec IN
        SELECT c.course_id, c.course_title
        FROM courses c
        JOIN takes t ON c.course_id = t.course_id
        WHERE t.student_id = student_id
    LOOP
        -- Initialize marks variables to 0
        quiz1_marks := 0;
        quiz2_marks := 0;
        quiz3_marks := 0;
        mid_marks := 0;
        final_marks := 0;

        -- Retrieve marks for each exam type
        SELECT COALESCE(m.marks_obtained, 0) INTO quiz1_marks
        FROM marks m
        JOIN exams e ON m.exam_id = e.exam_id
        WHERE m.student_id = student_id AND e.course_id = course_rec.course_id AND e.exam_type = 'Quiz1';

        SELECT COALESCE(m.marks_obtained, 0) INTO quiz2_marks
        FROM marks m
        JOIN exams e ON m.exam_id = e.exam_id
        WHERE m.student_id = student_id AND e.course_id = course_rec.course_id AND e.exam_type = 'Quiz2';

        SELECT COALESCE(m.marks_obtained, 0) INTO quiz3_marks
        FROM marks m
        JOIN exams e ON m.exam_id = e.exam_id
        WHERE m.student_id = student_id AND e.course_id = course_rec.course_id AND e.exam_type = 'Quiz3';

        SELECT COALESCE(m.marks_obtained, 0) INTO mid_marks
        FROM marks m
        JOIN exams e ON m.exam_id = e.exam_id
        WHERE m.student_id = student_id AND e.course_id = course_rec.course_id AND e.exam_type = 'Mid';

        SELECT COALESCE(m.marks_obtained, 0) INTO final_marks
        FROM marks m
        JOIN exams e ON m.exam_id = e.exam_id
        WHERE m.student_id = student_id AND e.course_id = course_rec.course_id AND e.exam_type = 'Final';

        RETURN QUERY SELECT
            course_rec.course_id,
            course_rec.course_title,
            quiz1_marks,
            quiz2_marks,
            quiz3_marks,
            mid_marks,
            final_marks;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

