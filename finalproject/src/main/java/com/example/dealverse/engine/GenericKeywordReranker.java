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

    // 通用配件/周邊詞（不綁特定品類）
    private static final Set<String> ACCESSORY_TERMS = Set.of(
            "殼","保護殼","保護套","皮套","套","膜","貼","保護貼","貼膜","玻璃貼",
            "線","線材","充電線","傳輸線","網路線","延長線",
            "轉接","轉接頭","轉換","接頭","轉換器","hub","dongle",
            "支架","底座","架","掛架",
            "包","收納包","保護包","袋","盒",
            "墊","滑鼠墊",
            "鍵帽","腕托","保護蓋"
    );

    // 中英數 token
    private static final Pattern TOKEN = Pattern.compile("[A-Za-z0-9]+|[\\u4e00-\\u9fff]+");
    // 常見型號片段（含數字的 token）
    private static final Pattern MODEL = Pattern.compile("(?i)\\b([a-z]{0,4}\\d{2,6}[a-z]{0,4}|\\d{2,6}[a-z]{0,4})\\b");

    public List<Result> rerank(Query query, List<Result> candidates, int topK) {
        if (candidates == null || candidates.isEmpty()) return List.of();

        String qText = normalize(query == null ? "" : query.toString());
        if (qText.isBlank()) return fallback(candidates, topK);

        Set<String> qTokens = new LinkedHashSet<>(tokenize(qText));
        String qModel = extractModel(qText);

        // query 是否在找配件？（query 本身含配件詞就視為允許配件）
        boolean queryWantsAccessory = containsAny(qText, ACCESSORY_TERMS);

        List<Scored> scored = new ArrayList<>(candidates.size());
        for (Result r : candidates) {
            String rawTitle = titleOf(r);
            String title = normalize(rawTitle);
            double rel = scoreOne(qText, qTokens, qModel, title, queryWantsAccessory);
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

    private double scoreOne(String qText, Set<String> qTokens, String qModel, String title, boolean queryWantsAccessory) {
        if (title.isBlank()) return -999;

        double s = 0;

        // 1) token 命中
        for (String tok : qTokens) {
            if (tok.length() <= 1) continue;
            if (title.contains(tok)) s += 1.2;
        }

        // 2) phrase bonus
        if (title.contains(qText)) s += 4.0;

        // 3) 型號 bonus
        if (qModel != null && !qModel.isBlank() && title.contains(qModel)) s += 5.0;

        // 4) 通用配件懲罰：只有當 query 不是在找配件時才扣
        if (!queryWantsAccessory) {
            int accessoryHits = countHits(title, ACCESSORY_TERMS);
            if (accessoryHits > 0) s -= 3.0 * accessoryHits; // 命中越多扣越多
        }

        return s;
    }

    private int countHits(String text, Set<String> terms) {
        int c = 0;
        for (String t : terms) if (text.contains(t)) c++;
        return c;
    }

    private boolean containsAny(String text, Set<String> terms) {
        for (String t : terms) if (text.contains(t)) return true;
        return false;
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

    private List<Result> fallback(List<Result> results, int k) {
        return results.stream()
                .sorted(Comparator.comparing(Result::getSaving).reversed().thenComparing(Result::getPay))
                .limit(k)
                .toList();
    }

    private String titleOf(Result r) {
        if (r == null || r.getOffer() == null) return "";
        String t = r.getOffer().getTitle();
        if (t != null && !t.isBlank()) return t;   // ✅ 優先用真正標題
        return r.getOffer().getUrl() == null ? "" : r.getOffer().getUrl(); // fallback
}


    private static class Scored {
        final Result r;
        final double rel;
        Scored(Result r, double rel) { this.r = r; this.rel = rel; }
    }
}
