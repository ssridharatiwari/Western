package com.milk.milkcollection.Fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.milk.milkcollection.Activity.MainActivity;
import com.milk.milkcollection.Database.MilkDBHelpers;
import com.milk.milkcollection.R;
import com.milk.milkcollection.adapter.SingleReportAdapter;
import com.milk.milkcollection.helper.DatePickerFragment;
import com.milk.milkcollection.helper.SharedPreferencesUtils;
import com.milk.milkcollection.model.SingleReport;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Er. Arjun on 28-02-2016.
 */
public class Fragment_SingleReport extends Fragment {
    private ListView savedmilk_listview;
    private Calendar calendar;
    private int year, month, day;
    private Button startDateView,endDateView,btnsearch;
    private TextView searchweight,searchamount;
    ImageView iv_share;
    Spinner spnr_membername;
    String membername,message="",printString="";
    int reportId;
    ArrayList<Integer> id_arrayList = new ArrayList<>();
    ArrayList<String>member_arrayList = new ArrayList<String>();
    ArrayList<Float>weight_arrayList = new ArrayList<>();
    ArrayList<Float>rate_arrayList = new ArrayList<>();
    ArrayList<Float>totalamount_arrayList = new ArrayList<>();
    ArrayList<String>date_arrayList = new ArrayList<String>();
    ArrayList<String>sift_arrayList = new ArrayList<String>();
    ArrayList<String>SetAllData_arrayList = new ArrayList<String>();
    ArrayList<String>GetAllData_arrayList = new ArrayList<String>();
    ArrayList<SingleReport>singleReportList = new ArrayList<>();
    SingleReportAdapter singleResultAdapter;
    SharedPreferencesUtils sharedPreferencesUtils;
    String title,memberNameReal;
    private List<String> dailyReportStringList = new ArrayList<>();
    private List<String> numberList = new ArrayList<>();


    public Fragment_SingleReport() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_single_report, container, false);
        startDateView =(Button)rootView.findViewById(R.id.btn_startdate);
        iv_share =(ImageView)rootView.findViewById(R.id.iv_share);
        endDateView =(Button)rootView.findViewById(R.id.btn_enddate);
        searchweight =(TextView)rootView.findViewById(R.id.search_weight);
        searchamount =(TextView)rootView.findViewById(R.id.search_amount);
        getCalendarDate();
        sharedPreferencesUtils= new SharedPreferencesUtils(getActivity());
        title= sharedPreferencesUtils.getTitle();

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
        savedmilk_listview = (ListView)rootView.findViewById(R.id.saved_listview);
        savedmilk_listview.setEmptyView(rootView.findViewById(R.id.empty_saved_list));
        savedmilk_listview.setOnCreateContextMenuListener(this);


        /// spinner

        spnr_membername =(Spinner)rootView.findViewById(R.id.spinr_membername);
        ArrayList<String>spinnerMemberCode_arrayList = new ArrayList<String>();
        MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
        spinnerMemberCode_arrayList = milkDBHelpers.searchMemberCode();
        ArrayAdapter<String> SpinnerAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinnertext, spinnerMemberCode_arrayList)
        {
            public View getView(int position, View convertView, android.view.ViewGroup parent)
            {
                TextView v = (TextView) super.getView(position, convertView, parent);
                //  v.setTypeface(typeface);
                v.setBackgroundColor(Color.TRANSPARENT);
                v.setPadding(15,0,0,0);
                // v.setTextSize(17);
                return v;
            }
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setGravity(Gravity.CENTER);
                // v.setTypeface(typeface);
                //  v.setTextColor(Color.RED);
                //  v.setPadding(50,0,0,0);
                v.setBackground(getResources().getDrawable(R.drawable.text_underline_spinner));
                //v.setTextSize(17);
                return v;
            }
        };
        //  SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnr_membername.setAdapter(SpinnerAdapter);

        spnr_membername.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                membername = parent.getItemAtPosition(position) + "";
