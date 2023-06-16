package com.milk.milkcollectionapp.model;

import android.util.Log;

import com.milk.milkcollectionapp.activity.MainActivity;

import java.io.IOException;

public class SingleEntry {



   public String id,code,date,sift,weight,rate,amount,fat,snf,datesave,fatWt,snfWt,mobie, cmf="0",memberName="",memberMobile="",title="",selfNumber="";


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

    public String getCmf() {
        return cmf;
    }
    public void setCMF(String cmf) {
        this.cmf = cmf;
    }


    public String getSMS() {

        String  message = MainActivity.getInstace().sharedPreferencesUtils.getTitle() + "\n" + "Dt:" + date + "(" + sift + ")\n" +
                MainActivity.getInstace().milkDBHelpers.getMemberNameByCode(code) + "(" + code + ")" +
                "\nFt: " + fat + " , Snf: " + snf +
                "\nQT=" + weight +  "\nRT=" + MainActivity.twoDecimal(rate) + " AMT=" + MainActivity.twoDecimal(amount);

        if (Float.parseFloat(cmf) > 0) {
            try {
                message = message + " CM Fund= " + cmf + " Total: " + MainActivity.twoDecimalFloatToString(Float.valueOf(cmf) + Float.valueOf(amount));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return message;
    };


    public String getPrintMassge(){


        if (title.equals("")) {
            title = MainActivity.getInstace().sharedPreferencesUtils.getTitle();
        }

        if (selfNumber.equals("")) {
            selfNumber = MainActivity.getInstace().sharedPreferencesUtils.getMobile();
        }

        if (memberName.equals("")) {
            Log.e("code",code);
            memberName = MainActivity.getInstace().milkDBHelpers.getMemberNameByCode(code);
        }

        String printString = title + "\n" +
                            selfNumber + "\n" + MainActivity.lineBreak() +
                            "\nName: " + memberName + "(" + code + ")" +
                            "\nDate: " + datesave + " - " +sift +
                            "\nFat: " + fat + " , SNF: " + snf +
                            "\nLitre: " + MainActivity.twoDecimal(weight) + " L" +
                            "\nRate/Ltr: " + MainActivity.twoDecimal(rate) +
                            "\nAmount: Rs " + amount;

        if (Float.parseFloat(cmf) > 0) {
            printString = printString +  "\nCM Fund: Rs " + cmf;
            printString = printString +  "\nTotal: Rs " + (Float.parseFloat(cmf) + Float.parseFloat(amount));
        }

        Log.e("print",printString );

        printString = printString + "\n\n" + MainActivity.lineBreak();
        return printString;

    }

}
