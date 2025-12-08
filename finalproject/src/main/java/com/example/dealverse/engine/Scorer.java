package com.example.dealverse.engine;

import org.springframework.stereotype.Component;

import com.example.dealverse.model.Explain;
import com.example.dealverse.model.Offer;
import com.example.dealverse.model.Result;

@Component
public class Scorer {

    public Result score(Offer offer, RuleEngine.RuleResult rr) {
        Result result = new Result(offer);

        double listPrice = offer.getListPrice();
        double shipping = offer.getShippingFee();
        double discount = rr.getDiscount();
        double cashback = rr.getCashback();

        double pay = listPrice - discount + shipping;
        if (pay < 0) pay = 0;

        // saving 這裡先簡化：折扣 + cashback
        double saving = discount + cashback;

        result.setPay(pay);
        result.setSaving(saving);
        result.setCashback(cashback);
        result.getSteps().addAll(rr.getSteps());

        Explain explain = new Explain(
                "基礎計算：listPrice - discount + shipping",
                "原價 " + listPrice + " - 折扣 " + discount + " + 運費 " + shipping
                        + "，回饋 " + cashback + "，總共省下 " + saving);
        result.setExplain(explain);

        return result;
    }
}
