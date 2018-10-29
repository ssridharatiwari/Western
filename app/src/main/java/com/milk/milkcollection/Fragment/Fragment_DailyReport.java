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
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.milk.milkcollection.helper.DatePickerFragment;
import com.milk.milkcollection.helper.SharedPreferencesUtils;
import com.milk.milkcollection.helper.UploadFile;
import com.milk.milkcollection.model.DailyReport;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import static android.content.ContentValues.TAG;
import static com.milk.milkcollection.Activity.MainActivity.instace;

/**
 * Created by Alpha on 07-01-2016.
 */
public class Fragment_DailyReport extends Fragment {

    private TextView daily_wight, daily_fat, daily_snf, daily_amut,lbl_snf_dr;
    private ListView savedmilk_listview;
    private Button startDateView, btnsearch;
    private String sift = "";
    String message = "";
    ImageView iv_share;
    ArrayList<Integer> id_arrayList = new ArrayList<>();
    ArrayList<String> membercode_arrayList = new ArrayList<String>();
    ArrayList<Float> weight_arrayList = new ArrayList<>();
    ArrayList<Float> rate_arrayList = new ArrayList<>();
    ArrayList<Float> fat_arrayList = new ArrayList<>();
    ArrayList<Float> fatwt_arrayList = new ArrayList<>();
    ArrayList<Float> snf_arrayList = new ArrayList<>();
    ArrayList<Float> snfwt_arrayList = new ArrayList<>();
    ArrayList<Float> totalamount_arrayList = new ArrayList<>();
    ArrayList<String> date_arrayList = new ArrayList<String>();
    ArrayList<String> SetAllData_arrayList = new ArrayList<String>();
    ArrayList<String> GetAllData_arrayList = new ArrayList<>();
    ArrayList<DailyReport> DailyReportList = new ArrayList<>();
    ArrayList<String> dailyReportStringList = new ArrayList<>();
    ArrayList<String> numberList = new ArrayList<>();
    private DailyReportAdapter dailyReportAdapter;
    private int reportId = 0;
    Spinner spinr_ampm;
    String shifts;

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
                                switch (which) {
                                    case 0:
                                        String data = GetAllData_arrayList.get(position);
                                        MainActivity.sendWhatsApp(data);
                                        break;
                                    case 1:

                                        if(numberList.get(position)!="1234"){

                                            String strSms = GetAllData_arrayList.get(position);
                                            MainActivity.sendTextSms(strSms, numberList.get(position));

                                        }else {
                                            Toast.makeText(getActivity(), "Update Number And Try Again", Toast.LENGTH_LONG).show();
                                        }

                                        break;
                                    case 2:
                                        String shareText = GetAllData_arrayList.get(position);
                                        MainActivity.shareText(shareText);
                                        break;

                                    case 3:

                                        String dailyReport = dailyReportStringList.get(position);
                                        print(dailyReport);
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

        setTextsAccordingRate();
        getCalendarDate();
        uploadFile();
        return rootView;
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
        try {
            lbl_snf_dr.setText(MainActivity.instace.rateString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteReport( final int position){

        MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
        ArrayList<Integer> reportIdList = new ArrayList<>();
        reportIdList = milkDBHelpers.reportId();
        reportId = id_arrayList.get(position);

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
        GetAllData_arrayList.clear();
        DailyReportList.clear();
        SearchSqlData();

        sortByAtoZ();
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

        id_arrayList.clear();
        membercode_arrayList.clear();
        weight_arrayList.clear();
        rate_arrayList.clear();
        totalamount_arrayList.clear();
        date_arrayList.clear();
        dailyReportStringList.clear();
        GetAllData_arrayList.clear();
        fat_arrayList.clear();
        rate_arrayList.clear();
        snf_arrayList.clear();
        snfwt_arrayList.clear();
        fatwt_arrayList.clear();
        numberList.clear();
        date_arrayList.clear();

        String startdate = startDateView.getText().toString();
        String startDate = startdate.replace("/", "");
        //  Log.e("replace Daily", startDate+" ;"+startdate);

        String dd = startDate.substring(0, 2);
        String mm = startDate.substring(2, 4);
        String yy = startDate.substring(4, 8);
        startDate = yy + mm + dd;


        try {
            MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
            SQLiteDatabase sqLiteDatabase = milkDBHelpers.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(" SELECT * FROM 'milk_amount' WHERE sift = '" + sift + "' and date = '" + startDate + "' ORDER BY memberCode", null);

            if (cursor != null && cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {
                    membercode_arrayList.add(cursor.getString(cursor.getColumnIndex("memberCode")));
                    id_arrayList.add(cursor.getInt(cursor.getColumnIndex("Id")));
                    weight_arrayList.add(cursor.getFloat(cursor.getColumnIndex("milkweight")));
                    rate_arrayList.add(cursor.getFloat(cursor.getColumnIndex("rateperliter")));
                    fat_arrayList.add(cursor.getFloat(cursor.getColumnIndex("fat")));
                    fatwt_arrayList.add(cursor.getFloat(cursor.getColumnIndex("fat_wt")));
                    snf_arrayList.add(cursor.getFloat(cursor.getColumnIndex("snf")));
                    snfwt_arrayList.add(cursor.getFloat(cursor.getColumnIndex("snf_wt")));
                    totalamount_arrayList.add(cursor.getFloat(cursor.getColumnIndex("totalamount")));
                    date_arrayList.add(cursor.getString(cursor.getColumnIndex("dateSave")));
                    GetAllData_arrayList.add(cursor.getString(cursor.getColumnIndex("sift")));
                    // dailyReportStringList.add(cursor.getString(cursor.getColumnIndex("dailyInformation")));
                    numberList.add(cursor.getString(cursor.getColumnIndex("milkinformation")));
                    cursor.moveToNext();


                }
            } else {
                // Toast.makeText(getApplication(), "Not Found", Toast.LENGTH_LONG).show();
            }
            message = "";
            float weightTotal = 0;
            float amountTotal = 0;
            float fat_wt = 0;
            float snf_wt = 0;
            String alldata = "";

            for (int i = 0; i < weight_arrayList.size(); i++) {

                DailyReport dailyReport = new DailyReport();
                dailyReport.setCode(membercode_arrayList.get(i));
                dailyReport.setWeight(String.valueOf(weight_arrayList.get(i)));
                dailyReport.setFat(String.valueOf(fat_arrayList.get(i)));
                dailyReport.setSnf(String.valueOf(snf_arrayList.get(i)));
                dailyReport.setRate(String.valueOf(rate_arrayList.get(i)));
                dailyReport.setAmount(String.valueOf(totalamount_arrayList.get(i)));
                dailyReport.setId(String.valueOf(id_arrayList.get(i)));
                dailyReport.setDate(String.valueOf(date_arrayList.get(i)));
                dailyReport.setShift(String.valueOf(GetAllData_arrayList.get(i)));

                weightTotal = weightTotal + weight_arrayList.get(i);
                amountTotal = amountTotal + totalamount_arrayList.get(i);
                fat_wt = fat_wt + fatwt_arrayList.get(i);
                snf_wt = snf_wt + snfwt_arrayList.get(i);
                DailyReportList.add(dailyReport);

                //SetAllData_arrayList.add(GetAllData_arrayList.get(i));
                alldata = alldata + GetAllData_arrayList.get(i);

                String nwt =  weight_arrayList.get(i).toString();

                String nft =  fat_arrayList.get(i).toString();
                String nsnf =  snf_arrayList.get(i).toString();
                String nrt =  rate_arrayList.get(i).toString();

                message = message + "\n" + String.format("%s %-4s %-4s %-4s %-4s %-5s",membercode_arrayList.get(i),nwt,nft,nsnf,nrt,totalamount_arrayList.get(i));

            }

            if (sift.equals("M"))
            {
                shifts = "Morning";
            }
            else
            {
                shifts = "Evening";
            }

            message = "Shift Report\n"+startdate+"  "+shifts+ "\n" + MainActivity.lineBreak() + "Code Qty Fat "+MainActivity.instace.rateString()+"  Rate   AMT\n"+message;


            DecimalFormat df = new DecimalFormat("#.##");
            float avgFat = Float.valueOf(df.format(fat_wt / weightTotal));
            float avgSnf = Float.valueOf(df.format(snf_wt / weightTotal));

            daily_fat.setText(String.valueOf(avgFat));
            daily_snf.setText(String.valueOf(avgSnf));
            daily_wight.setText(MainActivity.twoDecimalFloatToString(weightTotal) + " Kg");
            daily_amut.setText(MainActivity.twoDecimalFloatToString(amountTotal) + "/-");

            message = message + "\n" + MainActivity.lineBreak()
                    +"Total Weight  :   " + weightTotal + "\nAvarage Fat   :   " + avgFat +
                    "\nAvarage SNF   :   " + avgSnf + "\nTotal Amount  :   " + amountTotal + "/-";


            /// title

            SharedPreferencesUtils  sharedPreferencesUtils = new SharedPreferencesUtils(getActivity());
            String titlename = sharedPreferencesUtils.getTitle();
            message = titlename + "\n" + message;


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

        MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
        startDateView.setText(milkDBHelpers.getCurrentDateFromPublic());

        clickSearchButton();
    }

    private void sortByAtoZ() {
        Collections.sort(DailyReportList, new Comparator<DailyReport>() {
            @Override
            public int compare(DailyReport lhs, DailyReport rhs) {
                return lhs.getCode().compareTo(rhs.getCode());
            }
        });

        for (int i = 0; i < DailyReportList.size(); i++) {
            Log.e("getCorde", DailyReportList.get(i).getCode() + "");
        }

    }


    private void shareDialog() {
        final CharSequence[] options = {"WhatsApp", "Mail", "Other Share", "Print Report"};
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Send Report");
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
                else if (options[item].equals("Print Report")) {

                  print(message);
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
