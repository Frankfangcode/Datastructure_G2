package com.example.dealverse.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.dealverse.engine.SearchService;
import com.example.dealverse.model.Query;
import com.example.dealverse.model.Result;
import com.example.dealverse.model.SearchResponse;
import com.example.dealverse.service.OpenAiService;


@Service
public class DealVerseSearchService {

    private final SearchService searchService;
    private final OpenAiService openAiService;

    /**
     * 可用於快速關閉 AI（避免沒 key 時整個功能失效）
     * application.properties 可加：
     * openai.enabled=true
     */
    @Value("${openai.enabled:true}")
    private boolean openAiEnabled;

    public DealVerseSearchService(SearchService searchService, OpenAiService openAiService) {
        this.searchService = searchService;
        this.openAiService = openAiService;
    }

    // =========================
    // 新增：給新版 UI 用的回傳格式
    // =========================
    public SearchResponse searchWithAi(String keyword, String site, int topK) {
        String originalKeyword = keyword == null ? "" : keyword.trim();

        // 1) AI 優化查詢（翻譯）
        String optimizedKeyword = originalKeyword;
        if (openAiEnabled && !originalKeyword.isBlank()) {
            String refined = openAiService.refineQuery(originalKeyword);
            if (refined != null && !refined.isBlank()) optimizedKeyword = refined.trim();
        }

        // 2) AI 推薦相關商品（可選）
        List<String> recommendations = new ArrayList<>();
        if (openAiEnabled && !optimizedKeyword.isBlank()) {
            String recCsv = openAiService.getRecommendations(optimizedKeyword);
            recommendations = parseRecommendations(recCsv, 5);
        }

        // 3) 用 optimizedKeyword 去跑你的搜尋引擎
        Map<String, String> resultsMap = searchTitleUrlInternal(optimizedKeyword, site, topK);

        // 4) 回傳（給 Controller 丟進 model）
        return new SearchResponse(originalKeyword, optimizedKeyword, recommendations, resultsMap, site);
    }

    // =========================
    // 舊介面保留（不改 controller 也能跑）
    // =========================

    // ✅ 新增：支援指定站台（你原本就有）
    public Map<String, String> searchTitleUrl(String keyword, String site) {
        return searchTitleUrlInternal(keyword, site, 10);
    }

    // ✅ 舊方法保留：預設查 shopee（或你想改成 all 也行）
    public Map<String, String> searchTitleUrl(String keyword) {
        return searchTitleUrl(keyword, "shopee");
    }

    // =========================
    // Internal helpers
    // =========================

    private Map<String, String> searchTitleUrlInternal(String keyword, String site, int topK) {
        Query q = new Query(keyword);
        q.setSite(site); // ✅ 把 site 帶進 Query，讓 FetcherPool 能過濾 connector

        List<Result> results = searchService.search(q, topK);

        Map<String, String> map = new LinkedHashMap<>();
        for (Result r : results) {
            String source = safe(r.getOffer().getSource());
            String brand  = safe(r.getOffer().getBrand());
            String model  = safe(r.getOffer().getModel());

            // 你原本 title 的邏輯 + 加上 pay（如果你 Result 有 pay）
            // 若沒有 pay 也不會壞，catch 掉或移除即可
            String titleCore = (brand + " " + model).trim();
            if (titleCore.isEmpty()) titleCore = source;

            String title = source + " - " + titleCore;

            // 有些版本 Result 可能有 getPay()，沒有的話就註解這段
            try {
                title += " | pay=" + r.getPay();
            } catch (Exception ignore) {
                // do nothing
            }

            String url = safe(r.getOffer().getUrl());
            if (!url.isBlank()) {
                map.put(title, url);
            }
        }
        return map;
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private static List<String> parseRecommendations(String recCsv, int limit) {
        if (recCsv == null || recCsv.isBlank()) return List.of();

        return Arrays.stream(recCsv.split(","))
                .map(String::trim)
                .filter(x -> !x.isBlank())
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
    }
}