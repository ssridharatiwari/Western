package com.milk.milkcollectionapp.model;

/**
 * Created by Er. Arjun on 06-03-2016.
 */
public class ListRate {
    String fat,snf,rate;

    public ListRate(){}

    public ListRate(String fat, String rate, String snf) {
        this.fat = fat;
        this.snf = snf;
        this.rate = rate;
    }

    public void setData(String fat, String rate, String snf) {
        this.fat = fat;
        this.snf = snf;
        this.rate = rate;
    }

    public String getRate() {
        return rate;
    }

    public String getSnf() {
        return snf;
    }

    public String getFat() {
        return fat;
    }


}
