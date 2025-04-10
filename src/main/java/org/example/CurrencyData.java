package org.example;
import java.util.Map;

public class CurrencyData {
    private String date;
    private String base;
    private Map<String, String> rates;


    public String getDate() {
        return date;
    }

    public String getBase() {
        return base;
    }

    // define the getter of the map for the rate
    public Map<String, String> getRates() {
        return rates;
    }
}
