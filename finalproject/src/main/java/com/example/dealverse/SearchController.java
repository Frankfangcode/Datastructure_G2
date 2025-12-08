package com.example.dealverse;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SearchController {

    private final DealVerseSearchService dealverse;

    public SearchController(DealVerseSearchService dealverse) {
        this.dealverse = dealverse;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/ping")
    @ResponseBody
    public String ping() {
        return "ok";
    }

    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        model.addAttribute("keyword", keyword);

        try {
            Map<String, String> results = dealverse.search(keyword);
            model.addAttribute("results", results);

            if (results.isEmpty()) {
                model.addAttribute("error", "沒有搜尋到任何結果，可以換個商品或關鍵字再試試。");
            }
        } catch (Exception e) {
            model.addAttribute("error", "搜尋失敗：" + e.getMessage());
        }
        return "index";
    }
}
