package com.example.dealverse;

import java.util.ArrayList;
import java.util.List;

public class RuleEngine {

    public static class RuleResult {
        private double discount;
        private double cashback;
        private List<Step> steps = new ArrayList<>();
        private Explain explain = new Explain();

        public double getDiscount() { return discount; }
        public double getCashback() { return cashback; }
        public List<Step> getSteps() { return steps; }
        public Explain getExplain() { return explain; }

        public void addDiscount(double d, String msg) {
            discount += d;
            explain.addMessage(msg);
        }

        public void addCashback(double c, String msg) {
            cashback += c;
            explain.addMessage(msg);
        }

        public void addStep(Step s) {
            steps.add(s);
        }
    }

    public RuleResult resolve(Offer offer, Query query) {
        RuleResult result = new RuleResult();
        double price = offer.getListPrice();

        // 範例：滿 30000 折 2000
        if (price >= 30000) {
            result.addDiscount(2000, "使用平台滿 30000 折 2000 優惠券");
            result.addStep(new Step("COUPON", "領取平台滿額券", offer.getUrl()));
        }

        // 信用卡 3% 回饋，上限 1000
        double cashback = price * 0.03;
        if (cashback > 1000) cashback = 1000;
        result.addCashback(cashback, "信用卡 3% 現金回饋（上限 1000）");
        result.addStep(new Step("PAY", "刷指定信用卡付款", null));

        return result;
    }
}
