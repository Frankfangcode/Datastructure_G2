package com.example.dealverse;

import java.util.Objects;

public class Query {
    private final String text;
    private final String channel; // ONLINE / STORE ...

    public Query(String text, String channel) {
        this.text = text == null ? "" : text.trim();
        this.channel = (channel == null || channel.isBlank()) ? "ONLINE" : channel;
    }

    public String getText() {
        return text;
    }

    public String getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return "Query{" + "text='" + text + '\'' + ", channel='" + channel + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Query)) return false;
        Query query = (Query) o;
        return Objects.equals(text, query.text) && Objects.equals(channel, query.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, channel);
    }
}
