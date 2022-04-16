package com.milk.milkcollection.helper;

import com.milk.milkcollection.R;

import java.util.Calendar;

/**
 * Created by sanjay on 27/08/18.
 */

public class AppString {

     public static String  printername =  "WEG_Mobile";
    public static String  printerHC =  "HC-05";
     public static String  reentryTitle =  "आज इस सदस्य की एंट्री पहले से मौजूद है\nफिर से करना चाहते हैं";


    static public class memberTable {
          public static String  name =  "membername";
          public static String  mobile =  "membermobile";
          public static String  code =  "membercode";
          public static String  id =  "Id";
          public static String  detail =  "alldetails";

     }


     static public class milk {
          public static String  code =  "memberCode";
          public static String  id =  "Id";
          public static String  weight =  "milkweight";
          public static String  rate =  "rateperliter";
          public static String  amount =  "totalamount";
          public static String  date =  "date";
          public static String  number =  "number";  // milkinformation
          public static String  sift =  "sift";
          public static String  fat =  "fat";
          public static String  fat_wt =  "fat_wt";
          public static String  snf =  "snf";
          public static String  snf_wt =  "snf_wt";
          public static String  cmf =  "allInformation";
          public static String  message =  "message";
          public static String  info =  "allInformation";
          public static String  dateSave =  "dateSave";
          public static String  info2 =  "dailyInformation";
     }




    static public String getCurrentDate(){

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        month++;
        int monthlength =String.valueOf(month).length();
        int daylength =String.valueOf(day).length();

        String date = "";

        if(monthlength==1&&daylength==1){
             date = "0"+String.valueOf(day)+"/"+"0"+String.valueOf(month)+"/"+String.valueOf(year);

        }else if(monthlength==1&&daylength>1){
            date = String.valueOf(day)+"/"+"0"+String.valueOf(month)+"/"+""+String.valueOf(year);

        }else if(monthlength>1&&daylength==1){
            date = "0"+String.valueOf(day)+"/"+""+String.valueOf(month)+"/"+String.valueOf(year);

        }else if(monthlength>1&&daylength>1){
            date = String.valueOf(day)+"/"+""+String.valueOf(month)+"/"+""+String.valueOf(year);

        }
        return date;
    }


    static public String reverceDate(String date) {
        date = date.replace("/", "");
        String dd = date.substring(0, 2);
        String mm = date.substring(2, 4);
        String yy = date.substring(4, 8);
        return yy + mm + dd;

    }

    static public String lineBreak()  {
        return "===========================\n";
    }

    static public String AddSlipSignature()  {
        return "";
    }
}
