package com.milk.milkcollection.helper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import com.milk.milkcollection.R;

import java.util.Calendar;

 /**
 * Created by Er. Arjun on 14-02-2016.
 */
@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


    TextView textViewDate;
    public DatePickerFragment(TextView textView)
    {
        textViewDate=textView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
    public void onDateSet(DatePicker view, int year, int month, int day) {
       // populateSetDate(year, month+1, day);
        month++;
        int monthlength =String.valueOf(month).length();
        int daylength =String.valueOf(day).length();
        Log.e("monthlength", String.valueOf(monthlength));
        Log.e("daylength", String.valueOf(daylength));

        if(monthlength==1)
        {
            if (daylength==1)
            {
                String date = "0"+String.valueOf(day)+"/"+"0"+String.valueOf(month)+"/"+String.valueOf(year);
                textViewDate.setText(date);
            }
            else
            {
                String date = String.valueOf(day)+"/"+"0"+String.valueOf(month)+"/"+""+String.valueOf(year);
                textViewDate.setText(date);
            }
        }
        else
        {
            if (daylength==1)
            {
                String date = "0"+String.valueOf(day)+"/"+""+String.valueOf(month)+"/"+String.valueOf(year);
                textViewDate.setText(date);
            }
            else
            {
                String date = String.valueOf(day)+"/"+""+String.valueOf(month)+"/"+""+String.valueOf(year);
                textViewDate.setText(date);
            }
        }


//        int setmonth = month+1;
//        textViewDate.setText(year+"/"+setmonth+"/"+day);
    }
    public void populateSetDate(int year, int month, int day) {
        String date = day+"/"+month+"/"+year;
        ((TextView) getActivity().findViewById(R.id.tv_date)).setText(date);
    }



    public String getCurrentDateFromPublic() {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        month++;
        int monthlength = String.valueOf(month).length();
        int daylength = String.valueOf(day).length();
//        Log.e("monthlength", String.valueOf(monthlength));
//        Log.e("daylength", String.valueOf(daylength));
        String date = "";
        if(monthlength==1&&daylength==1){
             date = "0"+String.valueOf(day)+"/"+"0"+String.valueOf(month)+"/"+String.valueOf(year);


        }else if(monthlength==1&&daylength>1){
             date = String.valueOf(day)+"/"+"0"+String.valueOf(month)+"/"+""+String.valueOf(year);


        }else if(monthlength>1&&daylength==1){
             date = "0"+String.valueOf(day)+"/"+""+String.valueOf(month)+"/"+String.valueOf(year);

        }else if(monthlength>1&&daylength>1){
             date = String.valueOf(day)+"/"+""+String.valueOf(month)+"/"+""+String.valueOf(year);


        }
        return  date;
    }

}
