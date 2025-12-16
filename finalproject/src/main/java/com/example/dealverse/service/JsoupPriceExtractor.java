package com.example.dealverse.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class JsoupPriceExtractor implements PriceExtractor {

    private static final Pattern PRICE_PATTERN = Pattern.compile("(\\d{1,3}(,\\d{3})*|\\d{3,})(?=\\s*(元|NT|＄|\\$))");

    @Override
    public Integer extractPrice(String url, String source) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            Matcher m = PRICE_PATTERN.matcher(doc.text());
            if (m.find()) {
                String raw = m.group(1).replace(",", "");
                return Integer.parseInt(raw);
            }

            return null;

        } catch (Exception e) {
            System.out.println("Extractor err = " + e.getMessage());
            return null;
        }
    }
}
