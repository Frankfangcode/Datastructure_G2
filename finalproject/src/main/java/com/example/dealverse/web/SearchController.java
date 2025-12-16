package com.example.dealverse.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.dealverse.service.OpenAiService;

@Controller
public class SearchController {

    private final DealVerseSearchService dealVerseSearchService;
    private final OpenAiService openAiService; // 1. 宣告變數

    // 2. 建構子注入 OpenAiService
    public SearchController(DealVerseSearchService dealVerseSearchService, OpenAiService openAiService) {
        this.dealVerseSearchService = dealVerseSearchService;
        this.openAiService = openAiService;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "sources", required = false) List<String> sources, // 1. 接收平台列表
            Model model) {

        try {
            // 2. 處理預設值：如果沒選，預設找這三家
            if (sources == null || sources.isEmpty()) {
                sources = Arrays.asList("Shopee", "Momo", "PChome");
            }

            // 3. AI 翻譯/優化查詢 (從中文轉精準英文/關鍵字)
            String optimizedKeyword = openAiService.refineQuery(keyword);

            // 4. AI 取得推薦商品 (互動邏輯：搜尋後跳出推薦)
            String recString = openAiService.getRecommendations(keyword);
            List<String> recommendations = new ArrayList<>();
            if (recString != null && !recString.isEmpty()) {
                recommendations = Arrays.asList(recString.split(","));
            }

            // 5. 執行搜尋 (傳入 sources 進行過濾)
            Map<String, String> results = dealVerseSearchService.searchTitleUrl(optimizedKeyword, sources);

            // 6. 回傳資料給 View
            model.addAttribute("keyword", keyword);
            model.addAttribute("optimizedKeyword", optimizedKeyword);
            model.addAttribute("recommendations", recommendations);
            model.addAttribute("results", results);
            model.addAttribute("sources", sources); // 讓前端 checkbox 保持勾選狀態

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("keyword", keyword);
            model.addAttribute("error", "搜尋發生錯誤: " + e.getMessage());
        }
        return "index";
    }
}
