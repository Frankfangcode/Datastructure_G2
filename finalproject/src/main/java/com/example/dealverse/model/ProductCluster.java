package com.example.dealverse.model;

import java.util.ArrayList;
import java.util.List;

public class ProductCluster {
    private String clusterId; // = productKey
    private String canonicalSpec; // 簡單寫成 brand + model
    private List<Offer> offers = new ArrayList<>();

    public ProductCluster(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterId() { return clusterId; }

    public String getCanonicalSpec() { return canonicalSpec; }
    public void setCanonicalSpec(String canonicalSpec) { this.canonicalSpec = canonicalSpec; }

    public List<Offer> getOffers() { return offers; }
}
