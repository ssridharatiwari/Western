package com.milk.milkcollection.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.milk.milkcollection.Activity.MainActivity;
import com.milk.milkcollection.Activity.PinActivity;
import com.milk.milkcollection.Database.MilkDBHelpers;
import com.milk.milkcollection.R;
import com.milk.milkcollection.helper.SharedPreferencesUtils;
import com.milk.milkcollection.model.ListRate;
import com.milk.milkcollection.model.SingleReport;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;


import org.json.JSONException;

import static android.R.attr.key;
import static android.R.attr.logo;
import static android.R.attr.visibility;
import static android.R.attr.visible;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Alpha on 13-12-2015.
 */
public class Fragment_Excel extends Fragment {
    private static final int GALLERY_KITKAT_INTENT_CALLED = 0;
    private Button btn_select,btn_show,btn_edit,btn_save;
    MilkDBHelpers milkDBHelpers;
    String date,FILE_PATH = "";
    private EditText et_fat,et_snf,et_rate;

    String fat="",snf="" , id_rate = "";

    TextView txt_rate,text_temp;
    String rateMethod = "1";
    public Fragment_Excel() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.excel_fragment, container, false);

        txt_rate = (TextView) rootView.findViewById(R.id.txt_rate);
        text_temp = (TextView) rootView.findViewById(R.id.text_temp);
        milkDBHelpers=  new MilkDBHelpers(getActivity());

        et_snf =(EditText)rootView.findViewById(R.id.et_snf);
        et_fat =(EditText)rootView.findViewById(R.id.et_fat);
        et_rate =(EditText)rootView.findViewById(R.id.et_rate);

        btn_select = (Button)rootView.findViewById(R.id.btn_select);
        btn_show = (Button)rootView.findViewById(R.id.btn_show);
        btn_edit = (Button)rootView.findViewById(R.id.btn_edit);
        btn_save = (Button)rootView.findViewById(R.id.btn_save);


        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(MainActivity.instace);
         rateMethod =  sharedPreferencesUtils.getRateMethodCode();

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] mimeTypes = {"text/comma_separated_values/csv","text/plain","text/*"};

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                    if (mimeTypes.length > 0) {
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    }
                } else {
                    String mimeTypesStr = "";
                    for (String mimeType : mimeTypes) {
                        mimeTypesStr += mimeType + "|";
                    }
                    intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
                }

                startActivityForResult(Intent.createChooser(intent,"ChooseFile"), 0);

            }
        });

        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRate();
            }
        });

        if (rateMethod.equals("3")){
            et_snf.setHint("Clr");
        }



     //   getAllData();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if( data != null) {

            Log.e("request code ", String.valueOf(requestCode));
            Log.e("Path ", data.getData().getPath());


            Uri uri = data.getData();

            FILE_PATH = uri.getPath();
            Log.e("path ", FILE_PATH);


            if (FILE_PATH.length() > 0) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        MainActivity.instace.showLoading("Reading Data : wait for a minut..");
                        Log.e("method colled", rateMethod);
                        if (rateMethod.equals("2")) {
                            readFatSnfRateCSVChartData();
                        } else if (rateMethod.equals("3")) {
                            readFatClrRateCSVChartData();
                        }
                    }
                });

            } else {
                Toast.makeText(getActivity(), "file path not found", Toast.LENGTH_LONG).show();
            }
        }
    }



