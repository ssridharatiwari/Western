package com.milk.milkcollection.model;

/**
 * Created by Er. Arjun on 02-03-2016.
 */
public class DailyReport {

    String code,weight,fat,snf,rate,amount,id,date,shift;

    public DailyReport(){}
//    public DailyReport(String code, String weight, String fat, String snf, String rate, String amount,String id) {
//        this.code = code;
//        this.weight = weight;
//        this.fat = fat;
//        this.snf = snf;
//        this.rate = rate;
//        this.amount = amount;
//        this.id = amount;
//    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getSnf() {
        return snf;
    }

    public void setSnf(String snf) {
        this.snf = snf;
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
    public void setAmount(String amount) { this.amount = amount;}

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getShift() {
        return shift;
    }
    public void setShift(String shift) {
        this.shift = shift;
    }
}
