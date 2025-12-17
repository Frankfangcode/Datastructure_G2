package com.example.dealverse.model;

import java.util.List;
import java.util.Map;

/**
 * SearchResponse
 * 用來承接一次完整搜尋的回傳結果：
 * - 原始關鍵字
 * - AI 優化後關鍵字
 * - AI 推薦關聯商品
 * - 搜尋結果（title → url）
 * - 搜尋站台
 */
public class SearchResponse {

    private final String originalKeyword;
    private final String optimizedKeyword;
    private final List<String> recommendations;
    private final Map<String, String> results;
    private final String site;

    public SearchResponse(String originalKeyword,
                          String optimizedKeyword,
                          List<String> recommendations,
                          Map<String, String> results,
                          String site) {
        this.originalKeyword = originalKeyword;
        this.optimizedKeyword = optimizedKeyword;
        this.recommendations = recommendations;
        this.results = results;
        this.site = site;
    }

    public String getOriginalKeyword() {
        return originalKeyword;
    }

    public String getOptimizedKeyword() {
        return optimizedKeyword;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public Map<String, String> getResults() {
        return results;
    }

    public String getSite() {
        return site;
    }
}

