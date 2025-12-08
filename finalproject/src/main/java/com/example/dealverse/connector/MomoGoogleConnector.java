package com.example.dealverse.connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.dealverse.model.Query;
import com.example.dealverse.model.RawOffer;

@Component
public class MomoGoogleConnector implements Connector {

    private final GoogleSearchService googleSearchService;

    public MomoGoogleConnector(GoogleSearchService googleSearchService) {
        this.googleSearchService = googleSearchService;
    }

    @Override
    public String getSourceName() {
        return "MOMO_GOOGLE";
    }

    @Override

    public List<RawOffer> fetch(Query query) {
        // 先用「純關鍵字」測試
        Map<String, String> map = googleSearchService.search(query.getText());
        List<RawOffer> list = new ArrayList<>();
        for (Map.Entry<String, String> e : map.entrySet()) {
            RawOffer ro = new RawOffer(e.getKey(), "momo", e.getValue());
            ro.setListPrice(0);
            ro.setShippingFee(0);
            list.add(ro);
        }
        return list;
    }
}
