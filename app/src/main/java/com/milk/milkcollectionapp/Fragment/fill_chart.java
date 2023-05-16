package com.milk.milkcollectionapp.Fragment;

import static java.lang.Float.parseFloat;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.milk.milkcollectionapp.Activity.MainActivity;
import com.milk.milkcollectionapp.Database.MilkDBHelpers;
import com.milk.milkcollectionapp.R;
import com.milk.milkcollectionapp.helper.SharedPreferencesUtils;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by Alpha on 13-12-2015.
 */
public class fill_chart extends Fragment {
    private static final int GALLERY_KITKAT_INTENT_CALLED = 0;
    private Button btn_start_fill;
    MilkDBHelpers milkDBHelpers;

    private  EditText from_fat,to_fate,from_snf,to_snf,text_rate;

    String fat="",snf="" , to_fat="", to_snf_str="" , str_from_fat,str_from_snf;

    TextView txt_show_rate,lbl_snf,lbl_fat;

    ImageView left_side,rite_side;

    String rateMethod = "1";
    Boolean isAlready = false;

    public fill_chart() {}

    float difrence = (float) 0.1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fill_chart, container, false);
        milkDBHelpers=  new MilkDBHelpers(getActivity());

        from_fat =(EditText)rootView.findViewById(R.id.from_fat_fill);
        to_fate =(EditText)rootView.findViewById(R.id.to_fat_fill);

        from_snf =(EditText)rootView.findViewById(R.id.from_snf_fill);
        to_snf =(EditText)rootView.findViewById(R.id.to_snf_fill);

        text_rate =(EditText)rootView.findViewById(R.id.et_rate_fill);


        txt_show_rate = (TextView) rootView.findViewById(R.id.txt_show_rate);

        lbl_fat = (TextView) rootView.findViewById(R.id.txt_fill_fat);
        lbl_snf = (TextView) rootView.findViewById(R.id.txt_fill_snf);


        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(MainActivity.instace);
        rateMethod =  sharedPreferencesUtils.getRateMethodCode();


        txt_show_rate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
             focasontext();
            }
        });


        btn_start_fill = (Button)rootView.findViewById(R.id.btn_start_fill);
        btn_start_fill.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                start();
            }
        });


        text_rate.setAlpha(0);
        text_rate.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                String strRt =  String.valueOf(text_rate.getText());
                Log.e("data ", String.valueOf(text_rate.getText()));

                if (strRt.length() == 1) {

                    txt_show_rate.setText(strRt + "0.00");

                }else if (strRt.length() == 2) {
                    txt_show_rate.setText( strRt + ".00");


                }else if (strRt.length() == 3) {

                    txt_show_rate.setText( strRt.substring(0,2) + "." +  strRt.substring(2,3) );


                }else if (strRt.length() == 4) {
                    txt_show_rate.setText( strRt.substring(0,2) + "." +  strRt.substring(2,4) );

                    saveData();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            rightSide();

                        }

                    }, 500);

            }


            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        left_side = (ImageView) rootView.findViewById(R.id.left_side);
        rite_side = (ImageView) rootView.findViewById(R.id.right_side);

        left_side.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftSide();
            }
        });

        rite_side.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightSide();
            }
        });


        //   getAllData();



        if (rateMethod.equals("3")){

            from_snf.setHint("from clr");
            to_snf.setHint("to clr");

            MainActivity.instace.toolbartitle.setText("Fill Rate Chart");
            difrence = (float) 0.5;
        }


        return rootView;
    }

    private  void  focasontext(){

        text_rate.requestFocus();
        InputMethodManager imm = (InputMethodManager) MainActivity.instace.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }




    private void rightSide() {

        if (fat.length() == 0 || snf.length() == 0){
            return;
        }

        //txt_show_rate.setText("00.00");
        text_rate.setText("");
        focasontext();


        float fatNum = parseFloat(fat);

        float snfNum = parseFloat(snf);
        snfNum = (snfNum + difrence);

        snfNum = round(snfNum,1);

//        Log.e("snf num ", String.valueOf(snfNum));
//        Log.e("fat num ", String.valueOf(fatNum));
//        Log.e(" to snf num ", String.valueOf(to_snf_str));
//        Log.e("from snf num ", String.valueOf(str_from_snf));
//
//        Log.e("to fat  num ", String.valueOf(to_fat));
//        Log.e("from fat num ", String.valueOf(str_from_fat));
//        Log.e("Difference ", String.valueOf(difrence));


        if (Float.parseFloat(to_snf_str) < snfNum) {
            fatNum = (float) (fatNum + 0.1);
            snfNum = parseFloat(str_from_snf);
        }

        if (Float.parseFloat(to_fat) < fatNum) {
            fatNum =  parseFloat(str_from_fat);
        }

            try {
            snf = MainActivity.oneDecimalFloatToString(snfNum);
            fat = MainActivity.oneDecimalFloatToString(fatNum);

            getData();


        } catch (IOException e) {
            e.printStackTrace();
        }

        setData();
    }


    private void leftSide() {

        if (fat.length() == 0 || snf.length() == 0){
            return;
        }

        //txt_show_rate.setText("00.00");
        text_rate.setText("");
        focasontext();


        float fatNum = parseFloat(fat);

        float snfNum = parseFloat(snf);

        snfNum = (float)(snfNum - difrence);

        if (snfNum <  parseFloat(str_from_snf)){

            if (fatNum > parseFloat(str_from_fat))  {
                fatNum = (float) (fatNum - 0.1);
                snfNum = parseFloat(to_snf_str);;
            }else{
                return;
            }
        }


        try {
                snf = MainActivity.oneDecimalFloatToString(snfNum);
                fat = MainActivity.oneDecimalFloatToString(fatNum);

                getData();


            } catch (IOException e) {
                e.printStackTrace();
            }

        setData();

    }



    private void setData(){

        try {



            lbl_fat.setText("FAT : " + MainActivity.twoDecimalString(fat) );
            lbl_snf.setText("SNF : " +  MainActivity.oneDecimalString(snf));

            if (rateMethod.equals("3")) {
                lbl_snf.setText("CLR : " +  MainActivity.oneDecimalString(snf));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void saveData() {

        String rate = (String)txt_show_rate.getText();

        if (snf.length() > 0 && fat.length() > 0 && rate.length() > 0) {


            Log.e("fat" ,fat);
            Log.e("snf" ,fat);
            Log.e("rate" ,rate);

            if (rateMethod.equals("2")){

                if (isAlready){
                    milkDBHelpers.updateRateByRow(fat,snf, (String)txt_show_rate.getText());
                }else{
                    milkDBHelpers.addRate(fat,snf, (String)txt_show_rate.getText());
                }


            }else {

                if (isAlready){
                    milkDBHelpers.updateRateClrByRow(fat,snf, (String)txt_show_rate.getText());
                }else{
                    milkDBHelpers.addRateClr(fat,snf, (String)txt_show_rate.getText());
                }
            }
        }

        focasontext();

    }

    private void start(){

        try {
            fat =  MainActivity.oneDecimalString(String.valueOf(from_fat.getText())) ;
            snf =  MainActivity.oneDecimalString(String.valueOf(from_snf.getText())) ;

        } catch (IOException e) {
            e.printStackTrace();
        }


        to_fat = String.valueOf(to_fate.getText());
        to_snf_str = String.valueOf(to_snf.getText());

        setData();


        str_from_fat = fat;
        str_from_snf = snf;



        getData();

        focasontext();
    }


    private void getData(){

        try {
            String rate =  milkDBHelpers.getRatePerLiter(fat,snf);
            if (rate.length()>0) {
                txt_show_rate.setText(MainActivity.trimString(rate));
                isAlready = true;
            }

            if (rate.equals("0")){
                txt_show_rate.setText("00.00");
                isAlready = false;
            }
        } catch (IOException e) {
            isAlready = false;
            e.printStackTrace();
        }

    }


    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}