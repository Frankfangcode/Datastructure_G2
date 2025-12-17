package com.example.dealverse.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private final HttpClient httpClient;

    public OpenAiService() {
        // 設定 3 秒超時，避免搜尋卡太久
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    public String refineQuery(String userQuery) {
        // 如果使用者輸入太短或已經是英文，可能不需要翻譯 (可選邏輯)
        if (userQuery == null || userQuery.trim().isEmpty()) {
            return "";
        }

        try {
            // 1. 準備 Prompt：這段是關鍵,告訴 AI 把口語轉成購物關鍵字
            String systemPrompt = "You are a query optimizer for a shopping bot. " +
                    "Your task: Convert user input into concise English keywords for Google Shopping search. " +
                    "CRITICAL RULES: " +
                    "1. ALWAYS translate non-English text (Chinese, Japanese, etc.) to English. " +
                    "2. Use SINGLE English words or hyphenated terms (NO SPACES between words). " +
                    "3. Remove filler words and keep only essential keywords. " +
                    "4. Output ONLY the English keyword, nothing else. " +
                    "Examples: " +
                    "- Input: '手機' → Output: 'phone' " +
                    "- Input: '記憶體' → Output: 'memory' " +
                    "- Input: '筆記型電腦' → Output: 'laptop' " +
                    "- Input: '藍牙耳機' → Output: 'bluetooth-headphones' " +
                    "- Input: '無線滑鼠' → Output: 'wireless-mouse'";

            // 2. 建構 JSON Body
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", model);

            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "system").put("content", systemPrompt));
            messages.put(new JSONObject().put("role", "user").put("content", userQuery));
            jsonBody.put("messages", messages);
            jsonBody.put("temperature", 0.3); // 低溫度讓結果更穩定

            // 3. 發送 HTTP POST 請求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                    .timeout(Duration.ofSeconds(5)) // 等待回應最多 5 秒
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 4. 解析回傳結果
            if (response.statusCode() == 200) {
                JSONObject responseJson = new JSONObject(response.body());
                String refinedContent = responseJson.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                // 去除可能的多餘引號或換行
                return refinedContent.trim().replaceAll("^\"|\"$", "");
            } else {
                System.err.println("OpenAI API Error: " + response.statusCode() + " " + response.body());
                return userQuery; // 失敗時降級使用原始查詢
            }

        } catch (Exception e) {
            e.printStackTrace();
            return userQuery; // 發生例外時降級使用原始查詢
        }
    }

    /**
     * 取得 AI 推薦的相關商品關鍵字
     * 
     * @param userQuery 使用者輸入的查詢
     * @return 逗號分隔的推薦關鍵字字串，例如 "wireless-earbuds,bluetooth-speaker,smartwatch"
     */
    public String getRecommendations(String userQuery) {
        if (userQuery == null || userQuery.trim().isEmpty()) {
            return "";
        }

        try {
            // 1. 準備 Prompt：讓 AI 推薦相關商品
            String systemPrompt = "You are a shopping recommendation assistant. " +
                    "Given a product search query, suggest 3-5 related product keywords that users might also be interested in. "
                    +
                    "CRITICAL RULES: " +
                    "1. Output ONLY comma-separated English keywords (NO spaces after commas). " +
                    "2. Use single words or hyphenated terms (e.g., 'wireless-mouse', 'laptop-stand'). " +
                    "3. Suggest products that are complementary or in the same category. " +
                    "4. NO explanations, NO numbering, ONLY the comma-separated list. " +
                    "Examples: " +
                    "- Input: 'phone' → Output: 'phone-case,screen-protector,wireless-charger,earbuds' " +
                    "- Input: 'laptop' → Output: 'laptop-bag,mouse,keyboard,monitor,laptop-stand' " +
                    "- Input: 'camera' → Output: 'camera-lens,tripod,memory-card,camera-bag'";

            // 2. 建構 JSON Body
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", model);

            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "system").put("content", systemPrompt));
            messages.put(new JSONObject().put("role", "user").put("content", userQuery));
            jsonBody.put("messages", messages);
            jsonBody.put("temperature", 0.7); // 較高溫度讓推薦更多樣化

            // 3. 發送 HTTP POST 請求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                    .timeout(Duration.ofSeconds(5))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 4. 解析回傳結果
            if (response.statusCode() == 200) {
                JSONObject responseJson = new JSONObject(response.body());
                String recommendations = responseJson.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                // 清理結果：移除多餘空白和引號
                return recommendations.trim().replaceAll("^\"|\"$", "");
            } else {
                System.err.println(
                        "OpenAI API Error (Recommendations): " + response.statusCode() + " " + response.body());
                return ""; // 失敗時回傳空字串
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ""; // 發生例外時回傳空字串
        }
    }
}
