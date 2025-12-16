package com.example.dealverse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class MomoService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MomoService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String getMomoPrice(String goodsCode) {
        String url = "https://www.momoshop.com.tw/ajax/ajaxTool.jsp";

        // 1. 設定 Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        headers.set("Referer", "https://www.momoshop.com.tw/goods/GoodsDetail.jsp?i_code=" + goodsCode); // 必須有 Referer

        // 2. 設定 Payload (Form Data)
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("flag", "2018"); // 2018 通常是用於查詢價格/庫存的 flag
        map.add("goodsCode", goodsCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            // 3. 發送 POST 請求
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // 4. 解析 JSON
                JsonNode root = objectMapper.readTree(response.getBody());

                // 5. 提取價格
                if (root.has("price")) {
                    return root.get("price").asText();
                } else if (root.has("marketPrice")) {
                    return root.get("marketPrice").asText();
                } else {
                    // Log response for debugging if needed
                    System.out.println("MOMO Response: " + response.getBody());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "N/A";
    }
}
