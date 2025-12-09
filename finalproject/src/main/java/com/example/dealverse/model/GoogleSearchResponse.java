package com.example.dealverse.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleSearchResponse {

    // 對應 JSON 的 "items": [ {title, link, snippet, ...}, ... ]
    private List<GoogleResult> items;

    public GoogleSearchResponse() {}

    public List<GoogleResult> getItems() {
        return items;
    }

    public void setItems(List<GoogleResult> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "GoogleSearchResponse{items=" +
                (items == null ? "null" : items.size() + " items") +
                '}';
    }
}
