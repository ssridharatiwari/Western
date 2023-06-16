package com.milk.milkcollectionapp.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import com.milk.milkcollectionapp.activity.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void createSessionPdf(PDFDaily pdfDaily) {
            Rect bounds = new Rect();
            int pageWidth = 600;
            int yspace = 16;
            int pageheight = 200 + yspace * pdfDaily.getReportList().size();
            int pathHeight = 2;

            String outputFile =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + File.separator + pdfDaily.getFileName();
            File sourceFile = new File(outputFile);

            PdfDocument myPdfDocument = new PdfDocument();
            Paint paint = new Paint();
            paint.setTextSize(10);

            Paint paint2 = new Paint();
            Path path = new Path();
            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageheight, 1).create();
            PdfDocument.Page documentPage = myPdfDocument.startPage(myPageInfo);
            Canvas canvas = documentPage.getCanvas();
            int y = 25; // x = 10,
            int x = 10;

            Paint textPaint = new Paint();
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(20);

            int xPos = (canvas.getWidth() / 2);
            if (pdfDaily.title != null) {
                canvas.drawText(pdfDaily.getTitle(), xPos, y, textPaint);
            }else{
                canvas.drawText("", xPos, y, textPaint);
            }

            y += yspace;
            canvas.drawText("", x, y, paint);

            //horizontal line
            path.lineTo(pageWidth, pathHeight);
            paint2.setColor(Color.GRAY);
            paint2.setStyle(Paint.Style.STROKE);
            path.moveTo(x, y);
            canvas.drawLine(0, y, pageWidth, y, paint2);

            x = 40;
            y += yspace;
            canvas.drawText(pdfDaily.getReportTitle(), x, y, paint);

            y += yspace;
            canvas.drawText( "Date : " + pdfDaily.getDate(), x, y, paint);
            canvas.drawText( "Session : " + pdfDaily.shift, 150, y, paint);
            y += yspace;
            canvas.drawLine(0, y, pageWidth, y, paint2);
            //blank space
            y += yspace;
            canvas.drawText("", x, y, paint);

            Paint paintDisit = new Paint();
            paintDisit.setTextSize(10);
            paintDisit.setTextAlign(Paint.Align.RIGHT);
            paintDisit.setStrokeWidth(40);

            // headers

            y += yspace;
            x = 40;
            canvas.drawText("SNo.", x,y, paint);

            x += 40;
            canvas.drawText("Code", x,y, paint);

            x += 40;
            canvas.drawText("Member Name", x,y, paint);

            x += 140;
            canvas.drawText("Qty", x,y, paintDisit);

            x += 50;
            canvas.drawText("Fat", x,y, paintDisit);

            x += 50;
            canvas.drawText(MainActivity.instace.rateString(), x,y, paintDisit);

            x += 60;
            canvas.drawText("Rate", x,y, paintDisit);

            x += 60;
            canvas.drawText("Amount", x,y, paintDisit);

            int sNo = 1;
            for(SingleEntry entry : pdfDaily.getReportList()){

                y += yspace;
                x = 40;
                canvas.drawText(String.valueOf(sNo), x,y, paint);
                sNo += 1;
                x += 40;
                canvas.drawText(entry.getCode(), x,y, paint);

                x += 40;
                canvas.drawText(MainActivity.instace.milkDBHelpers.getMemberNameByCode(entry.getCode()), x,y, paint);

                x += 140;
                canvas.drawText(entry.getWeight(), x,y, paintDisit);

                x += 50;
                canvas.drawText(entry.getfat(), x,y, paintDisit);

                x += 50;
                canvas.drawText(entry.getSnf(), x,y, paintDisit);

                x += 60;
                canvas.drawText( MainActivity.twoDecimalString(entry.getRate()) , x,y, paintDisit);

                x += 60;
                canvas.drawText( MainActivity.twoDecimalString(entry.getAmount()), x,y, paintDisit);
            }



            x = 40;
            y += yspace;
            canvas.drawLine(0, y, pageWidth, y, paint2);

            y += yspace;
            canvas.drawText( "Total Weight : " + pdfDaily.getTotalWt(), x, y, paint);

            y += yspace;
            canvas.drawText( "Avg FAT : " + pdfDaily.getAvgFat(), x, y, paint);

            y += yspace;
            canvas.drawText( "AVG SNF : " + pdfDaily.getAvgSnf(), x, y, paint);

            y += yspace;
            canvas.drawText( "Total Amount : " + pdfDaily.getTotalAmt(), x, y, paint);



            //horizontal line
            path.lineTo(pageWidth, pathHeight);
            paint2.setColor(Color.GRAY);
            paint2.setStyle(Paint.Style.STROKE);

            y += 20;

            path.moveTo(x, y);
            canvas.drawLine(0, y, pageWidth, y, paint2);

            //blank space
            y += yspace;
            canvas.drawText("", x, y, paint);

            textPaint.setTextSize(8);
            canvas.drawText("Western Electronics Group", xPos, y, textPaint);
            myPdfDocument.finishPage(documentPage);


            try {
                myPdfDocument.writeTo(new FileOutputStream(sourceFile));
            } catch (IOException e) {
                e.printStackTrace();
            }

            myPdfDocument.close();
            MainActivity.instace.shareFile(outputFile);
    }



}
