package com.milk.milkcollection.model;

import android.util.Log;

import com.milk.milkcollection.Activity.MainActivity;

public class SingleEntry {



    String id,code,date,sift,weight,rate,amount,fat,snf,datesave,fatWt,snfWt,mobie;

    public SingleEntry(){}

    public void setId(String id) {
        this.id = id;
    }
    public String getID() {
        return id;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }
    public String getfat() {
        return fat;
    }

    public void setSnf(String snf) {
        this.snf = snf;
    }
    public String getSnf() {
        return snf;
    }

    public void setDatesave(String datesave) {
        this.datesave = datesave;
    }
    public String getDatesave() {
        return datesave;
    }

    public void setFatWt(String fatWt) {
        this.fatWt = fatWt;
    }
    public String getFatWt() {
        return fatWt;
    }

    public void setSnfWt(String snfWt) {
        if (snfWt!=null){
            this.snfWt = snfWt;
        }

    }
    public String getSnfWt() { return snfWt;}


    public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }

    public String getDate() {
        return date;
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


    public String mobile() {
        return mobie;
    }
    public void setMobile(String mobie) {
        this.mobie = mobie;
    }



    public String getSMS() {



        String  message = MainActivity.getInstace().sharedPreferencesUtils.getTitle() + "\n" + "Dt:" + date + "(" + sift + ")" +
                MainActivity.getInstace().milkDBHelpers.getMemberNameByCode(code) + "(" + code + ")\n" +
                "\nFat: " + fat + " ,  SNF: " + snf +
                "\nQT=" + weight +  "\nRT=" + MainActivity.twoDecimal(rate) + " AMT=" + MainActivity.twoDecimal(amount);
        return message;
    };


    public String getPrintMassge(){

        String printString = MainActivity.getInstace().sharedPreferencesUtils.getTitle() + "\n" + MainActivity.getInstace().sharedPreferencesUtils.getMobile() + "\n" + MainActivity.lineBreak() +
                "\nName: " + MainActivity.getInstace().milkDBHelpers.getMemberNameByCode(code) + "(" + code + ")" +
                "\nDate: " + datesave + " - " +sift +
                "\nFat: " + fat + " ,  SNF: " + snf +
                "\nLitre: " + MainActivity.twoDecimal(weight) + " L" +
                "\nRate/Ltr: " + MainActivity.twoDecimal(rate) +
                "\nAmount:  Rs " + amount + "\n";
        printString = printString + "\n" + MainActivity.lineBreak();

        return printString;
        //return "sanjay";
    }

}
