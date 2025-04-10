package org.example;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.Map;

public class MainVer1 {
    public static void main(String[] args) {

        // create OkHttpClient instance to handle HTTP request - you can find it in the example code in currencyfreak
        OkHttpClient client = new OkHttpClient();

        // construct the request url and add your api key for the website
        String apiKey = "cc365dd182f94827b38af0bac3f1f12f";
        String url = "https://api.currencyfreaks.com/v2.0/rates/latest?apikey=" + apiKey;

        // create a request constructor
        Request request = new Request.Builder()
                // set up request url and construct final request
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // obtain the json response
                String responseBody = response.body().string();
                System.out.println("Raw JSON Response: " + responseBody);

                /*
                Parse json using gson
                gpt help, but the json-lib have too many JAR files.
                Using Maven with Gson simply needs a single dependencies, keeping the project lightweight
                and maintainable
                 */
                Gson gson = new Gson();
                CurrencyData currencyData = gson.fromJson(responseBody, CurrencyData.class);

                System.out.println("Date: " + currencyData.getDate());
                System.out.println("Base Currency: " + currencyData.getBase());

                /*
                The "rates" field is a Map, which functions as a dictionary storing key-value pairs
                The key represents the currency code (e.g., "EUR"), and the value represents its exchange rate
                */
                Map<String, String> ratesMap = currencyData.getRates();
                // use forEach to iterate over each key-value in the rates map
                for (Map.Entry<String, String> entry : ratesMap.entrySet()) {
                    String currency = entry.getKey();
                    String rate = entry.getValue();
                    System.out.println(currency + " : " + rate);
                }
            } else {
                System.out.println("Request Failed! HTTP status code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
