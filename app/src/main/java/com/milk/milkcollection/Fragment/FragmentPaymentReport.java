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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.milk.milkcollection.Activity.MainActivity;
import com.milk.milkcollection.Database.MilkDBHelpers;
import com.milk.milkcollection.R;
import com.milk.milkcollection.adapter.PaymentReportAdapter;
import com.milk.milkcollection.helper.AppString;
import com.milk.milkcollection.helper.DatePickerFragment;
import com.milk.milkcollection.helper.SharedPreferencesUtils;
import com.milk.milkcollection.model.PaymentReport;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;

/**
 * Created by Alpha on 14-12-2015.
 */
public class FragmentPaymentReport extends Fragment {
    private ListView savedmilk_listview;
    private Calendar calendar;
    private int year, month, day;
    private Button startDateView,endDateView,btnsearch;
    private TextView searchweight,searchamount;
    ImageView iv_share;
    String message="",membername="All";
    int reportId;
    ArrayList<Integer> id_arrayList = new ArrayList<>();
    ArrayList<String>member_arrayList = new ArrayList<String>();
    ArrayList<Float>weight_arrayList = new ArrayList<>();
    ArrayList<Float>totalamount_arrayList = new ArrayList<>();

    ArrayList<String>SetAllData_arrayList = new ArrayList<String>();
    ArrayList<String>GetAllData_arrayList = new ArrayList<String>();
    ArrayList<PaymentReport>singleReportList = new ArrayList<>();
    PaymentReportAdapter paymentReportAdapter;
    private ArrayList<String> memberCodeArrayList;
    private ArrayList<String>member_name;
    public TextView toolbartitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_payment_report, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        startDateView =(Button)rootView.findViewById(R.id.btn_startdate);
        iv_share =(ImageView)rootView.findViewById(R.id.iv_share);
        endDateView =(Button)rootView.findViewById(R.id.btn_enddate);
        searchweight =(TextView)rootView.findViewById(R.id.search_weight);
        searchamount =(TextView)rootView.findViewById(R.id.search_amount);
        toolbartitle = (TextView)getActivity().findViewById(R.id.titletool);
        toolbartitle.setText(getResources().getString(R.string.payment_report_text));

///   --- Set Current date in date text Views

        MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
        startDateView.setText(AppString.getCurrentDate());
        endDateView.setText(AppString.getCurrentDate());


        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!message.equals("")) {
                    shareDialog();
                } else {
                    Toast.makeText(getActivity(), "Please Search Data First", Toast.LENGTH_LONG).show();
                }
            }
        });
        savedmilk_listview = (ListView)rootView.findViewById(R.id.saved_listview);
        savedmilk_listview.setEmptyView(rootView.findViewById(R.id.empty_saved_list));
        savedmilk_listview.setOnCreateContextMenuListener(this);



        btnsearch = (Button)rootView.findViewById(R.id.btn_searchimage);
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = "";
                singleReportList.clear();
                SearchSqlData();
                Log.e("=-m-m-m--=", " " + GetAllData_arrayList);
                sortByAtoZ(singleReportList, true, paymentReportAdapter, R.layout.listviewmember, savedmilk_listview);
                //ArrayList<SingleReportAdapter>si = new SingleReportAdapter(getActivity(), R.layout.listviewmember, singleReportList)

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

        savedmilk_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
                ArrayList<Integer> reportIdList = new ArrayList<>();
                reportIdList = milkDBHelpers.reportId();
                reportId = reportIdList.get(position);
                ////Log.e("-=-=-item=-=", String.valueOf(fatSnfId));
                // fatSnfPosition = position;

                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setMessage("Cant delete of all data of a member ,you can delete single entry");
                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
//                        Log.e("-=-=-ddd=-=", "ddddd");
//                        milkDBHelpers.deleteReport(reportId);
//                        paymentReportAdapter.remove((paymentReportAdapter.getItem(position)));
//                        paymentReportAdapter.notifyDataSetChanged();\
                        dialog.cancel();
                    }
                });
