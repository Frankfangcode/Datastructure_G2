package com.example.dealverse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Matcher {

    public List<ProductCluster> cluster(List<Offer> offers) {
        Map<String, ProductCluster> map = new HashMap<>();

        for (Offer offer : offers) {
            String key = offer.getProductKey();
            ProductCluster cluster = map.get(key);
            if (cluster == null) {
                cluster = new ProductCluster(key, offer.getSpec());
                map.put(key, cluster);
            }
            cluster.addOffer(offer);
        }
        return new ArrayList<>(map.values());
    }
}
