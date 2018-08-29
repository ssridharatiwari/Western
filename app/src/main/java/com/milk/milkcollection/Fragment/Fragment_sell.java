package com.milk.milkcollection.Fragment;


import android.app.DialogFragment;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.milk.milkcollection.Activity.MainActivity;
import com.milk.milkcollection.Database.MilkDBHelpers;
import com.milk.milkcollection.R;
import com.milk.milkcollection.helper.DatePickerFragment;
import com.milk.milkcollection.helper.SharedPreferencesUtils;
import com.milk.milkcollection.model.DailyReport;
import com.milk.milkcollection.model.SingleEntry;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import static com.milk.milkcollection.Activity.MainActivity.hideKeyboard;

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class Fragment_sell extends Fragment {

    ArrayAdapter<String> adapter;
    Button btn_ratesave, btn_message, btn_print, btn_pss;
    LinearLayout createrate,todayDetailLL;
    EditText et_weight, et_fat, et_snf,et_rate;
    TextView  total, btntotal, tv_datepicker, tv_code_holder,lbl_snf_home,lbl_snf_avg,toolbartitle;
    String weight, fat, snf, code , myCode,rateliter;
    String phone_number, message, sift, printString, titlename, mobile_self,comission;
    float rateperltr;
    Spinner sp_shift;
    int  totalrs, tagCode;
    MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
    SharedPreferencesUtils sharedPreferencesUtils;

    public String idEntry = "";
    Boolean isSMSSemd = false,isPrint;



    public Fragment_sell() {
    }

    byte FONT_TYPE;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sell_entry, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        comission = "+";

        toolbartitle = (TextView) getActivity().findViewById(R.id.titletool);
        toolbartitle.setText("Update");


        et_weight = (EditText) rootView.findViewById(R.id.s_weight);
        et_fat = (EditText) rootView.findViewById(R.id.s_fat);
        et_snf = (EditText) rootView.findViewById(R.id.s_snf);
        et_rate = (EditText) rootView.findViewById(R.id.s_rate);

        total = (TextView) rootView.findViewById(R.id.s_total);
        btntotal = (TextView) rootView.findViewById(R.id.s_click_total);
        tv_datepicker = (TextView) rootView.findViewById(R.id.s_date);
        sp_shift = (Spinner) rootView.findViewById(R.id.s_sp_shift);
        sharedPreferencesUtils = new SharedPreferencesUtils(getActivity());
        milkDBHelpers=  new MilkDBHelpers(getActivity());

        titlename = sharedPreferencesUtils.getTitle();
        mobile_self = sharedPreferencesUtils.getMobile();

        btn_ratesave = (Button) rootView.findViewById(R.id.s_btn_update);


        setClickEvents();

        return rootView;
    }


    void setClickEvents(){

        tv_datepicker.setText(milkDBHelpers.getCurrentDateFromPublic());

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


        tv_datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment picker = new DatePickerFragment(tv_datepicker);
                picker.show(getFragmentManager(), "datePicker");
            }
        });


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


        btn_ratesave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (funSaveEntry() == true) {

                }
            }
        });

        btntotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    createmilkvalue();
            }
        });


    }



    private void printMethod() {

        isPrint = false;
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

        SaveAllData();
        return true;
    }

    private void SaveAllData() {


        String replaceDate = reverceDate();

        if (MainActivity.getInstace().isDemoNotAccess(replaceDate)){
            return;
        }
        if (createmilkvalue()){
            saveData();
        }

    }


    private void saveData(){


        String date = tv_datepicker.getText().toString();
        String replaceDate = reverceDate();

        rateliter = "1";

        if (MainActivity.getInstace().isDemoNotAccess(replaceDate)){
            return;
        }

        weight = et_weight.getText().toString();
        fat = et_fat.getText().toString();
        snf = et_snf.getText().toString();

        String totalrupees = total.getText().toString();

        if (weight.length() == 0)
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


                SingleEntry entry = new SingleEntry();

                entry.setAmount(String.valueOf(totalrupees));
                entry.setFat( fat);
                entry.setSnf(snf);
                entry.setSift(sift);
                entry.setDate(date);
                entry.setRate(rateliter);
                entry.setDatesave(date);
                entry.setWeight(weight);


                milkDBHelpers.AddSellData(entry);
                total.setText("Total Amount");

                resetValue();

                Toast.makeText(getActivity(), "Weight :- " + weight + "\n" + "Rate/liter  :- " + rateliter + "\n" + "total amount :- " + totalrupees, Toast.LENGTH_LONG).show();



            } catch (Exception e) {
            }
        }
    }

    private String reverceDate (){

        String date = tv_datepicker.getText().toString();

        if (date.length() > 0){
            String replaceDate = date.replace("/", "");

            String dd = replaceDate.substring(0, 2);
            String mm = replaceDate.substring(2, 4);
            String yy = replaceDate.substring(4, 8);

            return yy + mm + dd;
        }else{
            return "";
        }

    }


    private void resetValue() {

        total.setText("Total Amount");
        et_weight.setText("");
        et_fat.setText("");
        et_snf.setText("");
        et_snf.setText("");
        et_weight.setText("");
        et_fat.setText("");
    }

    private boolean createmilkvalue() {

        try {

            weight = et_weight.getText().toString();
            fat = et_fat.getText().toString();
            snf = et_snf.getText().toString();


            Log.e("weight",weight);

            if (weight.length() == 0){
                et_weight.setError("Weight is required!");
                return false;
            }
            else if (fat.length() == 0){
                et_fat.setError("Fat is required!");
                return false;
            }
            else if (snf.length() == 0){
                et_snf.setError("Snf is required!");
                return false;
            }
            else {

                        try
                        {

                            if (sharedPreferencesUtils.getRateMethodCode().equals("3")){
                                double value = Double.parseDouble(snf);

                                value = roundToHalf(value);
                                Log.e("--------value", String.valueOf(value));

                                try {
                                    snf = MainActivity.oneDecimalString(String.valueOf(value));
                                    et_snf.setText(snf);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }


                            fat = MainActivity.oneDecimalString(fat);
                            snf = MainActivity.oneDecimalString(snf);
                            rateperltr = Float.parseFloat(milkDBHelpers.getRatePerLiter(fat,snf));


                            if (rateperltr == 0){
                                Toast.makeText(getActivity(), "rate not found", Toast.LENGTH_LONG).show();
                            } else {

                                et_rate.setText(String.valueOf(rateperltr));
                                float totalRate = rateperltr * Float.parseFloat(weight);
                                totalrs = (int) totalRate;
                                total.setText( MainActivity.twoDecimalFloatToString(totalRate));
                            }
                            return true;
                        } catch (Exception e) {
                            // Toast.makeText(getActivity(), "Not Found Any Data", Toast.LENGTH_LONG).show();
                            return false;
                        }
            }

        } catch (Exception e) {
            Log.wtf("DO THIS", " WHEN SAVE() FAILS");
            return false;
        }
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





    public static double roundToHalf(double d) {
        return Math.round(d * 2) / 2.0;
    }
}



