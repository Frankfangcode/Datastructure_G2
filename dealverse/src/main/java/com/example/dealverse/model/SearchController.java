package com.example.dealverse.model;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class SearchController {

    @GetMapping("/")
    public String home() { return "index"; }

    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        try {
            var results = new GoogleQuery(keyword).query();
            model.addAttribute("keyword", keyword);
            model.addAttribute("results", results);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "index";
    }
}
