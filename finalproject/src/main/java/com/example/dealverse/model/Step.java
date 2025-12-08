package com.example.dealverse.model;

public class Step {
    private String type;   // COUPON / PAY / SHIPPING ...
    private String text;   // Ex: "領取 3000 折價券"
    private String url;    // 相關連結

    public Step() {}

    public Step(String type, String text, String url) {
        this.type = type;
        this.text = text;
        this.url = url;
    }

    public String getType() { return type; }
    public String getText() { return text; }
    public String getUrl() { return url; }

    public void setType(String type) { this.type = type; }
    public void setText(String text) { this.text = text; }
    public void setUrl(String url) { this.url = url; }
}
