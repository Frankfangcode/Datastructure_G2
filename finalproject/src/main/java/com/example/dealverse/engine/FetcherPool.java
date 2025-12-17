package com.example.dealverse.engine;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.dealverse.connector.Connector;
import com.example.dealverse.model.Query;
import com.example.dealverse.model.RawOffer;

@Component
public class FetcherPool {

    private final List<Connector> connectors;

    public FetcherPool(List<Connector> connectors) {
        this.connectors = connectors;
    }

    public List<RawOffer> fetch(Query query) {
        // ✅ 一定要先宣告 results，不然就會你看到的那個錯
        List<RawOffer> results = new ArrayList<>();

        if (query == null) return results;

        for (Connector c : connectors) {
            String source = c.getSourceName(); // e.g. "shopee","momo","pchome","amazon"

            // ✅ 多站：用 Query.allowSource 決定要不要跑這個 connector
            if (!query.allowSource(source)) {
                continue;
            }

            try {
                List<RawOffer> part = c.fetch(query);
                if (part != null && !part.isEmpty()) {
                    results.addAll(part);
                }
            } catch (Exception e) {
                // 建議不要讓單一 connector 失敗拖垮全部
                System.out.println("[FetcherPool] " + source + " failed: " + e.getMessage());
            }
        }

        return results;
    }
    public List<RawOffer> fetchAll(Query query) {
    return fetch(query); // 如果你已有 fetch(Query)
}

}
