package com.example.dealverse.connector;

import java.util.ArrayList;
import java.util.List;

import com.example.dealverse.model.Query;
import com.example.dealverse.model.RawOffer;

public class DummyConnector implements Connector {

    @Override
    public String getSourceName() {
        return "DUMMY";
    }

    @Override
    public List<RawOffer> fetch(Query query) {
        List<RawOffer> list = new ArrayList<>();
        RawOffer o1 = new RawOffer("Dummy iPhone 17 Pro - momo", "momo", "https://www.momo.com.tw/");
        o1.setListPrice(39900);
        list.add(o1);

        RawOffer o2 = new RawOffer("Dummy iPhone 17 Pro - pchome", "pchome", "https://24h.pchome.com.tw/");
        o2.setListPrice(39500);
        list.add(o2);

        return list;
    }
}
