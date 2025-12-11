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
public class PchomeGoogleConnector implements Connector {

    private final GoogleSearchService googleSearchService;
    private final PriceExtractor priceExtractor;

    public PchomeGoogleConnector(GoogleSearchService googleSearchService,
                                 PriceExtractor priceExtractor) {
        this.googleSearchService = googleSearchService;
        this.priceExtractor = priceExtractor;
    }

    @Override
    public String getSourceName() {
        return "pchome";
    }

    @Override
    public List<RawOffer> fetch(Query query) {

        Map<String, String> map =
                googleSearchService.searchSite(query.getText(), "24h.pchome.com.tw");

        List<RawOffer> list = new ArrayList<>();

        for (Map.Entry<String, String> e : map.entrySet()) {
            String title = e.getKey();
            String url   = e.getValue();

            if (!urlMatchesDomain(url, "24h.pchome.com.tw"))
                continue;

            RawOffer ro = new RawOffer(title, "pchome", url);

            try {
                Integer price = priceExtractor.extractPrice(url, "pchome");

                if (price != null) {
                    ro.setListPrice(price);
                    System.out.println("[PriceExtractor] pchome price=" + price + " url=" + url);
                } else {
                    ro.setListPrice(0);
                    System.out.println("[PriceExtractor] pchome price=null, url=" + url);
                }

            } catch (Exception ex) {
                System.out.println("[PriceExtractor] failed for pchome url = " + url +
                                   " , err=" + ex.getMessage());
                ro.setListPrice(0);
            }

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
