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

import com.milk.milkcollectionapp.Activity.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class PDFMermberReport {

    public ArrayList<SingleEntry> reportList = new ArrayList<>();
    public String dateStart  = "";
    public String dateEnd  = "";
    public String title = "";
    public String member = "";
    public String code = "";
    public String reportTitle = "";
    public String fileName = "";
    public String totalWt = "0";
    public String totalAmt = "0";

    public PDFMermberReport(){}


    public  void setAmounts(Float wt, Float totalAmt) {

        try {
            this.totalWt = (MainActivity.twoDecimalFloatToString(wt));
            this.totalAmt = (MainActivity.twoDecimalFloatToString(totalAmt));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setData(String title, String reportTitle,String member,String code, String dateStart, String dateEnd) {
        this.title = title;
        this.reportTitle = reportTitle;
        this.member = member;
        this.code = code;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    public void setArray(ArrayList<SingleEntry> array){
        this.reportList = array;
    }
    public  String getFileName(){ return  fileName; }
    public  ArrayList<SingleEntry> getReportList(){ return  reportList; }
    public  String getTitle(){ return  title; }
    public  String getReportTitle(){ return  reportTitle; }
    public  String getTotalWt(){ return  totalWt; }
    public  String getTotalAmt(){ return  totalAmt; }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void createSessionPdf(PDFMermberReport pdfDaily) {
            Rect bounds = new Rect();
            int pageWidth = 600;
            int yspace = 16;
            int pageheight = 200 + yspace * pdfDaily.getReportList().size();
            int pathHeight = 2;

            String outputFile =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + File.separator + "mermber-report.pdf";
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
            canvas.drawText(pdfDaily.member , x, y, paint);

            y += yspace;
            canvas.drawText("code  : " + pdfDaily.code, x, y, paint);

            y += yspace;
            canvas.drawText( "Date : " + pdfDaily.dateStart +  "    to    " + pdfDaily.dateEnd, x, y, paint);

            y += yspace;
            canvas.drawLine(0, y, pageWidth, y, paint2);
            //blank space

            Paint paintDisit = new Paint();
            paintDisit.setTextSize(10);
            paintDisit.setTextAlign(Paint.Align.RIGHT);
            paintDisit.setStrokeWidth(40);

            // headers

            y += yspace;
            x = 40;
            canvas.drawText("Date", x,y, paint);

            x += 70;
            canvas.drawText("Qty", x,y, paint);

            x += 60;
            canvas.drawText("Fat", x,y, paint);

            x += 60;
            canvas.drawText("Snf", x,y, paintDisit);

            x += 60;
            canvas.drawText("Rate", x,y, paintDisit);

            x += 70;
            canvas.drawText("Amt", x,y, paintDisit);



            for(SingleEntry entry : pdfDaily.getReportList()){

                y += yspace;
                x = 40;
                canvas.drawText(String.valueOf(entry.getDatesave()), x,y, paint);

                x += 70;
                canvas.drawText(entry.getWeight(), x,y, paint);

                x += 60;
                canvas.drawText(entry.getfat(), x,y, paint);

                x += 60;
                canvas.drawText(entry.getSnf(), x,y, paintDisit);

                x += 60;
                canvas.drawText( MainActivity.twoDecimal( entry.getRate()), x,y, paintDisit);

                x += 70;
                canvas.drawText(MainActivity.twoDecimal( entry.getAmount()), x,y, paintDisit);
            }


            x = 40;
            y += yspace;
            canvas.drawLine(0, y, pageWidth, y, paint2);

            y += yspace;
            canvas.drawText( "Total Weight : " + pdfDaily.getTotalWt(), x, y, paint);

            y += yspace;
            canvas.drawText( "Total Amount : " + pdfDaily.getTotalAmt(), x, y, paint);


            // horizontal line
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
