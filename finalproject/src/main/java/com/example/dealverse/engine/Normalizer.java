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
        if (raws == null) return list;

        for (RawOffer r : raws) {
            if (r == null) continue;

            Offer o = new Offer();

            // ===== 基本欄位直接對映 =====
            o.setSource(r.getSource());
            o.setUrl(r.getUrl());
            o.setTitle(r.getTitle());          // ⭐ 關鍵：讓 reranker 用 title
            o.setListPrice(r.getListPrice());
            o.setShippingFee(r.getShippingFee());
            o.setTimestamp(r.getTimestamp());
            o.setAttrsJson(r.getAttrsJson());

            // ===== 非破壞式的 brand / model 推測（很保守）=====
            String title = r.getTitle() != null ? r.getTitle().trim() : "";
            if (!title.isEmpty()) {
                String[] parts = title.split("\\s+");
                if (parts.length >= 1) {
                    o.setBrand(parts[0].toLowerCase());
                }
                if (parts.length >= 2) {
                    o.setModel(parts[1].toLowerCase());
                }
            }

            // ===== productKey（維持你原本行為）=====
            String productKey =
                    (o.getBrand() == null ? "" : o.getBrand()) + "|" +
                    (o.getModel() == null ? "" : o.getModel());
            o.setProductKey(productKey);

            list.add(o);
        }

        return list;
    }
}
