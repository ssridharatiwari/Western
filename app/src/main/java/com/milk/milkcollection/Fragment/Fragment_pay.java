package com.milk.milkcollection.Fragment;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.milk.milkcollection.Database.MilkDBHelpers;
import com.milk.milkcollection.R;

import java.util.Calendar;

/**
 * Created by Alpha on 13-12-2015.
 */
public class Fragment_pay extends Fragment {
    private EditText etCode,etAmount;
    private Button btnPay;
    String membername,membercode,membermobile;
    MilkDBHelpers milkDBHelpers;
    String date;
    public Fragment_pay() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pay, container, false);
        etAmount=(EditText)rootView.findViewById(R.id.member_mobile);
        etCode=(EditText)rootView.findViewById(R.id.member_code);
        btnPay =(Button)rootView.findViewById(R.id.btn_pay);

        
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return rootView;
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