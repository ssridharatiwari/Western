package com.milk.milkcollection.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.milk.milkcollection.Database.MilkDBHelpers;
import com.milk.milkcollection.R;

import java.util.Calendar;

/**
 * Created by Alpha on 13-12-2015.
 */

public class Fragment_CreateBhav extends Fragment {
    ProgressDialog dialog;
    private EditText from_fat,to_fat,from_snf,to_snf,kg_fat_rat,kg_snf_rat,comition_liter;
    private Button btnchangevalue;
    String fromfat,tofat,fromsnf,tosnf,kgfatrat,kgsnfrat,comitionliter;
   // MilkDBHelpers milkDBHelpers;
    String date,commissionType;
    RadioGroup radioGroup;

    public Fragment_CreateBhav() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.create_bhav_fragment, container, false);
        dialog = new ProgressDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        from_fat =(EditText)rootView.findViewById(R.id.from_fat);
        to_fat=(EditText)rootView.findViewById(R.id.to_fat);
        from_snf=(EditText)rootView.findViewById(R.id.from_snf);
        to_snf =(EditText)rootView.findViewById(R.id.to_snf);
        kg_fat_rat=(EditText)rootView.findViewById(R.id.kg_fat_rat);
        kg_snf_rat=(EditText)rootView.findViewById(R.id.kg_snf_rat);
        comition_liter =(EditText)rootView.findViewById(R.id.comition);
        radioGroup =(RadioGroup)rootView.findViewById(R.id.radioGroup);

        btnchangevalue =(Button)rootView.findViewById(R.id.btn_change_value);
        btnchangevalue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveBhav();
            }
        });

        return rootView;
    }

    private void SaveBhav(){
        fromfat=from_fat.getText().toString();
        tofat = to_fat.getText().toString();
        fromsnf = from_snf.getText().toString();
        tosnf=to_snf.getText().toString();
        kgfatrat = kg_fat_rat.getText().toString();
        kgsnfrat = kg_snf_rat.getText().toString();
        comitionliter=comition_liter.getText().toString();
        if( fromfat.length() == 0 )
            from_fat.setError( " From Fat is required!" );
        else if(tofat.length() == 0)
            to_fat.setError( "To Fat is required!" );
        else if(fromsnf.length() == 0)
            from_snf.setError( "From Snf is required!" );
        else if(tosnf.length()==0)
            to_snf.setError( "To Snf is required!" );
        else if(kgfatrat.length() == 0)
            kg_fat_rat.setError( "Kg Fat Rat is required!" );
        else if(kgsnfrat.length() == 0)
            kg_snf_rat.setError( "Kg Snf Rat is required!" );
        else if(comitionliter.length()== 0)
            comition_liter.setError( "Comition/liter is required!" );
        else {
            checkFatSnfRange();
            /*milkDBHelpers=  new MilkDBHelpers(getActivity());
            String allfatsnf = "Fromfat :- "+fromfat+"; "+"Tofat :- "+tofat+"; "+"KgFatRat :- "+kgfatrat+"\n"+"Fromsnf :- "+fromsnf+"; "+"Tosnf :- "+tosnf +"; "+"KgSnfRat :-"+kgsnfrat+"\n" +"Comition :-"+comitionliter+"; "+date;
            milkDBHelpers.changebhav(Float.parseFloat(fromfat),Float.parseFloat(tofat), fromsnf, tosnf, kgfatrat, kgsnfrat, comitionliter, allfatsnf);
            Toast.makeText(getActivity(),  allfatsnf, Toast.LENGTH_LONG).show();
            Log.e("-=-=Fat Snf-=-",allfatsnf);
            from_fat.setText("");
            to_fat.setText("");
            from_snf.setText("");
            to_snf.setText("");
            kg_fat_rat.setText("");
            kg_snf_rat.setText("");
            comition_liter.setText("");*/
        }
    }

    private void checkFatSnfRange(){
        getCalendarDate();
        try {
            MilkDBHelpers   milkDBHelpers=  new MilkDBHelpers(getActivity());
            SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();
//            Log.e("-=-=Ffff-=-",fromfat);
//            Log.e("-=-=Fff-=-",tofat);
//            Log.e("-=-=sss-=-",fromsnf);
//            Log.e("-=-=sss-=-",tosnf);
//            Log.e("-=-=type-=-",radioGroup.getCheckedRadioButtonId()+"");
            Cursor cursor = sqLiteDatabase.rawQuery(" SELECT * FROM 'updatebhav' WHERE fromfat = '" + fromfat + "' and tofat='" + tofat + "'and fromsnf='" + fromsnf + "'and tosnf='" + tosnf + "'" , null);

            if (cursor != null && cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    Log.e("-=-=Found-=-","ffffff");
                    Toast.makeText(getActivity(), "This Range Already Register !....  ", Toast.LENGTH_LONG).show();
                    cursor.moveToNext();
                }
            } else{
                String type = ((RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
                Log.e("TYPE", "checkFatSnfRange: "+type );

                Log.e("-=-=not-=-","nnnooott");
                milkDBHelpers=  new MilkDBHelpers(getActivity());
                String allfatsnf = "Fromfat :- "+fromfat+"; "+"Tofat :- "+tofat+"; "+"KgFatRat :- "+kgfatrat+"\n"+"Fromsnf :- "+fromsnf+"; "+"Tosnf :- "+tosnf +"; "+"KgSnfRat :-"+kgsnfrat+"\n" +"Comition :- "+type+comitionliter+"; "+date;
                milkDBHelpers.changebhav(fromfat,tofat, fromsnf, tosnf, kgfatrat, kgsnfrat, comitionliter, allfatsnf,type);
                Toast.makeText(getActivity(),  allfatsnf, Toast.LENGTH_LONG).show();
                Log.e("-=-=Fat Snf-=-",allfatsnf);
                from_fat.setText("");
                to_fat.setText("");
                from_snf.setText("");
                to_snf.setText("");
                kg_fat_rat.setText("");
                kg_snf_rat.setText("");
                comition_liter.setText("");
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Enter Another Name", Toast.LENGTH_LONG).show();
        }
    }



    private void getCalendarDate(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        date = String.valueOf(day)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year);
        Log.e("-=-=date--=",date);
    }
}