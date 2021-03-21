package com.milk.milkcollection.Fragment;

import android.Manifest;
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
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.milk.milkcollection.Activity.MainActivity;
import com.milk.milkcollection.Database.MilkDBHelpers;
import com.milk.milkcollection.R;
import com.milk.milkcollection.adapter.DailyReportAdapter;
import com.milk.milkcollection.helper.AppString;
import com.milk.milkcollection.helper.DatePickerFragment;
import com.milk.milkcollection.helper.SharedPreferencesUtils;
import com.milk.milkcollection.helper.UploadFile;
import com.milk.milkcollection.model.DailyReport;
import com.milk.milkcollection.model.Member;
import com.milk.milkcollection.model.PDFDaily;
import com.milk.milkcollection.model.SingleEntry;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.milk.milkcollection.Activity.MainActivity.instace;

 public class Fragment_DailyReport extends Fragment {

    private TextView daily_wight, daily_fat, daily_snf, daily_amut,lbl_snf_dr;
    private ListView savedmilk_listview;
    private Button startDateView, btnsearch;
    private String sift = "";
    String message = "",totalSummery="",tiltle,fileName;

    ImageView iv_share;
    ArrayList<SingleEntry> DailyReportList = new ArrayList<>();
    private DailyReportAdapter dailyReportAdapter;
    private int reportId = 0;
    Spinner spinr_ampm;
    String shifts;
    PDFDaily pdfDaily = new PDFDaily();

     private AutoCompleteTextView acFrom,acTo;

     public Fragment_DailyReport() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dailyreport, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        iv_share = (ImageView) rootView.findViewById(R.id.iv_share);
        daily_wight = (TextView) rootView.findViewById(R.id.daily_wight);
        daily_fat = (TextView) rootView.findViewById(R.id.daily_fat);
        daily_snf = (TextView) rootView.findViewById(R.id.daily_snf);
        daily_amut = (TextView) rootView.findViewById(R.id.daily_amut);
        lbl_snf_dr = (TextView) rootView.findViewById(R.id.lbl_snf_dr);
        startDateView = (Button) rootView.findViewById(R.id.btn_startdatemilk);

        savedmilk_listview = (ListView) rootView.findViewById(R.id.saved_listviewmilk);
        savedmilk_listview.setEmptyView(rootView.findViewById(R.id.empty_saved_listmilk));
        savedmilk_listview.setOnCreateContextMenuListener(this);

        acFrom = (AutoCompleteTextView)  rootView.findViewById(R.id.aCFrom);
        acTo = (AutoCompleteTextView)  rootView.findViewById(R.id.aCTo);

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

        spinr_ampm = (Spinner) rootView.findViewById(R.id.spinner_ampm);
        String[] morning_evening = new String[]{"Morning", "Evening"};
        ArrayAdapter<String> SpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.listviewmember, morning_evening);
        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinr_ampm.setAdapter(SpinnerAdapter);
        spinr_ampm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                int i = position;
                Log.e("sift_int", String.valueOf(i));
                sift = parent.getItemAtPosition(position) + "";
                // Log.e("sift", sift);
                if (i == 0) {
                    sift = "M";
                    //shifts = "Morning";
                } else if (i == 1) {
                    sift = "E";
                   // shifts = "Evening";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        btnsearch = (Button) rootView.findViewById(R.id.btn_searchmilk);
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSearchButton();

            }
        });


        startDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment picker = new DatePickerFragment(startDateView);
                picker.show(getFragmentManager(), "datePicker");
            }
        });

        savedmilk_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

               // deleteReport(position);

                return true;
            }
        });

        savedmilk_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Daily Report .... ");
                builder.setItems(new CharSequence[]
                                {"Whats App","SMS", "Other Share", "Print", "Delete","Edit"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item


                                SingleEntry entry = DailyReportList.get(position);



                                switch (which) {
                                    case 0:
                                        MainActivity.sendWhatsApp(entry.getPrintMassge());
                                        break;
                                    case 1:
                                        MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
                                        Member newMenber =  milkDBHelpers.getMember(entry.getCode());

                                        if(newMenber.getMobile()!="1234"){
                                            String strSms = entry.getSMS();
                                            MainActivity.sendTextSms(strSms,newMenber.getMobile());
                                        }else {
                                            Toast.makeText(getActivity(), "Update Number And Try Again", Toast.LENGTH_LONG).show();
                                        }

                                        break;
                                    case 2:

                                        MainActivity.sendWhatsApp(entry.getPrintMassge());
                                        String dataShare = entry.getPrintMassge();
                                        MainActivity.shareText(dataShare);
                                        break;
                                    case 3:

                                        print(entry.getPrintMassge());
                                        break;

                                    case 4:
                                        deleteReport(position);
                                        break;

                                    case 5:
                                        updateEntry(position);
                                        break;
                                }
                            }
                        });
                builder.create().show();

            }
        });

        setAcData();
        setTextsAccordingRate();
        getCalendarDate();
        uploadFile();
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


    void updateEntry(int position){


        Fragment_update fragment = new Fragment_update();
        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();

        fragment.entry = DailyReportList.get(position);

        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack("home");
        ft.commit();
    }

    public void setTextsAccordingRate()  {
        lbl_snf_dr.setText(MainActivity.getInstace().rateString());
    }

    private void deleteReport( final int position){

        MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
        ArrayList<Integer> reportIdList = new ArrayList<>();
        reportIdList = milkDBHelpers.reportId();
        SingleEntry entry = DailyReportList.get(position);
        reportId = Integer.valueOf(entry.getID());

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setMessage("Are You sure want to delete this Report ");
        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
                Log.e("-=-=-ddd=-=", "ddddd");
                milkDBHelpers.deleteReport(reportId);
                dailyReportAdapter.remove((dailyReportAdapter.getItem(position)));
                dailyReportAdapter.notifyDataSetChanged();
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

    public void clickSearchButton() {

        DailyReportList.clear();
        SearchSqlData();


        dailyReportAdapter = new DailyReportAdapter(getActivity(), R.layout.listviewmember, DailyReportList) {
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);

                v.setBackground(getResources().getDrawable(R.drawable.text_underline_spinner));

                return v;
            }
        };
        savedmilk_listview.setAdapter(dailyReportAdapter);
        dailyReportAdapter.notifyDataSetChanged();

    }


    public void SearchSqlData() {

        DailyReportList.clear();

        String startdate = startDateView.getText().toString();
        String startDate = AppString.reverceDate(startdate);

        if ( acFrom.getText().toString().length() == 0 ||  acTo.getText().toString().length() == 0  ) {

            MainActivity.makeTost("Fill Codes");
            return;
        }

        if (Integer.valueOf(acFrom.getText().toString()) > Integer.valueOf(acTo.getText().toString()) ){
            MainActivity.makeTost("From code lest must less than To Code");
            return;
        }

        try {
            MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
            SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();

            String query = " SELECT * FROM 'milk_amount' WHERE sift = '" + sift + "' AND date = '" + startDate + "' AND memberCode >= '" + acFrom.getText().toString() + "' AND memberCode <= '" + acTo.getText().toString() + "' ORDER BY memberCode";
            Log.e("query ::: ",query);
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);

            float weightTotal = 0;
            float amountTotal = 0;
            float fat_wt = 0;
            float snf_wt = 0;

            message = "";
            if (cursor != null && cursor.moveToFirst()) {

                while (cursor.isAfterLast() == false) {

                    SingleEntry entry = new SingleEntry();
                    entry.setCode(cursor.getString(cursor.getColumnIndex(AppString.milk.code)));
                    entry.setId(cursor.getString(cursor.getColumnIndex(AppString.milk.id)));
                    entry.setRate(cursor.getString(cursor.getColumnIndex(AppString.milk.rate)));
                    entry.setAmount(cursor.getString(cursor.getColumnIndex(AppString.milk.amount)));
                    entry.setSift(cursor.getString(cursor.getColumnIndex(AppString.milk.sift)));
                    entry.setDatesave(cursor.getString(cursor.getColumnIndex(AppString.milk.dateSave)));
                    entry.setDate(cursor.getString(cursor.getColumnIndex(AppString.milk.date)));
                    entry.setWeight(cursor.getString(cursor.getColumnIndex(AppString.milk.weight)));
                    entry.setFat(cursor.getString(cursor.getColumnIndex(AppString.milk.fat)));
                    entry.setSnf(cursor.getString(cursor.getColumnIndex(AppString.milk.snf)));
                    entry.setFatWt(cursor.getString(cursor.getColumnIndex(AppString.milk.fat_wt)));
                    entry.setSnfWt(cursor.getString(cursor.getColumnIndex(AppString.milk.snf_wt)));
                    DailyReportList.add(entry);

                    weightTotal = weightTotal + Float.valueOf(entry.getWeight());
                    amountTotal = amountTotal + Float.valueOf(entry.getAmount());
                    fat_wt = fat_wt + Float.valueOf(entry.getFatWt());


                    snf_wt = snf_wt + Float.valueOf(entry.getSnfWt());

                    message = message + "\n" + String.format("%s %-4s %-4s %-4s %-4s %-5s",
                            entry.getCode(),
                            entry.getWeight(),
                            entry.getfat(),
                            entry.getSnf(),
                            entry.getRate(),
                            entry.getAmount());


                    cursor.moveToNext();
                }
            } else {
                // Toast.makeText(getApplication(), "Not Found", Toast.LENGTH_LONG).show();
            }



            if (sift.equals("M")) {
                shifts = "Morning";
            } else {
                shifts = "Evening";
            }

            // headre
            String header = "Shift Report\n " +startdate+ "  "+shifts+ "\n" + MainActivity.lineBreak() ;
            String header2 = "Code Qty Fat " + MainActivity.getInstace().rateString() + "  Rate   AMT" ;


            message = header + header2 + message;

            // set data
            float avgFat = fat_wt / weightTotal;
            float avgSnf = snf_wt / weightTotal;

            daily_fat.setText(MainActivity.twoDecimalFloatToString(avgFat));
            daily_snf.setText(MainActivity.twoDecimalFloatToString(avgSnf));
            daily_wight.setText(MainActivity.twoDecimalFloatToString(weightTotal) + " Kg");
            daily_amut.setText(MainActivity.twoDecimalFloatToString(amountTotal) + "/-");


            String footer = MainActivity.lineBreak()
                    +"Total Weight  :   " + weightTotal + "\nAvarage Fat   :   " + MainActivity.twoDecimalFloatToString(avgFat) +
                    "\nAvarage SNF   :   " + MainActivity.twoDecimalFloatToString(avgSnf)+ "\nTotal Amount  :   " + MainActivity.twoDecimalFloatToString(amountTotal)  + "/-";

            message = message + "\n" + footer;

            SharedPreferencesUtils  sharedPreferencesUtils = new SharedPreferencesUtils(getActivity());
            tiltle = sharedPreferencesUtils.getTitle();
            message = tiltle + "\n" + message;


            fileName = "SessionReport-" + startDate + "-" + shifts + ".pdf";
            totalSummery = "Shift Report\n"+startdate+"  "+shifts+ "\n" + MainActivity.lineBreak() + "Total Weight  :   " + weightTotal + "\nAvarage Fat   :   " + MainActivity.twoDecimalFloatToString(avgFat) +
            "\nAvarage SNF   :   " + MainActivity.twoDecimalFloatToString(avgSnf)+ "\nTotal Amount  :   " + MainActivity.twoDecimalFloatToString(amountTotal)  + "/-";

            pdfDaily.setAmounts(weightTotal, avgFat, avgSnf, amountTotal);
            pdfDaily.setArray(DailyReportList);
            pdfDaily.setData(tiltle,startdate,"Session Report",shifts,fileName);


        } catch (Exception e) {
            Toast.makeText(getActivity(), "Not Found Any Data", Toast.LENGTH_LONG).show();
        }
    }

    private void getCalendarDate() {

        Calendar calendar = Calendar.getInstance();
        int am_pm = calendar.get(Calendar.AM_PM);
        if (am_pm == 0) {
            sift = "M";
            spinr_ampm.setSelection(0);

        } else {
            spinr_ampm.setSelection(1);
            sift = "E";
        }
        startDateView.setText(AppString.getCurrentDate());

        clickSearchButton();
    }



    private void shareDialog() {
        final CharSequence[] options = {"WhatsApp", "Mail", "Other Share", "Print Summery","Print Total Summery","Share Pdf"};
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Send Report");
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

                        waIntent.putExtra(Intent.EXTRA_TEXT, message);
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
                    email.putExtra(Intent.EXTRA_TEXT, message);
                    email.setType("message/rfc822");
                    startActivity(Intent.createChooser(email, "Choose an Email client"));

                } else if (options[item].equals("Other Share")) {

                    Intent sentIntent = new Intent(Intent.ACTION_SEND);
                    sentIntent.putExtra(Intent.EXTRA_TEXT, message);
                    sentIntent.setType("text/plain");
                    startActivity(sentIntent);
                }
                else if (options[item].equals("Print Summery")) {
                  print(message);


                }
                else if (options[item].equals("Print Total Summery")) {
                    if (totalSummery.length() > 0){
                        print(totalSummery);
                        Log.e("total",totalSummery);
                    }
                }

                else if (options[item].equals("Share Pdf")) {
                    instace.createSessionPdf(pdfDaily);
                }


            }
        });
        adb.show();
    }


    String getSummery(){

        if (DailyReportList.size()==0) {
            return "";
        }

        String titlename = MainActivity.getInstace().sharedPreferencesUtils.getTitle();

        String summery = titlename + "\nShift Report\n"+startDateView.getText()+"  "+shifts+ "\n" + MainActivity.lineBreak() +
                         "\nTotal Milk Weight : " + daily_wight.getText() +
                         "\nAvg Fat : " + daily_fat.getText() +
                         "\nAvg " + MainActivity.getInstace().rateString()+ ": " + daily_fat.getText() +
                         "\nTotal Amount : " + daily_amut.getText();


        return  summery;
    }


    private void print(String printString){
        MainActivity.getInstace().print(printString);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void uploadFile() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED  || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission is granted");

            requestPermissions(new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);                }
        else {
            if (instace.isNetworkConnected()){
                // MainActivity.getInstace().showLoading("Uploading..");
                new UploadFile().execute("");
            }else{
                // MainActivity.makeToast("internet not connected");
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0){
            if (instace.isNetworkConnected()){
                // MainActivity.getInstace().showLoading("Uploading..");
                new UploadFile().execute("");
            }else{
                // MainActivity.makeToast("internet not connected");
            }
        }else {
            uploadFile();
        }
    }




}
