import java.util.Scanner;

public class StudentDashboard {
    private static final StudentCourseService studentCourseService = new StudentCourseService();
    private static final StudentService studentService = new StudentService();

    public static void showDashboard(int userId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome, Student " + userId);

        while (true) {
            System.out.println("1. View registered courses");
            System.out.println("2. Register for courses");
            System.out.println("3. View Exam Schedule");
            System.out.println("4. View Marks");
            System.out.println("5. View Result");
            System.out.println("6. Logout");

            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    studentCourseService.viewRegisteredCourses(userId);
                    break;
                case 2:
                    studentCourseService.registerForCourses(userId);
                    break;
                case 3:
                    studentService.viewExamSchedules(userId);
                    break;
                case 4:
                    studentService.displayStudentMarks(userId);
                    break;
                case 5:
                    studentService.showStudentResult(userId);
                    break;
                case 6:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            System.out.print("Press Enter to continue...");
            scanner.nextLine(); // Wait for the user to press Enter
            scanner.nextLine(); // Consume the newline
        }
    }
}
