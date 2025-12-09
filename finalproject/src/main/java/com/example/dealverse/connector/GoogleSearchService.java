package com.example.dealverse.connector;

import com.example.dealverse.model.GoogleResult;
import com.example.dealverse.model.GoogleSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoogleSearchService {

    @Value("${google.api-key}")
    private String apiKey;

    @Value("${google.cx}")
    private String cx;

    private static final String BASE_URL = "https://www.googleapis.com/customsearch/v1";

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 在指定 site 上搜尋 keyword，回傳 Map<title, link>
     */
    public Map<String, String> searchSite(String keyword, String domain) {
        System.out.println("[searchSite] keyword = " + keyword + ", site = " + domain);

        URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("key", apiKey)
                .queryParam("cx", cx)
                .queryParam("q", keyword)             // 只放純關鍵字
                .queryParam("siteSearch", domain)     // ✅ 真正鎖定網域
                .queryParam("siteSearchFilter", "i")  // ✅ include：只搜尋這個網域
                .build(true)
                .toUri();

        System.out.println("[Google API URL] " + uri);

        GoogleSearchResponse result = restTemplate.getForObject(uri, GoogleSearchResponse.class);

        Map<String, String> map = new LinkedHashMap<>();

        if (result == null) {
            System.out.println("[searchSite] result is null");
            return map;
        }

        List<GoogleResult> items = result.getItems();
        if (items == null || items.isEmpty()) {
            System.out.println("[searchSite] items is null/empty, result = " + result);
            return map;
        }

        for (GoogleResult gr : items) {
            String title = gr.getTitle();
            String link = gr.getLink();
            if (title == null || link == null) continue;

            // 再用 host 檢查一次（雙保險）
            if (!urlMatchesDomain(link, domain)) continue;

            map.put(title, link);
        }

        System.out.println("[searchSite] filtered size = " + map.size() + ", domain = " + domain);
        return map;
    }

    private boolean urlMatchesDomain(String url, String expectedDomain) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost(); // 例如 www.momoshop.com.tw
            return host != null && host.endsWith(expectedDomain);
        } catch (Exception e) {
            return false;
        }
    }
}
