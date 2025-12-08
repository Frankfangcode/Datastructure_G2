package com.example.dealverse.model;

public class Query {
    private String text;
    private String channel; // ONLINE / STORE 等

    public Query(String text) {
        this(text, "ONLINE");
    }

    public Query(String text, String channel) {
        this.text = text;
        this.channel = channel;
    }

    public String getText() {
        return text;
    }

    public String getChannel() {
        return channel;
    }
}
