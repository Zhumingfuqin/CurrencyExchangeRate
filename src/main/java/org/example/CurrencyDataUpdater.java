package org.example;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class CurrencyDataUpdater {

    /**
     * Saves currency data into the database.
     * This method inserts records into both the main exchange_rates table and the historical exchange_rate_history table.
     *
     * @param currencyList the list of currencies to be inserted
     * @param ratesMap the map containing the latest exchange rates for the currencies
     * @param conn the database connection
     */

    public static void saveCurrencyRates(List<CurrencyInfo> currencyList, Map<String, Double> ratesMap, Connection conn) {
        if (currencyList == null || currencyList.isEmpty()) {
            System.out.println("❌ Failed to insert data: currencyList is empty");
            return;
        }

        // insert statement
        String sql = "INSERT INTO exchange_rates "
                + "(currency_code, currency_name, country_code, country_name, status, available_from, "
                + " available_until, icon, exchange_rate) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                + "ON DUPLICATE KEY UPDATE exchange_rate = VALUES(exchange_rate)";

        String historySql = "INSERT INTO exchange_rate_history (currency_code, exchange_rate) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             PreparedStatement historyStmt = conn.prepareStatement(historySql)) {

            for (CurrencyInfo currency : currencyList) {
                if (currency.getCurrencyCode() != null && currency.getCurrencyCode().length() > 10) {
                    System.out.println("⚠️ Currency code exceeds maximum length = " + currency.getCurrencyCode());
                }

                // Insert Main Table
                pstmt.setString(1, currency.getCurrencyCode());
                pstmt.setString(2, currency.getCurrencyName());
                pstmt.setString(3, currency.getCountryCode());
                pstmt.setString(4, currency.getCountryName());
                pstmt.setString(5, currency.getStatus());
                pstmt.setString(6, currency.getAvailableFrom());
                pstmt.setString(7, currency.getAvailableUntil());
                pstmt.setString(8, currency.getIcon());
                pstmt.setDouble(9, currency.getExchangeRate() != null ? currency.getExchangeRate() : 0.0);
                pstmt.executeUpdate();

                // Insert Historical Record Table
                historyStmt.setString(1, currency.getCurrencyCode());
                historyStmt.setDouble(2, currency.getExchangeRate() != null ? currency.getExchangeRate() : 0.0);
                historyStmt.executeUpdate();
            }

            System.out.println("✅ Inserted " + currencyList.size() + " data into MySQL!");
        } catch (SQLException e) {
            System.out.println("❌ Failed to insert data：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates the exchange_rates table if it does not already exist.
     *
     * @param conn the database connection
     */
        public static void createTableIfNotExists (Connection conn){
            String sql = "CREATE TABLE IF NOT EXISTS exchange_rates (" +
                    "currency_code VARCHAR(1000) PRIMARY KEY, " +
                    "currency_name VARCHAR(100), " +
                    "country_code VARCHAR(200), " +
                    "country_name VARCHAR(100), " +
                    "status VARCHAR(200), " +
                    "available_from VARCHAR(500), " +
                    "available_until VARCHAR(500), " +
                    "icon TEXT, " +
                    "exchange_rate DOUBLE)";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("✅ The 'exchange_rates' table has been created or already exists");
            } catch (SQLException e) {
                System.out.println("❌ Failed to create table：" + e.getMessage());
            }
        }

}
