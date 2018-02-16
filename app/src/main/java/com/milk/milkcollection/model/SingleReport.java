package com.milk.milkcollection.model;

/**
 * Created by Er. Arjun on 06-03-2016.
 */
public class SingleReport {
    String code,date,sift,weight,rate,amount;

    public SingleReport(){}
    public SingleReport(String code,String date, String sift, String weight,String rate, String amount) {
        this.code = code;
        this.date = date;
        this.weight = weight;
        this.sift = sift;
        this.weight = weight;
        this.rate = rate;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getSift() {
        return sift;
    }

    public void setSift(String sift) {
        this.sift = sift;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
