package com.example.dealverse.engine;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.dealverse.connector.Connector;
import com.example.dealverse.model.Query;
import com.example.dealverse.model.RawOffer;

@Component
public class FetcherPool {

    private final List<Connector> connectors = new ArrayList<>();

    public FetcherPool(List<Connector> connectors) {
        // Spring 會自動注入所有實作 Connector 並放進來
        this.connectors.addAll(connectors);
    }

    public List<RawOffer> fetchAll(Query query) {
        List<RawOffer> all = new ArrayList<>();
        for (Connector c : connectors) {
            try {
                List<RawOffer> list = c.fetch(query);
                if (list != null) {
                    all.addAll(list);
                }
            } catch (Exception e) {
                System.err.println("Connector failed: " + c.getSourceName() + " - " + e.getMessage());
            }
        }
        return all;
    }
}
