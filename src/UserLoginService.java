import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserLoginService {
    private final String url = "jdbc:postgresql://localhost:5432/dbms";
    private final String user = "postgres";
    private final String password = "pgadmin";

    public String validateLogin(int userId, String userPassword) {
        String query = "SELECT UserRole FROM Users WHERE User_ID = ? AND Password = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            statement.setString(2, userPassword);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("UserRole");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
