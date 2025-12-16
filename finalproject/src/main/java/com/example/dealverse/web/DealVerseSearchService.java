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

    /**
     * 舊的搜尋方法 (保留相容性)
     * 
     * @param keyword 搜尋關鍵字
     * @return 標題與 URL 的對應表
     */
    public Map<String, String> searchTitleUrl(String keyword) {
        return searchTitleUrl(keyword, null);
    }

    /**
     * 新的搜尋方法 (支援平台過濾)
     * 
     * @param keyword 搜尋關鍵字
     * @param sources 允許的平台列表 (例如 ["Shopee", "Momo"])，null 或空表示不過濾
     * @return 標題與 URL 的對應表
     */
    public Map<String, String> searchTitleUrl(String keyword, List<String> sources) {
        Query q = new Query(keyword);

        // 取得搜尋引擎回傳的 Top 10 結果
        List<Result> results = searchService.search(q, 10);

        Map<String, String> map = new LinkedHashMap<>();
        for (Result r : results) {
            String sourceName = r.getOffer().getSource(); // 例如 "Shopee", "Momo"

            // --- 過濾邏輯 ---
            // 如果 sources 不為空，且該結果的來源不在 sources 列表中，就跳過
            if (sources != null && !sources.isEmpty()) {
                boolean isAllowed = false;
                for (String allowedSource : sources) {
                    // 使用 contains 比較寬鬆的匹配 (忽略大小寫)
                    if (sourceName.toLowerCase().contains(allowedSource.toLowerCase())) {
                        isAllowed = true;
                        break;
                    }
                }
                if (!isAllowed) {
                    continue; // 跳過這個結果
                }
            }
            // ----------------

            String title = sourceName + " - " + r.getOffer().getBrand() + " " + r.getOffer().getModel();
            if (title.trim().isEmpty()) {
                title = sourceName;
            }

            // 組合顯示字串 (標題 | 價格)
            map.put(title + " | pay=" + r.getPay(), r.getOffer().getUrl());
        }
        return map;
    }
}
