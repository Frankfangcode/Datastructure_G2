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

    public Map<String, String> searchTitleUrl(String keyword) {
        Query q = new Query(keyword);
        List<Result> results = searchService.search(q, 10);

        Map<String, String> map = new LinkedHashMap<>();
        for (Result r : results) {
            String title = r.getOffer().getSource() + " - " + r.getOffer().getBrand() + " " + r.getOffer().getModel();
            if (title.trim().isEmpty()) {
                title = r.getOffer().getSource();
            }
            map.put(title + " | pay=" + r.getPay(), r.getOffer().getUrl());
        }
        return map;
    }
}
