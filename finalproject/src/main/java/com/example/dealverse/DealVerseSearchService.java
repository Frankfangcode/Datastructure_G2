package com.example.dealverse;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DealVerseSearchService {

    private final SearchService searchService;

    public DealVerseSearchService() {
        FetcherPool pool = new FetcherPool();
        pool.addConnector(new DummyConnector("momo"));
        pool.addConnector(new DummyConnector("shopee"));

        Normalizer normalizer = new Normalizer();
        Matcher matcher = new Matcher();
        RuleEngine ruleEngine = new RuleEngine();
        Scorer scorer = new Scorer();
        TopKRanker ranker = new TopKRanker();

        this.searchService = new SearchService(
                pool, normalizer, matcher, ruleEngine, scorer, ranker
        );
    }

    public Map<String, String> search(String keyword) {
        List<Result> results = searchService.search(keyword, 5);

        Map<String, String> map = new LinkedHashMap<>();
        for (Result r : results) {
            Offer o = r.getOffer();
            String title = String.format(
                    "%s｜實付 $%.0f（省 %.0f，回饋 %.0f）",
                    o.getSource(),
                    r.getPay(),
                    r.getSaving(),
                    r.getCashback()
            );
            map.put(title, o.getUrl());
        }
        return map;
    }
}
