import java.util.Scanner;

public class LoginApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserLoginService loginService = new UserLoginService();

        System.out.print("Enter User ID: ");
        int userId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        if (password.isEmpty()) {
            System.out.println("Please enter both User ID and Password.");
            return;
        }

        String role = loginService.validateLogin(userId, password);

        if (role != null) {
            System.out.println("Login successful!");

            switch (role) {
                case "Admin":
                    AdminDashboard.showDashboard(userId);
                    break;
                case "Student":
                    StudentDashboard.showDashboard(userId);
                    break;
                case "Teacher":
                    TeacherDashboard.showDashboard(userId);
                    break;
                default:
                    System.out.println("Invalid role.");
                    break;
            }
        } else {
            System.out.println("Invalid User ID or Password.");
        }

        scanner.close();
    }
}
