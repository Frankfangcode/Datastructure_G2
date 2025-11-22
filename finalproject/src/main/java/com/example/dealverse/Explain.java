package com.example.dealverse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Explain {
    private List<String> messages = new ArrayList<>();
    private List<String> sources = new ArrayList<>();
    private Instant ts = Instant.now();

    public void addMessage(String msg) {
        messages.add(msg);
    }

    public void addSource(String s) {
        sources.add(s);
    }

    public List<String> getMessages() { return messages; }
    public List<String> getSources() { return sources; }
    public Instant getTs() { return ts; }
}
