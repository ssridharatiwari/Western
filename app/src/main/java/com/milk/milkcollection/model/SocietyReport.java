package com.milk.milkcollection.model;

/**
 * Created by Er. Arjun on 06-03-2016.
 */
public class SocietyReport {
    String weight,amount,date,count,shift;

    public SocietyReport(){}

    public void setData(String weight,String date,String count,String amount,String sift) {
        this.date = date;
        this.weight = weight;
        this.count = count;
        this.amount = amount;
        this.shift = sift;
    }




    public String getShift() { return shift;}

    public String getCount() { return count;}

    public void setCount(String count) {this.count = count;}

    public String getDate() { return date;}

    public void setDate(String date) {this.date = date;}

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


}
