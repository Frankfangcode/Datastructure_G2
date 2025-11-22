package com.example.dealverse;

import java.util.*;

public class TopKRanker {

    public List<Result> rankTopK(List<Result> all, int k) {
        if (k <= 0 || all.isEmpty()) return List.of();

        Comparator<Result> cmp = Comparator
                .comparingDouble(Result::getSaving)
                .thenComparingDouble(Result::getPay)
                .thenComparing(r -> r.getOffer().getTs());

        PriorityQueue<Result> pq = new PriorityQueue<>(cmp);

        for (Result r : all) {
            if (pq.size() < k) {
                pq.offer(r);
            } else if (cmp.compare(r, pq.peek()) > 0) {
                pq.poll();
                pq.offer(r);
            }
        }

        List<Result> out = new ArrayList<>(pq);
        out.sort(cmp.reversed());
        return out;
    }
}
