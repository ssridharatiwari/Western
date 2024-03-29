package com.milk.milkcollectionapp.fragment;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.milk.milkcollectionapp.activity.MainActivity;
import com.milk.milkcollectionapp.activity.LoginActivity;
import com.milk.milkcollectionapp.activity.SelectBTDeviceActivity;
import com.milk.milkcollectionapp.utils.db.MilkDBHelpers;
import com.milk.milkcollectionapp.R;
import com.milk.milkcollectionapp.utils.AppString;
import com.milk.milkcollectionapp.utils.AppUrl;
import com.milk.milkcollectionapp.utils.DatePickerFragment;
import com.milk.milkcollectionapp.utils.DownloadFile;
import com.milk.milkcollectionapp.utils.SharedPreferencesUtils;
import com.milk.milkcollectionapp.model.SingleEntry;

import java.util.Calendar;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import static com.milk.milkcollectionapp.activity.MainActivity.hideKeyboard;
import static com.milk.milkcollectionapp.activity.MainActivity.instace;
import static java.lang.System.exit;

public class Fragment_home extends Fragment {

    ArrayAdapter<String> adapter;
    Button btn_ratesave, btn_pss,btn_auto_manual,btn_push_fat;
    LinearLayout createrate,todayDetailLL;
    EditText et_weight, et_fat, et_snf, et_code,rate;
    TextView  total, btnrate, btntotal, tv_datepicker, tv_code_holder,lbl_avgFat,
            lbl_avgSnf,lbl_wgt,lbl_amt,lbl_SeriolNo,lbl_snf_home,lbl_snf_avg,toolbartitle;
    String weight, fat, snf, code , myCode;
    String phone_number, message, sift, printString, titlename, mobile_self,comission;

    Spinner sp_shift;
    int  totalrs, tagCode;
    ProgressDialog dialog;
    MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
    SharedPreferencesUtils sharedPreferencesUtils;

    Float rateMain = Float.valueOf((float) 0.0);
    Switch switchManual;
    Boolean isSMSSemd = false,isPrint,isAuto=false;



    public Fragment_home() {

    }



    @SuppressLint("ValidFragment")
    public Fragment_home(ArrayAdapter<String> adapter) {
        this.adapter = adapter;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);




        rootView.playSoundEffect(android.view.SoundEffectConstants.CLICK);
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
        rate = (EditText) rootView.findViewById(R.id.rate);
        tv_code_holder = (TextView) rootView.findViewById(R.id.tv_code_holder);
        total = (TextView) rootView.findViewById(R.id.total);
        btnrate = (TextView) rootView.findViewById(R.id.click_rate);
        btntotal = (TextView) rootView.findViewById(R.id.click_total);
        btntotal = (TextView) rootView.findViewById(R.id.click_total);
        tv_datepicker = (TextView) rootView.findViewById(R.id.tv_date);
        btn_pss = (Button) rootView.findViewById(R.id.btn_pss);
        btn_auto_manual = (Button) rootView.findViewById(R.id.btn_auto_manual);
        btn_push_fat = (Button) rootView.findViewById(R.id.btn_push_fat);

        sp_shift = (Spinner) rootView.findViewById(R.id.sp_shift);
        switchManual = (Switch)rootView.findViewById(R.id.switch_manual);

        sharedPreferencesUtils = new SharedPreferencesUtils(getActivity());
        milkDBHelpers=  new MilkDBHelpers(getActivity());

        titlename = sharedPreferencesUtils.getTitle();
        mobile_self = sharedPreferencesUtils.getMobile();
        toolbartitle.setText(titlename);

        verifyDetailApi();

