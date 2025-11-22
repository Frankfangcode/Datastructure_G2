package com.example.dealverse;

import java.time.Instant;
import java.util.Map;

public class RawOffer {
    private String source;
    private String title;
    private String brand;
    private String model;
    private String sku;
    private Map<String, String> attrsJson;
    private double listPrice;
    private double shippingFee;
    private String url;
    private Instant fetchedAt;

    public RawOffer(String source, String title, String brand, String model, String sku,
                    Map<String, String> attrsJson, double listPrice, double shippingFee,
                    String url, Instant fetchedAt) {
        this.source = source;
        this.title = title;
        this.brand = brand;
        this.model = model;
        this.sku = sku;
        this.attrsJson = attrsJson;
        this.listPrice = listPrice;
        this.shippingFee = shippingFee;
        this.url = url;
        this.fetchedAt = fetchedAt;
    }

    public String getSource() { return source; }
    public String getTitle() { return title; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getSku() { return sku; }
    public Map<String, String> getAttrsJson() { return attrsJson; }
    public double getListPrice() { return listPrice; }
    public double getShippingFee() { return shippingFee; }
    public String getUrl() { return url; }
    public Instant getFetchedAt() { return fetchedAt; }
}
