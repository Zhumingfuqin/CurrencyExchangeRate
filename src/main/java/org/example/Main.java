package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.lang.reflect.Type;
import java.util.*;
import java.util.List;
import javax.swing.SwingUtilities;

public class Main {
    private static final String API_KEY = "cc365dd182f94827b38af0bac3f1f12f";
    private static final String API_URL = "https://api.currencyfreaks.com/v2.0/supported-currencies?apikey=" + API_KEY;
    private static final String LATEST_RATES_URL = "https://api.currencyfreaks.com/v2.0/rates/latest?apikey=" + API_KEY;

    /**
     * Fetches the list of supported currencies from the external API.
     *
     * @return a list of CurrencyInfo objects representing supported currencies
     */

    public static List<CurrencyInfo> fetchSupportedCurrencies() {
        //test api call
        System.out.println("Starting API call to fetch data...");

        List<CurrencyInfo> currencyList = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("🙅Fail! HTTP status: " + response.code());
                return currencyList;
            }

            String responseBody = response.body().string();
            System.out.println("API returned content：" + responseBody);
            Gson gson = new Gson();

            JsonObject fullJson = gson.fromJson(responseBody, JsonObject.class);
            JsonObject supportedMap = fullJson.getAsJsonObject("supportedCurrenciesMap");

            if (supportedMap == null) {
                System.out.println("❌ supportedCurrenciesMap field does not exist, possibly using the wrong API endpoint");
                return currencyList;
            }


            Type mapType = new TypeToken<Map<String, CurrencyInfo>>() {}.getType();
            Map<String, CurrencyInfo> parsedMap = gson.fromJson(supportedMap, mapType);

            currencyList.addAll(parsedMap.values());

            System.out.println("✅ Successfully fetched currency data, count：" + currencyList.size());
        } catch (IOException e) {
            System.out.println("❌ Failed to fetch currency data：" + e.getMessage());
        }
        return currencyList;
    }

    /**
     * Fetches the latest exchange rates from the API.
     *
     * @return a map where the keys are currency codes and the values are their respective exchange rates
     */
    public static Map<String, Double> fetchLatestRates() {
        // test
        System.out.println("[DEBUG] Entering fetchLatestRates() ...");

        Map<String, Double> rateMap = new HashMap<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(LATEST_RATES_URL).build();

        Gson gson = new Gson();

        // test data pulling
        System.out.println("Rates map keys: " + rateMap.keySet());

        try (Response response = client.newCall(request).execute()) {
            // test
            System.out.println("latest-rates response code: " + response.code());
            String responseBody = response.body().string();
            System.out.println("latest-rates response body: " + responseBody);

            if (!response.isSuccessful()) {
                System.out.println("Response not successful: " + response.code()); // test
                System.out.println("❌ latest-rates request failed, status code:  " + response.code());
                return rateMap;
            }

            // Parse JSON
            JsonObject root = gson.fromJson(responseBody, JsonObject.class);
            JsonObject ratesObj = root.getAsJsonObject("rates");
            for (Map.Entry<String, com.google.gson.JsonElement> entry : ratesObj.entrySet()) {
                String currencyCode = entry.getKey();
                double rate = entry.getValue().getAsDouble();
                rateMap.put(currencyCode, rate);
            }
            System.out.println("✅ [fetchLatestRates] Fetched rates count: " + rateMap.size());
        } catch (IOException e) {
            System.out.println("❌ Failed to fetch latest rates:  " + e.getMessage());
        }

        return rateMap;
    }

    /**
     * Main entry point for the program, where data is fetched, merged, and inserted into both local and AWS databases.
     */
    public static void main(String[] args) {
        // Fetch currency data/info
        List<CurrencyInfo> currencies = fetchSupportedCurrencies();
        // Get latest rates
        Map<String, Double> ratesMap = fetchLatestRates();

        // Merge - populate the rates from ratesMap into the currency list
        for (CurrencyInfo info : currencies) {
            Double rate = ratesMap.get(info.getCurrencyCode());
            if (rate != null) {
                info.setExchangeRate(rate);
            } else {
                // If no corresponding rate, set to 0.0 (or keep null)
                info.setExchangeRate(0.0);
            }
        }

        // Insert into AWS
        try (Connection awsConn = DatabaseConnector.getAWSConnection()) {
            CurrencyDataUpdater.createTableIfNotExists(awsConn); // 建表
            CurrencyDataUpdater.saveCurrencyRates(currencies, ratesMap,awsConn);
        } catch (SQLException e) {
            System.err.println("❌ AWS database operation failed: " + e.getMessage());
            throw new RuntimeException(e);
        }

        // Insert into local database(mysql)
        try (Connection localConn = DatabaseConnector.getConnection()) {
            CurrencyDataUpdater.createTableIfNotExists(localConn); // 建表
            CurrencyDataUpdater.saveCurrencyRates(currencies, ratesMap,localConn);
        } catch (SQLException e) {
            System.err.println("❌ Local database operation failed: " + e.getMessage());
            throw new RuntimeException(e);
        }

        // Start the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new MyGUI().setVisible(true);
        });
    }
}
