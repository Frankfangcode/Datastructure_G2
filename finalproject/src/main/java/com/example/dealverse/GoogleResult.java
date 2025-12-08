package com.example.dealverse;

public class GoogleResult {
    private final String title;
    private final String link;
    private final String snippet;
    private final Double priceHint; // 可能解析不到，就用 null

    public GoogleResult(String title, String link, String snippet, Double priceHint) {
        this.title = title;
        this.link = link;
        this.snippet = snippet;
        this.priceHint = priceHint;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getSnippet() {
        return snippet;
    }

    public Double getPriceHint() {
        return priceHint;
    }
}
