package com.milk.milkcollection.model;

import com.milk.milkcollection.Activity.MainActivity;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Er. Arjun on 06-03-2016.
 */
public class PDFDaily {

    public ArrayList<SingleEntry> reportList = new ArrayList<>();
    public String date  = "";
    public String shift  = "";
    public String title = "";
    public String reportTitle = "";
    public String fileName = "";
    public String rate = "";
    public String totalWt = "0";
    public String avgFat = "0";
    public String avgSnf = "0";
    public String totalAmt = "0";

    public PDFDaily(){}


    public  void setAmounts(Float wt, Float snf,Float fat, Float totalAmt) {

        try {
            this.totalWt = (MainActivity.twoDecimalFloatToString(wt));
            this.avgFat = (MainActivity.twoDecimalFloatToString(fat));
            this.avgSnf = (MainActivity.twoDecimalFloatToString(snf));
            this.totalAmt = (MainActivity.twoDecimalFloatToString(totalAmt));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setData(String title, String date, String reportTitle,String shift,String fileName) {
        this.title = title;
        this.date = date;
        this.reportTitle = reportTitle;
        this.shift = shift;
        this.fileName = fileName;
    }

    public void setArray(ArrayList<SingleEntry> array){
        this.reportList = array;
    }

    public  String getFileName(){ return  fileName; }
    public  ArrayList<SingleEntry> getReportList(){ return  reportList; }
    public  String getDate(){ return  date; }
    public  String getTitle(){ return  title; }
    public  String getReportTitle(){ return  reportTitle; }
    public  String getTotalWt(){ return  totalWt; }
    public  String getAvgSnf(){ return  avgSnf; }
    public  String getAvgFat(){ return  avgFat; }
    public  String getTotalAmt(){ return  totalAmt; }
    public  String getShift(){ return  shift; }

//    public String getDate() {
//        return rate;
//    }
//
//    public String getSnf() {
//        return snf;
//    }
//
//    public String getFat() {
//        return fat;
//    }


}
