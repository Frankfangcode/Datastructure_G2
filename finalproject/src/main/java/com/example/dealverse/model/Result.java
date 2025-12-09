package com.example.dealverse.model;

import java.util.ArrayList;
import java.util.List;

public class Result {

    private Offer offer;
    private double pay;      // 實付
    private double saving;   // 總共省多少
    private double cashback; // 回饋
    private Explain explain;
    private List<Step> steps = new ArrayList<>();

    public Result() {
    }

    public Result(Offer offer) {
        this.offer = offer;
    }

    // --- getters & setters ---
    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public double getPay() {
        return pay;
    }

    public void setPay(double pay) {
        this.pay = pay;
    }

    public double getSaving() {
        return saving;
    }

    public void setSaving(double saving) {
        this.saving = saving;
    }

    public double getCashback() {
        return cashback;
    }

    public void setCashback(double cashback) {
        this.cashback = cashback;
    }

    public Explain getExplain() {
        return explain;
    }

    public void setExplain(Explain explain) {
        this.explain = explain;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void addStep(Step step) {
        this.steps.add(step);
    }
}
