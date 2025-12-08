package com.example.dealverse.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.dealverse.model.Offer;
import com.example.dealverse.model.ProductCluster;

@Component
public class Matcher {

    public List<ProductCluster> cluster(List<Offer> offers) {
        Map<String, ProductCluster> map = new HashMap<>();
        for (Offer o : offers) {
            String key = o.getProductKey();
            if (key == null || key.isEmpty()) {
                key = "UNKNOWN";
            }
            ProductCluster cluster = map.computeIfAbsent(key, ProductCluster::new);
            if (cluster.getCanonicalSpec() == null) {
                cluster.setCanonicalSpec(o.getBrand() + " " + o.getModel());
            }
            cluster.getOffers().add(o);
        }
        return new ArrayList<>(map.values());
    }
}
