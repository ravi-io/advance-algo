import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCConnectivityDemo {

    // JDBC connectivity demo:
    // Time complexity is O(1) for connection setup and O(n) for query execution.
    // Space complexity is O(1) apart from result storage.
    public static void main(String[] args) {
          String url = "jdbc:mysql://localhost:3306/testdb";
          String user = "javauser";  
          String password = "java123";

        try {
            // Load the JDBC driver for MySQL.
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(url, user, password);
                 Statement statement = connection.createStatement()) {

                System.out.println("Database connected successfully.");

                // Ensure the student table exists with the expected columns.
                String createSql = "CREATE TABLE IF NOT EXISTS student (id INT PRIMARY KEY, name VARCHAR(50), course VARCHAR(50), reg_no INT)";
                statement.executeUpdate(createSql);
                System.out.println("Table checked/created successfully.");

                // Query and print all rows from the student table.
                String querySql = "SELECT * FROM student";
                try (ResultSet resultSet = statement.executeQuery(querySql)) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // Print header row.
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(metaData.getColumnLabel(i));
                        if (i < columnCount) {
                            System.out.print(" | ");
                        }
                    }
                    System.out.println();
                    System.out.println("--------------------------------------------");

                    // Print each row.
                    while (resultSet.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            System.out.print(resultSet.getString(i));
                            if (i < columnCount) {
                                System.out.print(" | ");
                            }
                        }
                        System.out.println();
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
