import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TeacherService {
    private final String url = "jdbc:postgresql://localhost:5432/dbms";
    private final String user = "postgres";
    private final String password = "pgadmin";
    private final Scanner scanner = new Scanner(System.in);

    public void viewAssignedCourses(int teacherId) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT c.Course_ID, c.Course_title, c.credit "
                    + "FROM Courses c "
                    + "WHERE c.Instructor_ID = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, teacherId);
                ResultSet resultSet = statement.executeQuery();

                System.out.println("Assigned Courses:");
                while (resultSet.next()) {
                    String courseId = resultSet.getString("Course_ID");
                    String courseTitle = resultSet.getString("Course_title");
                    int credit = resultSet.getInt("credit");

                    System.out.println("Course ID: " + courseId + ", Title: " + courseTitle + ", Credit: " + credit);
                    viewStudentsInCourse(courseId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        waitForUserInput();
    }

    private void viewStudentsInCourse(String courseId) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT s.Student_ID, s.Name "
                    + "FROM Takes t JOIN Students s ON t.Student_ID = s.Student_ID "
                    + "WHERE t.Course_ID = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, courseId);
                ResultSet resultSet = statement.executeQuery();

                System.out.println("Students in Course " + courseId + ":");
                while (resultSet.next()) {
                    int studentId = resultSet.getInt("Student_ID");
                    String studentName = resultSet.getString("Name");

                    System.out.println("Student ID: " + studentId + ", Name: " + studentName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void scheduleExam(int teacherId) {
        List<Course> courses = getAssignedCourses(teacherId);
        if (courses.isEmpty()) {
            System.out.println("No courses assigned.");
            waitForUserInput();
            return;
        }

        System.out.println("Select a course to schedule the exam:");
        for (int i = 0; i < courses.size(); i++) {
            System.out.println((i + 1) + ". " + courses.get(i).getCourseId() + " - " + courses.get(i).getCourseTitle());
        }
        System.out.print("Choice: ");
        int courseChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (courseChoice < 1 || courseChoice > courses.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Course selectedCourse = courses.get(courseChoice - 1);

        System.out.print("Enter Academic Year (e.g., 2024): ");
        String academicYear = scanner.nextLine();

        System.out.print("Enter Semester (Summer/Winter): ");
        String semester = scanner.nextLine();

        System.out.println("Select Exam Type: ");
        System.out.println("1. Quiz1");
        System.out.println("2. Quiz2");
        System.out.println("3. Quiz3");
        System.out.println("4. Mid");
        System.out.println("5. Final");
        System.out.print("Choice: ");
        int examTypeChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        String examType = switch (examTypeChoice) {
            case 1 -> "Quiz1";
            case 2 -> "Quiz2";
            case 3 -> "Quiz3";
            case 4 -> "Mid";
            case 5 -> "Final";
            default -> throw new IllegalArgumentException("Invalid choice");
        };

        System.out.print("Enter Exam Date (YYYY-MM-DD): ");
        String examDate = scanner.nextLine();

        System.out.print("Enter Start Time (HH:MM:SS): ");
        String startTime = scanner.nextLine();

        System.out.print("Enter End Time (HH:MM:SS): ");
        String endTime = scanner.nextLine();

        String examId = generateExamId(academicYear, selectedCourse.getCourseCode(), examType);
        scheduleExamInDB(examId, selectedCourse.getCourseId(), academicYear, semester, examType, Date.valueOf(examDate), Time.valueOf(startTime), Time.valueOf(endTime));
    }

    private List<Course> getAssignedCourses(int teacherId) {
        List<Course> courses = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT Course_ID, Course_code, Course_title, credit " +
                    "FROM Courses " +
                    "WHERE Instructor_ID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, teacherId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String courseId = resultSet.getString("Course_ID");
                    int courseCode = resultSet.getInt("Course_code");
                    String courseTitle = resultSet.getString("Course_title");
                    int credit = resultSet.getInt("credit");
                    courses.add(new Course(courseId, courseCode, courseTitle, credit));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching assigned courses: " + e.getMessage());
        }
        return courses;
    }


    private String generateExamId(String academicYear, int courseCode, String examType) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT generate_exam_id(?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, academicYear);
                statement.setInt(2, courseCode);
                statement.setString(3, examType);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getString(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void scheduleExamInDB(String examId, String courseId, String academicYear, String semester, String examType, Date examDate, Time startTime, Time endTime) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "INSERT INTO Exams (Exam_ID, Course_ID, Academic_year, Semester, Exam_type, Exam_date, StartTime, EndTime) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, Long.parseLong(examId));
                statement.setString(2, courseId);
                statement.setString(3, academicYear);
                statement.setString(4, semester);
                statement.setString(5, examType);
                statement.setDate(6, examDate);
                statement.setTime(7, startTime);
                statement.setTime(8, endTime);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Exam successfully scheduled.");
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

    public void viewExamSchedules(int teacherId) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT e.Exam_ID, e.Course_ID, e.Academic_year, e.Semester, e.Exam_type, e.Exam_date, e.StartTime, e.EndTime "
                    + "FROM Exams e "
                    + "JOIN Courses c ON e.Course_ID = c.Course_ID "
                    + "WHERE c.Instructor_ID = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, teacherId);
                ResultSet resultSet = statement.executeQuery();

                System.out.println("Scheduled Exams:");
                while (resultSet.next()) {
                    String examId = resultSet.getString("Exam_ID");
                    String courseId = resultSet.getString("Course_ID");
                    String academicYear = resultSet.getString("Academic_year");
                    String semester = resultSet.getString("Semester");
                    String examType = resultSet.getString("Exam_type");
                    Date examDate = resultSet.getDate("Exam_date");
                    Time startTime = resultSet.getTime("StartTime");
                    Time endTime = resultSet.getTime("EndTime");

                    System.out.println("Exam ID: " + examId + ", Course ID: " + courseId + ", Academic Year: " + academicYear
                            + ", Semester: " + semester + ", Exam Type: " + examType + ", Exam Date: " + examDate
                            + ", Start Time: " + startTime + ", End Time: " + endTime);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        waitForUserInput();
    }

    private float getMaxAllowedMarks(String examType, int courseCredit) {
        return switch (examType) {
            case "Quiz1", "Quiz2", "Quiz3" -> 5 * courseCredit;
            case "Mid" -> 25 * courseCredit;
            case "Final" -> 60 * courseCredit;
            default -> throw new IllegalArgumentException("Invalid exam type");
        };
    }

    public void evaluateMarks(int teacherId) {
        List<Course> courses = getAssignedCourses(teacherId);
        if (courses.isEmpty()) {
            System.out.println("No courses assigned.");
            waitForUserInput();
            return;
        }

        System.out.println("Select a course to evaluate marks:");
        for (int i = 0; i < courses.size(); i++) {
            System.out.println((i + 1) + ". " + courses.get(i).getCourseId() + " - " + courses.get(i).getCourseTitle());
        }
        System.out.print("Choice: ");
        int courseChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (courseChoice < 1 || courseChoice > courses.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Course selectedCourse = courses.get(courseChoice - 1);
        List<Exam> exams = getCourseExams(selectedCourse.getCourseId());
        if (exams.isEmpty()) {
            System.out.println("No exams scheduled for this course.");
            waitForUserInput();
            return;
        }

        System.out.println("Select an exam to evaluate:");
        for (int i = 0; i < exams.size(); i++) {
            System.out.println((i + 1) + ". " + exams.get(i).getExamType() + " on " + exams.get(i).getExamDate());
        }
        System.out.print("Choice: ");
        int examChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (examChoice < 1 || examChoice > exams.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Exam selectedExam = exams.get(examChoice - 1);
        List<Student> students = getStudentsInCourse(selectedCourse.getCourseId());
        float maxAllowedMarks = getMaxAllowedMarks(selectedExam.getExamType(), selectedCourse.getCredit());

        System.out.println("Maximum allowed marks for " + selectedExam.getExamType() + ": " + maxAllowedMarks);

        for (Student student : students) {
            System.out.print("Enter marks for Student ID " + student.getStudentId() + " (" + student.getName() + "): ");
            float marks = scanner.nextFloat();
            scanner.nextLine(); // consume newline
            inputMarks(selectedExam.getExamId(), student.getStudentId(), marks);
        }
        System.out.println("Marks updated successfully.");
        waitForUserInput();
    }

    private List<Exam> getCourseExams(String courseId) {
        List<Exam> exams = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT * FROM Exams WHERE Course_ID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, courseId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    exams.add(new Exam(
                            resultSet.getLong("Exam_ID"),
                            resultSet.getString("Course_ID"),
                            resultSet.getString("Academic_year"),
                            resultSet.getString("Semester"),
                            resultSet.getString("Exam_type"),
                            resultSet.getDate("Exam_date"),
                            resultSet.getTime("StartTime"),
                            resultSet.getTime("EndTime")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exams;
    }

    private List<Student> getStudentsInCourse(String courseId) {
        List<Student> students = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT s.Student_ID, s.Name FROM Takes t JOIN Students s ON t.Student_ID = s.Student_ID WHERE t.Course_ID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, courseId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    students.add(new Student(
                            resultSet.getInt("Student_ID"),
                            resultSet.getString("Name")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    private void inputMarks(long examId, int studentId, float marks) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "INSERT INTO Marks (Exam_ID, Student_ID, Marks_obtained) VALUES (?, ?, ?) ON CONFLICT (Exam_ID, Student_ID) DO UPDATE SET Marks_obtained = EXCLUDED.Marks_obtained";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, examId);
                statement.setInt(2, studentId);
                statement.setFloat(3, marks);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}