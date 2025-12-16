package com.example.dealverse.model;

public class Query {

    private String text;
    private String channel; // ONLINE / STORE 等

    // 指定來源站台（shopee / momo / pchome）
    private String site;

    // 原本用法完全不變
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

    // ---------- setters ----------
    public void setSite(String site) {
        this.site = site;
    }
}
