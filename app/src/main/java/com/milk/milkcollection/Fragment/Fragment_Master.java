package com.milk.milkcollection.Fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.milk.milkcollection.Activity.MainActivity;
import com.milk.milkcollection.Database.MilkDBHelpers;
import com.milk.milkcollection.R;
import com.milk.milkcollection.helper.DownloadFile;
import com.milk.milkcollection.helper.SharedPreferencesUtils;
import com.milk.milkcollection.helper.UploadFile;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;

import static android.content.ContentValues.TAG;
import static com.milk.milkcollection.Activity.MainActivity.*;


public class Fragment_Master extends Fragment {

    private Button btn_showrate, btn_addrate, btn_showmember, btn_addmember, btn_import, send_mail, btn_excel, btn_export, btn_preImport;
    public TextView toolbartitle;
    private String url;
    private String sqlitePath;
    MilkDBHelpers milkDBHelpers;
    private File backupDB;
    private LinearLayout lay_addrate, lay_showrate, lay_ratechart;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;

    public Fragment_Master() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_master, container, false);
        milkDBHelpers = new MilkDBHelpers(getActivity());
        toolbartitle = (TextView) getActivity().findViewById(R.id.titletool);
        btn_showrate = (Button) rootView.findViewById(R.id.btn_showrate);
        btn_addrate = (Button) rootView.findViewById(R.id.btn_addrate);
        btn_showmember = (Button) rootView.findViewById(R.id.btn_showmember);
        btn_addmember = (Button) rootView.findViewById(R.id.btn_addmember);
        btn_import = (Button) rootView.findViewById(R.id.btn_import);
        send_mail = (Button) rootView.findViewById(R.id.btn_mail);
        btn_export = (Button) rootView.findViewById(R.id.btn_export);
        btn_excel = (Button) rootView.findViewById(R.id.btn_excel);
        btn_preImport = (Button) rootView.findViewById(R.id.btn_preImport);
        lay_addrate = (LinearLayout) rootView.findViewById(R.id.lay_addrate);
        lay_ratechart = (LinearLayout) rootView.findViewById(R.id.lay_ratechart);
        lay_showrate = (LinearLayout) rootView.findViewById(R.id.lay_showrate);


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showHideMethod();
            }
        });


        btn_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.READ_PHONE_STATE)) {
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE},
                                MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    }
                } else {
                    askForImport();
                }
            }
        });

        toolbartitle.setText("Master");

        btn_addmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbartitle.setText(getResources().getString(R.string.mas_add_member));
                Fragment fragment = new Fragment_Addmember();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack("home");
                ft.commit();
            }
        });
        btn_showmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbartitle.setText(getResources().getString(R.string.mas_show_member));
                Fragment fragment = new Fragment_SearchMember();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack("home");
                ft.commit();

            }
        });
        btn_showrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(getActivity(), SearchActivity.class));
                toolbartitle.setText(getResources().getString(R.string.mas_show_rt));
                Fragment fragment = new Fragment_Searchfat();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack("home");
                ft.commit();
            }
        });

        btn_addrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toolbartitle.setText(getResources().getString(R.string.mas_add_rate));
                Fragment fragment = new Fragment_CreateBhav();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack("home");
                ft.commit();

                /* Fragment fragment = new Fragment_DailyReport();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack("home");
                ft.commit();  */
                // startActivity(new Intent(getActivity(), ShowAllMilkActivity.class));

            }
        });

        btn_excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbartitle.setText(getResources().getString(R.string.mas_add_member));
                Fragment fragment = new Fragment_Excel();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack("home");
                ft.commit();
            }
        });




        send_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String db_name = milkDBHelpers.DATABASE_NAME;
                File sd = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                        File.separator + "MyData" +
                        File.separator);


                //Log.e("Export", String.valueOf(sd));
                boolean success = true;
                if (!sd.exists()) {
                    success = sd.mkdir();
                }
                if (success) {

                    File data = Environment.getDataDirectory();
                    FileChannel source = null;
                    FileChannel destination = null;
                    String currentDBPath = "/data/" + getActivity().getPackageName() + "/databases/" + db_name;
                    String backupDBPath = db_name;
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);
                    try {
                        source = new FileInputStream(currentDB).getChannel();
                        destination = new FileOutputStream(backupDB).getChannel();
                        destination.transferFrom(source, 0, source.size());
                        source.close();
                        destination.close();
                        Uri path = Uri.fromFile(backupDB);
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
// set the type to 'email'
                        emailIntent.setType("vnd.android.cursor.dir/email");
                        String to[] = {"kishorejangir@gmail.com"};
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
// the attachment
                        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
// the mail subject
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        });
        btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForExport();
            }
        });


        return rootView;
    }

    int PERMISSION_REQUEST_CODE;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void exportDB() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED  || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission is granted");

            requestPermissions(new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);                }
        else {
            uploadFile();
        }



