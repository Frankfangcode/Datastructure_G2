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
    // 新版：多來源（sources）+ AI 翻譯/推薦
    // =========================
    public SearchResponse searchWithAi(String keyword, List<String> sources, int topK) {
        String originalKeyword = keyword == null ? "" : keyword.trim();

        // 1) AI 優化查詢（翻譯）
        String optimizedKeyword = originalKeyword;
        if (openAiEnabled && !originalKeyword.isBlank()) {
            String refined = openAiService.refineQuery(originalKeyword);
            if (refined != null && !refined.isBlank()) {
                optimizedKeyword = refined.trim();
            }
        }

        // 2) AI 推薦相關商品（可選）
        List<String> recommendations = new ArrayList<>();
        if (openAiEnabled && !optimizedKeyword.isBlank()) {
            String recCsv = openAiService.getRecommendations(optimizedKeyword);
            recommendations = parseRecommendations(recCsv, 5);
        }

        // 3) 用 optimizedKeyword + sources 跑搜尋引擎（多站）
        Map<String, String> resultsMap = searchTitleUrlInternalBySources(optimizedKeyword, sources, topK);

        // 4) site 欄位：你現在是多站，這裡用 join 方便 debug/顯示
        String siteLabel = sources == null ? "" : String.join(",", sources);

        return new SearchResponse(originalKeyword, optimizedKeyword, recommendations, resultsMap, siteLabel);
    }

    // =========================
    // 舊介面保留：單站（site）搜尋，不用動舊程式也能跑
    // =========================
    public Map<String, String> searchTitleUrl(String keyword, String site) {
        return searchTitleUrlInternalBySite(keyword, site, 10);
    }

    public Map<String, String> searchTitleUrl(String keyword) {
        return searchTitleUrl(keyword, "shopee");
    }

    // =========================
    // Internal helpers (多站版)
    // =========================
    private Map<String, String> searchTitleUrlInternalBySources(String keyword, List<String> sources, int topK) {
        Query q = new Query(keyword);

        // ✅ 多站：用 sources（Query 需已新增 setSources / allowSource）
        if (sources != null) {
            // 統一小寫，避免 connector.getSourceName() 對不上
            List<String> normalized = sources.stream()
                    .filter(s -> s != null && !s.isBlank())
                    .map(s -> s.trim().toLowerCase())
                    .distinct()
                    .collect(Collectors.toList());
            q.setSources(normalized);
        }

        List<Result> results = searchService.search(q, topK);
        return toTitleUrlMap(results);
    }

    // =========================
    // Internal helpers (單站相容版)
    // =========================
    private Map<String, String> searchTitleUrlInternalBySite(String keyword, String site, int topK) {
        Query q = new Query(keyword);

        if (site != null && !site.isBlank()) {
            q.setSite(site.trim().toLowerCase());
        }

        List<Result> results = searchService.search(q, topK);
        return toTitleUrlMap(results);
    }

    // =========================
    // Result -> Map<title,url>
    // =========================
    private Map<String, String> toTitleUrlMap(List<Result> results) {
        Map<String, String> map = new LinkedHashMap<>();
        if (results == null) return map;

        for (Result r : results) {
            if (r == null || r.getOffer() == null) continue;

            String source = safe(r.getOffer().getSource());
            String brand  = safe(r.getOffer().getBrand());
            String model  = safe(r.getOffer().getModel());

            String titleCore = (brand + " " + model).trim();
            if (titleCore.isEmpty()) titleCore = source;

            String title = source + " - " + titleCore;

            // 若 Result 有 getPay() 就顯示，沒有就忽略
            try {
                title += " | pay=" + r.getPay();
            } catch (Exception ignore) {
                // no-op
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
