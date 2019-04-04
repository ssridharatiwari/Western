package com.milk.milkcollection.Fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.IllegalFormatCodePointException;
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
import static android.content.ContentValues.TAG;

/**
 * Created by Alpha on 13-12-2015.
 */
public class Fragment_Excel extends Fragment {
    private static final int GALLERY_KITKAT_INTENT_CALLED = 0;
    private Button btn_select,btn_show,btn_save,btn_select_from_download,btn_create_new;
    MilkDBHelpers milkDBHelpers;
    String date,FILE_PATH = "";
    private EditText et_fat,et_snf,et_rate;

    String fat="",snf="" , id_rate = "";

    TextView txt_rate,text_temp;
    String rateMethod = "1";
    public Fragment_Excel() {}
    int PERMISSION_REQUEST_CODE = 1 , PERMISSION_REQUEST_CODE_DOWNLOAD;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.excel_fragment, container, false);

        text_temp = (TextView) rootView.findViewById(R.id.text_temp);
        milkDBHelpers=  new MilkDBHelpers(getActivity());

        et_snf =(EditText)rootView.findViewById(R.id.et_snf);
        et_fat =(EditText)rootView.findViewById(R.id.et_fat);
        et_rate =(EditText)rootView.findViewById(R.id.et_rate);

        btn_select = (Button)rootView.findViewById(R.id.btn_select);
        btn_show = (Button)rootView.findViewById(R.id.btn_show);
        btn_create_new = (Button)rootView.findViewById(R.id.btn_create_new);
        btn_save = (Button)rootView.findViewById(R.id.btn_save);
        btn_select_from_download = (Button)rootView.findViewById(R.id.btn_select_from_download);


        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(MainActivity.instace);
        rateMethod =  sharedPreferencesUtils.getRateMethodCode();




        btn_select.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                callIntent();
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
            btn_create_new.setText("Fat Clr Manual Chart");
        }else{
            btn_create_new.setText("Fat Snf Manual Chart");

        }



        btn_select_from_download.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED  || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission is granted");
                    requestPermissions(new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE_DOWNLOAD);                }
                else {

                    importFromDownload();

                    // downloadFile();
                }
            }
        });

        btn_create_new.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Fragment fragment = new fill_chart();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack("home");
                ft.commit();
            }
        });


        return rootView;
    }


    public void importFromDownload(){

        String fileName = "";

        if(rateMethod.equals("2")){
            fileName = "snf.csv";
        }else if(rateMethod.equals("3")){
            fileName = "clr.csv";
        }

        final String finalFileName = fileName;
        new AlertDialog.Builder(getActivity()).setTitle("Western")
                .setMessage("is there " + fileName + " exist")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FILE_PATH= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                                File.separator + finalFileName;

                        if (new File(FILE_PATH).exists()){

                            MainActivity.instace.showLoading("Reading Data : wait for a minut..");

                            if (rateMethod.equals("2")) {
                                readFatSnfRateCSVChartData();
                            } else if (rateMethod.equals("3")) {
                                readFatClrRateCSVChartData();
                            }
                        }else{

                            Toast.makeText(getActivity(), "file not found", Toast.LENGTH_LONG).show();
                        }

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                  @Override
                    public void onClick(DialogInterface dialog, int which) {


                     }
        }).show();



    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE_DOWNLOAD){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
                importFromDownload();
        }else {
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
                callIntent();
        }


    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public void callIntent(){

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED  || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission is granted");
            requestPermissions(new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);                }
        else {

            String[] mimeTypes = {"text/csv", "text/comma-separated-values", "application/csv"};

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
                intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
            }
            startActivityForResult(Intent.createChooser(intent, "ChooseFile"), 0);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if( data != null) {

            final Uri uri = data.getData();

            FILE_PATH = uri.getPath();
            Log.e("path ", FILE_PATH);


            if (FILE_PATH.length() > 0) {

                getActivity().runOnUiThread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {

                        Log.e("method colled", rateMethod);

                        if (new File(FILE_PATH).exists()){
                            MainActivity.instace.showLoading("Reading Data : wait for a minut..");

                            if (rateMethod.equals("2")) {
                                readFatSnfRateCSVChartData();
                            } else if (rateMethod.equals("3")) {
                                readFatClrRateCSVChartData();
                            }
                        }else{

                            Toast.makeText(getActivity(), "file path not found", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            } else {
                Toast.makeText(getActivity(), "file path not found", Toast.LENGTH_LONG).show();
            }
        }
    }




    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
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
        try {
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



    private void readFatSnfRateCSVChartData (){


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

                    int tag = 0;
                    while ((line = br.readLine()) != null) {

                        if (MainActivity.trimString(line).length() > 0) {

                            String[] country = line.split(cvsSplitBy);

                            if (country.length>0) {

                            float tempClr = (float) 11;

                            String fat =  country[0];

                            if (tag == 0){
                                tag = 1;
                                if (!fat.equals("FATSNF")){
                                    MainActivity.showToast("This is not fat correct FAT-SNF chart");
                                    MainActivity.instace.dismiss();
                                    Log.e("fat - " ,fat);
                                    return;
                                }else{
                                    milkDBHelpers.emptyfatSnf();
                                }
                            }

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


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    FileReader in = new FileReader(FILE_PATH);
                    BufferedReader br = new BufferedReader(in);
                    String line = "";
                    String cvsSplitBy = ",";


                    int tag = 0;
                    while ((line = br.readLine()) != null) {

                        if (MainActivity.trimString(line).length() > 0) {

                            String[] country = line.split(cvsSplitBy);

                            if (country.length>0){
                            float tempClr = (float) 30;
                            String fat = country[0];

                                if (tag == 0){
                                    tag = 1;
                                    if (!fat.equals("FATCLR")){
                                        MainActivity.showToast("This is not fat correct fat clr chart");
                                        MainActivity.instace.dismiss();
                                        Log.e("fat - " ,fat);

                                        return;
                                    }else{
                                        milkDBHelpers.emptyClrTable();
                                    }
                                }

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





//
//
//    private static void downloadFile() {
//        try {
//            URL u = new URL("https://wokosoft.com/western/snf.csv");
//            URLConnection conn = u.openConnection();
//            int contentLength = conn.getContentLength();
//
//            DataInputStream stream = new DataInputStream(u.openStream());
//
//            byte[] buffer = new byte[contentLength];
//            stream.readFully(buffer);
//            stream.close();
//
//            DataOutputStream fos = new DataOutputStream(new FileOutputStream( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
//                    File.separator + "sfn1.csv"));
//            fos.write(buffer);
//            fos.flush();
//            fos.close();
//        } catch(FileNotFoundException e) {
//            return; // swallow a 404
//        } catch (IOException e) {
//            return; // swallow a 404
//        }
//    }



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