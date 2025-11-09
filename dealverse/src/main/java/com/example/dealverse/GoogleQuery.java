package com.example.dealverse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GoogleQuery {
    private String url;
    private String content;

    public GoogleQuery(String searchKeyword) {
        try {
            String enc = URLEncoder.encode(searchKeyword, "utf-8");
            this.url = "https://www.google.com/search?q=" + enc + "&oe=utf8&num=20";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String fetchContent() throws IOException {
        StringBuilder sb = new StringBuilder();
        URLConnection conn = new URL(url).openConnection();
        conn.setRequestProperty("User-agent", "Chrome/119.0");
        try (BufferedReader br =
                     new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    public HashMap<String, String> query() throws IOException {
        if (content == null) content = fetchContent();
        HashMap<String, String> map = new HashMap<>();

        Document doc = Jsoup.parse(content);
        Elements blocks = doc.select("div.kCrYT");
        for (Element li : blocks) {
            try {
                String link = li.select("a").attr("href").replace("/url?q=", "");
                String title = li.select(".vvjwJb").text();
                if (!title.isEmpty() && !link.isEmpty()) map.put(title, link);
            } catch (Exception ignore) {}
        }
        return map;
    }
}
