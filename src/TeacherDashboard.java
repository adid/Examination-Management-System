import java.util.Scanner;

public class TeacherDashboard {
    private static final TeacherService teacherService = new TeacherService();

    public static void showDashboard(int userId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome, Teacher " + userId);

        while (true) {
            System.out.println("1. View assigned courses and students");
            System.out.println("2. Schedule exams");
            System.out.println("3. View Scheduled exams");
            System.out.println("4. Evaluate Exam Marks");
            System.out.println("5. Logout");

            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    teacherService.viewAssignedCourses(userId);
                    break;
                case 2:
                    teacherService.scheduleExam(userId);
                    break;
                case 3:
                    teacherService.viewExamSchedules(userId);
                    break;
                case 4:
                    teacherService.evaluateMarks(userId);
                    break;
                case 5:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            System.out.print("Press Enter to continue...");
            scanner.nextLine(); // wait for the user to press Enter
        }
    }
}
