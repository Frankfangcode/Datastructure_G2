package com.example.dealverse.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "google.search")
public class GoogleSearchProperties {

    // application.yml:
    // google:
    //   search:
    //     api-key: xxx
    //     cx: xxx

    private String apiKey;
    private String cx;

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getCx() { return cx; }
    public void setCx(String cx) { this.cx = cx; }
}
