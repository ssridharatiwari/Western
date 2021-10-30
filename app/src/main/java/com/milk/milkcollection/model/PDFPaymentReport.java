package com.milk.milkcollection.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;

import com.milk.milkcollection.Activity.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class PDFPaymentReport {

    public ArrayList<PaymentReport> reportList = new ArrayList<>();
    public String dateStart  = "";
    public String dateEnd  = "";
    public String title = "";
    public String reportTitle = "";
    public String totalWt = "0";
    public String totalAmt = "0";

    public PDFPaymentReport(){

    }

    public  void setAmounts(Float wt, Float totalAmt) {

        try {
            this.totalWt = (MainActivity.twoDecimalFloatToString(wt));
            this.totalAmt = (MainActivity.twoDecimalFloatToString(totalAmt));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setData(String title, String reportTitle, String dateStart, String dateEnd) {
        this.title = title;
        this.reportTitle = reportTitle;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    public void setArray(ArrayList<PaymentReport> array){
        this.reportList = array;
    }

    public  ArrayList<PaymentReport> getReportList(){ return  reportList; }
    public  String getTitle(){ return  title; }
    public  String getReportTitle(){ return  reportTitle; }
    public  String getTotalWt(){ return  totalWt; }
    public  String getTotalAmt(){ return  totalAmt; }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void createSessionPdf(PDFPaymentReport pdfDaily) {
            Rect bounds = new Rect();
            int pageWidth = 600;
            int pageheight = 200 + 11 * pdfDaily.getReportList().size();
            int pathHeight = 2;

            String outputFile =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + File.separator + "payment-report.pdf";
            File sourceFile = new File(outputFile);

            PdfDocument myPdfDocument = new PdfDocument();
            Paint paint = new Paint();
            paint.setTextSize(10);


            Paint paint2 = new Paint();
            Path path = new Path();
            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageheight, 1).create();
            PdfDocument.Page documentPage = myPdfDocument.startPage(myPageInfo);
            Canvas canvas = documentPage.getCanvas();
            int y = 25;
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

            y += paint.descent() - paint.ascent();
            canvas.drawText("", x, y, paint);


            path.lineTo(pageWidth, pathHeight);
            paint2.setColor(Color.GRAY);
            paint2.setStyle(Paint.Style.STROKE);
            path.moveTo(x, y);
            canvas.drawLine(0, y, pageWidth, y, paint2);

            x = 40;
            y += paint.descent() - paint.ascent();
            canvas.drawText("Payment Report", x, y, paint);

            y += paint.descent() - paint.ascent();
            canvas.drawText( "Date : " + pdfDaily.dateStart +  "    to    " + pdfDaily.dateEnd, x, y, paint);

            y += paint.descent() - paint.ascent();
            canvas.drawLine(0, y, pageWidth, y, paint2);


            Paint paintDisit = new Paint();
            paintDisit.setTextSize(10);
            paintDisit.setTextAlign(Paint.Align.RIGHT);
            paintDisit.setStrokeWidth(40);

            // headers

            y += paint.descent() - paint.ascent();
            x = 40;
            canvas.drawText("code", x,y, paint);

            x += 50;
            canvas.drawText("Name", x,y, paint);

            x += 160;
            canvas.drawText("Weight", x,y, paintDisit);

            x += 70;
            canvas.drawText("Amount", x,y, paintDisit);



            for(PaymentReport entry : pdfDaily.getReportList()){

                x = 40;
                y += paint.descent() - paint.ascent();
                canvas.drawText(String.valueOf(entry.getCode()), x,y, paint);

                x += 50;
                canvas.drawText(entry.getName() , x,y, paint);


                x += 160;
                canvas.drawText(entry.getWeight(), x,y, paintDisit);

                x += 70;
                canvas.drawText(MainActivity.twoDecimal( entry.getAmount()), x,y, paintDisit);
            }


            x = 40;
            y += paint.descent() - paint.ascent();
            canvas.drawLine(0, y, pageWidth, y, paint2);

            y += paint.descent() - paint.ascent();
            canvas.drawText( "Total Weight : " + pdfDaily.getTotalWt(), x, y, paint);

            y += paint.descent() - paint.ascent();
            canvas.drawText( "Total Amount : " + pdfDaily.getTotalAmt(), x, y, paint);


            // horizontal line
            path.lineTo(pageWidth, pathHeight);
            paint2.setColor(Color.GRAY);
            paint2.setStyle(Paint.Style.STROKE);

            y += 20;

            path.moveTo(x, y);
            canvas.drawLine(0, y, pageWidth, y, paint2);

            //blank space
            y += paint.descent() - paint.ascent();
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
