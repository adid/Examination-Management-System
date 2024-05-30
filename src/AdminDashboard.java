import java.util.Scanner;

public class AdminDashboard {
    private static final CourseService courseService = new CourseService();

    public static void showDashboard(int userId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome, Admin " + userId);

        while (true) {
            System.out.println("1. View all courses");
            System.out.println("2. Add a course");
            System.out.println("3. Delete a course");
            System.out.println("4. View courses without instructor");
            System.out.println("5. Assign instructor to course");
            System.out.println("6. View courses with instructor");
            System.out.println("7. Remove instructor from course");
            System.out.println("8. Logout");

            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            ConsoleUtils.clearConsole();  // Clear the console after the choice is made

            switch (choice) {
                case 1:
                    courseService.viewAllCourses();
                    break;
                case 2:
                    courseService.addCourse();
                    break;
                case 3:
                    courseService.deleteCourse();
                    break;
                case 4:
                    courseService.viewCoursesWithoutInstructor();
                    break;
                case 5:
                    courseService.assignInstructorToCourse();
                    break;
                case 6:
                    courseService.viewCoursesWithInstructor();
                    break;
                case 7:
                    courseService.removeInstructorFromCourse();
                    break;
                case 8:
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
