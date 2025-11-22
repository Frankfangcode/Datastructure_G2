package com.example.dealverse;

import java.time.Instant;
import java.util.Map;

public class Offer {

    public static class Spec {
        private final String brand;
        private final String model;
        private final String variant;
        private final Map<String, String> attrs;

        public Spec(String brand, String model, String variant, Map<String, String> attrs) {
            this.brand = brand;
            this.model = model;
            this.variant = variant;
            this.attrs = attrs;
        }

        public String getBrand() { return brand; }
        public String getModel() { return model; }
        public String getVariant() { return variant; }
        public Map<String, String> getAttrs() { return attrs; }
    }

    private final String productKey;
    private final Spec spec;
    private final double listPrice;
    private final double shippingFee;
    private final String url;
    private final Instant ts;
    private final String source;

    public Offer(String productKey, Spec spec, double listPrice, double shippingFee,
                 String url, Instant ts, String source) {
        this.productKey = productKey;
        this.spec = spec;
        this.listPrice = listPrice;
        this.shippingFee = shippingFee;
        this.url = url;
        this.ts = ts;
        this.source = source;
    }

    public String getProductKey() { return productKey; }
    public Spec getSpec() { return spec; }
    public double getListPrice() { return listPrice; }
    public double getShippingFee() { return shippingFee; }
    public String getUrl() { return url; }
    public Instant getTs() { return ts; }
    public String getSource() { return source; }
}
