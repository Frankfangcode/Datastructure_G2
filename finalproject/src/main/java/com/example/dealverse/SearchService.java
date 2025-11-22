package com.example.dealverse;

import java.util.ArrayList;
import java.util.List;

public class SearchService {

    private final FetcherPool fetcherPool;
    private final Normalizer normalizer;
    private final Matcher matcher;
    private final RuleEngine ruleEngine;
    private final Scorer scorer;
    private final TopKRanker ranker;

    public SearchService(FetcherPool fetcherPool,
                         Normalizer normalizer,
                         Matcher matcher,
                         RuleEngine ruleEngine,
                         Scorer scorer,
                         TopKRanker ranker) {
        this.fetcherPool = fetcherPool;
        this.normalizer = normalizer;
        this.matcher = matcher;
        this.ruleEngine = ruleEngine;
        this.scorer = scorer;
        this.ranker = ranker;
    }

    public List<Result> search(String userText, int topK) {
        Query query = new Query(userText, "ONLINE");

        List<RawOffer> rawOffers = fetcherPool.fetchAll(query);

        List<Offer> offers = new ArrayList<>();
        for (RawOffer raw : rawOffers) {
            offers.add(normalizer.normalize(raw));
        }

        List<ProductCluster> clusters = matcher.cluster(offers);

        List<Result> allResults = new ArrayList<>();
        for (ProductCluster c : clusters) {
            for (Offer o : c.getOffers()) {
                RuleEngine.RuleResult ruleResult = ruleEngine.resolve(o, query);
                Result result = scorer.score(o, ruleResult);
                allResults.add(result);
            }
        }

        return ranker.rankTopK(allResults, topK);
    }
}
