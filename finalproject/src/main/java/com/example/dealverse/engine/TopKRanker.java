package com.example.dealverse.engine;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.dealverse.model.Result;

@Component
public class TopKRanker {

    public List<Result> rank(List<Result> results, int k) {
        return results.stream()
                .sorted(Comparator
                        .comparing(Result::getSaving).reversed()   // saving 高的優先
                        .thenComparing(Result::getPay)             // saving 一樣比 pay 低
                )
                .limit(k)
                .collect(Collectors.toList());
    }
}