        et_snf.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == event.KEYCODE_ENTER) {
                createmilkvalue();
                hideKeyboard(getActivity());
            }
            return false;
        });

        et_fat.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == event.KEYCODE_ENTER) {
                if (sharedPreferencesUtils.getDefaultSNF()>0) {
                    createmilkvalue();
                    hideKeyboard(getActivity());
                }
            }
            return false;
        });

        et_weight.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                if (et_code.getText().length() > 0) {
                    rentryMethod();
                }else{
                    et_code.setError(null);
                }
            } else {

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

            }
        });

         createrate.setOnClickListener(v -> {
             if (comission.equals("-")) {
                 createmilkvalue();
             }
             if (comission.equals("+")) {
                 createmilkvalue();
             }
         });

        btn_pss.setOnClickListener(v -> {
            toolbartitle.setText("Pay");
            Fragment fragment = new Fragment_DailyReport();
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(fragment + "");
            ft.commit();
        });



        todayDetailLL.setOnClickListener(v -> {
            toolbartitle.setText("Daily Report");
            Fragment fragment = new Fragment_DailyReport();
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(fragment + "");
            ft.commit();
        });

        tv_datepicker.setOnClickListener(v -> {
            DialogFragment picker = new DatePickerFragment(tv_datepicker);
            picker.show(getFragmentManager(), "datePicker");
        });

        getCurrentDate();

        int memberlength = milkDBHelpers.getMember();
        String memberrecord = String.valueOf(memberlength);
        if (memberrecord != null) {
            //Log.e("-----member--------", memberrecord);
        }
        int updatebhavlingth = milkDBHelpers.getupdatebhav();
        String bhavrecord = String.valueOf(updatebhavlingth);
        if (bhavrecord != null) {
            //Log.e("-------bhav------", bhavrecord);
        }

        btn_ratesave = (Button) rootView.findViewById(R.id.btn_rate_home);
        btn_ratesave.setOnClickListener(v -> {
            if (sharedPreferencesUtils.getSavePrint().equals("1")){
                isPrint = true;
            }else{
                isPrint = false;
            }
            if (sharedPreferencesUtils.getSaveSms().equals("1")){
                isSMSSemd = true;
            }else{
                isSMSSemd = false;
            }

            if (funSaveEntry() == true) {

            }
        });

        btn_auto_manual.setOnClickListener(v -> {
            String wtt =  instace.twoDecimal(String.valueOf(et_weight.getText()));

            if (wtt.length() == 4){
                wtt = "000" + wtt;
            }else if (wtt.length() == 5){
                wtt = "00" + wtt;
            }else if (wtt.length() == 6){
                wtt = "0" + wtt;
            }

            instace.print("$" + wtt);
        });

        btn_push_fat.setOnClickListener(v -> {
            String ft = String.valueOf(et_fat.getText());
            String snf1 = String.valueOf(et_snf.getText());
            ft = instace.twoDecimal(ft);
            snf1 = instace.twoDecimal(snf1);
            if (ft.length() < 5) {
                ft = "0" + ft;
            }
            if (snf1.length() < 5) {
                snf1 = "0" + snf1;
            }
            instace.print("#" + ft + " " + snf1);
        });

        Log.e("push weight ", String.valueOf(sharedPreferencesUtils.isPushWeight()));
        if (sharedPreferencesUtils.isPushWeight()){
            btn_push_fat.setVisibility(View.VISIBLE);
            btn_auto_manual.setVisibility(View.VISIBLE);
        }else{
            btn_push_fat.setVisibility(View.INVISIBLE);
            btn_auto_manual.setVisibility(View.INVISIBLE);
        }

        getCurrentCollection();
        setTextsAccordingRate();

        checkDefaultSnf();
        downloadFile();

        changeTextMethds();
        return rootView;
    }



    private void changeTextMethds(){
        et_code.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (et_code.getText().length() > 0) {
                    getUserName();
                }else{

                    tv_code_holder.setText("Code");
                    et_code.setError(null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });


        et_fat.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                if (comission.equals("-")) {
                    createmilkvalue();
                }
                if (comission.equals("+")) {
                    createmilkvalue();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });



        et_snf.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (comission.equals("-")) {
                    createmilkvalue();
                }
                if (comission.equals("+")) {
                    createmilkvalue();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });



    }







    private void rentryMethod(){

        if (milkDBHelpers.isAlredy(reverceDate(),sift,code)){
            
            new AlertDialog.Builder(instace).setTitle("Message")
                    .setMessage(AppString.reentryTitle)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.instace.focusOnTextField(et_code);
                }
            }).show();

        }else{
            et_code.setError(null);
        }
    }

    private void messageAlertDialog() {

        Log.e("meessage",message);
        isSMSSemd = false;
        MainActivity.sendTextSms(message,phone_number);
    }

    private void printMethod() {

        isPrint = false;
        try {
            MainActivity.getInstace().print(printString);
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

            code = String.valueOf(Integer.valueOf(code));
            //MainActivity.makeToast(code);

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

    /////  save data method

    private void SaveAllData() {


        String replaceDate = reverceDate();

        if (MainActivity.getInstace().isDemoNotAccess(replaceDate)){
            return;
        }

        saveData();
    }



    private void saveData(){


        String date = tv_datepicker.getText().toString();
        String replaceDate = reverceDate();


        if (MainActivity.getInstace().isDemoNotAccess(replaceDate)){
            return;
        }

        weight = et_weight.getText().toString();
        fat = et_fat.getText().toString();
        snf = et_snf.getText().toString();



        if(rateMain == 0) {
            if (rate.getText().toString().length() > 0){
                if (Float.parseFloat(rate.getText().toString()) > 0.0) {
                    rateMain =  Float.parseFloat(rate.getText().toString());
                }
            }
        }

        if (rateMain == 0) {
            Toast.makeText(getActivity(), "Rate Amount Not Found", Toast.LENGTH_LONG).show();
        } else if (weight.length() == 0)
            et_weight.setError("Weight is required!");
        else if (Float.parseFloat(weight) >= 10000)
            et_weight.setError("Weight limit 1 - 9999");
        else if (fat.length() == 0)
            et_fat.setError("Fat is required!");
        else if (snf.length() == 0)
            et_snf.setError("Snf is required!");
        else if (total.getText().toString().equals("Total Rs/-")) {
            Toast.makeText(getActivity(), "Please create Total Rs/- ", Toast.LENGTH_LONG).show();
        } else {
            try {


                SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery("Select * From member where membercode='" + code + "'", null);

                if (cursor != null && cursor.moveToFirst()) {

                    while (cursor.isAfterLast() == false) {

                        Float currentweight = Float.parseFloat(weight);
                        Float currentfat = Float.parseFloat(fat);
                        Float currentsnf = Float.parseFloat(snf);

                        Float fat_wt = currentfat * currentweight;
                        Float snf_wt = currentsnf * currentweight;

                        phone_number = cursor.getString(3);

                        weight = MainActivity.twoDecimal(weight);
                        fat = MainActivity.twoDecimal(fat);
                        snf = MainActivity.twoDecimal(snf);
                        Float totalamount = currentweight *  rateMain;
                        String cmf = sharedPreferencesUtils.getCMF();
                        float cmffloat = Float.parseFloat(cmf) * Float.parseFloat(weight);
                        cmf =  String.valueOf(cmffloat);

                        SingleEntry entry = new SingleEntry();
                        entry.setCMF(cmf);
                        entry.setCode(code);
                        entry.setRate(String.valueOf(rateMain));
                        entry.setAmount(String.valueOf(totalamount));
                        entry.setSift(sift);
                        entry.setDatesave(date);
                        entry.setDate(replaceDate);
                        entry.setWeight(weight);
                        entry.setFat(fat);
                        entry.setSnf(snf);
                        entry.setFatWt(String.valueOf(fat_wt));
                        entry.setSnfWt(String.valueOf(snf_wt));
                        entry.title = titlename;
                        entry.memberName = (String)tv_code_holder.getText();
                        printString = entry.getPrintMassge();
                        message = entry.getSMS();
                        printString = entry.getPrintMassge();

                        String strShipt = "Eve";
                        if (sift.equals("M")){
                            strShipt = "Mor";
                        }


//                        printString = "";
//                        printString = titlename + "\n" + mobile_self + "\n" + MainActivity.lineBreak() +
//                                "Name: " + member_name + "(" + code + ")" +
//                                "\nDate: " + date +
//                                "\nShift: " + getTimeOne() + " (" + strShipt + ")" +
//                                "\nLitre: " + MainActivity.twoDecimalString(weight) + " L" +
//                                "\nFat: " + fat + "  "+ MainActivity.instace.rateString() +": " + snf +
//                                "\nRate/Ltr: " + rateMain +
//                                "\nAmount:  Rs " + MainActivity.oneDecimalFloatToString(totalamount) + "\n";


                        myCode = code;
                        milkDBHelpers.AddMilk(code,weight, rateMain,
                                totalamount, replaceDate,
                                phone_number, sift, fat, fat_wt, snf, snf_wt, cmf, "", date);

                        resetValue();
                        et_code.requestFocus();

                        Toast.makeText(getActivity(), "Weight :- " + weight + "\n" + "Rate/liter  :- " + rateMain + "\n" + "total amount :- " + totalamount, Toast.LENGTH_LONG).show();
                        cursor.moveToNext();

                        getCurrentCollection();
                        commulativeMethod();

                    }
                } else {
                    et_code.setError("not found!");
                }
            } catch (Exception e) {
            }
        }
    }





    private String reverceDate () {

        String date = tv_datepicker.getText().toString();
        String replaceDate = date.replace("/", "");

        String dd = replaceDate.substring(0, 2);
        String mm = replaceDate.substring(2, 4);
        String yy = replaceDate.substring(4, 8);

        return yy + mm + dd;

    }







    private void commulativeMethod(){
        if (sharedPreferencesUtils.getLastDate().length() > 1) {
            float totalAmount = 0;
            float totalWeight = 0;
            SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM 'milk_amount' WHERE memberCode = '" + myCode + "' and date >= '" + sharedPreferencesUtils.getfromDate() + "' and date <= '" + sharedPreferencesUtils.getLastDate() + "'", null);
            if (cursor != null && cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    totalAmount += cursor.getFloat(cursor.getColumnIndex("totalamount"));
                    totalWeight += cursor.getFloat(cursor.getColumnIndex("milkweight"));
                    cursor.moveToNext();
                }
            }

            String preDate = sharedPreferencesUtils.getfromDate();
            String endDate = sharedPreferencesUtils.getLastDate();

            if (preDate.length() > 1){

                String yy = preDate.substring(0, 4);
                String mm = preDate.substring(4, 6);
                String dd = preDate.substring(6, 8);

                preDate = dd + "/" + mm + "/" + yy;


            }
            if (endDate.length() > 1){

                String yy = endDate.substring(0, 4);
                String mm = endDate.substring(4, 6);
                String dd = endDate.substring(6, 8);

                endDate = dd + "/" + mm + "/" + yy;

            }


            printString = printString + "\n" + "Commilative Total :" +
                    "\nFrom : " + preDate +
                    "To : " + endDate + "\n" +
                    "Weight : " + totalWeight + "\nAmount : " + totalAmount;

            printString = printString + "\n" + MainActivity.lineBreak();


            message = message + "\n" + "Com. Ttl(" + preDate +
                    " - " + endDate + ") :\n" +
                    "QTY=" + totalWeight + "\nAMT= " + totalAmount;


            Log.e("printe  ",printString);
        }



        //        Log.e("printing string",printString);

        if (isSMSSemd){
            isSMSSemd = false;
            messageAlertDialog();
        }

        if (isPrint) {
            isPrint = false;
            printMethod();
        }

        MainActivity.instace.focusOnTextField(et_code);
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
        tv_datepicker.setText(AppString.getCurrentDate());
    }

    private void resetValue() {

        rateMain = Float.valueOf(0);
        total.setText("Total Amount");
        rate.setText("");
        et_weight.setText("");
        et_fat.setText("");
        et_code.setText("");

        if (sharedPreferencesUtils.getDefaultSNF() > 0){
            et_snf.setText(String.valueOf(sharedPreferencesUtils.getDefaultSNF()));

        }else{
            et_snf.setText("");
        }
    }





    private void createmilkvalue() {

        try {

            code = et_code.getText().toString();
            weight = et_weight.getText().toString();
            fat = et_fat.getText().toString();
            snf = et_snf.getText().toString();

            if (sharedPreferencesUtils.getDefaultSNF() > 0){
                snf = String.valueOf(sharedPreferencesUtils.getDefaultSNF());
            }else{
                snf = et_snf.getText().toString();
            }


            if (weight.length() == 0)
                et_weight.setError("Weight is required!");
            else if (fat.length() == 0)
                et_fat.setError("Fat is required!");
            else if (snf.length() == 0)
                et_snf.setError("Snf is required!");
            else {
                        try {
                            if (sharedPreferencesUtils.getRateMethodCode().equals("3")){
                                snf = MainActivity.twoDecimalString(String.valueOf(snf));
                            }


                            rateMain = Float.parseFloat(milkDBHelpers.getRatePerLiter(fat,snf));

                            if (rateMain == 0){
                                rate.setText("0.0");
                                total.setText("0.0");
                                Toast.makeText(getActivity(), "rate not found", Toast.LENGTH_LONG).show();
                            } else {

                                rate.setText(MainActivity.twoDecimalFloatToString(rateMain));
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

        code = et_code.getText().toString();

        code = String.valueOf(Integer.valueOf(code));
        if (code.length() == 1) {
            code = "00" + code;
        } else if (code.length() == 2) {
            code = "0" + code;
        }


        try {


            SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("Select * From member where membercode='" + code + "'", null);
            if (cursor != null && cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    phone_number = cursor.getString(3);
                    String member_name = cursor.getString(1);
                    tv_code_holder.setText(cursor.getString(1));
                    tv_code_holder.setTextColor(getResources().getColor(R.color.colorPink));

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

    public void setTextsAccordingRate()  {
        lbl_snf_home.setText(MainActivity.instace.rateString());
        et_snf.setHint("Enter " + MainActivity.instace.rateString());
        lbl_snf_avg.setText("Avg " + MainActivity.instace.rateString());
    }

    private void  checkDefaultSnf(){
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (sharedPreferencesUtils.getDefaultSNF() > 0){
                et_snf.setText(String.valueOf(sharedPreferencesUtils.getDefaultSNF()));
                et_snf.setEnabled(false);
            }else{
                et_snf.setText("");
                et_snf.setEnabled(true);
            }
        },100);


        if (sharedPreferencesUtils.isManualRate()) {
            rate.setEnabled(true);
            switchManual.setVisibility(View.VISIBLE);
        }else {
            rate.setEnabled(false);
            switchManual.setVisibility(View.INVISIBLE);
        }




        switchManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String statusSwitch1, statusSwitch2;
                if (switchManual.isChecked()){
                    rate.setEnabled(true);
                }else{
                    rate.setEnabled(false);
                    hideKeyboard(getActivity());
                }
            }
        });


        rate.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                if (rate.getText().length() > 0) {

                    if (rate.getText().toString().equals("Rate/ltr")) {
                        return;
                    }

                    Float rateperliter = Float.valueOf(rate.getText().toString());
                    if (et_weight.getText().toString().length() > 0){
                        try {
                            total.setText(MainActivity.twoDecimalFloatToString(rateperliter * Float.valueOf(et_weight.getText().toString())));
                        } catch (IOException e) {
                        }
                    }

                }else{

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


    }





    public void getCurrentCollection() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                try {

                    SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();

                    String date = AppString.getCurrentDate();

                    date = AppString.reverceDate(date);

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
                        lbl_avgFat.setText( MainActivity.twoDecimalFloatToString(avgFat/wgt) );
                        lbl_avgSnf.setText( MainActivity.twoDecimalFloatToString(avgSnf/wgt) );
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

        }, 100);
    }



    private void verifyDetailApi(){
        if (!MainActivity.getInstace().isNetworkConnected()){
            return;
        }
        if (MainActivity.getInstace().userID().toString().length() == 0 && MainActivity.getInstace().userID().toString().equals("0")) {
            exit(0);
        }
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = AppUrl.mainUrl + "uid=" + MainActivity.instace.userID() + "&action=6&android_id=" + LoginActivity.getInstace().AndroidID;
        Log.e("final ulr", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Log.e("resonce", response);
                        Log.e("resonce", String.valueOf(response.length()));
                        if (response.length() > 0) {
                            JSONObject jsonObject = new JSONObject(response);
                            String authStatus = jsonObject.getString("status");
                            if (authStatus == "401"){
                                exit(0);
                            }

                            jsonObject = jsonObject.getJSONObject("details");
                            String status = jsonObject.getString("status").toString();
                            String hc_05 = "0";
                            try {
                                 hc_05 = jsonObject.getString("hc_05_enable").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                MainActivity.dismiss();
                            }
                            Log.e("should exit mobile app",status);
                            sharedPreferencesUtils.setHC05(hc_05);
                            if (!status.equals("")){
                                MainActivity.instace.setStatus(status);
                            }
                            if (status.equals("1") ){
                                sharedPreferencesUtils.setIsDemoFalse();
                                String settitle = jsonObject.getString("name").toString();
                                String mobile_string = jsonObject.getString("mobile").toString();
                                sharedPreferencesUtils.setTitle(settitle);
                                sharedPreferencesUtils.setMobile(mobile_string);
                            }
                            else if (status.equals("2")){
                                String demo_date = jsonObject.getString("demo_date").toString();
                                sharedPreferencesUtils.setDemoDate(demo_date);

                                sharedPreferencesUtils.setIsDemoTrue();
                                sharedPreferencesUtils.setTitle("DEMO");
                                sharedPreferencesUtils.setMobile("DEMO");
                            }
                            else {
                                Log.e("should exit mobile app", String.valueOf(jsonObject));
                                exit(1);
                                exit(0);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        MainActivity.dismiss();
                    }
                }, error -> {

                });
        queue.add(stringRequest);
    }

    public void download() {
        if (!MainActivity.instace.isNetworkConnected()) {
            return;
        }

        if (sharedPreferencesUtils.isDownloaded().equals("0")) {
            sharedPreferencesUtils.setIsDownloaded();
            MainActivity.instace.showLoading("Data Downloading...");
            String url =   "http://wokosoftware.com/western/uploads/" + sharedPreferencesUtils.getUserID() + "/" ;
            Log.e("url",url);
            new DownloadFile().execute(url);
        }
    }

    int PERMISSION_REQUEST_CODE = 10;
    @RequiresApi(api = Build.VERSION_CODES.M)
    void downloadFile(){

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED  || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);                }
        else {
            download();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
                download();
        }else{
            downloadFile();
        }
    }
    public static double roundToHalf(double d) {
        return Math.round(d * 2) / 2.0;
    }
}


