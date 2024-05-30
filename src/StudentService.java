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

    private void waitForUserInput() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}
