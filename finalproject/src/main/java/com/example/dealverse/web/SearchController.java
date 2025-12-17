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
                         // checkbox: sources=Shopee&sources=Momo...
                         @RequestParam(required = false) List<String> sources,
                         // 舊參數相容：?site=shopee
                         @RequestParam(required = false, defaultValue = "") String site,
                         Model model) {

        try {
            // 1) UI 沒勾 → 預設全平台（含 Amazon）
            if (sources == null || sources.isEmpty()) {
                sources = List.of("Shopee", "Momo", "PChome", "Amazon");
            }

            // 2) 若 site 有值：覆蓋 sources，代表只查單站（相容舊用法）
            if (site != null && !site.isBlank()) {
                String only = normalizeSiteToSource(site);
                if (!only.isBlank()) {
                    sources = toUiSourceLabelList(only); // 給 UI 回填用
                }
            }

            // 3) 轉成後端可用的 sources（小寫，對應 connector.getSourceName()）
            List<String> normalizedSources = normalizeSources(sources);

            // 4) 呼叫新版（多站）：AI 翻譯 + 推薦 + 多站搜尋
            //    你需要讓 DealVerseSearchService.searchWithAi(...) 接 List<String>
            SearchResponse resp = dealVerseSearchService.searchWithAi(keyword, normalizedSources, 10);

            // 5) 丟給 Thymeleaf
            model.addAttribute("keyword", resp.getOriginalKeyword());
            model.addAttribute("optimizedKeyword", resp.getOptimizedKeyword());
            model.addAttribute("recommendations", resp.getRecommendations());
            model.addAttribute("results", resp.getResults());

            // checkbox 回填（保持 UI 原本用的大寫格式：Shopee/Momo/PChome/Amazon）
            model.addAttribute("sources", sources);

            // 若你頁面還有用 site（可留著 debug）
            model.addAttribute("site", String.join(",", normalizedSources));

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

    // -------------------------
    // helpers
    // -------------------------

    /**
     * 把 UI sources（Shopee/Momo/PChome/Amazon）轉成後端 sources（shopee/momo/pchome/amazon）
     */
    private List<String> normalizeSources(List<String> sources) {
        List<String> out = new ArrayList<>();
        for (String s : sources) {
            if (s == null) continue;
            String v = s.trim().toLowerCase();
            if (v.contains("shopee")) out.add("shopee");
            else if (v.contains("momo")) out.add("momo");
            else if (v.contains("pchome")) out.add("pchome");
            else if (v.contains("amazon")) out.add("amazon");
        }
        // 若轉完反而空，當作全開
        if (out.isEmpty()) {
            return List.of("shopee", "momo", "pchome", "amazon");
        }
        return out;
    }

    /**
     * 相容舊參數 ?site=shopee/momo/pchome/amazon
     */
    private String normalizeSiteToSource(String site) {
        String s = site.trim().toLowerCase();
        if (s.contains("shopee")) return "shopee";
        if (s.contains("momo")) return "momo";
        if (s.contains("pchome")) return "pchome";
        if (s.contains("amazon")) return "amazon";
        return "";
    }

    /**
     * 用於「site 覆蓋 sources」時，把單一 source 轉成 UI label，讓 checkbox 回填正常
     */
    private List<String> toUiSourceLabelList(String source) {
        return switch (source) {
            case "shopee" -> List.of("Shopee");
            case "momo" -> List.of("Momo");
            case "pchome" -> List.of("PChome");
            case "amazon" -> List.of("Amazon");
            default -> List.of("Shopee", "Momo", "PChome", "Amazon");
        };
    }
}
