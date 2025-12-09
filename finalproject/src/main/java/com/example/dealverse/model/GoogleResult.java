package com.example.dealverse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleResult {

    private String title;
    private String link;
    private String snippet;

    public GoogleResult() {
        // Jackson 需要無參建構子
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {     
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    @Override
    public String toString() {
        return "GoogleResult{title='" + title + "', link='" + link + "'}";
    }
}
