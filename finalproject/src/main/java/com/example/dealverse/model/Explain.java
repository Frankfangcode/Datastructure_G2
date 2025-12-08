package com.example.dealverse.model;

public class Explain {
    private String summary;
    private String detail;

    public Explain() {}

    public Explain(String summary, String detail) {
        this.summary = summary;
        this.detail = detail;
    }

    public String getSummary() { return summary; }
    public String getDetail() { return detail; }

    public void setSummary(String summary) { this.summary = summary; }
    public void setDetail(String detail) { this.detail = detail; }
}
