package com.example.dealverse.connector;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.dealverse.config.GoogleSearchProperties;

@Service
public class GoogleSearchService {

    private final GoogleSearchProperties props;
    private final RestTemplate restTemplate = new RestTemplate();

    public GoogleSearchService(GoogleSearchProperties props) {
        this.props = props;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> search(String queryText) {
        String url = "https://www.googleapis.com/customsearch/v1"
                + "?key=" + props.getApiKey()
                + "&cx=" + props.getCx()
                + "&q=" + urlEncode(queryText);

        // ⭐ 印出實際請求的 URL（你可以複製到瀏覽器測試）
        System.out.println("🔍 [Google API Request] " + url);

        Map<String, Object> response = null;

        try {
            response = restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            System.out.println("❌ [Google API ERROR] " + e.getMessage());
            return Map.of();  // 回傳空結果
        }

        // ⭐ 印出 API 回傳原始 JSON
        System.out.println("📦 [Google API Raw Response] " + response);

        Map<String, String> result = new LinkedHashMap<>();

        if (response == null) {
            System.out.println("❌ response = null");
            return result;
        }

        Object itemsObj = response.get("items");

        if (!(itemsObj instanceof List<?>)) {
            System.out.println("⚠️ No items found in response.");
            return result;
        }

        List<?> items = (List<?>) itemsObj;

        System.out.println("📄 [Google API Items Count] " + items.size());

        for (Object item : items) {
            if (item instanceof Map<?, ?>) {
                Map<String, Object> m = (Map<String, Object>) item;
                String title = (String) m.get("title");
                String link = (String) m.get("link");
                if (title != null && link != null) {
                    result.put(title, link);
                }
            }
        }

        return result;
    }

    public Map<String, String> searchSite(String queryText, String siteDomain) {
        String q = "site:" + siteDomain + " " + queryText;
        return search(q);
    }

    private String urlEncode(String s) {
        try {
            return java.net.URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }
}
