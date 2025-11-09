package com.example.dealverse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoogleSearchService {

    @Value("${google.cse.enabled:true}")
    private boolean enabled;

    @Value("${google.cse.apiKey:}")
    private String apiKey;

    @Value("${google.cse.cx:}")
    private String cx;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public Map<String, String> search(String query) {
        var results = new LinkedHashMap<String, String>();

        if (!enabled) return results;
        if (apiKey == null || apiKey.isBlank() || cx == null || cx.isBlank()) return results;

        try {
            String q = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://www.googleapis.com/customsearch/v1"
                    + "?key=" + apiKey + "&cx=" + cx + "&num=10&q=" + q;

            ResponseEntity<Map> resp = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> body = resp.getBody();
            if (body == null) return results;

            Object itemsObj = body.get("items");
            if (itemsObj instanceof List<?> items) {
                for (Object it : items) {
                    if (it instanceof Map<?, ?> item) {
                        Object titleObj = item.get("title");
                        Object linkObj  = item.get("link");
                        String title = titleObj == null ? "" : titleObj.toString();
                        String link  = linkObj  == null ? "" : linkObj.toString();
                        if (!title.isBlank() && !link.isBlank()) {
                            results.put(title, link);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("CSE 呼叫失敗：" + e.getMessage(), e);
        }
        return results;
    }
}
