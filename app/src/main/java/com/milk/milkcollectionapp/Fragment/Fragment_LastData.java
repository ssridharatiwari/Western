package com.milk.milkcollectionapp.Fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.milk.milkcollectionapp.Activity.MainActivity;
import com.milk.milkcollectionapp.R;
import com.milk.milkcollectionapp.helper.DatePickerFragment;
import com.milk.milkcollectionapp.helper.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Er. Arjun on 28-02-2016.
 */
public class Fragment_LastData extends Fragment {

    private Calendar calendar;
    private int year, month, day;
    private Button startDateView,endDateView,btnDone;
    private TextView preViousDate;
    ImageView iv_share;
    SharedPreferencesUtils sharedPreferencesUtils;

    private List<String> dailyReportStringList = new ArrayList<>();
    private List<String> numberList = new ArrayList<>();


    public Fragment_LastData() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_last_data, container, false);
        startDateView =(Button)rootView.findViewById(R.id.btn_fromDate);
        endDateView =(Button)rootView.findViewById(R.id.btn_lastDate);
        btnDone =(Button)rootView.findViewById(R.id.btn_DoneDate);
        preViousDate = (TextView) rootView.findViewById(R.id.previousDate);

        sharedPreferencesUtils = new SharedPreferencesUtils(getActivity());

        getCalendarDate();





        startDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment picker = new DatePickerFragment(startDateView);
                picker.show(getFragmentManager(), "datePicker");
            }
        });
        endDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment picker = new DatePickerFragment(endDateView);
                picker.show(getFragmentManager(), "datePicker");
            }
        });


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveDates();
            }
        });


        setValuses();
        return rootView;
    }


     public void setValuses() {

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
             preViousDate.setText("Selected : " + preDate + "  to " + endDate);

         }else {

             preViousDate.setText("Not Selected Yet");

         }

         Log.e("date saved" , preDate);
         Log.e("date saved" , endDate);

     }

     public void saveDates() {

        String preDate = startDateView.getText().toString();
        String endDate = endDateView.getText().toString();;


            String replaceDate = preDate.replace("/", "");

            String dd = replaceDate.substring(0, 2);
            String mm = replaceDate.substring(2, 4);
            String yy = replaceDate.substring(4, 8);

            preDate = yy+mm+dd;
            sharedPreferencesUtils.setFromDateData(preDate);

          replaceDate = endDate.replace("/", "");

          dd = replaceDate.substring(0, 2);
          mm = replaceDate.substring(2, 4);
          yy = replaceDate.substring(4, 8);

         endDate = yy+mm+dd;
         sharedPreferencesUtils.setLastDateData(endDate);


         Log.e("date saved" , preDate);
         Log.e("date saved" , endDate);

         MainActivity.makeToast("Date Saved");
         setValuses();
     }



    private void getCalendarDate(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        // String date = String.valueOf(year)+"/"+String.valueOf(month+1)+"/"+String.valueOf(day);
        //startDateView.setText(date);
        //endDateView.setText(date);
        month++;
        int monthlength =String.valueOf(month).length();
        int daylength =String.valueOf(day).length();
        //Log.e("monthlength", String.valueOf(monthlength));
        //Log.e("daylength", String.valueOf(daylength));

        if(monthlength==1&&daylength==1){
            String date = "0"+String.valueOf(day)+"/"+"0"+String.valueOf(month)+"/"+String.valueOf(year);
            startDateView.setText(date);
            endDateView.setText(date);
        }else if(monthlength==1&&daylength>1){
            String date = String.valueOf(day)+"/"+"0"+String.valueOf(month)+"/"+""+String.valueOf(year);
            startDateView.setText(date);
            endDateView.setText(date);
            endDateView.setText(date);
        }else if(monthlength>1&&daylength==1){
            String date = "0"+String.valueOf(day)+"/"+""+String.valueOf(month)+"/"+String.valueOf(year);
            startDateView.setText(date);
        }else if(monthlength>1&&daylength>1){
            String date = String.valueOf(day)+"/"+""+String.valueOf(month)+"/"+""+String.valueOf(year);
            startDateView.setText(date);
            endDateView.setText(date);
        }
    }


    private void print(String printString){

        MainActivity.getInstace().print(printString);

    }



}
