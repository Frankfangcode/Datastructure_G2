package com.example.dealverse;

import java.util.Locale;

public class Normalizer {

    public Offer normalize(RawOffer raw) {
        String brandKey = safeLower(raw.getBrand());
        String modelKey = safeLower(raw.getModel());
        String variant = "default";
        String productKey = brandKey + "|" + modelKey + "|" + variant;

        Offer.Spec spec = new Offer.Spec(
                raw.getBrand(),
                raw.getModel(),
                variant,
                raw.getAttrsJson()
        );

        return new Offer(
                productKey,
                spec,
                raw.getListPrice(),
                raw.getShippingFee(),
                raw.getUrl(),
                raw.getFetchedAt(),
                raw.getSource()
        );
    }

    private String safeLower(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT).trim();
    }
}
