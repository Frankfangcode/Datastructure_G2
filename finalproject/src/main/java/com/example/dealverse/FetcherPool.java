package com.example.dealverse;

import java.util.ArrayList;
import java.util.List;

public class FetcherPool {
    private final List<Connector> connectors = new ArrayList<>();

    public void addConnector(Connector connector) {
        connectors.add(connector);
        connectors.add(new ShopeeConnector());
    }

    public List<RawOffer> fetchAll(Query query) {
        List<RawOffer> all = new ArrayList<>();
        for (Connector c : connectors) {
            try {
                all.addAll(c.fetch(query));
            } catch (Exception e) {
                System.err.println("Connector failed: " + c.getSourceName() + " " + e.getMessage());
            }
        }
        return all;
    }
}
