package com.example.dealverse.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dealverse.model.SearchResponse;

@Controller
public class SearchController {

    private final DealVerseSearchService dealVerseSearchService;

    public SearchController(DealVerseSearchService dealVerseSearchService) {
        this.dealVerseSearchService = dealVerseSearchService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword,
            // 你的新版 UI 用的是 sources（checkbox）
            @RequestParam(required = false) List<String> sources,
            // 你舊版可能有 site；保留但不強依賴
            @RequestParam(required = false, defaultValue = "") String site,
            Model model) {

        try {
            // 1) 若 sources 沒勾，預設全平台
            if (sources == null || sources.isEmpty()) {
                sources = List.of("Shopee", "Momo", "PChome");
            }

            // 2) 決定要傳給後端的 site（你目前 Query 是 setSite(site) 來過濾）
            //    目前先用「只支援單一站台」的方式：優先用 sources 第一個
            //    （若你要支援多站台並行搜尋，需要改 Query/FetcherPool 支援 list）
            String chosenSite = normalizeSite(site, sources);

            // 3) 走新版：AI 翻譯 + 推薦 + 搜尋
            SearchResponse resp
                    = dealVerseSearchService.searchWithAi(keyword, chosenSite, 10);

            // 4) 塞進前端需要的資料
            model.addAttribute("keyword", resp.getOriginalKeyword());
            model.addAttribute("optimizedKeyword", resp.getOptimizedKeyword());
            model.addAttribute("recommendations", resp.getRecommendations());
            model.addAttribute("results", resp.getResults());

            // 5) checkbox 勾選狀態要回填（你 HTML 用 sources.contains('Shopee')）
            model.addAttribute("sources", sources);

            // 若你頁面還有用 site（舊版）也給它
            model.addAttribute("site", chosenSite);

            model.addAttribute("error", null);
        } catch (Exception e) {
            model.addAttribute("keyword", keyword);
            model.addAttribute("optimizedKeyword", null);
            model.addAttribute("recommendations", null);
            model.addAttribute("results", null);

            model.addAttribute("sources", sources == null ? new ArrayList<>() : sources);
            model.addAttribute("site", site);

            model.addAttribute("error", e.getMessage());
        }

        return "index";
    }

    /**
     * 將 UI 的 sources / site 統一成 Query 目前能吃的單一站台字串。 你現在的引擎是 q.setSite(site)（通常期望
     * "shopee"/"momo"/"pchome"）
     */
    private String normalizeSite(String site, List<String> sources) {
        // 1) 若 query string 有傳 site，優先使用
        if (site != null && !site.isBlank()) {
            return site.trim().toLowerCase();
        }

        // 2) 否則用 sources 第一個當作 chosenSite
        //    UI 的 value 是 "Shopee"/"Momo"/"PChome"
        String first = sources.get(0);
        String s = first.trim().toLowerCase();

        // 對應你 connector 內部可能用的名稱
        if (s.contains("shopee")) {
            return "shopee";
        }
        if (s.contains("momo")) {
            return "momo";
        }
        if (s.contains("pchome")) {
            return "pchome";
        }

        // fallback：不過濾（視你的 FetcherPool 實作）
        return "";
    }
}
