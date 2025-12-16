package com.example.dealverse.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.dealverse.engine.SearchService;
import com.example.dealverse.model.Query;
import com.example.dealverse.model.Result;

@Service
public class DealVerseSearchService {

    private final SearchService searchService;

    public DealVerseSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    // ✅ 新增：支援指定站台
    public Map<String, String> searchTitleUrl(String keyword, String site) {
        Query q = new Query(keyword);
        q.setSite(site); // ✅ 把 site 帶進 Query，讓 FetcherPool 能過濾 connector

        List<Result> results = searchService.search(q, 10);

        Map<String, String> map = new LinkedHashMap<>();
        for (Result r : results) {
            // 先用 source + brand model（你現在的做法）
            String title = r.getOffer().getSource() + " - " +
                    (r.getOffer().getBrand() == null ? "" : r.getOffer().getBrand()) + " " +
                    (r.getOffer().getModel() == null ? "" : r.getOffer().getModel());

            if (title.trim().equals("-") || title.trim().isEmpty()) {
                title = r.getOffer().getSource();
            }

            map.put(title, r.getOffer().getUrl());
        }
        return map;
    }

    // ✅ 舊方法保留：預設查 shopee（或你想改成 all 也行）
    public Map<String, String> searchTitleUrl(String keyword) {
        return searchTitleUrl(keyword, "shopee");
    }
}
