package com.milk.milkcollectionapp.Fragment;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.milk.milkcollectionapp.Activity.MainActivity;
import com.milk.milkcollectionapp.Database.MilkDBHelpers;
import com.milk.milkcollectionapp.R;
import com.milk.milkcollectionapp.adapter.SingleReportAdapter;
import com.milk.milkcollectionapp.helper.AppString;
import com.milk.milkcollectionapp.helper.DatePickerFragment;
import com.milk.milkcollectionapp.helper.FSSession;
import com.milk.milkcollectionapp.helper.SharedPreferencesUtils;
import com.milk.milkcollectionapp.model.SingleEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Er. Arjun on 28-02-2016.
 */
public class Fragment_CellReport extends Fragment {
    private ListView savedmilk_listview;
    private Calendar calendar;
    private int year, month, day;
    private Button startDateView,endDateView,btnsearch;
    private TextView searchamount;
    ImageView iv_share;

    String message="", printString="";
    int reportId;

    ArrayList<SingleEntry>singleReportList = new ArrayList<>();
    SingleReportAdapter singleResultAdapter;
    SharedPreferencesUtils sharedPreferencesUtils;
    String title,memberNameReal;
    private List<String> dailyReportStringList = new ArrayList<>();
    private List<String> numberList = new ArrayList<>();


    public Fragment_CellReport() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_cell_report, container, false);
        startDateView =(Button)rootView.findViewById(R.id.btn_startdate);
        iv_share =(ImageView)rootView.findViewById(R.id.iv_share);
        endDateView =(Button)rootView.findViewById(R.id.btn_enddate);
        searchamount =(TextView)rootView.findViewById(R.id.search_amount);
        getCalendarDate();
        sharedPreferencesUtils= new SharedPreferencesUtils(getActivity());
        title= sharedPreferencesUtils.getTitle();

        iv_share.setOnClickListener(v -> {
            if(!printString.equals("")){
            shareDialog();
            } else {
                Toast.makeText(getActivity(), "Please Search Data First", Toast.LENGTH_LONG).show();
            }
        });
        savedmilk_listview = (ListView)rootView.findViewById(R.id.saved_listview);
        savedmilk_listview.setEmptyView(rootView.findViewById(R.id.empty_saved_list));
        savedmilk_listview.setOnCreateContextMenuListener(this);

        btnsearch = (Button)rootView.findViewById(R.id.btn_searchimage);
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message ="";
                singleReportList.clear();
                SearchSqlData();

                singleResultAdapter = new SingleReportAdapter(getActivity(), R.layout.listviewmember,singleReportList ){
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        TextView v = (TextView) super.getView(position, convertView, parent);
                        v.setBackground(getResources().getDrawable(R.drawable.text_underline_spinner));
                        return v;
                    }
                };
                savedmilk_listview.setAdapter(singleResultAdapter);
            }
        });

        savedmilk_listview.setOnItemLongClickListener((parent, view, position, id) -> true);

        startDateView.setText(AppString.getCurrentDate());
        endDateView.setText(AppString.getCurrentDate());

        startDateView.setOnClickListener(v -> {
            DialogFragment picker = new DatePickerFragment(startDateView);
            picker.show(getFragmentManager(), "datePicker");
        });
        endDateView.setOnClickListener(v -> {
            DialogFragment picker = new DatePickerFragment(endDateView);
            picker.show(getFragmentManager(), "datePicker");
        });

        onclickAdapter();
        return rootView;
    }

    void onclickAdapter(){
        savedmilk_listview.setOnItemClickListener((adapterView, view, position, l) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Sales Report");
            builder.setItems(new CharSequence[]
                            { "Delete"},
                    (dialog, which) -> {
                        switch (which) {
                            case 0:
                                deleteReport(position);
                                break;
                        }
                    });
            builder.create().show();

        });

    }

    private void deleteReport( final int position){
        SingleEntry entry = singleReportList.get(position);
        reportId = Integer.valueOf(entry.getID());
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setMessage("Are You sure want to delete this entry.");
        alertBuilder.setPositiveButton("Yes", (dialog, which) -> {
            MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
            Log.e("-=-=-ddd=-=", "ddddd");
            milkDBHelpers.deleteSaleEntry(reportId);
            singleResultAdapter.remove((singleResultAdapter.getItem(position)));
            singleResultAdapter.notifyDataSetChanged();
        });
        alertBuilder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        final AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    @SuppressLint("Range")
    public void SearchSqlData(){
        String startdate = startDateView.getText().toString();
        String enddate = endDateView.getText().toString();
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


                    String date =  cursor.getString(cursor.getColumnIndex("dateSave"));
                    singleReportList.add(entry);

                    try {
                                String line = String.format("%5s%1s %-4s %-4s %-4s %-6s", date.substring(0,5),
                                cursor.getString(cursor.getColumnIndex("sift")),
                                MainActivity.oneDecimalString(entry.getWeight()),
                                MainActivity.oneDecimalString(entry.getfat()),
                                MainActivity.oneDecimalString(entry.getSnf()),
                                MainActivity.twoDecimalString(entry.getAmount()));

                        alldata = alldata + "\n" + line;

                    } catch (Exception e) {
                       // Toast.makeText(getActivity(), "Not Found Any Data", Toast.LENGTH_LONG).show();
                    }

                    weightsize = weightsize + Float.valueOf(entry.getWeight());
                    amountsize = amountsize + Float.valueOf(entry.getAmount());

                    cursor.moveToNext();

                }
            }


//            searchweight.setText("Wgt:- "+MainActivity.twoDecimalFloatToString(weightsize)+" Kg");
//            searchamount.setText("Amt:- "+MainActivity.twoDecimalFloatToString(amountsize)+" /-");
//
//            printString =  printString +  MainActivity.lineBreak() + "Date   Qty  Fat  "+MainActivity.instace.rateString()+"  Amt".toUpperCase();
//            printString = printString+alldata+"\n " + MainActivity.lineBreak() + "Total Wgt : " + MainActivity.twoDecimalFloatToString(weightsize)+" Kg" +"\nTotal Amt : "+ MainActivity.twoDecimalFloatToString(amountsize)+"Rs";
//
//
//            SharedPreferencesUtils  sharedPreferencesUtils = new SharedPreferencesUtils(getActivity());
//            String titlename = sharedPreferencesUtils.getTitle();
//
//            printString = titlename +  "\nMember Ladger\n"+memberNameReal + "   "+printString;
//            printString = printString.toUpperCase();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Not Found Any Data", Toast.LENGTH_LONG).show();
        }
    }

    private void getCalendarDate(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
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

        }else if(monthlength>1&&daylength==1){
            String date = "0"+String.valueOf(day)+"/"+""+String.valueOf(month)+"/"+String.valueOf(year);
            startDateView.setText(date);
            endDateView.setText(date);
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
