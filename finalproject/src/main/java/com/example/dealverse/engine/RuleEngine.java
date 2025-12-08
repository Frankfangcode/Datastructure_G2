package com.example.dealverse.engine;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.dealverse.model.Offer;
import com.example.dealverse.model.Step;

@Component
public class RuleEngine {

    public static class RuleResult {
        private double discount;
        private double cashback;
        private List<Step> steps = new ArrayList<>();

        public double getDiscount() { return discount; }
        public void setDiscount(double discount) { this.discount = discount; }

        public double getCashback() { return cashback; }
        public void setCashback(double cashback) { this.cashback = cashback; }

        public List<Step> getSteps() { return steps; }
    }

    public RuleResult apply(Offer offer) {
        RuleResult rr = new RuleResult();

        // TODO: 之後依照 source/金額套不同規則
        // 範例：momo 滿 20000 折 1000 之類
        rr.setDiscount(0);
        rr.setCashback(0);

        return rr;
    }
}
