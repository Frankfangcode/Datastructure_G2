package com.example.dealverse.connector;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.dealverse.model.Query;
import com.example.dealverse.model.RawOffer;
import com.example.dealverse.service.PriceExtractor;

@Component
public class ShopeeGoogleConnector implements Connector {

    private final GoogleSearchService googleSearchService;
    private final PriceExtractor priceExtractor;

    public ShopeeGoogleConnector(GoogleSearchService googleSearchService,
                                 PriceExtractor priceExtractor) {
        this.googleSearchService = googleSearchService;
        this.priceExtractor = priceExtractor;
    }

    @Override
    public String getSourceName() {
        return "shopee";
    }

    @Override
    public List<RawOffer> fetch(Query query) {

        Map<String, String> map =
                googleSearchService.searchSite(query.getText(), "shopee.tw");

        List<RawOffer> list = new ArrayList<>();

        for (Map.Entry<String, String> e : map.entrySet()) {
            String title = e.getKey();
            String url   = e.getValue();

            // 確保真的屬於 Shopee
            if (!urlMatchesDomain(url, "shopee.tw")) {
                continue;
            }

            RawOffer ro = new RawOffer(title, "shopee", url);

            // ======== 價格爬取在這裡 =========
            try {
                Integer price = priceExtractor.extractPrice(url, "shopee");
                if (price != null) {
                    ro.setListPrice(price);
                    System.out.println("[PriceExtractor] shopee price = " + price + " , url = " + url);
                } else {
                    ro.setListPrice(0);
                }
            } catch (Exception ex) {
                System.out.println("[PriceExtractor] failed for shopee, url = " + url
                        + " , err = " + ex.getMessage());
                ro.setListPrice(0);
            }
            // =================================

            ro.setShippingFee(0);  // 目前先固定 0
            list.add(ro);
        }

        return list;
    }

    private boolean urlMatchesDomain(String url, String expectedDomain) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost(); // e.g. shopee.tw / shopee.tw.xxx
            return host != null && host.endsWith(expectedDomain);
        } catch (Exception e) {
            return false;
        }
    }
}
