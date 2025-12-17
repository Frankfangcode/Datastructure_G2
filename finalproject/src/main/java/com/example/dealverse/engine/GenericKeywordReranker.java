package com.example.dealverse.engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.example.dealverse.model.Query;
import com.example.dealverse.model.Result;

@Component
public class GenericKeywordReranker {

    // 常見「非商品頁」訊號（出現就強烈扣分）
    private static final Set<String> NON_PRODUCT_HINTS = Set.of(
            "推薦", "好評推薦", "排行榜", "熱銷", "活動", "優惠", "折扣", "領券",
            "分類", "category", "lgrpcategory", "dgrpcategory",
            "搜尋", "search", "q=", "keyword=", "store", "sites"
    );

    // ✅ URL 判斷：商品頁常見 pattern（加分）
    private static final Set<String> PRODUCT_URL_HINTS = Set.of(
            // momo
            "goodsdetail", "goodsdetail.jsp", "i_code=",
            // pchome
            "/prod/", "/books/prod/",
            // shopee
            "shopee.tw/", "/i.", "i."
    );

    // ✅ URL 判斷：非商品頁常見 pattern（扣分）
    private static final Set<String> NON_PRODUCT_URL_HINTS = Set.of(
            // momo
            "/category/", "category", "grp", "lgrp", "dgrp", "/edm/",
            // pchome
            "/search/", "sites/",
            // 通用
            "q=", "keyword=", "event", "promo"
    );

    // 若完全沒命中任何 query token 就扣分
    private static final double NO_TOKEN_PENALTY = 6.0;

    // 中英數 token
    private static final Pattern TOKEN = Pattern.compile("[A-Za-z0-9]+|[\\u4e00-\\u9fff]+");
    // 常見型號片段（含數字的 token）
    private static final Pattern MODEL = Pattern.compile("(?i)\\b([a-z]{0,4}\\d{2,6}[a-z]{0,4}|\\d{2,6}[a-z]{0,4})\\b");

    public List<Result> rerank(Query query, List<Result> candidates, int topK) {
        if (candidates == null || candidates.isEmpty()) return List.of();

        String qRaw = (query == null ? "" : query.getText());
        String qText = normalize(qRaw);
        if (qText.isBlank()) return fallback(candidates, topK);

        Set<String> qTokens = new LinkedHashSet<>(tokenize(qText));
        String qModel = extractModel(qText);

        List<Scored> scored = new ArrayList<>(candidates.size());

        for (Result r : candidates) {
            String rawTitle = titleOf(r);
            String title = normalize(rawTitle);

            String rawUrl = (r == null || r.getOffer() == null) ? "" : safe(r.getOffer().getUrl());
            String url = rawUrl.toLowerCase(Locale.ROOT);

            double rel = scoreOne(qText, qTokens, qModel, title, url);

            System.out.println("[RERANK] rel=" + rel + " title=" + rawTitle);
            scored.add(new Scored(r, rel));
        }

        scored.sort((a, b) -> {
            int c = Double.compare(b.rel, a.rel);
            if (c != 0) return c;

            // relevance 相同：沿用你原本排序精神
            c = Double.compare(b.r.getSaving(), a.r.getSaving());
            if (c != 0) return c;

            return Double.compare(a.r.getPay(), b.r.getPay());
        });

        List<Result> out = new ArrayList<>();
        for (int i = 0; i < scored.size() && out.size() < topK; i++) out.add(scored.get(i).r);
        return out;
    }

    private double scoreOne(String qText, Set<String> qTokens, String qModel, String title, String url) {
        if (title.isBlank()) return -999;

        double s = 0;

        // 1) token 命中
        boolean hitAnyToken = false;
        for (String tok : qTokens) {
            if (tok.length() <= 1) continue;
            if (title.contains(tok)) {
                s += 1.2;
                hitAnyToken = true;
            }
        }

        // 2) phrase bonus
        if (title.contains(qText)) s += 4.0;

        // 3) 型號 bonus
        if (qModel != null && !qModel.isBlank() && title.contains(qModel)) s += 5.0;

        // 4) 非商品頁扣分（標題文字）
        int noiseHits = countHits(title, NON_PRODUCT_HINTS);
        if (noiseHits > 0) s -= 4.0 * noiseHits;

        // 5) ✅ 用 URL 判斷商品頁/非商品頁（比配件詞更穩）
        int productUrlHits = countHits(url, PRODUCT_URL_HINTS);
        int nonProductUrlHits = countHits(url, NON_PRODUCT_URL_HINTS);

        if (productUrlHits > 0) s += 3.0 * productUrlHits;
        if (nonProductUrlHits > 0) s -= 3.5 * nonProductUrlHits;

        // 6) 完全沒命中任何 token：強扣分，避免 0 分亂排
        if (!hitAnyToken) s -= NO_TOKEN_PENALTY;

        return s;
    }

    private int countHits(String text, Set<String> terms) {
        int c = 0;
        if (text == null || text.isBlank()) return 0;
        for (String t : terms) if (text.contains(t)) c++;
        return c;
    }

    private String extractModel(String text) {
        Matcher m = MODEL.matcher(text);
        if (m.find()) return m.group(1).toLowerCase(Locale.ROOT);
        return null;
    }

    private List<String> tokenize(String s) {
        List<String> out = new ArrayList<>();
        Matcher m = TOKEN.matcher(s);
        while (m.find()) {
            String tok = m.group().toLowerCase(Locale.ROOT).trim();
            if (!tok.isBlank()) out.add(tok);
        }
        return out;
    }

    private String normalize(String s) {
        s = (s == null ? "" : s).toLowerCase(Locale.ROOT);
        s = s.replaceAll("[\\[\\]【】()（）{}<>「」\"'`]", " ");
        s = s.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\p{IsHan}\\s]", " ");
        s = s.replaceAll("\\s+", " ").trim();
        return s;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private List<Result> fallback(List<Result> results, int k) {
        return results.stream()
                .sorted(Comparator.comparing(Result::getSaving).reversed().thenComparing(Result::getPay))
                .limit(k)
                .toList();
    }

    private String titleOf(Result r) {
        if (r == null || r.getOffer() == null) return "";
        String t = r.getOffer().getTitle();
        if (t != null && !t.isBlank()) return t;
        String url = r.getOffer().getUrl();
        return (url == null ? "" : url);
    }

    private static class Scored {
        final Result r;
        final double rel;
        Scored(Result r, double rel) { this.r = r; this.rel = rel; }
    }
}
