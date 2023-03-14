package com.milk.milkcollection.Fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.milk.milkcollection.Activity.MainActivity;
import com.milk.milkcollection.Database.MilkDBHelpers;
import com.milk.milkcollection.R;
import com.milk.milkcollection.adapter.ProfitAdapter;
import com.milk.milkcollection.adapter.SocietyReportAdapter;
import com.milk.milkcollection.helper.DatePickerFragment;
import com.milk.milkcollection.helper.FSSession;
import com.milk.milkcollection.helper.SharedPreferencesUtils;
import com.milk.milkcollection.model.LossReport;
import com.milk.milkcollection.model.SingleEntry;
import com.milk.milkcollection.model.SocietyReport;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;


public class ReportLossProfit extends Fragment {

    private ListView listview;


    private Button startDateView,endDateView,btnsearch;
    private TextView txtweight,txtamount;
    ImageView iv_share;

    String message="",printString="";

    ArrayList<LossReport>arrayList = new ArrayList<>();
    ProfitAdapter adapter;
    SharedPreferencesUtils sharedPreferencesUtils;
    String title;

    JSONObject jsonBuyObject;

    public ReportLossProfit() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.report_lossprofit, container, false);
        startDateView =(Button)rootView.findViewById(R.id.btn_startdate);
        iv_share =(ImageView)rootView.findViewById(R.id.iv_share);
        endDateView =(Button)rootView.findViewById(R.id.btn_enddate);
        txtweight =(TextView)rootView.findViewById(R.id.search_weight);
        txtamount =(TextView)rootView.findViewById(R.id.search_amount);

        sharedPreferencesUtils= new SharedPreferencesUtils(getActivity());
        title= sharedPreferencesUtils.getTitle();


        listview = (ListView)rootView.findViewById(R.id.loss_listview);
        //listview.setEmptyView(rootView.findViewById(R.id.ep));
        listview.setOnCreateContextMenuListener(this);

        btnsearch = (Button)rootView.findViewById(R.id.btn_searchimage);

        setDate();
        getter();

        return rootView;
    }



    void getter(){

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



        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                message ="";
                arrayList.clear();
                SearchSqlData();
            }
        });


        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!printString.equals("")){
                    shareDialog();
                } else {
                    Toast.makeText(getActivity(), "Please Search Data First", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public void SearchSqlData(){


        String startdate = startDateView.getText().toString();
        String enddate = endDateView.getText().toString();
        printString = "";
        printString = "\nDT:"+startdate+" to DT:"+enddate+"\n" ;

        String startDate = FSSession.reverceDate(startdate);
        String endDate = FSSession.reverceDate(enddate);


        try{

            MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
            SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();
            Cursor cursor;

            String query =  "SELECT * FROM 'milk_amount' WHERE  date>='" + startDate + "' AND date <='" + endDate + "' ORDER BY date";
            Log.e("query",query);
            cursor = sqLiteDatabase.rawQuery(query, null);



            String alldata = "";

            float weightsize = 0;
            float amountsize = 0;

            JSONObject jsonObject = new JSONObject();

            if (cursor != null && cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {


                    String date =  cursor.getString(cursor.getColumnIndex("date"));
                    String amount =  cursor.getString(cursor.getColumnIndex("totalamount"));
                    String weight =  cursor.getString(cursor.getColumnIndex("milkweight"));
                    String dateReal =  cursor.getString(cursor.getColumnIndex("dateSave"));
                    String shift =  cursor.getString(cursor.getColumnIndex("sift"));


                    String keyOne =  date + shift;

                    JSONObject item = new JSONObject();
                    if (jsonObject.has(keyOne)) {

                        item = jsonObject.getJSONObject(keyOne);
                        if (item.has("amount")){
                            Float amt = Float.valueOf((float) item.getDouble("amount"));
                            amt = amt + Float.valueOf(amount);
                            item.put("amount",amt);
                        }

                        if (item.has("weight")){
                            Float amt = Float.valueOf((float) item.getDouble("weight"));
                            amt = amt + Float.valueOf(weight);
                            item.put("weight",amt);
                        }

                        if (item.has("count")){
                            int count =  item.getInt("count");
                            count++;
                            item.put("count",count);
                        }

                    }else{

                        item.put("weight",weight);
                        item.put("amount",amount);
                        item.put("count","1");
                        item.put("date",dateReal);
                        item.put("shift",shift);
                    }

                    jsonObject.putOpt(keyOne,item);

                    cursor.moveToNext();
                }
            }

            jsonBuyObject = jsonObject;
            getSellData();
            Log.e("json value",jsonObject.toString());


        } catch (Exception e) {
            Toast.makeText(getActivity(), "Not Found Any Data", Toast.LENGTH_LONG).show();
        }

    }



    public void getSellData(){


        String startdate = startDateView.getText().toString();
        String enddate = endDateView.getText().toString();
        printString = "";
        printString = "\nDT:"+startdate+" to DT:"+enddate+"\n" ;

        String startDate = FSSession.reverceDate(startdate);
        String endDate = FSSession.reverceDate(enddate);


        try{

            MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
            SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();
            Cursor cursor;

            String query =  "SELECT * FROM 'sell_data' WHERE  date>='" + startDate + "' AND date <='" + endDate + "' ORDER BY date";
            Log.e("query",query);
            cursor = sqLiteDatabase.rawQuery(query, null);



            String alldata = "";

            float weightsize = 0;
            float amountsize = 0;
            if (cursor != null && cursor.moveToFirst()) {

                JSONObject jsonObject = new JSONObject();

                while (cursor.isAfterLast() == false) {

                    SingleEntry entry = new SingleEntry();


                    entry.setId(cursor.getString(cursor.getColumnIndex("Id")));
                    entry.setDate(cursor.getString(cursor.getColumnIndex("date")));
                    entry.setSift(cursor.getString(cursor.getColumnIndex("sift")));
                    entry.setDatesave(cursor.getString(cursor.getColumnIndex("dateSave")));
                    entry.setWeight(cursor.getString(cursor.getColumnIndex("weight")));
                    entry.setFat(cursor.getString(cursor.getColumnIndex("fat")));
                    entry.setSnf(cursor.getString(cursor.getColumnIndex("snf")));
                    entry.setRate(cursor.getString(cursor.getColumnIndex("rate")));
                    entry.setAmount(cursor.getString(cursor.getColumnIndex("amount")));


                    String key = entry.getDate() + entry.getSift();


                    JSONObject item = new JSONObject();

                    if (jsonObject.has(key)) {

                        item = jsonObject.getJSONObject(key);
                        if (item.has("amount")){
                            Float amt = Float.valueOf((float) item.getDouble("amount"));
                            amt = amt + Float.valueOf(entry.getAmount());
                            item.put("amount",amt);
                        }

                        if (item.has("weight")){
                            Float amt = Float.valueOf((float) item.getDouble("weight"));
                            amt = amt + Float.valueOf(entry.getWeight());
                            item.put("weight",amt);
                        }

                        if (item.has("count")){
                            int count =  item.getInt("count");
                            count++;
                            item.put("count",count);
                        }

                    }else{

                        item.put("weight",entry.getWeight());
                        item.put("amount",entry.getAmount());
                        item.put("date",entry.getDatesave());
                        item.put("shift",entry.getSift());
                    }

                    jsonObject.putOpt(key,item);

                    cursor.moveToNext();

                }

                setData(jsonObject);
            }


        } catch (Exception e) {
            Toast.makeText(getActivity(), "Not Found Any Data", Toast.LENGTH_LONG).show();
        }
    }





    void setData(JSONObject json){

//        double weightTotal = 0.0;
//        double amountTotal = 0.0;

        Iterator<String> iter = jsonBuyObject.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {


                JSONObject value = jsonBuyObject.getJSONObject(key);

                LossReport report = new LossReport();
                report.setBuydataData(value.getString("weight"),
                        value.getString("date"),
                        value.getString("count"),
                        value.getString("amount"),
                        value.getString("shift"));

                if (json.has(key)) {

                    JSONObject newvalue = json.getJSONObject(key);
                    report.setSellData(newvalue.getString("weight"),
                            newvalue.getString("amount"));

                    if (newvalue.getDouble("amount") - value.getDouble("amount") > 0) {
                        report.setIsLoss(false);
                    }else{
                        report.setIsLoss(true);
                    }

                    report.setlossProfitAmount(String.valueOf(newvalue.getDouble("amount") - value.getDouble("amount")));
                }


//                weightTotal += value.getDouble("weight");
//                amountTotal += value.getDouble("amount");

                arrayList.add(report);
            } catch (JSONException e) {
                // Something went wrong!
            }
        }

        setAdapter();

//        txtweight.setText(String.format("%.2f Ltr", weightTotal));
//        txtamount.setText(String.format("Rs %.2f", amountTotal));

    }


    void setAdapter(){

        adapter = new ProfitAdapter(getActivity(), R.layout.adoptor_lossprofit_report,arrayList){
            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setBackground(getResources().getDrawable(R.drawable.text_underline_spinner));
                return v;
            }
        };

        listview.setAdapter(adapter);
    }








    private void setDate(){

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


    private void shareDialog() {
        final CharSequence[] options = { "WhatsApp", "Mail","Other Share","Print Report" };
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Send Report");
        adb.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("WhatsApp")) {
                    PackageManager pm = getActivity().getPackageManager();
                    try {
//pe.diegoveloper.printerserverapp
                        Intent waIntent = new Intent(Intent.ACTION_SEND);
                        waIntent.setType("text/plain");
                        //   String text = "YOUR TEXT HERE";

                        PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                        //Check if package exists or not. If not then code
                        //in catch block will be called
                        waIntent.setPackage("com.whatsapp");

                        waIntent.putExtra(Intent.EXTRA_TEXT, printString);
                        startActivity(Intent.createChooser(waIntent, "Share with"));

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (options[item].equals("Mail")) {
                    String to = "";
                    String subject = "Milk Collection Report";
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                    email.putExtra(Intent.EXTRA_SUBJECT, subject);
                    email.putExtra(Intent.EXTRA_TEXT, printString);
                    // need this to prompts email client only
                    email.setType("message/rfc822");

                    startActivity(Intent.createChooser(email, "Choose an Email client"));


                } else if (options[item].equals("Other Share"))
                {

                    Intent sentIntent = new Intent(Intent.ACTION_SEND);
                    sentIntent.putExtra(Intent.EXTRA_TEXT, printString);
                    sentIntent.setType("text/plain");
                    startActivity(sentIntent);

            }
             else if (options[item].equals("Print Report")) {
                    print(printString);
                }
            }
        });
        adb.show();
    }

    private void print(String printString){
        MainActivity.getInstace().print(printString);
    }

}
