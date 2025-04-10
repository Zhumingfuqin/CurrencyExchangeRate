package org.example;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class CurrencyDataUpdater {
    public static void saveCurrencyRates(List<CurrencyInfo> currencyList, Map<String, Double> ratesMap, Connection conn) {
        if (currencyList == null || currencyList.isEmpty()) {
            System.out.println("❌ Failed to insert data: currencyList is empty");
            return;
        }

        // insert statement
        String sql = "INSERT INTO exchange_rates "
                + "(currency_code, currency_name, country_code, country_name, status, available_from, "
                + " available_until, icon, exchange_rate) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int count = 0;
            for (CurrencyInfo currency : currencyList) {
                // --- 👇 icon 清洗逻辑开始 ---
                String icon = currency.getIcon();
                if (icon != null) {
                    if (icon.length() > 1000) {
                        icon = icon.substring(0, 1000); // 截断超长 icon
                    }
                    if (!StandardCharsets.UTF_8.newEncoder().canEncode(icon)) {
                        icon = ""; // 清空非法编码字符
                    }
                } else {
                    icon = "";
                }
                // --- 👆 icon 清洗逻辑结束 ---

                if (currency.getCurrencyCode() != null && currency.getCurrencyCode().length() > 10) {
                    System.out.println("⚠️ 长度超限 currencyCode = " + currency.getCurrencyCode());
                }

                // Insert each data/info individually
                pstmt.setString(1, currency.getCurrencyCode());
                pstmt.setString(2, currency.getCurrencyName());
                pstmt.setString(3, currency.getCountryCode());
                pstmt.setString(4, currency.getCountryName());
                pstmt.setString(5, currency.getStatus());
                pstmt.setString(6, currency.getAvailableFrom());
                pstmt.setString(7, currency.getAvailableUntil());
                pstmt.setString(8, currency.getIcon());
                //pstmt.setDouble(9, currency.getExchangeRate() != null ? currency.getExchangeRate() : 0.0);


                Double rate = currency.getExchangeRate();
                if (rate == null) {
                    rate = ratesMap.getOrDefault(currency.getCurrencyCode(), 0.0);
                }
                pstmt.setDouble(9, rate);

                pstmt.addBatch(); // batch processing
                count++;

                // To avoid processing too much data/info at once, execute in batches
                if (count % 500 == 0) {
                    pstmt.executeBatch();
                }
            }


        pstmt.executeBatch(); // Execute remaining batch

            System.out.println("✅ Inserted " + currencyList.size() + " data into MySQL!");
        } catch (SQLException e) {
            System.out.println("❌ Failed to insert data：" + e.getMessage());
            e.printStackTrace();
        }
    }



    public static void createTableIfNotExists(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS exchange_rates (" +
                "currency_code VARCHAR(1000), " +
                "currency_name VARCHAR(100), " +
                "country_code VARCHAR(200), " +
                "country_name VARCHAR(100), " +
                "status VARCHAR(200), " +
                "available_from VARCHAR(500), " +
                "available_until VARCHAR(500), " +
                "icon TEXT, " +
                "exchange_rate DOUBLE)"
                ;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ The 'exchange_rates' table has been created or already exists");
        } catch (SQLException e) {
            System.out.println("❌ Failed to create table：" + e.getMessage());
        }
    }

}
