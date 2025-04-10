package org.example;

import java.sql.DriverManager;
import java.sql.Connection;

public class CurrencyUpdaterTest {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // 手动加载驱动
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://currency-db.cs5y8w8isga2.us-east-1.rds.amazonaws.com:3306/currency_db",
                    "currency_user",
                    "yizhimodouli"
            );
            System.out.println("✅ Connection successful!");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}