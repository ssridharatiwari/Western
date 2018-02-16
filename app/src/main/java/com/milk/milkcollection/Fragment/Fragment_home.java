package com.milk.milkcollection.Fragment;


import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.milk.milkcollection.Activity.MainActivity;
import com.milk.milkcollection.Activity.PinActivity;
import com.milk.milkcollection.Database.MilkDBHelpers;
import com.milk.milkcollection.R;
import com.milk.milkcollection.SClient;
import com.milk.milkcollection.application.AppApplication;
import com.milk.milkcollection.helper.DatePickerFragment;
import com.milk.milkcollection.helper.SharedPreferencesUtils;
import com.milk.milkcollection.myutility;
import com.milk.milkcollection.retrofit.HttpServerBackend;
import com.milk.milkcollection.retrofit.RestAdapter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

import static com.milk.milkcollection.Activity.MainActivity.hideKeyboard;

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class Fragment_home extends Fragment {

    ArrayAdapter<String> adapter;
    Button btn_ratesave, btn_message, btn_print, btn_pss;
    LinearLayout createrate,todayDetailLL;
    EditText et_weight, et_fat, et_snf, et_code;
    TextView rate, total, btnrate, btntotal, tv_datepicker, tv_code_holder,lbl_avgFat,
            lbl_avgSnf,lbl_wgt,lbl_amt,lbl_SeriolNo,lbl_snf_home,lbl_snf_avg,toolbartitle;
    String weight, fat, snf, code;
    String phone_number, message, sift, printString, titlename, mobile_self,comission;
    float rateperltr;
    Spinner sp_shift;
    int  totalrs, tagCode;
    ProgressDialog dialog;
    MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
    SharedPreferencesUtils sharedPreferencesUtils;

    public Fragment_home() {
    }

    byte FONT_TYPE;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        comission = "+";

        toolbartitle = (TextView) getActivity().findViewById(R.id.titletool);
        toolbartitle.setText("Home");

        dialog = new ProgressDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        createrate = (LinearLayout) rootView.findViewById(R.id.createrate);
        todayDetailLL = (LinearLayout) rootView.findViewById(R.id.todayDetailLL);

        et_weight = (EditText) rootView.findViewById(R.id.weight);
        lbl_avgFat = (TextView) rootView.findViewById(R.id.lbl_fat);
        lbl_avgSnf = (TextView) rootView.findViewById(R.id.lbl_snf);
        lbl_amt = (TextView) rootView.findViewById(R.id.lbl_amount);
        lbl_wgt = (TextView) rootView.findViewById(R.id.lbl_wgt);
        lbl_SeriolNo = (TextView) rootView.findViewById(R.id.lbl_SeriolNo);
        lbl_snf_home = (TextView) rootView.findViewById(R.id.lbl_snf_home);
        lbl_snf_avg = (TextView) rootView.findViewById(R.id.lbl_snf_avg);

        et_fat = (EditText) rootView.findViewById(R.id.fat);
        et_snf = (EditText) rootView.findViewById(R.id.snf);
        et_code = (EditText) rootView.findViewById(R.id.et_code);
        rate = (TextView) rootView.findViewById(R.id.rate);
        tv_code_holder = (TextView) rootView.findViewById(R.id.tv_code_holder);
        total = (TextView) rootView.findViewById(R.id.total);
        btnrate = (TextView) rootView.findViewById(R.id.click_rate);
        btntotal = (TextView) rootView.findViewById(R.id.click_total);
        tv_datepicker = (TextView) rootView.findViewById(R.id.tv_date);
        btn_message = (Button) rootView.findViewById(R.id.btn_messsag);
        btn_print = (Button) rootView.findViewById(R.id.btn_print);
        btn_pss = (Button) rootView.findViewById(R.id.btn_pss);

        sp_shift = (Spinner) rootView.findViewById(R.id.sp_shift);

        sharedPreferencesUtils = new SharedPreferencesUtils(getActivity());
        milkDBHelpers=  new MilkDBHelpers(getActivity());

        titlename = sharedPreferencesUtils.getTitle();
        mobile_self = sharedPreferencesUtils.getMobile();
        toolbartitle.setText(titlename);

        et_snf.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) {
                    createmilkvalue();
                    hideKeyboard(getActivity());
                }
                return false;
            }
        });

        et_code.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                getUserName();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        String[] morning_evening = new String[]{"Morning", "Evening"};
        ArrayAdapter<String> SpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinnertext, morning_evening);
        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_shift.setAdapter(SpinnerAdapter);
        sp_shift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {
                int i = position;
                Log.e("sift_int", String.valueOf(i));
                sift = parent.getItemAtPosition(position) + "";
                Log.e("sift", sift);
                if (i == 0) {
                    sift = "M";
                } else if (i == 1) {
                    sift = "E";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

         createrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comission.equals("-")) {
                    createmilkvalue();
                }
                if (comission.equals("+")) {
                    createmilkvalue();
                }
            }
        });

        btn_pss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toolbartitle.setText("Pay");
                Fragment fragment = new Fragment_DailyReport();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack(fragment + "");
                ft.commit();
            }
        });


        todayDetailLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toolbartitle.setText("Daily Report");
                Fragment fragment = new Fragment_DailyReport();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack(fragment + "");
                ft.commit();
            }
        });



        tv_datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment picker = new DatePickerFragment(tv_datepicker);
                picker.show(getFragmentManager(), "datePicker");
            }
        });

        getCurrentDate();

        MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
        int memberlength = milkDBHelpers.getMember();
        String memberrecord = String.valueOf(memberlength);
        if (memberrecord != null) {
            Log.e("-----member--------", memberrecord);
        }
        int updatebhavlingth = milkDBHelpers.getupdatebhav();
        String bhavrecord = String.valueOf(updatebhavlingth);
        if (bhavrecord != null) {
            Log.e("-------bhav------", bhavrecord);
        }

        btn_ratesave = (Button) rootView.findViewById(R.id.btn_rate_home);

        btn_ratesave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (funSaveEntry() == true) {
                    printMethod();
                    messageAlertDialog();
                }
            }
        });

        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (funSaveEntry() == true) {
                   printMethod();
               }
            }
        });

        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (funSaveEntry()==true){
                    messageAlertDialog();
                }
            }
        });

        getCurrentCollection();
        setTextsAccordingRate();

        return rootView;
    }

    private void messageAlertDialog() {
        MainActivity.sendTextSms(message,phone_number);
    }

    private void printMethod() {

        try {
            try {
                MainActivity.print(printString);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Print failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public boolean funSaveEntry() {
        code = et_code.getText().toString();
        if (code.length() == 0) {
            et_code.setError("Code is required!");
            return  false;
        } else {
            if (code.length() == 1) {
                code = "00" + code;
                SaveAllData();
                return  true;
            } else if (code.length() == 2) {
                code = "0" + code;
                SaveAllData();
                return  true;
            } else {
                SaveAllData();
                return  true;
            }
        }
    }

    private void SaveAllData() {

        weight = et_weight.getText().toString();
        fat = et_fat.getText().toString();
        snf = et_snf.getText().toString();

        String totalrupees = total.getText().toString();
        String rateliter = rate.getText().toString();

        if (rateliter.equals("00.00")) {
            Toast.makeText(getActivity(), "Amount Not Found", Toast.LENGTH_LONG).show();
        } else if (weight.length() == 0)
            et_weight.setError("Weight is required!");
        else if (Float.parseFloat(weight) >= 1000)
            et_weight.setError("Weight limit 1 - 999");
        else if (fat.length() == 0)
            et_fat.setError("Fat is required!");
        else if (snf.length() == 0)
            et_snf.setError("Snf is required!");
        else if (totalrupees.equals("Total Rs/-")) {
            Toast.makeText(getActivity(), "Please create Total Rs/- ", Toast.LENGTH_LONG).show();
        } else {
            try {
                milkDBHelpers = new MilkDBHelpers(getActivity());
                SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();

                Cursor cursor = sqLiteDatabase.rawQuery("Select * From member where membercode='" + code + "'", null);

                if (cursor != null && cursor.moveToFirst()) {

                    while (cursor.isAfterLast() == false) {

                        String date = tv_datepicker.getText().toString();
                        MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
                        Float rateperliter = Float.valueOf(rateliter);
                        Float currentweight = Float.parseFloat(weight);
                        Float currentfat = Float.parseFloat(fat);
                        Float currentsnf = Float.parseFloat(snf);
                        Float fat_wt = currentfat * currentweight;
                        Float snf_wt = currentsnf * currentweight;
                        phone_number = cursor.getString(3);
                        String member_name = (String)tv_code_holder.getText();
                        Float totalamount = Float.parseFloat(totalrupees);
                        DecimalFormat df = new DecimalFormat("#.##");
                        fat = String.valueOf(df.format(Float.parseFloat(fat)));
                        snf = String.valueOf(df.format(Float.parseFloat(snf)));
                        weight = String.valueOf(df.format(Float.parseFloat(weight)));

                        String replaceDate = date.replace("/", "");

                        String dd = replaceDate.substring(0, 2);
                        String mm = replaceDate.substring(2, 4);
                        String yy = replaceDate.substring(4, 8);

                        String printDate = dd + "." + mm;

                        replaceDate = yy + mm + dd;



                        String strShipt = "Eve";
                        if (sift.equals("M")){
                            strShipt = "Mor";
                        }

                        message = titlename + "\n" + "Date: " + date + "(" + strShipt + ")" + "\nCode: " + code + "-" + member_name +
                                "\nQTY=" + weight + ", FAT=" + fat + ", " + MainActivity.instace.rateString().toUpperCase() +"=" + snf + "; RT=" + rateperliter + "/- AMT=" + totalrupees + "/-";
                        String getDate = getDateTimeOne();

                        printString = "";
                        printString = titlename + "\n" + mobile_self + "\n" + MainActivity.lineBreak() +
                                      "Name: " + member_name +
                                      "\nDate: " + date +
                                      "\nShift: " + getTimeOne() + " (" + strShipt + ")" +
                                      "\nLitre: " + MainActivity.twoDecimalString(weight) + " L" +
                                      "\nFat: " + fat + "  "+MainActivity.instace.rateString()+": " + snf +
                                      "\nRate/Ltr: " + rateliter +
                                      "\nAmount:  Rs " + totalamount + "\n\n   _western_ \n";

                        printString = printString + MainActivity.lineBreak() + "\n";

                        milkDBHelpers.AddMilk(code, df.format(Float.parseFloat(weight)), rateperliter,
                                totalamount, replaceDate,
                                phone_number, sift, fat, fat_wt, snf, snf_wt, message, printString, date);

                        total.setText("Total Amount");
                        rate.setText("Rate/ltr");
                        et_weight.setText("");
                        et_fat.setText("");
                        et_snf.setText("");
                        et_code.setText("");
                        resetValue();

                        et_code.requestFocus();
                        Toast.makeText(getActivity(), "Weight :- " + weight + "\n" + "Rate/liter  :- " + rateperliter + "\n" + "total amount :- " + totalrupees, Toast.LENGTH_LONG).show();
                        cursor.moveToNext();

                        getCurrentCollection();

                    }
                } else {
                    et_code.setError("not found!");
                }
            } catch (Exception e) {
            }
        }
    }

    private void getCurrentDate() {

        Calendar calendar = Calendar.getInstance();
        int am_pm = calendar.get(Calendar.AM_PM);
        if (am_pm == 0) {
            sift = "M";
            sp_shift.setSelection(0);
        } else {
            sp_shift.setSelection(1);
            sift = "E";
        }

        MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
        tv_datepicker.setText(milkDBHelpers.getCurrentDateFromPublic());
    }

    private void resetValue() {
        total.setText("Total Amount");
        rate.setText("Rate/ltr");
        et_weight.setText("");
        et_fat.setText("");
        et_snf.setText("");
        et_code.setText("");
    }

    private void createmilkvalue() {

        try {

            code = et_code.getText().toString();
            weight = et_weight.getText().toString();
            fat = et_fat.getText().toString();
            snf = et_snf.getText().toString();

            if (weight.length() == 0)
                et_weight.setError("Weight is required!");
            else if (fat.length() == 0)
                et_fat.setError("Fat is required!");
            else if (snf.length() == 0)
                et_snf.setError("Snf is required!");
            else {

                        try
                        {
                            fat = MainActivity.oneDecimalString(fat);
                            snf = MainActivity.oneDecimalString(snf);
                            rateperltr = Float.parseFloat(milkDBHelpers.getRatePerLiter(fat,snf));

                            if (rateperltr == 0){
                                Toast.makeText(getActivity(), "Problem to rate found", Toast.LENGTH_LONG).show();
                            } else {

                                rate.setText(String.valueOf(rateperltr));
                                float totalRate = Float.parseFloat(rate.getText().toString()) * Float.parseFloat(weight);
                                totalrs = (int) totalRate;
                                total.setText( MainActivity.twoDecimalFloatToString(totalRate));
                            }
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Not Found Any Data", Toast.LENGTH_LONG).show();
                        }
            }

        } catch (Exception e) {
            Log.wtf("DO THIS", " WHEN SAVE() FAILS");
        }
    }

    public void getUserName() {

        Log.e("getUserName", "getUserName");
        code = et_code.getText().toString();

        if (code.length() == 1) {
            code = "00" + code;
        } else if (code.length() == 2) {
            code = "0" + code;
        }

        try {
            milkDBHelpers = new MilkDBHelpers(getActivity());
            SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("Select * From member where membercode='" + code + "'", null);
            if (cursor != null && cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    phone_number = cursor.getString(3);
                    String member_name = cursor.getString(1);
                    tv_code_holder.setText(cursor.getString(1));
                    tv_code_holder.setTextColor(getResources().getColor(R.color.colorAccent));
                    tagCode = 0;
                    cursor.moveToNext();
                }
            } else {
                tv_code_holder.setText("Code");
                tv_code_holder.setTextColor(getResources().getColor(R.color.black));
                tagCode = 1;
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Enter Another Code", Toast.LENGTH_LONG).show();
        }
    }

    private String getDateTimeOne() {
        Calendar ccc = Calendar.getInstance();
        System.out.println("Current time => " + ccc.getTime());

        int year = ccc.get(Calendar.YEAR);
        int month = ccc.get(Calendar.MONTH);
        int day = ccc.get(Calendar.DAY_OF_MONTH);
        int mm = ccc.get(Calendar.MINUTE);
        int hh = ccc.get(Calendar.HOUR);
        String ttmm = day + "/" + month + "/" + year + " " + hh + ":" + mm;
        return ttmm;
    }

    private String getTimeOne() {
        Calendar ccc = Calendar.getInstance();
        System.out.println("Current time => " + ccc.getTime());

        int year = ccc.get(Calendar.YEAR);
        int month = ccc.get(Calendar.MONTH);
        int day = ccc.get(Calendar.DAY_OF_MONTH);
        int mm = ccc.get(Calendar.MINUTE);
        int hh = ccc.get(Calendar.HOUR);
        String ttmm =  hh + ":" + mm;
        return ttmm;
    }

    public void setTextsAccordingRate()  {
        try {
            lbl_snf_home.setText(MainActivity.instace.rateString());
            et_snf.setHint("Enter " + MainActivity.instace.rateString());
            lbl_snf_avg.setText("Avg " + MainActivity.instace.rateString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getCurrentCollection() {
        try {
            MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
            SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();

            String date = milkDBHelpers.getCurrentDateFromPublic();
            date = date.replace("/", "");
            String dd = date.substring(0, 2);
            String mm = date.substring(2, 4);
            String yy = date.substring(4, 8);
            date = yy + mm + dd;

            Cursor cursor = sqLiteDatabase.rawQuery(" SELECT * FROM 'milk_amount' WHERE sift = '" + sift + "' and date = '" + date + "' ", null);
            int value = 1;
            float wgt = 0 ,avgFat =0, avgSnf =0,amt =  0;
            if (cursor != null && cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    wgt +=  cursor.getFloat(cursor.getColumnIndex("milkweight"));
                    avgFat +=  cursor.getFloat(cursor.getColumnIndex("fat_wt"));
                    avgSnf +=  cursor.getFloat(cursor.getColumnIndex("snf_wt"));
                    amt += cursor.getFloat(cursor.getColumnIndex("totalamount"));
                    value++;
                    cursor.moveToNext();
                }
                lbl_avgFat.setText( MainActivity.oneDecimalFloatToString(avgFat/wgt) );
                lbl_avgSnf.setText( MainActivity.oneDecimalFloatToString(avgSnf/wgt) );
                lbl_wgt.setText( MainActivity.twoDecimalFloatToString(wgt) );
                lbl_amt.setText( MainActivity.twoDecimalFloatToString(amt) );
                lbl_SeriolNo.setText("Sr. No. " + value);
            } else {
                lbl_avgFat.setText("0") ;
                lbl_avgSnf.setText("0");
                lbl_wgt.setText("0");
                lbl_amt.setText("0");
                lbl_SeriolNo.setText("Sr. No. 1");
            }
        }
        catch (Exception e) {}
    }
}