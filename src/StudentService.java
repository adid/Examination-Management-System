import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StudentService {
    private final String url = "jdbc:postgresql://localhost:5432/dbms";
    private final String user = "postgres";
    private final String password = "pgadmin";
    private final Scanner scanner = new Scanner(System.in);

    public void viewExamSchedules(int studentId) {
        List<ExamSchedule> examSchedules = fetchExamSchedules(studentId);

        System.out.println("Exam Schedules:");
        for (ExamSchedule schedule : examSchedules) {
            System.out.println("Exam ID: " + schedule.getExamId());
            System.out.println("Course ID: " + schedule.getCourseId());
            System.out.println("Course Title: " + schedule.getCourseTitle());
            System.out.println("Academic Year: " + schedule.getAcademicYear());
            System.out.println("Semester: " + schedule.getSemester());
            System.out.println("Exam Type: " + schedule.getExamType());
            System.out.println("Exam Date: " + schedule.getExamDate());
            System.out.println("Start Time: " + schedule.getStartTime());
            System.out.println("End Time: " + schedule.getEndTime());
            System.out.println("----------------------------");
        }
        waitForUserInput();
    }

    private List<ExamSchedule> fetchExamSchedules(int studentId) {
        List<ExamSchedule> examSchedules = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT e.Exam_ID, c.Course_ID, c.Course_title, e.Academic_year, e.Semester, e.Exam_type, e.Exam_date, e.StartTime, e.EndTime " +
                    "FROM Exams e " +
                    "JOIN Takes t ON e.Course_ID = t.Course_ID " +
                    "JOIN Courses c ON e.Course_ID = c.Course_ID " +
                    "WHERE t.Student_ID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, studentId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String examId = resultSet.getString("Exam_ID");
                    String courseId = resultSet.getString("Course_ID");
                    String courseTitle = resultSet.getString("Course_title");
                    String academicYear = resultSet.getString("Academic_year");
                    String semester = resultSet.getString("Semester");
                    String examType = resultSet.getString("Exam_type");
                    Date examDate = resultSet.getDate("Exam_date");
                    Time startTime = resultSet.getTime("StartTime");
                    Time endTime = resultSet.getTime("EndTime");
                    examSchedules.add(new ExamSchedule(examId, courseId, courseTitle, academicYear, semester, examType, examDate, startTime, endTime));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return examSchedules;
    }

    public void displayStudentMarks(int studentId) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT * FROM get_student_marks(?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, studentId);
                ResultSet resultSet = statement.executeQuery();

                System.out.println("Student Id: " + studentId);
                System.out.println("Course Id | Course Title | Quiz 1 | Quiz 2 | Quiz 3 | Mid | Final");
                System.out.println("---------------------------------------------------------------");

                while (resultSet.next()) {
                    String courseId = resultSet.getString("course_id");
                    String courseTitle = resultSet.getString("course_title");
                    Float quiz1 = resultSet.getObject("quiz1") != null ? resultSet.getFloat("quiz1") : null;
                    Float quiz2 = resultSet.getObject("quiz2") != null ? resultSet.getFloat("quiz2") : null;
                    Float quiz3 = resultSet.getObject("quiz3") != null ? resultSet.getFloat("quiz3") : null;
                    Float mid = resultSet.getObject("mid") != null ? resultSet.getFloat("mid") : null;
                    Float finalMarks = resultSet.getObject("final") != null ? resultSet.getFloat("final") : null;

                    System.out.printf("%-10s | %-12s | %-6s | %-6s | %-6s | %-4s | %-5s%n",
                            courseId, courseTitle,
                            quiz1 != null ? quiz1.toString() : "null",
                            quiz2 != null ? quiz2.toString() : "null",
                            quiz3 != null ? quiz3.toString() : "null",
                            mid != null ? mid.toString() : "null",
                            finalMarks != null ? finalMarks.toString() : "null");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void waitForUserInput() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    public void showStudentResult(int studentId) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // Check if the student has results for all enrolled courses
            String checkQuery = "SELECT COUNT(*) FROM takes t WHERE t.student_id = ? " +
                    "AND NOT EXISTS (SELECT 1 FROM result r WHERE r.student_id = t.student_id AND r.course_id = t.course_id)";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, studentId);
                ResultSet checkResult = checkStmt.executeQuery();
                if (checkResult.next() && checkResult.getInt(1) > 0) {
                    System.out.println("Result isn't published yet.");
                    return;
                }
            }

            // Calculate CGPA
            String cgpaQuery = "SELECT calculate_cgpa(?)";
            double cgpa = 0;
            try (PreparedStatement cgpaStmt = connection.prepareStatement(cgpaQuery)) {
                cgpaStmt.setInt(1, studentId);
                ResultSet cgpaResult = cgpaStmt.executeQuery();
                if (cgpaResult.next()) {
                    cgpa = cgpaResult.getDouble(1);
                }
            }

            // Fetch and display the result
            String resultQuery = "SELECT c.Course_code, c.Course_title, " +
                    "CASE " +
                    "WHEN r.GPA = 4.00 THEN 'A+' " +
                    "WHEN r.GPA >= 3.75 THEN 'A' " +
                    "WHEN r.GPA >= 3.50 THEN 'A-' " +
                    "WHEN r.GPA >= 3.25 THEN 'B+' " +
                    "WHEN r.GPA >= 3.00 THEN 'B' " +
                    "WHEN r.GPA >= 2.75 THEN 'B-' " +
                    "WHEN r.GPA >= 2.50 THEN 'C+' " +
                    "WHEN r.GPA >= 2.25 THEN 'C' " +
                    "WHEN r.GPA >= 2.00 THEN 'D' " +
                    "ELSE 'F' " +
                    "END AS grade " +
                    "FROM result r " +
                    "JOIN courses c ON r.Course_ID = c.Course_ID " +
                    "WHERE r.Student_ID = ?";

            try (PreparedStatement resultStmt = connection.prepareStatement(resultQuery)) {
                resultStmt.setInt(1, studentId);
                ResultSet resultSet = resultStmt.executeQuery();

                System.out.println("Result of Student: " + studentId);
                System.out.println("Course Code | Course Title       | Grade");
                System.out.println("------------------------------------------");

                while (resultSet.next()) {
                    String courseCode = resultSet.getString("Course_code");
                    String courseTitle = resultSet.getString("Course_title");
                    String grade = resultSet.getString("grade");

                    System.out.printf("%-11s | %-17s | %s%n", courseCode, courseTitle, grade);
                }

                // Print CGPA
                System.out.printf("%nCGPA: %.2f%n", cgpa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
