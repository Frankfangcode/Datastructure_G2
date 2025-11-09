package com.example.dealverse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Controller
public class SearchController {
    private final GoogleSearchService google;

    public SearchController(GoogleSearchService google) {
        this.google = google;
    }

    @GetMapping("/")
    public String home() { return "index"; }

    @GetMapping("/ping")
    @ResponseBody
    public String ping() { return "ok"; }

    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        try {
            Map<String, String> results = google.search(keyword);
            model.addAttribute("keyword", keyword);
            model.addAttribute("results", results);
            if (results.isEmpty()) {
                model.addAttribute("error", "沒有抓到結果（檢查 Key、CX，或換關鍵字）。");
            }
        } catch (Exception e) {
            model.addAttribute("keyword", keyword);
            model.addAttribute("error", "搜尋失敗：" + e.getMessage());
        }
        return "index";
    }
}
