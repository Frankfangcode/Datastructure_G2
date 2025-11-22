package com.example.dealverse;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class DummyConnector implements Connector {

    private final String sourceName;

    public DummyConnector(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }

    @Override
    public List<RawOffer> fetch(Query query) {
        // demo：輸入任何東西，都回兩筆 iPhone 範例
        if (sourceName.equals("momo")) {
            return List.of(
                    new RawOffer("momo", "Apple iPhone 17 Pro 256G",
                            "Apple", "iPhone 17 Pro", "IP17P-256",
                            Map.of("color", "black"), 40000, 0,
                            "https://www.momo.com/ip17p", Instant.now())
            );
        } else if (sourceName.equals("shopee")) {
            return List.of(
                    new RawOffer("shopee", "iPhone 17 Pro 256GB (local warranty)",
                            "Apple", "iPhone 17 Pro", "IP17P-256",
                            Map.of("color", "black"), 39500, 60,
                            "https://shopee.tw/ip17p", Instant.now())
            );
        } else {
            return List.of();
        }
    }
}
