import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCConnectivityDemo {

    // JDBC connectivity demo:
    // Time complexity is O(1) for connection setup and O(n) for query execution.
    // Space complexity is O(1) apart from result storage.
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/testdb";
        String user = "root";
        String password = "password";

        try {
            // Load the JDBC driver for MySQL.
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(url, user, password);
                 Statement statement = connection.createStatement()) {

                System.out.println("Database connected successfully.");

                // Example SQL statement.
                String sql = "CREATE TABLE IF NOT EXISTS students (id INT PRIMARY KEY, name VARCHAR(50))";
                statement.executeUpdate(sql);
                System.out.println("Table checked/created successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }
}
