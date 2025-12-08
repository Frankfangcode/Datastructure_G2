package com.example.dealverse.connector;

import java.util.List;

import com.example.dealverse.model.Query;
import com.example.dealverse.model.RawOffer;

public interface Connector {
    String getSourceName();
    List<RawOffer> fetch(Query query);
}
