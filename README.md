# Datastructure_G2
資料結構第二組


Final Project
# Dealverse Search

Dealverse 是一個簡易的「跨平台價格比較引擎」，目前支援從 **momo / PChome / Shopee** 三個網站，用 **Google Custom Search API (CSE)** 抓取商品搜尋結果，並在前端統一呈現。

---

## 1. 系統架構概念

使用者在首頁輸入關鍵字（例如：`滑鼠`、`iphone 17`）：

1. 前端 `index.html` 送出查詢請求到後端（Spring Boot REST API）。
2. 後端 `SearchController` 收到查詢字串，建立 `Query` 物件。
3. `Aggregator` 呼叫三個 Connector：
   - `MomoGoogleConnector`
   - `PchomeGoogleConnector`
   - `ShopeeGoogleConnector`
4. 每個 Connector 透過 `GoogleSearchService` 呼叫 Google Custom Search API：
   - 使用同一組 `cx`（自訂搜尋引擎）
   - 使用相同關鍵字 `q`
5. `GoogleSearchService` 將 JSON 解析為：
   - `GoogleSearchResponse`（整體回應）
   - `GoogleResult`（單筆搜尋結果：title / link / snippet）
6. 每個 Connector 將 `GoogleResult` 轉成統一的 `RawOffer`：
   - `title`：商品標題
   - `source`：momo / pchome / shopee
   - `url`：商品頁面連結
7. `Aggregator` 合併所有 `RawOffer`，再回傳給前端顯示。

---




