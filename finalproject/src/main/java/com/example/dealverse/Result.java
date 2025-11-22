package com.example.dealverse;

import java.util.List;

public class Result {
    private Offer offer;
    private double pay;
    private double saving;
    private double cashback;
    private List<Step> path;
    private Explain explain;

    public Result(Offer offer, double pay, double saving, double cashback,
                  List<Step> path, Explain explain) {
        this.offer = offer;
        this.pay = pay;
        this.saving = saving;
        this.cashback = cashback;
        this.path = path;
        this.explain = explain;
    }

    public Offer getOffer() { return offer; }
    public double getPay() { return pay; }
    public double getSaving() { return saving; }
    public double getCashback() { return cashback; }
    public List<Step> getPath() { return path; }
    public Explain getExplain() { return explain; }
}
