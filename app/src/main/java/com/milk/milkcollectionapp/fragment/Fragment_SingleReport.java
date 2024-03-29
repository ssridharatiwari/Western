package com.milk.milkcollectionapp.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.milk.milkcollectionapp.activity.MainActivity;
import com.milk.milkcollectionapp.utils.db.MilkDBHelpers;
import com.milk.milkcollectionapp.R;
import com.milk.milkcollectionapp.adapter.SingleReportAdapter;
import com.milk.milkcollectionapp.utils.AppString;
import com.milk.milkcollectionapp.utils.DatePickerFragment;
import com.milk.milkcollectionapp.utils.SharedPreferencesUtils;
import com.milk.milkcollectionapp.model.Member;
import com.milk.milkcollectionapp.model.PDFMermberReport;
import com.milk.milkcollectionapp.model.SingleEntry;

import java.util.ArrayList;
import java.util.List;

public class Fragment_SingleReport extends Fragment {
    private ListView savedmilk_listview;
    private TextView startDateView, endDateView, btnsearch;
    private TextView searchweight, searchamount;
    private ImageView iv_share;
    private Spinner spnr_membername;
    private String membername;
    private String printString = "";
    private int reportId;
    private ArrayList<SingleEntry> singleReportList = new ArrayList<>();
    private SingleReportAdapter singleResultAdapter;
    private SharedPreferencesUtils sharedPreferencesUtils;
    private String title, memberNameReal;
    private List<String> dailyReportStringList = new ArrayList<>();
    private List<String> arrayPrints = new ArrayList<>();
    private PDFMermberReport report = new PDFMermberReport();

