package com.example.dealverse.service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SimplePriceExtractor implements PriceExtractor {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final Pattern PRICE_PATTERN =
            Pattern.compile("\\$\\s*([0-9,]+)");

    @Override
    public Integer extractPrice(String url, String source) {
        try {
            String html = restTemplate.getForObject(url, String.class);
            if (html == null) return null;

            Matcher m = PRICE_PATTERN.matcher(html);
            Set<Integer> prices = new HashSet<>();

            while (m.find()) {
                String num = m.group(1).replace(",", "");
                try {
                    int value = Integer.parseInt(num);
                    if (value > 100) {
                        prices.add(value);
                    }
                } catch (NumberFormatException ignore) {}
            }

            if (prices.size() == 1) {
                return prices.iterator().next();
            }
            return null;

        } catch (Exception e) {
            return null;
        }
    }
}
