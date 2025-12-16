package com.example.dealverse.web;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
                         @RequestParam(required = false, defaultValue = "") String site,
                         Model model) {
        try {
            // ✅ 這裡改成把 site 傳下去
            Map<String, String> results = dealVerseSearchService.searchTitleUrl(keyword, site);

            model.addAttribute("keyword", keyword);
            model.addAttribute("site", site);
            model.addAttribute("results", results);
            model.addAttribute("error", null);
        } catch (Exception e) {
            model.addAttribute("keyword", keyword);
            model.addAttribute("site", site);
            model.addAttribute("results", null);
            model.addAttribute("error", e.getMessage());
        }
        return "index";
    }
}
