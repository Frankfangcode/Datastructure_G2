# Datastructure_G2
資料結構第二組


# DealVerse 系統目前的執行邏輯（後端完整說明）

以下是整個比價流程的架構圖（概念）：

```
User Input → Controller → GoogleSearchService → Connector (Shopee/PChome/Momo)
→ JsoupPriceExtractor → 整理 RawOffer → 回傳 List<Offer> → 前端渲染
```

---

# 1. 使用者在前端輸入關鍵字

例如：「iphone17」「airpods pro」「任天堂 switch」

前端會將搜尋字串送到後端：

```
GET /api/search?query=iphone17
```

---

# 2. Controller 接收到搜尋字串，呼叫 SearchService

Controller 不處理搜尋細節，它只做：

```java
searchService.search(queryText)
```

SearchService 會負責：

1. 串所有 Connector
2. 把結果合併
3. 回傳給 Controller

---

# 3. SearchService 呼叫所有 *Connector*

目前你的 Connector 有：

1. **ShopeeGoogleConnector**
2. **PchomeGoogleConnector**
3. **MomoGoogleConnector**

SearchService 做的事：

```java
for (Connector c : allConnectors) {
    rawOffers.addAll(c.fetch(query));
}
```

也就是說：

**每個 Connector 會各自從 Google 去找該網站的商品。**

---

# 4. Connector 的運作方式（最重要的邏輯）

每個 Connector 都提供：

* `getSourceName()` 例如 `"shopee"`
* `fetch(query)` → 回傳 RawOffer(list)

流程如下：

## Step 4-1：用 GoogleSearchService 搜尋該網站商品

以 Shopee 為例：

```java
map = googleSearchService.searchSite("iphone17", "shopee.tw");
```

searchSite() 會做：

### 4-1-1. 呼叫 Google Custom Search API

問 Google：

> 幫我搜尋 `iphone17` 但只限於 `shopee.tw` 範圍。

Google 回傳一堆結果 (title + link)。

例如：

```
"shopee - iphone17 保護殼" → https://shopee.tw/xxxxx
```

---

# 5. Connector 過濾資料（確保 domain 正確）

例如：

```java
if (!urlMatchesDomain(url, "shopee.tw")) continue;
```

這會避免：

* 廣告頁面
* 無效連結
* Google 自己的 redirect

---

# 6. Connector 建 RawOffer（商品原始資料）

RawOffer 是最基本的資料模型：

```
title
source (shopee/pchome/momo)
url
listPrice
shippingFee
```

---

# 7. Connector 呼叫 PriceExtractor（爬取價格）

這是剛新增的功能：

```
Integer price = priceExtractor.extractPrice(url, "shopee");
```

PriceExtractor 的工作是：

### Step 7-1. 使用 Jsoup 抓 HTML

```java
Document doc = Jsoup.connect(url).get();
```

### Step 7-2. 嘗試在 HTML 裡找價格元素

例如 momo：

```
#price
.priceArea
.prodPrice
```

Shopee：

```
._2Shl1j   (Shopee 會變動)
```

PChome：

```
span.price
```

### Step 7-3. 找不到 → 回傳 null → 代表價格抓不到

---

# 8. Connector 填入價格 + 運費

```java
rawOffer.setListPrice(price != null ? price : 0);
rawOffer.setShippingFee(0);
```

---

# 9. SearchService 整合所有 RawOffer

整合：

* Shopee 5 筆
* momo 8 筆
* PChome 3 筆

總共 16 筆 RawOffer。

SearchService 會：

* 轉成 Offer（標準化模型）
* 丟回 Controller

---

# 10. Controller 回傳 JSON 給前端

前端收到資料後：

```
[
  { "title": "...", "url": "...", "price": 299, "source": "shopee" },
  { "title": "...", "url": "...", "price": 350, "source": "momo" },
  ...
]
```

前端渲染成你現在看到的列表。

---

# 你的程式目前最大瓶頸（核心問題）

1. **價格抓不到 → 大部分網站反爬，不能直接 Jsoup 抓 HTML 動態內容**
   → Shopee / momo 價格使用 JavaScript 或 API，需要另一種策略。

2. **Google Search API 回傳的結果不精準**
   → 你要做 query cleaning or keyword refine。

3. **只能搜尋英文**
   → 因為 Google Custom Search 把中文拆掉了
   → 你需要：

   * URL encode
   * 改用 UTF-8
   * 或丟到 Gemini / DeepL 做 normalize

4. **前端顯示 price=0 是正常 fallback**
   → 需要改善價格抓取。


---