//        String db_name = milkDBHelpers.DATABASE_NAME ;
//        File sd = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
//                File.separator + "MyData" +
//                File.separator );
//
//        Log.e("Export", String.valueOf(sd));
//        boolean success = true;
//        if (!sd.exists()) {
//            success = sd.mkdir();
//        }
//        if (success) {
//
//            File data = Environment.getDataDirectory();
//            FileChannel source = null;
//            FileChannel destination = null;
//
//            String currentDBPath = "/data/" + getActivity().getPackageName() + "/databases/" + db_name;
//            String backupDBPath = "western"+".db";
//
//
//            File currentDB = new File(data, currentDBPath);
//            File backupDB = new File(sd, backupDBPath );
//
//            if (backupDB.exists()){
//
//            try {
//                source = new FileInputStream(currentDB).getChannel();
//                destination = new FileOutputStream(backupDB).getChannel();
//                destination.transferFrom(source, 0, source.size());
//                source.close();
//                destination.close();
//                 Toast.makeText(getActivity(), "Please wait", Toast.LENGTH_SHORT).show();
//
//               if (MainActivity.instace.isNetworkConnected()){
//                   //uploadFile();
//                   instace.showLoading("Uploading..");
//                   new UploadFileAsync().execute(backupDB.getPath());
//
//
//               }else{
//                   MainActivity.makeToast("Internet Connection not available");
//               }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            }
//        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE){
            uploadFile();
        }else {
            exportDB();
        }

    }


    private void askForImport() {

        new AlertDialog.Builder(getActivity()).setTitle("IMPORT")
                .setMessage("Do you want to import data from external storage")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        importDB();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }


    private void askForExport() {

        new AlertDialog.Builder(getActivity()).setTitle("")
                .setMessage("Make online Backup")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        exportDB();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        }).show();


    }


    public void importDB() {

        //preExportDB();

        downloadFile();


//        String db_name = milkDBHelpers.DATABASE_NAME;
//        File sd = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
//                File.separator + "MyData" + File.separator);
//
//        Log.e("ImportDb", String.valueOf(sd.getPath()));
//        Log.e("ImportDb", String.valueOf(sd));
//        File data = Environment.getDataDirectory();
//        FileChannel source = null;
//        FileChannel destination = null;
//
//        String backupDBPath = "/data/" + getActivity().getPackageName() + "/databases/" + db_name;
//        String currentDBPath = db_name;
//        File currentDB = new File(sd, currentDBPath);
//        File backupDB = new File(data, backupDBPath);
//        try {
//            source = new FileInputStream(currentDB).getChannel();
//            destination = new FileOutputStream(backupDB).getChannel();
//            destination.transferFrom(source, 0, source.size());
//            source.close();
//            destination.close();
//            Toast.makeText(getActivity(), "Please wait", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    private void showHideMethod() {

        final SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(instace);
        String rateMethod = sharedPreferencesUtils.getRateMethodCode();

        if (rateMethod.equals("1")) {
            lay_ratechart.setVisibility(View.GONE);
            lay_addrate.setVisibility(View.VISIBLE);
            lay_showrate.setVisibility(View.VISIBLE);
        } else {
            lay_ratechart.setVisibility(View.VISIBLE);
            lay_addrate.setVisibility(View.GONE);
            lay_showrate.setVisibility(View.GONE);
        }
    }


    private static void downloadFile() {

        instace.showLoading("Downloading..");

        SharedPreferencesUtils unit = new SharedPreferencesUtils(instace);
        String url =   "http://wokosoftware.com/western/uploads/" + unit.getUserID() + "/MyDBName";
        Log.e("url",url);

        new DownloadFile().execute(url);
    }

    public void uploadFile(){
        if (MainActivity.instace.isNetworkConnected()){
            //uploadFile();
            MainActivity.getInstace().showLoading("Uploading..");
            new UploadFile().execute();


        }else{
            MainActivity.makeToast("Internet Connection not available");
        }
    }


}

//