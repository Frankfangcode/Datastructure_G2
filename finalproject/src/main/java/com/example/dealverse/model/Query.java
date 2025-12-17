package com.example.dealverse.model;

import java.util.ArrayList;
import java.util.List;

public class Query {

    private String text;
    private String channel; // ONLINE / STORE 等

    // ✅ 舊版：單一來源（保留，向下相容）
    private String site;

    // ✅ 新版：多來源（shopee / momo / pchome / amazon）
    private List<String> sources = new ArrayList<>();

    // ---------- constructors ----------
    public Query(String text) {
        this(text, "ONLINE");
    }

    public Query(String text, String channel) {
        this.text = text;
        this.channel = channel;
    }

    // ---------- getters ----------
    public String getText() {
        return text;
    }

    public String getChannel() {
        return channel;
    }

    public String getSite() {
        return site;
    }

    public List<String> getSources() {
        return sources;
    }

    // ---------- setters ----------
    public void setSite(String site) {
        this.site = site;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    // ---------- helper（🔥 核心方法） ----------
    /**
     * 判斷某個 connector source 是否允許執行
     * 規則：
     * 1. 如果有 sources → 以 sources 為準
     * 2. 否則如果有 site → 只允許該 site
     * 3. 都沒設 → 全部允許
     */
    public boolean allowSource(String source) {
        if (source == null) return false;

        // 1️⃣ 多站模式（優先）
        if (sources != null && !sources.isEmpty()) {
            return sources.contains(source.toLowerCase());
        }

        // 2️⃣ 單站相容模式
        if (site != null && !site.isBlank()) {
            return site.equalsIgnoreCase(source);
        }

        // 3️⃣ 都沒指定 → 全開
        return true;
    }
}

