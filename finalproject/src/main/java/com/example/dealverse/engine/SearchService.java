package com.example.dealverse.engine;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.dealverse.model.Offer;
import com.example.dealverse.model.ProductCluster;
import com.example.dealverse.model.Query;
import com.example.dealverse.model.RawOffer;
import com.example.dealverse.model.Result;

@Service
public class SearchService {

    private final FetcherPool fetcherPool;
    private final Normalizer normalizer;
    private final Matcher matcher;
    private final RuleEngine ruleEngine;
    private final Scorer scorer;
    private final TopKRanker topKRanker;

    public SearchService(FetcherPool fetcherPool,
                         Normalizer normalizer,
                         Matcher matcher,
                         RuleEngine ruleEngine,
                         Scorer scorer,
                         TopKRanker topKRanker) {
        this.fetcherPool = fetcherPool;
        this.normalizer = normalizer;
        this.matcher = matcher;
        this.ruleEngine = ruleEngine;
        this.scorer = scorer;
        this.topKRanker = topKRanker;
    }

    public List<Result> search(Query query, int topK) {
        // 1. 抓 RawOffer
        List<RawOffer> raws = fetcherPool.fetchAll(query);

        // 2. Normalizer
        List<Offer> offers = normalizer.normalize(raws);

        // 3. 群組
        List<ProductCluster> clusters = matcher.cluster(offers);

        // 4. 對每個 cluster 挑最划算
        List<Result> allResults = new ArrayList<>();
        for (ProductCluster cluster : clusters) {
            for (Offer o : cluster.getOffers()) {
                RuleEngine.RuleResult rr = ruleEngine.apply(o);
                Result r = scorer.score(o, rr);
                allResults.add(r);
            }
        }

        // 5. 排序 + TopK
        return topKRanker.rank(allResults, topK);
    }
}