//                alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
                final AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
                return true;
            }
        });
        return rootView;
    }

    public void SearchSqlData(){
        id_arrayList.clear();
        member_arrayList.clear();
        weight_arrayList.clear();
        totalamount_arrayList.clear();

        String startdate = startDateView.getText().toString();
        String enddate = endDateView.getText().toString();
        String startDate = startdate.replace("/", "");
        Log.e("-=-=start date--=", startDate + " ;" + startdate);
        String endDate = enddate.replace("/","");
        Log.e("-=-=end date--=", endDate + " ;" + enddate);

        message = startdate +"  to  "+enddate+"\n*******************************\nCode  Weight  Amount   Name";

        String dd=startDate.substring(0,2);
        String mm=startDate.substring(2,4);
        String yy=startDate.substring(4,8);

        startDate = yy+mm+dd;

        dd=endDate.substring(0,2);
        mm=endDate.substring(2,4);
        yy=endDate.substring(4,8);
        endDate = yy+mm+dd;

        try
        {
            MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
            SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();
            Cursor cursor;
            memberCodeArrayList = new ArrayList<String>();
            memberCodeArrayList = milkDBHelpers.searchMemberCode();
            member_name= new ArrayList<>();
            member_name= milkDBHelpers.SearchName();
            float weightSize = 0,totalWeight = 0;
            float amountSize = 0,totalAmount = 0;
            String memberCode="";
            DecimalFormat df = new DecimalFormat("#.##");

            if(memberCodeArrayList.size()>0){

                for(int i=0; i < memberCodeArrayList.size();i++)
                {
                    weightSize = weightSize*0;
                    amountSize = amountSize*0;
                    memberCode = memberCodeArrayList.get(i).toString();
                    String name= member_name.get(i).toString();

                    weight_arrayList.clear();
                    totalamount_arrayList.clear();

                    cursor = sqLiteDatabase.rawQuery("SELECT * FROM 'milk_amount' WHERE memberCode = '" + memberCodeArrayList.get(i).toString() + "' and date >= '" + startDate + "' and date <= '" + endDate + "'", null);
                    if (cursor != null && cursor.moveToFirst()) {
                        while (cursor.isAfterLast() == false) {

                            weight_arrayList.add(cursor.getFloat(cursor.getColumnIndex("milkweight")));
                            totalamount_arrayList.add(cursor.getFloat(cursor.getColumnIndex("totalamount")));
                            cursor.moveToNext();



                        }
                    }

                    for (int j = 0; j < weight_arrayList.size(); j++) {

                        weightSize = weightSize + weight_arrayList.get(j);
                        amountSize = amountSize + totalamount_arrayList.get(j);

                        totalWeight = totalWeight + weight_arrayList.get(j);
                        totalAmount = totalAmount + totalamount_arrayList.get(j);

                        if(weight_arrayList.size()==(j+1)){
                            PaymentReport paymentReport = new PaymentReport();
                            paymentReport.setCode(memberCode);
                            paymentReport.setName(name);
                            paymentReport.setWeight(MainActivity.twoDecimalFloatToString(weightSize));
                            paymentReport.setAmount(MainActivity.twoDecimalFloatToString(amountSize));
                            singleReportList.add(paymentReport);
                        }
                    }

                    searchweight.setText("Wgt:-" + (MainActivity.twoDecimalFloatToString(totalWeight)) + "Kg");
                    searchamount.setText("Amt:-" + (MainActivity.twoDecimalFloatToString(totalAmount)) + "/-");

                    if (  i > 1 && weightSize > 0)
                    {
                        String wt = String.valueOf(MainActivity.twoDecimalFloatToString(weightSize)) ,
                                amt = String.valueOf(MainActivity.twoDecimalFloatToString(amountSize));

                        try {
                            String line = String.format("%6s %8s %-10s", wt, amt, name);
                             message = message+ "\n" + memberCode + " " + line;
                        } catch (Exception e) {
                        }

                    }
                }

                message = "Payment Report\n"+message+ "\n*******************************\n"+"Total weight  : "+ MainActivity.twoDecimalFloatToString( totalWeight)+"\n" + "Total Amount  : " +MainActivity.twoDecimalFloatToString(totalAmount) +" RS";

                SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(getActivity());

                String titlename = sharedPreferencesUtils.getTitle();
                message = titlename + "\n" + message;

            }

        } catch (Exception e) {

        }
    }

    private void sortByAtoZ(ArrayList<PaymentReport> singleReportList, final boolean isAsc, PaymentReportAdapter paymentReportAdapter, int listviewmember, ListView savedmilk_listview) {

        PaymentReport arr[] = new PaymentReport[singleReportList.size()];
        singleReportList.toArray(arr);
        Arrays.sort(arr, new Comparator<PaymentReport>() {
            public int compare(PaymentReport o1, PaymentReport o2) {
                try {
                    String t1 = (String) o1.getCode();
                    String t2 = (String) o2.getCode();

                    if (isAsc)
                        return t1.compareTo(t2);
                    else
                        return t2.compareTo(t1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
                }
            });

        ArrayList<PaymentReport> newList = new ArrayList<PaymentReport>();
        newList.addAll(Arrays.asList(arr));
        paymentReportAdapter = new PaymentReportAdapter(getActivity(), R.layout.listviewmember, newList) {
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                // v.setTypeface(typeface);
                //  v.setTextColor(Color.RED);

                v.setBackground(getResources().getDrawable(R.drawable.text_underline_spinner));
                //v.setTextSize(17);
                return v;
            }
        };
        savedmilk_listview.setAdapter(paymentReportAdapter);
    }


    private void shareDialog(){
        final CharSequence[] options = { "WhatsApp", "Mail","Other Share","Print Report"};
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Send Payment Report");
        adb.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("WhatsApp")) {

                    PackageManager pm = getActivity().getPackageManager();
                    try {

                        Intent waIntent = new Intent(Intent.ACTION_SEND);
                        waIntent.setType("text/plain");
                        //   String text = "YOUR TEXT HERE";

                        PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                        //Check if package exists or not. If not then code
                        //in catch block will be called
                        waIntent.setPackage("com.whatsapp");

                        waIntent.putExtra(Intent.EXTRA_TEXT, String.format(message));
                        startActivity(Intent.createChooser(waIntent, "Share with"));

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (options[item].equals("Mail")) {
                    String to = "";
                    String subject = "Milk Collection Payment Report";
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                    email.putExtra(Intent.EXTRA_SUBJECT, subject);
                    email.putExtra(Intent.EXTRA_TEXT, message);
                    // need this to prompts email client only
                    email.setType("message/rfc822");

                    startActivity(Intent.createChooser(email, "Choose an Email client"));


                } else if (options[item].equals("Other Share")) {

                    Intent sentIntent = new Intent(Intent.ACTION_SEND);
                    sentIntent.putExtra(Intent.EXTRA_TEXT, message);
                    sentIntent.setType("text/plain");
                    startActivity(sentIntent);

                }

                else if (options[item].equals("Print Report")) {

                 print(message);
                }
            }
        });
        adb.show();
    }

    private void print(String printString) {

        MainActivity.getInstace().print(printString);
    }

}