    public Fragment_SingleReport() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_single_report, container, false);
        startDateView = rootView.findViewById(R.id.btn_startdate);
        iv_share = (ImageView) rootView.findViewById(R.id.iv_share);
        endDateView = rootView.findViewById(R.id.btn_enddate);
        searchweight = (TextView) rootView.findViewById(R.id.search_weight);
        searchamount = (TextView) rootView.findViewById(R.id.search_amount);
        getCalendarDate();
        sharedPreferencesUtils = new SharedPreferencesUtils(getActivity());
        title = sharedPreferencesUtils.getTitle();

        iv_share.setOnClickListener(v -> {
            if (!printString.equals("")) {
                shareDialog();
            } else {
                Toast.makeText(getActivity(), "Please Search Data First", Toast.LENGTH_LONG).show();
            }
        });
        savedmilk_listview = (ListView) rootView.findViewById(R.id.saved_listview);
        savedmilk_listview.setEmptyView(rootView.findViewById(R.id.empty_saved_list));
        savedmilk_listview.setOnCreateContextMenuListener(this);

        /// spinner
        spnr_membername = (Spinner) rootView.findViewById(R.id.spinr_membername);
        ArrayList<String> spinnerMemberCode_arrayList;
        MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
        spinnerMemberCode_arrayList = milkDBHelpers.searchMemberCode();
        ArrayAdapter<String> SpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinnertext, spinnerMemberCode_arrayList) {
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setBackgroundColor(Color.TRANSPARENT);
                v.setPadding(15, 0, 0, 0);
                return v;
            }

            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setGravity(Gravity.CENTER);
                v.setBackground(getResources().getDrawable(R.drawable.text_underline_spinner));
                return v;
            }
        };

        spnr_membername.setAdapter(SpinnerAdapter);
        spnr_membername.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {
                membername = parent.getItemAtPosition(position) + "";
                if (position == 0) {
                    memberNameReal = "All";
                } else {
                    getUserName();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        btnsearch = rootView.findViewById(R.id.btn_searchimage);
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleReportList.clear();
                SearchSqlData();
                singleResultAdapter = new SingleReportAdapter(getActivity(), R.layout.listviewmember, singleReportList) {
                    public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                        TextView v = (TextView) super.getView(position, convertView, parent);
                        v.setBackground(getResources().getDrawable(R.drawable.text_underline_spinner));
                        return v;
                    }
                };
                savedmilk_listview.setAdapter(singleResultAdapter);
            }
        });

        savedmilk_listview.setOnItemLongClickListener((parent, view, position, id) -> {
            //   deleteReport(position);
            return true;
        });


        savedmilk_listview.setOnItemClickListener((adapterView, view, position, l) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Daily Report .... ");
            builder.setItems(new CharSequence[]
                            {"Whats App", "SMS", "Other Share", "Print", "Delete"},
                    (dialog, which) -> {
                        // The 'which' argument contains the index position
                        // of the selected item

                        SingleEntry entry = singleReportList.get(position);
                        switch (which) {
                            case 0:
                                MainActivity.sendWhatsApp(entry.getPrintMassge());
                                break;
                            case 1:
                                MilkDBHelpers milkDBHelpers1 = new MilkDBHelpers(getActivity());
                                Member newMenber = milkDBHelpers1.getMember(entry.getCode());
                                if (newMenber.getMobile() != "1234") {
                                    String strSms = entry.getSMS();
                                    MainActivity.sendTextSms(strSms, newMenber.getMobile());
                                } else {
                                    Toast.makeText(getActivity(), "Update Number And Try Again", Toast.LENGTH_LONG).show();
                                }
                                break;
                            case 2:
                                entry = singleReportList.get(position);
                                MainActivity.sendWhatsApp(entry.getPrintMassge());
                                String dataShare = entry.getPrintMassge();
                                MainActivity.shareText(dataShare);
                                break;
                            case 3:
                                Log.e("message", entry.getPrintMassge());
                                print(entry.getPrintMassge());
                                break;
                            case 4:
                                deleteReport(position);
                                break;
                        }
                    });
            builder.create().show();
        });


        startDateView.setOnClickListener(v -> {
            DialogFragment picker = new DatePickerFragment(startDateView);
            picker.show(getFragmentManager(), "datePicker");
        });
        endDateView.setOnClickListener(v -> {
            DialogFragment picker = new DatePickerFragment(endDateView);
            picker.show(getFragmentManager(), "datePicker");
        });

        return rootView;
    }


    private void deleteReport(final int position) {
        SingleEntry entry = singleReportList.get(position);
        reportId = Integer.getInteger(entry.getID());

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setMessage("Are You sure want to delete this Report ");
        alertBuilder.setPositiveButton("Yes", (dialog, which) -> {
            MilkDBHelpers milkDBHelpers1 = new MilkDBHelpers(getActivity());
            milkDBHelpers1.deleteReport(reportId);
            singleResultAdapter.remove((singleResultAdapter.getItem(position)));
            singleResultAdapter.notifyDataSetChanged();
        });
        alertBuilder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        final AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

    }

    int count = 0;

    @SuppressLint("Range")
    public void SearchSqlData() {
        singleReportList.clear();
        String startdate = startDateView.getText().toString();
        String enddate = endDateView.getText().toString();
        printString = "";

        String startDate = startdate.replace("/", "");
        String endDate = enddate.replace("/", "");

        String dd = startDate.substring(0, 2);
        String mm = startDate.substring(2, 4);
        String yy = startDate.substring(4, 8);

        startDate = yy + mm + dd;

        dd = endDate.substring(0, 2);
        mm = endDate.substring(2, 4);
        yy = endDate.substring(4, 8);
        endDate = yy + mm + dd;

        if (membername.equals("Select"))
            Toast.makeText(getActivity(), "Please Select Member Code First", Toast.LENGTH_LONG).show();
        else {
            try {
                MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
                SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();
                Cursor cursor;

                if (membername.equals("All")) {
                    cursor = sqLiteDatabase.rawQuery("SELECT * FROM 'milk_amount' WHERE date >= '" + startDate + "' and date <= '" + endDate + "' ORDER BY date", null);
                } else {
                    cursor = sqLiteDatabase.rawQuery("SELECT * FROM 'milk_amount' WHERE memberCode = '" + membername + "' and date >= '" + startDate + "' and date <= '" + endDate + "' ORDER BY date", null);
                }

                String alldata = "";
                String tempString = "";
                float weightsize = 0;
                float amountsize = 0;
                count = 0;
                arrayPrints.clear();
                List<String> tempArray = new ArrayList<>();

                if (cursor != null && cursor.moveToFirst()) {
                    while (cursor.isAfterLast() == false) {
                        SingleEntry entry = new SingleEntry();
                        entry.setCode(cursor.getString(cursor.getColumnIndex(AppString.milk.code)));
                        entry.setId(cursor.getString(cursor.getColumnIndex(AppString.milk.id)));
                        entry.setRate(cursor.getString(cursor.getColumnIndex(AppString.milk.rate)));
                        entry.setAmount(cursor.getString(cursor.getColumnIndex(AppString.milk.amount)));
                        entry.setSift(cursor.getString(cursor.getColumnIndex(AppString.milk.sift)));
                        entry.setDatesave(cursor.getString(cursor.getColumnIndex(AppString.milk.dateSave)));
                        entry.setWeight(cursor.getString(cursor.getColumnIndex(AppString.milk.weight)));
                        entry.setFat(cursor.getString(cursor.getColumnIndex(AppString.milk.fat)));
                        entry.setSnf(cursor.getString(cursor.getColumnIndex(AppString.milk.snf)));
                        entry.setFatWt(cursor.getString(cursor.getColumnIndex(AppString.milk.fat_wt)));
                        entry.setSnfWt(cursor.getString(cursor.getColumnIndex(AppString.milk.snf_wt)));
                        entry.setCMF(cursor.getString(cursor.getColumnIndex(AppString.milk.cmf)));
                        singleReportList.add(entry);
                        weightsize = weightsize + Float.valueOf(entry.getWeight());
                        amountsize = amountsize + Float.valueOf(entry.getAmount());
                        String date = cursor.getString(cursor.getColumnIndex("dateSave"));

                        try {
                            String line = String.format("%5s%1s %-4s %-4s %-4s %-6s", date.substring(0, 5),
                                    cursor.getString(cursor.getColumnIndex("sift")),
                                    MainActivity.oneDecimalString(cursor.getString(cursor.getColumnIndex("milkweight"))),
                                    MainActivity.oneDecimalString(cursor.getString(cursor.getColumnIndex("fat"))),
                                    MainActivity.oneDecimalString(cursor.getString(cursor.getColumnIndex("snf"))),
                                    MainActivity.twoDecimalString(cursor.getString(cursor.getColumnIndex("totalamount"))));
                            alldata = alldata + "\n" + line;
                            tempString = tempString + "\n" + line;
                        } catch (Exception e) {
                            // Toast.makeText(getActivity(), "Not Found Any Data", Toast.LENGTH_LONG).show();
                        }

                        if (count > 0 && count % 30 == 0) {
                            tempArray.add(tempString);
                            tempString = "";
                        }
                        count++;
                        cursor.moveToNext();
                    }
                }

                searchweight.setText("Wgt:- " + MainActivity.twoDecimalFloatToString(weightsize) + " Kg");
                searchamount.setText("Amt:- " + MainActivity.twoDecimalFloatToString(amountsize) + " /-");
                printString = "";

                SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(getActivity());
                String titlename = sharedPreferencesUtils.getTitle();

                printString = titlename + "\nMember Ladger\n" + memberNameReal + " " + membername + "\nDT:" + startdate + " to DT:" + enddate + "\n";
                printString = printString + MainActivity.lineBreak() + "Date   Qty  Fat  " + MainActivity.instace.rateString() + "  Amt".toUpperCase();
                printString = printString + "\n" + alldata + "\n" + MainActivity.lineBreak() + "Total Wgt : "
                        + MainActivity.twoDecimalFloatToString(weightsize) + " Kg" + "\nTotal Amt : "
                        + MainActivity.twoDecimalFloatToString(amountsize) + "Rs";

                report.setData(title, "Member Ladger", memberNameReal, membername, startdate, enddate);
                report.setAmounts(weightsize, amountsize);
                report.setArray(singleReportList);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Not Found Any Data", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void getUserName() {
        try {
            MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
            SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("Select * From member where membercode='" + membername + "'", null);
            if (cursor != null && cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    memberNameReal = cursor.getString(1);
                    cursor.moveToNext();
                }
            } else {
                // et_code.setError("Code Not Register..!");
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Enter Another Code", Toast.LENGTH_LONG).show();
        }
    }

    private void getCalendarDate() {
        String date = AppString.getCurrentDate();
        startDateView.setText(date);
        endDateView.setText(date);
    }


    private void shareDialog() {
        final CharSequence[] options = {"WhatsApp", "Mail", "Other Share", "Print Report", "Share PDF"};
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Send Report");
        adb.setItems(options, (dialog, item) -> {
            if (options[item].equals("WhatsApp")) {
                PackageManager pm = getActivity().getPackageManager();
                try {
                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
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
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client"));
            } else if (options[item].equals("Other Share")) {
                Intent sentIntent = new Intent(Intent.ACTION_SEND);
                sentIntent.putExtra(Intent.EXTRA_TEXT, printString);
                sentIntent.setType("text/plain");
                startActivity(sentIntent);
            } else if (options[item].equals("Print Report")) {
                print(printString);
            } else if (options[item].equals("Share PDF")) {
                report.createSessionPdf(report);
            }
        });
        adb.show();
    }

    private void print(String printString) {
        MainActivity.getInstace().print(printString);
    }

}
