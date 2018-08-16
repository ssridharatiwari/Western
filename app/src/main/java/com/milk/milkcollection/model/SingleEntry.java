package com.milk.milkcollection.model;

/**
 * Created by Er. Arjun on 06-03-2016.
 */
public class SingleEntry {

// a.execSQL("create table milk_amount(Id Integer primary Key Autoincrement,memberCode text,milkweight text,rateperliter text ,totalamount text,date text,milkinformation text," + "sift text,fat text,fat_wt text,snf text,snf_wt text,allInformation text,dailyInformation text,dateSave text)");


    String id,code,date,sift,weight,rate,amount,fat,snf,datesave,fatWt,snfWt;

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

}
