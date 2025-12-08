package com.example.dealverse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ShopeeConnector implements Connector {

    private static final String BASE_URL = "https://shopee.tw/search?keyword=";
    // 一次抓幾頁，可自行調整（太多會很慢）
    private static final int MAX_PAGES = 2;

    @Override
    public String getSourceName() {
        return "Shopee";
    }

    @Override
    public List<RawOffer> fetch(Query query) {
        // 若沒輸入東西，就直接回空
        if (query == null || query.getText() == null || query.getText().isBlank()) {
            return Collections.emptyList();
        }

        List<RawOffer> results = new ArrayList<>();
        Set<String> seenUrls = new HashSet<>();

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");

        // 關通知
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);

        WebDriver driver = new ChromeDriver(options);

        try {
            String encodedKeyword = URLEncoder.encode(query.getText(), StandardCharsets.UTF_8);
            Instant now = Instant.now();

            for (int page = 0; page < MAX_PAGES; page++) {
                String url = BASE_URL + encodedKeyword + "&page=" + page;
                driver.get(url);

                // 等頁面載入
                Thread.sleep(3000);

                // 模仿人類往下滑，讓更多商品載入
                for (int s = 0; s < 6; s++) {
                    ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,1000)");
                    Thread.sleep(1200);
                }

                // 對齊你 Python：找 data-sqe="item"
                List<WebElement> blocks =
                        driver.findElements(By.xpath("//*[@data-sqe='item']"));

                for (WebElement block : blocks) {
                    String inner = block.getAttribute("innerHTML");
                    if (inner == null || inner.isBlank()) continue;

                    Document soup = Jsoup.parse(inner);
                    Element anchor = soup.selectFirst("a");
                    if (anchor == null) continue;

                    String href = anchor.attr("href");
                    if (href == null || href.isBlank()) continue;

                    String fullUrl = "https://shopee.tw" + href;
                    if (!seenUrls.add(fullUrl)) {
                        // 已經抓過這個商品就跳過
                        continue;
                    }

                    // 商品名稱：對齊 Python 的邏輯
                    Element nameDiv = soup.selectFirst("div[data-sqe=name] div");
                    if (nameDiv == null) continue;
                    String title = nameDiv.text();
                    if (title == null || title.isBlank()) continue;

                    // 取得 name 區塊的 parent，然後找第二個 div 當價格容器
                    Element nameWrapper = soup.selectFirst("div[data-sqe=name]");
                    if (nameWrapper == null) continue;
                    Element parent = nameWrapper.parent();
                    if (parent == null) continue;

                    List<Element> directDivs = parent.children()
                            .stream()
                            .filter(e -> e.tagName().equals("div"))
                            .collect(Collectors.toList());

                    if (directDivs.size() < 2) {
                        // 跟 Python 一樣，抓不到價格就放棄這個商品
                        continue;
                    }

                    Element priceContainer = directDivs.get(1);
                    Elements spans = priceContainer.select("span");

                    List<Integer> prices = new ArrayList<>();
                    for (Element span : spans) {
                        String t = span.text();
                        if (t == null) continue;

                        // 清洗價格字串：移除「萬、$、,、空白」
                        t = t.replace("萬", "");
                        t = t.replace("$", "");
                        t = t.replace(",", "");
                        t = t.replace(" ", "");

                        if (!t.isEmpty() && t.chars().allMatch(Character::isDigit)) {
                            try {
                                prices.add(Integer.parseInt(t));
                            } catch (NumberFormatException ignored) {}
                        }
                    }

                    if (prices.isEmpty()) {
                        continue; // 沒抓到價格就略過
                    }

                    double avgPrice = prices.stream()
                            .mapToInt(Integer::intValue)
                            .average()
                            .orElse(0.0);

                    // 目前還沒爬到品牌 / 型號 / SKU，就交給 Normalizer 之後去解析 title
                    String brand = null;
                    String model = null;
                    String sku = null;
                    Map<String, String> attrsJson = Collections.emptyMap();
                    double shippingFee = 0.0; // 詳細運費之後可另外估

                    RawOffer offer = new RawOffer(
                            getSourceName(),
                            title,
                            brand,
                            model,
                            sku,
                            attrsJson,
                            avgPrice,
                            shippingFee,
                            fullUrl,
                            now
                    );

                    results.add(offer);
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // 可以換成 logger
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return results;
    }
}
