package org.example;

public class CurrencyInfo {
    private String currencyCode;
    private String currencyName;
    private String countryCode;
    private String countryName;
    private String status;
    private String availableFrom;
    private String availableUntil;
    private String icon;
    private double exchangeRate;

    // getter/setter
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(String availableFrom) {
        this.availableFrom = availableFrom;
    }

    public String getAvailableUntil() {
        return availableUntil;
    }

    public void setAvailableUntil(String availableUntil) {
        this.availableUntil = availableUntil;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }


    @Override
    public String toString() {
        return "CurrencyInfo{" +
                "currencyCode='" + currencyCode + '\'' +
                ", currencyName='" + currencyName + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", countryName='" + countryName + '\'' +
                ", status='" + status + '\'' +
                ", availableFrom='" + availableFrom + '\'' +
                ", availableUntil='" + availableUntil + '\'' +
                ", icon='" + icon + '\'' +
                ", exchangeRate=" + exchangeRate +
                '}';
    }

}
