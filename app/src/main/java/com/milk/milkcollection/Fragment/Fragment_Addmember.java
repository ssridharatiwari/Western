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
public class Fragment_Addmember extends Fragment {
    private EditText addname,addcode,addmoible;
    private Button btn_add_member,btn_member_update;
    String membername,membercode,membermobile;
    MilkDBHelpers milkDBHelpers;
    String date;
    public Fragment_Addmember() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_member_fragment, container, false);
        addname =(EditText)rootView.findViewById(R.id.member_name);
        addmoible=(EditText)rootView.findViewById(R.id.member_mobile);
        addcode=(EditText)rootView.findViewById(R.id.member_code);
        btn_member_update =(Button)rootView.findViewById(R.id.btn_member_update);



        btn_member_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                membername = addname.getText().toString();
                membercode = addcode.getText().toString();
                membermobile = addmoible.getText().toString();

                if (addname.getText().toString().length() == 0)
                    addname.setError("Registered Name is required!");
                else if (addcode.getText().toString().length() == 0)
                    addcode.setError("Code is required!");
                else if (addmoible.getText().toString().length() == 0)
                    addmoible.setError("Mobile is required!");
                else if (addmoible.getText().toString().length() < 10)
                    addmoible.setError("Mobile no is Incorrect!");
                else if (Long.valueOf(membermobile) < 5999999999L)
                    addmoible.setError("Mobile no is Incorrect!");
                else {
                    if (addcode.getText().toString().length() < 1) {
                        addcode.setError("Code is Incorrect!");
                    } else {


                        updateMember();
                    }
                }
            }
        });
        btn_add_member =(Button)rootView.findViewById(R.id.btn_member_add);
        btn_add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                membername = addname.getText().toString();
                membercode = addcode.getText().toString();
                if (addcode.getText().toString().length() > 0)
                {  if( membercode.length() == 1 )
                {
                    membercode = "00"+membercode;

                }
                else if( membercode.length() == 2 )
                {
                    membercode = "0"+membercode;

                }}


                if (addmoible.getText().toString().length() == 0)
                {
                    membermobile = "1234";
                }
                else
                {
                    membermobile = addmoible.getText().toString();
                }

                if (addname.getText().toString().length() == 0)
                    addname.setError("Registered Name is required!");
                else if (addcode.getText().toString().length() == 0)
                    addcode.setError("Code is required!");
             //   else if (addmoible.getText().toString().length() == 0)
               //     addmoible.setError("Mobile is required!");
                else if (addmoible.getText().toString().length() < 10 && addmoible.getText().toString().length() > 0)
                    addmoible.setError("Mobile no is Incorrect!");
                else if (Long.valueOf(membermobile) < 5999999999L && addmoible.getText().toString().length() > 0 )
                    addmoible.setError("Mobile no is Incorrect!");
                else {
                    addMember();

//                    if (addcode.getText().toString().length() < 1) {
//                        addcode.setError("Code is Incorrect!");
//                    } else {
//
//                    }
                }
            }
        });
        return rootView;
    }

    private void updateMember(){
        getCalendarDate();

        try {
            milkDBHelpers=  new MilkDBHelpers(getActivity());
            SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("Select * From member where membercode='" + membercode + "'", null);
            if (cursor != null && cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    int memberId = cursor.getInt(cursor.getColumnIndex("Id"));
                    Log.e("memberId", String.valueOf(memberId));

                    String AllEntity = membername + "    " + membercode + "     " + membermobile;

                    Log.e("memberId", AllEntity);


                    milkDBHelpers=  new MilkDBHelpers(getActivity());
                    milkDBHelpers.updateMember(memberId,membername, membercode, membermobile, AllEntity);
                    Toast.makeText(getActivity(), membername + "   "  + membercode + "\n" + "     " + membermobile, Toast.LENGTH_LONG).show();
                    addmoible.setText("");
                    addcode.setText("");
                    addname.setText("");
                    cursor.moveToNext();
                }
            } else{

                addcode.setError("Code Not Registered !");
                Toast.makeText(getActivity(), "This Code Not Register"+"\n"+" Please  Enter Another Code  ", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "please Check Code", Toast.LENGTH_LONG).show();
        }

    }



    private void addMember(){
        getCalendarDate();
        try {
            milkDBHelpers=  new MilkDBHelpers(getActivity());
            SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("Select * From member where membercode='" + membercode + "'", null);
            if (cursor != null && cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    addcode.setError("Code Already Registered !");
                    Toast.makeText(getActivity(), "This Code Already Register"+"\n"+" Please  Enter Another Code  ", Toast.LENGTH_LONG).show();
                    cursor.moveToNext();
                }
            }
            else
            {
                String AllEntity =  membercode + "       "+"\t \t" +membername+"       \t \t"+  membermobile;
                milkDBHelpers=  new MilkDBHelpers(getActivity());
                milkDBHelpers.newmember(membername, membercode, membermobile, AllEntity);
                Toast.makeText(getActivity(), "Name :- " + membername + "\n" + "Code :- " + membercode + "\n" + "Mobile :- " + membermobile, Toast.LENGTH_LONG).show();
                addmoible.setText("");
                addcode.setText("");
                addname.setText("");
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
        Log.e("-=-=date--=", date);
    }

    // validating name
    private boolean isValidName(String name) {
        if (name != null && name.length() >= 1) {
            return true;
        }
        return false;
    }


    private void update(){
      /*  public void updateroute(int id,String routelatitude,String routelongitude,String routedetails,String time,String weekday,String routeinformation,String routename,String startpoint,String endpoint)
        {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("set_Latitude",routelatitude);
            values.put("set_longitude",routelongitude);
            values.put("set_routedetail",routedetails);
            values.put("set_time",time);
            values.put("set_weekday",weekday);
            values.put("routeinformation",routeinformation);
            values.put("set_routename", routename);
            values.put("start_point",startpoint);
            values.put("end_point", endpoint);
            sqLiteDatabase.update("set_route", values,"Sr"+"="+id, null);
        }*/
    }
}
