package com.milk.milkcollection.Fragment;

import android.Manifest;
import android.Manifest.permission;
import android.app.Fragment;
import android.content.ContentProvider;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.milk.milkcollection.Activity.MainActivity;
import com.milk.milkcollection.Database.MilkDBHelpers;
import com.milk.milkcollection.R;
import com.milk.milkcollection.helper.SharedPreferencesUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import static android.content.ContentValues.TAG;

/**
 * Created by Alpha on 13-12-2015.
 */
public class Fragment_Setting extends Fragment {
    private static final int GALLERY_KITKAT_INTENT_CALLED = 0;
    private Button btn_rate_method,btn_select_print,btn_select_old_chart;
    private ContentProvider contentResolver;
    SharedPreferencesUtils sharedPreferencesUtils;
    int PERMISSION_REQUEST_CODE = 1;
    TextView lblRate,lblPrint;
    public Fragment_Setting() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        sharedPreferencesUtils = new SharedPreferencesUtils(getActivity());


        btn_rate_method =(Button)rootView.findViewById(R.id.btn_rate_method);
        btn_select_print =(Button)rootView.findViewById(R.id.btn_select_print);
        btn_select_old_chart =(Button)rootView.findViewById(R.id.btn_select_old_chart);
        lblRate = (TextView) rootView.findViewById(R.id.txtRate);
        lblPrint = (TextView) rootView.findViewById(R.id.txtPrint);

        lblRate.setText("Selected : " + sharedPreferencesUtils.getRateMethodText());
        lblPrint.setText("Selected : " + sharedPreferencesUtils.getprintByText());

        btn_select_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    String printBefore = sharedPreferencesUtils.getprintBy();
                    String messge = "";

                    if (printBefore.equals("wifi"))
                        messge = "Derect Wifi Printer";
                    if (printBefore.equals("blutooth"))
                        messge = "Blutooth Printer App";
                    if (printBefore.equals("pos"))
                        messge = "Pos App";

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                    builder.setTitle(messge + " Activeted" );
                    builder.setItems(new CharSequence[]
                                    {"Direct Wift", "Blutooth Printer App", "Pos App", "Cancel"},
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // The 'which' argument contains the index position
                                    // of the selected item
                                    switch (which) {
                                        case 0:

                                            sharedPreferencesUtils.printBy("wifi");
                                            updateData();
                                            break;
                                        case 1:
                                            updateData();
                                            sharedPreferencesUtils.printBy("blutooth");
                                            updateData();
                                            break;
                                        case 2:
                                            sharedPreferencesUtils.printBy("pos");
                                            updateData();
                                            break;
                                    }
                                }
                            });
                    builder.create().show();
                }

        });


        btn_rate_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messge = "Change Rate Method";

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setTitle(messge );
                builder.setItems(new CharSequence[]
                                {"Manual Fat and SNF Rate", "Fat Snf Rate Chart", "Fat Clr Rate Chart", "Cancel"},
                        new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        sharedPreferencesUtils.applyRateMethod("1");
                                        updateData();

                                        break;
                                    case 1:
                                        fetchCharts();
                                        sharedPreferencesUtils.applyRateMethod("2");
                                        updateData();
                                        break;
                                    case 2:

                                        fetchCharts();

                                        sharedPreferencesUtils.applyRateMethod("3");
                                        updateData();
                                        break;
                                }
                            }
                        });
                builder.create().show();

            }

        });



        btn_select_old_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String messge = "Are you sure want to fetch old default FAT-SNF and FAT-CLR  rate Chart";


                new AlertDialog.Builder(getActivity()).setTitle("Fetch Default Chart")
                        .setMessage(messge)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                try {
                                    getData();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                         }).setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface diaclog, int which) {

                            }
                }).show();


            }

        });





        return rootView;
    }

    public void updateData(){
        lblRate.setText("Selected : " + sharedPreferencesUtils.getRateMethodText());
        lblPrint.setText("Selected : " + sharedPreferencesUtils.getprintByText());
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fetchCharts()  {
        if (sharedPreferencesUtils.getImported().equals("1")){
            return;
        }
        try {
            getData();
        }catch (IOException e){

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getData() throws IOException {

        Log.e("get data method",lblRate.getText().toString());




        try {

            if (ActivityCompat.checkSelfPermission(getActivity(), permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED  || ActivityCompat.checkSelfPermission(getActivity(), permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {


                requestPermissions(new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);

                return ;

            }else {
                InputStream in = getResources().openRawResource(R.raw.mydb);

                FileOutputStream out = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                        File.separator + "MyData"+ File.separator  + "MyDBName");
                byte[] buff = new byte[1024];
                int read = 0;

                try {
                    while ((read = in.read(buff)) > 0) {
                        out.write(buff, 0, read);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    in.close();
                    out.close();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            importDB();
                        }
                    }, 100);
                }

            }





        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            try {
                getData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void importDB()  {

        MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());

        String db_name = milkDBHelpers.DATABASE_NAME;
        File sd = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                File.separator + "MyData"+ File.separator  );

        Log.e("ImportDb", String.valueOf(sd.getPath()));
        Log.e("ImportDb", String.valueOf(sd));
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;

        String backupDBPath = "/data/"+ getActivity().getPackageName() +"/databases/"+db_name;
        String currentDBPath = db_name;
        File currentDB = new File(sd, currentDBPath);
        File backupDB = new File(data, backupDBPath);
        try {

            sharedPreferencesUtils.setImported();
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(getActivity(), "Importing Charts", Toast.LENGTH_SHORT).show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
