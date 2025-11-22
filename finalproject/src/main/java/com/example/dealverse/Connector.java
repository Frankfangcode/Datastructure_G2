package com.example.dealverse;

import java.util.List;

public interface Connector {
    String getSourceName();
    List<RawOffer> fetch(Query query);
}
