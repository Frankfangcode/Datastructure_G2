package com.example.dealverse.connector;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.dealverse.model.Query;
import com.example.dealverse.model.RawOffer;

@Component
public class ShopeeGoogleConnector implements Connector {

    private final GoogleSearchService googleSearchService;

    public ShopeeGoogleConnector(GoogleSearchService googleSearchService) {
        this.googleSearchService = googleSearchService;
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
            String url = e.getValue();

            if (!urlMatchesDomain(url, "shopee.tw")) continue;

            RawOffer ro = new RawOffer(title, "shopee", url);
            ro.setListPrice(0);
            ro.setShippingFee(0);
            list.add(ro);
        }
        return list;
    }

    private boolean urlMatchesDomain(String url, String expectedDomain) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            return host != null && host.endsWith(expectedDomain);
        } catch (Exception e) {
            return false;
        }
    }
}
