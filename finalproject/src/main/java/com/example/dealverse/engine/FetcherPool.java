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

        // ✅ 讀取前端選的站台：shopee / momo / pchome（若為 null/空字串就表示不過濾）
        String site = (query == null ? null : query.getSite());

        for (Connector c : connectors) {

            // 若使用者有選 site，就只跑對應的 connector
            if (site != null && !site.isBlank()) {
                String source = c.getSourceName(); // 例如 "shopee" / "momo" / "pchome"
                if (source == null || !source.equalsIgnoreCase(site)) {
                    continue;
                }
            }

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