if (position == 0)
{
    memberNameReal = "All";

}else
{
    getUserName();
}
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });


        btnsearch = (Button)rootView.findViewById(R.id.btn_searchimage);
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message ="";
                singleReportList.clear();
                SearchSqlData();
              //  Log.e("=-m-m-m--=", " " + GetAllData_arrayList);
                //ArrayList<SingleReportAdapter>si = new SingleReportAdapter(getActivity(), R.layout.listviewmember, singleReportList)
                 singleResultAdapter = new SingleReportAdapter(getActivity(), R.layout.listviewmember,singleReportList ){
                    public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                        TextView v = (TextView) super.getView(position, convertView, parent);
                        // v.setTypeface(typeface);
                        //  v.setTextColor(Color.RED);

                        v.setBackground(getResources().getDrawable(R.drawable.text_underline_spinner));
                        //v.setTextSize(17);

                        return v;
                    }
                };
                savedmilk_listview.setAdapter(singleResultAdapter);
            }
        });

        savedmilk_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

             //   deleteReport(position);
                return true;
            }
        });


        savedmilk_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Daily Report .... ");
                builder.setItems(new CharSequence[]
                                {"Whats App","SMS", "Other Share", "Print", "Delete"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {
                                    case 0:
                                        String data = GetAllData_arrayList.get(position);
                                        MainActivity.sendWhatsApp(data);

                                        break;
                                    case 1:

                                        if(numberList.get(position)!="1234"){
                                                String dataSms = GetAllData_arrayList.get(position);
                                                MainActivity.sendTextSms(dataSms,numberList.get(position));
                                        }else {
                                            Toast.makeText(getActivity(), "Update Number And Try Again", Toast.LENGTH_LONG).show();
                                        }

                                        break;

                                    case 2:
                                            String dataShare = GetAllData_arrayList.get(position);
                                            MainActivity.shareText(dataShare);
                                        break;
                                    case 3:

                                        String dailyReport = dailyReportStringList.get(position);
                                        print(dailyReport);
                                        break;

                                    case 4:

                                        deleteReport(position);
                                        break;
                                }
                            }
                        });
                builder.create().show();

            }
        });


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

        return rootView;
    }


    private void deleteReport( final int position){

        MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
        ArrayList<Integer> reportIdList = new ArrayList<>();
        reportIdList = milkDBHelpers.reportId();
        reportId = id_arrayList.get(position);
        ////Log.e("-=-=-item=-=", String.valueOf(fatSnfId));
        // fatSnfPosition = position;

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setMessage("Are You sure want to delete this Report ");
        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
                // Log.e("-=-=-ddd=-=", "ddddd");
                milkDBHelpers.deleteReport(reportId);
                singleResultAdapter.remove((singleResultAdapter.getItem(position)));
                singleResultAdapter.notifyDataSetChanged();
            }
        });
        alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

    }

    public void SearchSqlData(){
        id_arrayList.clear();
        member_arrayList.clear();
        weight_arrayList.clear();
        rate_arrayList.clear();
        totalamount_arrayList.clear();
        date_arrayList.clear();

        String startdate = startDateView.getText().toString();
        String enddate = endDateView.getText().toString();
        printString = "";
        printString = membername+"\nDT:"+startdate+" to DT:"+enddate+"\n" ;

        String startDate = startdate.replace("/", "");
        String endDate = enddate.replace("/","");

        String dd=startDate.substring(0,2);
        String mm=startDate.substring(2,4);
        String yy=startDate.substring(4,8);

        startDate = yy+mm+dd;

        dd=endDate.substring(0,2);
        mm=endDate.substring(2,4);
        yy=endDate.substring(4,8);
        endDate = yy+mm+dd;




        if (membername.equals("Select"))
            Toast.makeText(getActivity(), "Please Select Member Code First", Toast.LENGTH_LONG).show();
        else {

            try
            {
                MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
                SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();
                Cursor cursor;

                if (membername.equals("All")) {
                    cursor = sqLiteDatabase.rawQuery("SELECT * FROM 'milk_amount' WHERE date >= '" + startDate + "' and date <= '" + endDate + "' ORDER BY date", null);
                }
                else {
                    cursor = sqLiteDatabase.rawQuery("SELECT * FROM 'milk_amount' WHERE memberCode = '" + membername + "' and date >= '" + startDate + "' and date <= '" + endDate + "' ORDER BY date", null);
                }

                String alldata ="";

                if (cursor != null && cursor.moveToFirst()) {
                    while (cursor.isAfterLast() == false) {

                        member_arrayList.add(cursor.getString(cursor.getColumnIndex("memberCode")));
                        id_arrayList.add(cursor.getInt(cursor.getColumnIndex("Id")));
                        weight_arrayList.add(cursor.getFloat(cursor.getColumnIndex("milkweight")));
                        rate_arrayList.add(cursor.getFloat(cursor.getColumnIndex("rateperliter")));
                        totalamount_arrayList.add(cursor.getFloat(cursor.getColumnIndex("totalamount")));
                        sift_arrayList.add(cursor.getString(cursor.getColumnIndex("sift")));
                        date_arrayList.add(cursor.getString(cursor.getColumnIndex("dateSave")));
                        GetAllData_arrayList.add(cursor.getString(cursor.getColumnIndex("allInformation")));
                        dailyReportStringList.add(cursor.getString(cursor.getColumnIndex("dailyInformation")));

                        numberList.add(cursor.getString(cursor.getColumnIndex("milkinformation")));

                        String date =  cursor.getString(cursor.getColumnIndex("dateSave"));

                        try {
                                    String line = String.format("%5s%1s %-4s %-4s %-4s %-6s", date.substring(0,5),
                                    cursor.getString(cursor.getColumnIndex("sift")),
                                    MainActivity.oneDecimalString(cursor.getString(cursor.getColumnIndex("milkweight"))),
                                    MainActivity.oneDecimalString(cursor.getString(cursor.getColumnIndex("fat"))),
                                    MainActivity.oneDecimalString(cursor.getString(cursor.getColumnIndex("snf"))),
                                    MainActivity.twoDecimalString(cursor.getString(cursor.getColumnIndex("totalamount"))));

                            alldata = alldata + "\n" + line;

                        } catch (Exception e) {
                           // Toast.makeText(getActivity(), "Not Found Any Data", Toast.LENGTH_LONG).show();
                        }

                        cursor.moveToNext();

                        //alldata = alldata + "\n" + getNewString( String.valueOf(cursor.getColumnIndex("milkweight")), String.valueOf(cursor.getColumnIndex("fat")), String.valueOf(cursor.getColumnIndex("snf")),String.valueOf(cursor.getColumnIndex("rateperliter")),String.valueOf(cursor.getColumnIndex("totalamount")),String.valueOf(cursor.getColumnIndex("dateSave")),String.valueOf(cursor.getColumnIndex("sift")));
                    }

                }

                float weightsize = 0;
                float amountsize = 0;

                for(int j =0;j<weight_arrayList.size();j++){
                    SingleReport singleReport = new SingleReport();
                    singleReport.setCode(String.valueOf(member_arrayList.get(j)));
                    singleReport.setDate(String.valueOf(date_arrayList.get(j)));
                    singleReport.setWeight(String.valueOf(weight_arrayList.get(j)));
                    singleReport.setSift(String.valueOf(sift_arrayList.get(j)));
                    singleReport.setRate(String.valueOf(rate_arrayList.get(j)));
                    singleReport.setAmount(String.valueOf(totalamount_arrayList.get(j)));
                    singleReportList.add(singleReport);

                    weightsize = weightsize + weight_arrayList.get(j);
                    amountsize = amountsize + totalamount_arrayList.get(j);
                    SetAllData_arrayList.add(GetAllData_arrayList.get(j));

                    // alldata = alldata + "\n" + GetAllData_arrayList.get(j);
                }

                searchweight.setText("Wgt:- "+MainActivity.twoDecimalFloatToString(weightsize)+" Kg");
                searchamount.setText("Amt:- "+MainActivity.twoDecimalFloatToString(amountsize)+" /-");

                printString =  printString +  MainActivity.lineBreak() + "Date   Qty  Fat  "+MainActivity.instace.rateString()+"  Amt".toUpperCase();
                printString = printString+alldata+"\n " + MainActivity.lineBreak() + "Total Wgt : " + MainActivity.twoDecimalFloatToString(weightsize)+" Kg" +"\nTotal Amt : "+ MainActivity.twoDecimalFloatToString(amountsize)+"Rs";


                SharedPreferencesUtils  sharedPreferencesUtils = new SharedPreferencesUtils(getActivity());
                String titlename = sharedPreferencesUtils.getTitle();

                printString = titlename +  "\nMember Ladger\n"+memberNameReal + "   "+printString;
                printString = printString.toUpperCase();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Not Found Any Data", Toast.LENGTH_LONG).show();
            }
        }
        // }
    }

    public void getUserName() {


        try {
            MilkDBHelpers  milkDBHelpers = new MilkDBHelpers(getActivity());
            SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();

            Cursor cursor = sqLiteDatabase.rawQuery("Select * From member where membercode='" + membername + "'", null);
            if (cursor != null && cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    memberNameReal = cursor.getString(1);
                    // Toast.makeText(getActivity(),"member_name :- " + member_name, Toast.LENGTH_LONG).show();
                    cursor.moveToNext();
                }
            } else {
                // et_code.setError("Code Not Register..!");
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Enter Another Code", Toast.LENGTH_LONG).show();
        }

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

        try {
            MainActivity.print(printString);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
