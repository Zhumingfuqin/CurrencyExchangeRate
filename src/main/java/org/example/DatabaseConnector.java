package org.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
jdbc连接mysql数据库
 */
public class DatabaseConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/currency_db?serverTimezone=UTC";
    private static final String AWS_URL = "jdbc:mysql://currency-db.cs5y8w8isga2.us-east-1.rds.amazonaws.com:3306/currency_db?serverTimezone=UTC";
    private static final String USER = "currency_user";
    private static final String PASSWORD = "yizhimodouli";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }

    public static Connection getAWSConnection() throws SQLException {
        return DriverManager.getConnection(AWS_URL,USER,PASSWORD);
    }

    public static void main(String[] args) {

        try (Connection conn = getConnection()) {
            System.out.println("✅ MySQL Connection Successful!");
        } catch (SQLException e) {
            System.out.println("❌ MySQL Connection Failed: " + e.getMessage());
        }

        // AWS database connection test
        try (Connection awsConn = getAWSConnection()) {
            System.out.println("✅ AWS MySQL Connection Successful!");
        } catch (SQLException e) {
            System.out.println("❌ AWS MySQL Connection Failed: " + e.getMessage());
        }

    }
}
