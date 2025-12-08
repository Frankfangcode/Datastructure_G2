package com.example.dealverse.model;

import java.time.Instant;

public class RawOffer {
    private String title;
    private String source;      // momo / pchome / shopee
    private String brand;
    private String model;
    private String attrsJson;
    private double listPrice;
    private double shippingFee;
    private String url;
    private Instant timestamp;

    public RawOffer() {}

    public RawOffer(String title, String source, String url) {
        this.title = title;
        this.source = source;
        this.url = url;
        this.timestamp = Instant.now();
    }

    // --- getters & setters ---

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getAttrsJson() { return attrsJson; }
    public void setAttrsJson(String attrsJson) { this.attrsJson = attrsJson; }

    public double getListPrice() { return listPrice; }
    public void setListPrice(double listPrice) { this.listPrice = listPrice; }

    public double getShippingFee() { return shippingFee; }
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
