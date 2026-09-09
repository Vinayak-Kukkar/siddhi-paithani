package com.siddhi.paithani.service;

import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyService {

    public static class CurrencyConfig {
        private final String code;
        private final String symbol;
        private final double rateFromInr; // How many units of target currency for 1 INR

        public CurrencyConfig(String code, String symbol, double rateFromInr) {
            this.code = code;
            this.symbol = symbol;
            this.rateFromInr = rateFromInr;
        }

        public String getCode() { return code; }
        public String getSymbol() { return symbol; }
        public double getRateFromInr() { return rateFromInr; }
    }

    private final Map<String, CurrencyConfig> currencies = new HashMap<>();

    public CurrencyService() {
        // Base Currency: INR (₹)
        currencies.put("INR", new CurrencyConfig("INR", "₹", 1.0));
        currencies.put("USD", new CurrencyConfig("USD", "$", 0.012));      // 1 USD ≈ 83.3 INR
        currencies.put("EUR", new CurrencyConfig("EUR", "€", 0.011));      // 1 EUR ≈ 90.9 INR
        currencies.put("GBP", new CurrencyConfig("GBP", "£", 0.0094));     // 1 GBP ≈ 106.3 INR
        currencies.put("AED", new CurrencyConfig("AED", "AED ", 0.044));   // 1 AED ≈ 22.7 INR
    }

    public Map<String, CurrencyConfig> getAvailableCurrencies() {
        return currencies;
    }

    public CurrencyConfig getCurrency(String code) {
        if (code == null) return currencies.get("INR");
        CurrencyConfig config = currencies.get(code.toUpperCase().trim());
        return config != null ? config : currencies.get("INR");
    }

    public String formatPrice(Double priceInInr, String currencyCode) {
        if (priceInInr == null) priceInInr = 0.0;
        CurrencyConfig config = getCurrency(currencyCode);

        double converted = priceInInr * config.getRateFromInr();

        if ("INR".equalsIgnoreCase(config.getCode())) {
            DecimalFormat inrFormatter = new DecimalFormat("##,##,##0.00");
            return config.getSymbol() + inrFormatter.format(priceInInr);
        } else {
            DecimalFormat standardFormatter = new DecimalFormat("#,##0.00");
            return config.getSymbol() + standardFormatter.format(converted);
        }
    }
}
