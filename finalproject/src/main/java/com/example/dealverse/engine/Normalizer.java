package com.example.dealverse.engine;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.dealverse.model.Offer;
import com.example.dealverse.model.RawOffer;

@Component
public class Normalizer {

    public List<Offer> normalize(List<RawOffer> raws) {
        List<Offer> list = new ArrayList<>();
        for (RawOffer r : raws) {
            Offer o = new Offer();
            o.setSource(r.getSource());
            o.setUrl(r.getUrl());
            o.setListPrice(r.getListPrice());
            o.setShippingFee(r.getShippingFee());
            o.setTimestamp(r.getTimestamp());
            o.setAttrsJson(r.getAttrsJson());
            o.setTitle(r.getTitle());

            // 超簡單暴力示範：brand = 第一個字, model = 其他
            String title = r.getTitle() != null ? r.getTitle() : "";
            String[] parts = title.split("\\s+");
            if (parts.length > 0) {
                o.setBrand(parts[0].toLowerCase());
                if (parts.length > 1) {
                    o.setModel(parts[1].toLowerCase());
                }
            }

            String productKey = (o.getBrand() == null ? "" : o.getBrand()) + "|"
                    + (o.getModel() == null ? "" : o.getModel());
            o.setProductKey(productKey);

            list.add(o);
        }
        return list;
    }
}