private  void  getData () {

    try{

            fat = MainActivity.oneDecimalString(et_fat.getText().toString());
            snf = MainActivity.oneDecimalString(et_snf.getText().toString());

            Log.e("fat  --  ", fat);
            Log.e("clr  --  ", snf);

            if (rateMethod.equals("2")){

                milkDBHelpers = new MilkDBHelpers(getActivity());
                SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery("Select * From 'ratechart' WHERE fat ='" + fat + "' and snf ='" + snf + "'", null);

                if (cursor != null && cursor.moveToFirst()) {

                    while (cursor.isAfterLast() == false) {

                        txt_rate.setText(cursor.getString(cursor.getColumnIndex("rate")));
                        et_rate.setText(cursor.getString(cursor.getColumnIndex("rate")));
                        id_rate = cursor.getString(cursor.getColumnIndex("Id"));

                        et_rate.setVisibility(View.VISIBLE);
                        btn_save.setVisibility(View.VISIBLE);
                        text_temp.setVisibility(View.VISIBLE);
                        cursor.moveToNext();
                    }
                }else{
                    MainActivity.showToast("No data found");
                }

            }else{

                milkDBHelpers = new MilkDBHelpers(getActivity());
                SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery("Select * From 'ratechartclr' WHERE fat ='" + fat + "' and snf ='" + snf + "'", null);
                cursor.moveToFirst();

                if (cursor != null) {
                    while (cursor.isAfterLast() == false) {

                        txt_rate.setText(cursor.getString(cursor.getColumnIndex("rate")));
                        et_rate.setText(cursor.getString(cursor.getColumnIndex("rate")));
                        id_rate = cursor.getString(cursor.getColumnIndex("Id"));

                        btn_save.setVisibility(View.VISIBLE);
                        text_temp.setVisibility(View.VISIBLE);
                        et_rate.setVisibility(View.VISIBLE);
                        cursor.moveToNext();
                    }
                } else {
                    MainActivity.showToast("No data found");
                }
            }

    } catch (Exception e) {
            Toast.makeText(getActivity(), "Not Found Any Data", Toast.LENGTH_LONG).show();
    }
}






    private  void  updateRate () {
        try
        {
            if (et_rate.getText().length() > 0) {

                fat = MainActivity.oneDecimalString(et_fat.getText().toString());
                snf = MainActivity.oneDecimalString(et_snf.getText().toString());

                milkDBHelpers = new MilkDBHelpers(getActivity());
                if (rateMethod.equals("2")){
                    milkDBHelpers.updateRate(id_rate,et_rate.getText().toString());
                }else if (rateMethod.equals("3")){
                    milkDBHelpers.updateRateClr(id_rate,et_rate.getText().toString());
                }

            }else {
                Toast.makeText(getActivity(), "Enter Rate", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Not Found Any Data", Toast.LENGTH_LONG).show();
        }
    }





    /////////////////////

    private void readFatSnfRateCSVChartData (){

        milkDBHelpers.emptyfatSnf();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("data colled",rateMethod);

                try{

                    FileReader in = new FileReader(FILE_PATH);
                    BufferedReader br = new BufferedReader(in);
                    String line = "";
                    String cvsSplitBy = ",";


                    while ((line = br.readLine()) != null) {

                        if (MainActivity.trimString(line).length() > 0) {

                            String[] country = line.split(cvsSplitBy);

                            if (country.length>0){

                            float tempClr = (float) 11;

                            String fat =  country[0];

                            if (MainActivity.trimString(fat).length() > 0 ) {

                                for (int a=1; a< country.length ; a++){
                                    String rat =  country[a];
                                    String snf = MainActivity.oneDecimalFloatToString(tempClr);

                                    if (fat.length() > 0 && rat.length() > 0) {

                                         fat = MainActivity.oneDecimalString(fat);
                                         Log.e("fat - " ,fat);
                                         Log.e("snf" ,snf);
                                         Log.e("rate" ,rat);

                                        if (snf.length() > 0 && fat.length() > 0 && rat.length() > 0) {
                                               milkDBHelpers.addRate(fat, snf, rat);
                                        }
                                    }

                                    tempClr = (float)(tempClr - 0.1);
                                }
                            }}
                        }else{
                            break;
                        }
                    }


                    MainActivity.instace.dismiss();
                    MainActivity.showToast("Data Successfully updated");
                }catch(FileNotFoundException e){

                    try {
                        MainActivity.showToast("File not found");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    MainActivity.instace.dismiss();
                    e.printStackTrace();
                } catch (IOException e) {

                    try {
                        MainActivity.showToast("Buffer reader");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    MainActivity.instace.dismiss();
                    e.printStackTrace();
                }}


        }, 100);


    }

    private void readFatClrRateCSVChartData (){

        milkDBHelpers.emptyClrTable();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    FileReader in = new FileReader(FILE_PATH);
                    BufferedReader br = new BufferedReader(in);
                    String line = "";
                    String cvsSplitBy = ",";


                    while ((line = br.readLine()) != null) {

                        if (MainActivity.trimString(line).length() > 0) {

                            String[] country = line.split(cvsSplitBy);

                            if (country.length>0){
                            float tempClr = (float) 30;
                            String fat = country[0];

                            if (MainActivity.trimString(fat).length() > 0 ) {

                                for (int a = 1; a < country.length; a++) {
                                    String rat = country[a];
                                    String snf = MainActivity.oneDecimalFloatToString(tempClr);

                                    if (fat.length() > 0 && rat.length() > 0) {

                                        fat = MainActivity.oneDecimalString(fat);
                                        Log.e("fat - ", fat);
                                        Log.e("Clr", snf);
                                        Log.e("rate", rat);

                                        if (snf.length() > 0 && fat.length() > 0 && rat.length() > 0) {
                                            milkDBHelpers.addRateClr(fat, snf, rat);
                                        }
                                    }
                                    tempClr = (float) (tempClr - 0.5);
                                }
                             }
                            }
                        }else{
                            break;
                        }
                    }


//                    CSVReader reader = new CSVReader(new InputStreamReader( new FileInputStream(initialFile)));
//                    //CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.clr_rate)));
//                    milkDBHelpers = new MilkDBHelpers(getActivity());
//
//                    String [] nextLine;
//                    while ((nextLine = reader.readNext()) != null) {
//
//                        float tempClr = (float) 30;
//                        String fat =  nextLine[0];
//                        for (int a=1; a< nextLine.length ; a++){
//                            String rat =  nextLine[a];
//                            String snf = MainActivity.oneDecimalFloatToString(tempClr);
//
//                            if (fat.length() > 0 && rat.length() > 0) {
//
//                                fat = MainActivity.oneDecimalString(fat);
//                                // Log.e("fat - " ,fat);
//                                // Log.e("Clr" ,snf);
//
//                                if (snf.length() > 0 && fat.length() > 0 && rat.length() > 0) {
//                                    milkDBHelpers.addRateClr(fat, snf, rat);
//                                }
//                            }
//
//                            tempClr = (float)(tempClr - 0.5);
//                        }
//                    }
                    MainActivity.instace.dismiss();
                    MainActivity.showToast("Data Successfully updated");

                }catch(FileNotFoundException e){

                    try {
                        MainActivity.showToast("File not found");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    MainActivity.instace.dismiss();
                    e.printStackTrace();
                } catch (IOException e) {

                    try {
                        MainActivity.showToast("Buffer reader");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    MainActivity.instace.dismiss();
                    e.printStackTrace();
                }}


        }, 100);

    }





    private  void  getAllData () {
        try
        {
            milkDBHelpers = new MilkDBHelpers(getActivity());
            SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();

            Cursor cursor = sqLiteDatabase.rawQuery("Select * From ratechart", null);

            if (cursor != null && cursor.moveToFirst()) {

                while (cursor.isAfterLast() == false) {

                    Log.e("rate ",cursor.getString(cursor.getColumnIndex("rate")));
                    Log.e("snf ",cursor.getString(cursor.getColumnIndex("fat")));
                    Log.e("fat ",cursor.getString(cursor.getColumnIndex("snf")));
                    Log.e("id ",cursor.getString(cursor.getColumnIndex("Id")));

                    cursor.moveToNext();
                }
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Not Found Any Data", Toast.LENGTH_LONG).show();
        }
    }
    private void getCalendarDate(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        date = String.valueOf(day)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year);
        Log.e("-=-=date--=", date);
    }




}

//    File initialFile = new File(FILE_PATH);
//    CSVReader reader = new CSVReader(new FileReader(initialFile));
//// CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.snfrate)));//Specify asset file name
//// CSVReader reader = new CSVReader(new InputStreamReader( new FileInputStream(initialFile)));
//            milkDBHelpers=  new MilkDBHelpers(getActivity());
//
//                    String [] nextLine;
//                    while ((nextLine = reader.readNext()) != null) {
//
//                    float tempSnf = 11;
//
//                    String fat =  nextLine[0];
//
//                    //  Log.e("fat snf -- " , fat);
//
//                    for (int a=1; a< nextLine.length ; a++){
//
//        String rat =  nextLine[a];
//        String snf = MainActivity.oneDecimalFloatToString(tempSnf);
//        //Log.e("SNF" ,snf);
//
//        if (fat.length() > 0 && rat.length() > 0) {
//        fat = MainActivity.oneDecimalString(fat);
//        if (snf.length() > 0 && fat.length() > 0 && rat.length() > 0) {
//        milkDBHelpers.addRate(fat,snf,rat);
//        }
//        }
//
//        tempSnf = (float) (tempSnf - 0.1);
//        }
//        }