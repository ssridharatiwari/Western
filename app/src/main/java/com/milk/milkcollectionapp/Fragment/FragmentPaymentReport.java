package com.milk.milkcollectionapp.Fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.milk.milkcollectionapp.Activity.MainActivity;
import com.milk.milkcollectionapp.Database.MilkDBHelpers;
import com.milk.milkcollectionapp.R;
import com.milk.milkcollectionapp.adapter.PaymentReportAdapter;
import com.milk.milkcollectionapp.helper.AppString;
import com.milk.milkcollectionapp.helper.DatePickerFragment;
import com.milk.milkcollectionapp.helper.SharedPreferencesUtils;
import com.milk.milkcollectionapp.model.PDFPaymentReport;
import com.milk.milkcollectionapp.model.PaymentReport;

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
    private TextView searchweight, searchamount;
    ImageView iv_share;
    String message="",membername="All",totalSummery="";
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
    PDFPaymentReport report = new PDFPaymentReport();

    private AutoCompleteTextView acFrom,acTo;
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

        MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
        startDateView.setText(AppString.getCurrentDate());
        endDateView.setText(AppString.getCurrentDate());

        acFrom = (AutoCompleteTextView)  rootView.findViewById(R.id.codeFrom);
        acTo = (AutoCompleteTextView)  rootView.findViewById(R.id.codeTo);

        setAcData();
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

                sortByAtoZ(singleReportList, true, paymentReportAdapter, R.layout.listviewmember, savedmilk_listview);
            }
        });

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

                        dialog.cancel();
                    }
                });

                final AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
                return true;
            }
        });
        return rootView;
    }

    void setAcData(){


        ArrayList<String> arryMebers = MainActivity.getInstace().milkDBHelpers.memberCodeAutoComplet();

        if (arryMebers.size()> 0){
            ArrayAdapter<String> adapter;
            adapter = new ArrayAdapter<String>
                    (getActivity(),android.R.layout.simple_list_item_1,arryMebers);
            acFrom.setAdapter(adapter);
            acTo.setAdapter(adapter);
            acFrom.setText(arryMebers.get(0));
            acTo.setText(arryMebers.get(arryMebers.size()-1));
        }

    }


    public void SearchSqlData(){


        id_arrayList.clear();
        member_arrayList.clear();
        weight_arrayList.clear();
        totalamount_arrayList.clear();

        String startdate = startDateView.getText().toString();
        String enddate = endDateView.getText().toString();
        String startDate = startdate.replace("/", "");

        String endDate = enddate.replace("/","");


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

            float totalWeight = 0;
            float totalAmount = 0;
            String memberCode="";
            DecimalFormat df = new DecimalFormat("#.##");

            String query = "SELECT m.membername,ma.memberCode,SUM(ma.milkweight) as wgt,SUM(ma.totalamount) as amt FROM 'milk_amount' ma " +
                    "LEFT JOIN 'member' m ON m.membercode=ma.memberCode WHERE ma.date >= '" + startDate + "' AND ma.date <= '" + endDate + "' AND ma.memberCode >= '" + acFrom.getText().toString() + "' AND ma.memberCode <= '" + acTo.getText().toString() + "' GROUP BY ma.memberCode";

            cursor = sqLiteDatabase.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {

                while (cursor.isAfterLast() == false) {

                    String code = cursor.getString(cursor.getColumnIndex("memberCode"));
                    String name = cursor.getString(cursor.getColumnIndex("membername"));
                    Float weight = cursor.getFloat(cursor.getColumnIndex("wgt"));
                    Float amount = cursor.getFloat(cursor.getColumnIndex("amt"));

                    totalWeight = totalWeight +  weight;
                    totalAmount = totalAmount +  amount;

                    PaymentReport paymentReport = new PaymentReport();
                    paymentReport.setCode(code);
                    paymentReport.setName(name);
                    paymentReport.setWeight(MainActivity.twoDecimalFloatToString(weight));
                    paymentReport.setAmount(MainActivity.twoDecimalFloatToString(amount));
                    singleReportList.add(paymentReport);


                    String wt = String.valueOf(MainActivity.twoDecimalFloatToString(weight)) ,
                            amt = String.valueOf(MainActivity.twoDecimalFloatToString(amount));
                    try {
                        String line = String.format("%6s %8s %-10s " , wt, amt, name);
                        message = message+ "\n" + code + " " + line;
                    }  catch (Exception e) {
                    }

                    cursor.moveToNext();
                }
                searchweight.setText("Wgt:-" + (MainActivity.twoDecimalFloatToString(totalWeight)) + "Kg");
                searchamount.setText("Amt:-" + (MainActivity.twoDecimalFloatToString(totalAmount)) + "/-");
            }

            message = "Payment Report\n"+message+ "\n*******************************\n"+"Total weight  : "+ MainActivity.twoDecimalFloatToString( totalWeight)+"\n" + "Total Amount  : " +MainActivity.twoDecimalFloatToString(totalAmount) +" RS";
            SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(getActivity());
            String titlename = sharedPreferencesUtils.getTitle();
            message = titlename + "\n" + message;
            totalSummery = "";

            report.setAmounts(totalWeight,totalAmount);
            report.setArray(singleReportList);
            report.setData(titlename,"",startdate,enddate);
            return;

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

        final CharSequence[] options = { "WhatsApp", "Mail","Other Share","Print Report","Share PDF"};
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Send Payment Report");
        adb.setItems(options, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
                    Log.e("",message);
                } else if (options[item].equals("Print Report")) {
                    print(message);
                } else if (options[item].equals("Share PDF")) {
                    report.createSessionPdf(report);
                }
            }
        });
        adb.show();
    }

    private void print(String printString) {

        MainActivity.getInstace().print(printString);
    }

}