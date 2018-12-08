package com.milk.milkcollection.model;

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
        this.datesave = snfWt;
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

        String  message =   MainActivity.instace.sharedPreferencesUtils.getTitle() + "\n" + "Date: " + date + "(" + sift + ")" + "\nCode: " + code +
                "\nQTY=" + weight +  "\nRT " + rate + " AMT= " + amount ;
        return message;
    };
    public String getPrintMassge(){

        String printString = MainActivity.instace.sharedPreferencesUtils.getTitle() + "\n" + MainActivity.instace.sharedPreferencesUtils.getMobile() + "\n" + MainActivity.lineBreak() +
                "Code: " + code +
                "\nDate: " + date + " - " +sift +
                "\nLitre: " + MainActivity.twoDecimal(weight) + " L" +
                "\nRate/Ltr: " + rate +
                "\nAmount:  Rs " + amount + "\n";
        printString = printString + "\n" + MainActivity.lineBreak();

        //return printString;
        return "sanjay";
    }

}
