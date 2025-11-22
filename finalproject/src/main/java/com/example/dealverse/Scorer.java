package com.example.dealverse;

public class Scorer {

    public Result score(Offer offer, RuleEngine.RuleResult ruleResult) {
        double listPrice = offer.getListPrice();
        double shipping = offer.getShippingFee();

        double pay = listPrice - ruleResult.getDiscount() + shipping;
        if (pay < 0) pay = 0;

        double saving = (listPrice - pay) + ruleResult.getCashback();

        Explain explain = ruleResult.getExplain();
        explain.addMessage("原價: " + listPrice + ", 運費: " + shipping);
        explain.addMessage("實付: " + pay + ", 總共省: " + saving);

        return new Result(
                offer,
                pay,
                saving,
                ruleResult.getCashback(),
                ruleResult.getSteps(),
                explain
        );
    }
}
