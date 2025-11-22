package com.example.dealverse;

public class Step {
    private String type;
    private String label;
    private String url;

    public Step(String type, String label, String url) {
        this.type = type;
        this.label = label;
        this.url = url;
    }

    public String getType() { return type; }
    public String getLabel() { return label; }
    public String getUrl() { return url; }
}
