import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CourseService {
    private final String url = "jdbc:postgresql://localhost:5432/dbms";
    private final String user = "postgres";
    private final String password = "pgadmin";
    private final Scanner scanner = new Scanner(System.in);

    public void viewAllCourses() {
        String query = "SELECT c.Course_ID, c.Course_title, c.credit, t.Name AS Instructor_name "
                + "FROM Courses c "
                + "LEFT JOIN Teachers t ON c.Instructor_ID = t.Teacher_ID";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String courseId = resultSet.getString("Course_ID");
                String courseTitle = resultSet.getString("Course_title");
                int credit = resultSet.getInt("credit");
                String instructorName = resultSet.getString("Instructor_name");

                System.out.println("Course ID: " + courseId + ", Title: " + courseTitle + ", Credit: " + credit + ", Instructor: " + (instructorName != null ? instructorName : "None"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCourse() {
        System.out.print("Enter Course Code: ");
        int courseCode = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.print("Enter Course Title: ");
        String courseTitle = scanner.nextLine();

        System.out.print("Enter Department ID: ");
        String department = scanner.nextLine();

        System.out.print("Enter Credit: ");
        int credit = scanner.nextInt();
        scanner.nextLine(); // consume newline

        String query = "INSERT INTO Courses (Course_code, Course_title, Department, credit) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, courseCode);
            statement.setString(2, courseTitle);
            statement.setString(3, department);
            statement.setInt(4, credit);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Course added successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCourse() {
        System.out.print("Enter Course ID to delete: ");
        String courseId = scanner.nextLine();

        String query = "DELETE FROM Courses WHERE Course_ID = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, courseId);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Course deleted successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewCoursesWithoutInstructor() {
        String query = "SELECT Course_ID, Course_title FROM Courses WHERE Instructor_ID IS NULL";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String courseId = resultSet.getString("Course_ID");
                String courseTitle = resultSet.getString("Course_title");

                System.out.println("Course ID: " + courseId + ", Title: " + courseTitle);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void assignInstructorToCourse() {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // Display courses without instructors
            Map<Integer, String> courses = new HashMap<>();
            String courseQuery = "SELECT Course_ID, Course_title, Department FROM Courses WHERE Instructor_ID IS NULL";
            try (PreparedStatement courseStatement = connection.prepareStatement(courseQuery);
                 ResultSet courseResultSet = courseStatement.executeQuery()) {

                System.out.println("Courses without Instructors:");
                while (courseResultSet.next()) {
                    String courseId = courseResultSet.getString("Course_ID");
                    String courseTitle = courseResultSet.getString("Course_title");
                    String department = courseResultSet.getString("Department");

                    courses.put(courses.size() + 1, courseId);
                    System.out.println((courses.size()) + ". " + courseTitle + " (Course ID: " + courseId + ", Department: " + department + ")");
                }
            }

            if (courses.isEmpty()) {
                System.out.println("No courses available without instructors.");
                return;
            }

            System.out.print("Select a course by number: ");
            int courseChoice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            String selectedCourseId = courses.get(courseChoice);

            if (selectedCourseId == null) {
                System.out.println("Invalid course selection.");
                return;
            }

            // Fetch the department of the selected course
            String departmentQuery = "SELECT Department FROM Courses WHERE Course_ID = ?";
            String department = null;
            try (PreparedStatement deptStatement = connection.prepareStatement(departmentQuery)) {
                deptStatement.setString(1, selectedCourseId);
                try (ResultSet deptResultSet = deptStatement.executeQuery()) {
                    if (deptResultSet.next()) {
                        department = deptResultSet.getString("Department");
                    }
                }
            }

            if (department == null) {
                System.out.println("Department not found for the selected course.");
                return;
            }

            // Display instructors from the respective department
            Map<Integer, Integer> instructors = new HashMap<>();
            String instructorQuery = "SELECT Teacher_ID, Name FROM Teachers WHERE Department = ?";
            try (PreparedStatement instructorStatement = connection.prepareStatement(instructorQuery)) {
                instructorStatement.setString(1, department);
                try (ResultSet instructorResultSet = instructorStatement.executeQuery()) {
                    System.out.println("Instructors in Department " + department + ":");
                    while (instructorResultSet.next()) {
                        int instructorId = instructorResultSet.getInt("Teacher_ID");
                        String instructorName = instructorResultSet.getString("Name");

                        instructors.put(instructors.size() + 1, instructorId);
                        System.out.println((instructors.size()) + ". " + instructorName + " (Instructor ID: " + instructorId + ")");
                    }
                }
            }

            if (instructors.isEmpty()) {
                System.out.println("No instructors available in the department " + department + ".");
                return;
            }

            System.out.print("Select an instructor by number: ");
            int instructorChoice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            Integer selectedInstructorId = instructors.get(instructorChoice);

            if (selectedInstructorId == null) {
                System.out.println("Invalid instructor selection.");
                return;
            }

            // Assign the selected instructor to the selected course
            String updateQuery = "UPDATE Courses SET Instructor_ID = ? WHERE Course_ID = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setInt(1, selectedInstructorId);
                updateStatement.setString(2, selectedCourseId);

                int rowsUpdated = updateStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Instructor assigned to course successfully.");
                } else {
                    System.out.println("Failed to assign instructor to course.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewCoursesWithInstructor() {
        String query = "SELECT c.Course_ID, c.Course_title, t.Name AS Instructor_name "
                + "FROM Courses c "
                + "JOIN Teachers t ON c.Instructor_ID = t.Teacher_ID";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String courseId = resultSet.getString("Course_ID");
                String courseTitle = resultSet.getString("Course_title");
                String instructorName = resultSet.getString("Instructor_name");

                System.out.println("Course ID: " + courseId + ", Title: " + courseTitle + ", Instructor: " + instructorName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeInstructorFromCourse() {
        System.out.print("Enter Course ID to remove instructor: ");
        String courseId = scanner.nextLine();

        String query = "UPDATE Courses SET Instructor_ID = NULL WHERE Course_ID = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, courseId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Instructor removed from course successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
