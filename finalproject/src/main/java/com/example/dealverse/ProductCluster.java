package com.example.dealverse;

import java.util.ArrayList;
import java.util.List;

public class ProductCluster {
    private String clusterId;
    private Offer.Spec canonicalSpec;
    private List<Offer> offers = new ArrayList<>();

    public ProductCluster(String clusterId, Offer.Spec canonicalSpec) {
        this.clusterId = clusterId;
        this.canonicalSpec = canonicalSpec;
    }

    public void addOffer(Offer offer) {
        offers.add(offer);
    }

    public String getClusterId() { return clusterId; }
    public Offer.Spec getCanonicalSpec() { return canonicalSpec; }
    public List<Offer> getOffers() { return offers; }
}
