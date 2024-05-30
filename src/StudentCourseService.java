import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StudentCourseService {
    private final String url = "jdbc:postgresql://localhost:5432/dbms";
    private final String user = "postgres";
    private final String password = "pgadmin";
    private final Scanner scanner = new Scanner(System.in);

    public void viewRegisteredCourses(int studentId) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT c.Course_ID, c.Course_title, c.credit "
                    + "FROM Takes t JOIN Courses c ON t.Course_ID = c.Course_ID "
                    + "WHERE t.Student_ID = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, studentId);
                ResultSet resultSet = statement.executeQuery();

                System.out.println("Registered Courses:");
                while (resultSet.next()) {
                    String courseId = resultSet.getString("Course_ID");
                    String courseTitle = resultSet.getString("Course_title");
                    int credit = resultSet.getInt("credit");

                    System.out.println("Course ID: " + courseId + ", Title: " + courseTitle + ", Credit: " + credit);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        waitForUserInput();
    }

    public void registerForCourses(int studentId) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // Check the total credits the student is currently registered for
            String totalCreditsQuery = "SELECT SUM(c.credit) AS total_credits "
                    + "FROM Takes t JOIN Courses c ON t.Course_ID = c.Course_ID "
                    + "WHERE t.Student_ID = ?";
            int totalCredits = 0;
            try (PreparedStatement totalCreditsStmt = connection.prepareStatement(totalCreditsQuery)) {
                totalCreditsStmt.setInt(1, studentId);
                try (ResultSet rs = totalCreditsStmt.executeQuery()) {
                    if (rs.next()) {
                        totalCredits = rs.getInt("total_credits");
                    }
                }
            }

            if (totalCredits >= 20) {
                System.out.println("You cannot register for more courses. Maximum 20 credits allowed.");
                waitForUserInput();
                return;
            }

            // Display available courses
            Map<Integer, String> availableCourses = new HashMap<>();
            String availableCoursesQuery = "SELECT c.Course_ID, c.Course_title, c.credit, COUNT(t.Student_ID) AS student_count "
                    + "FROM Courses c LEFT JOIN Takes t ON c.Course_ID = t.Course_ID "
                    + "WHERE c.Course_ID NOT IN (SELECT Course_ID FROM Takes WHERE Student_ID = ?) "
                    + "GROUP BY c.Course_ID "
                    + "HAVING COUNT(t.Student_ID) < 50";
            try (PreparedStatement availableCoursesStmt = connection.prepareStatement(availableCoursesQuery)) {
                availableCoursesStmt.setInt(1, studentId);
                try (ResultSet rs = availableCoursesStmt.executeQuery()) {

                    System.out.println("Available Courses:");
                    while (rs.next()) {
                        String courseId = rs.getString("Course_ID");
                        String courseTitle = rs.getString("Course_title");
                        int credit = rs.getInt("credit");
                        int studentCount = rs.getInt("student_count");

                        if (totalCredits + credit <= 20) {
                            availableCourses.put(availableCourses.size() + 1, courseId);
                            System.out.println((availableCourses.size()) + ". " + courseTitle + " (Course ID: " + courseId + ", Credit: " + credit + ", Registered Students: " + studentCount + ")");
                        }
                    }
                }
            }

            if (availableCourses.isEmpty()) {
                System.out.println("No available courses to register.");
                waitForUserInput();
                return;
            }

            System.out.print("Select a course by number: ");
            int courseChoice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            String selectedCourseId = availableCourses.get(courseChoice);

            if (selectedCourseId == null) {
                System.out.println("Invalid course selection.");
                waitForUserInput();
                return;
            }

            // Register the student for the selected course
            String registerQuery = "INSERT INTO Takes (Student_ID, Course_ID) VALUES (?, ?)";
            try (PreparedStatement registerStmt = connection.prepareStatement(registerQuery)) {
                registerStmt.setInt(1, studentId);
                registerStmt.setString(2, selectedCourseId);

                int rowsInserted = registerStmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Successfully registered for the course.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        waitForUserInput();
    }

    private void waitForUserInput() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}
