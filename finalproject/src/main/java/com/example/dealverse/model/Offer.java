package com.example.dealverse.model;

import java.time.Instant;

public class Offer {
    // 規格
    private String brand;
    private String model;
    private String variant;
    private String attrsJson;

    // 用來比對的 key（brand|model|variant）
    private String productKey;

    // 價格相關
    private double listPrice;
    private double shippingFee;

    // 來源資訊
    private String url;
    private Instant timestamp;
    private String source;

    public Offer() {}

    // --- getters & setters ---
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getVariant() { return variant; }
    public void setVariant(String variant) { this.variant = variant; }

    public String getAttrsJson() { return attrsJson; }
    public void setAttrsJson(String attrsJson) { this.attrsJson = attrsJson; }

    public String getProductKey() { return productKey; }
    public void setProductKey(String productKey) { this.productKey = productKey; }

    public double getListPrice() { return listPrice; }
    public void setListPrice(double listPrice) { this.listPrice = listPrice; }

    public double getShippingFee() { return shippingFee; }
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
