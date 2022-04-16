package com.milk.milkcollection.model;

/**
 * Created by Er. Arjun on 06-03-2016.
 */
public class PaymentReport {
    public String code,weight,amount,name,cmfund,total;

    public PaymentReport(){}

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
