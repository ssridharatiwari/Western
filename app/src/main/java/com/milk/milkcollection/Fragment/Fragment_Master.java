package com.milk.milkcollection.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import com.milk.milkcollection.helper.SharedPreferencesUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;


public class Fragment_Master extends Fragment {

    private Button btn_showrate, btn_addrate, btn_showmember, btn_addmember,btn_import,send_mail,btn_excel,btn_export;
    public TextView toolbartitle;
    private String url;
    private String sqlitePath;
    MilkDBHelpers milkDBHelpers;
    private File backupDB;
    private LinearLayout lay_addrate,lay_showrate,lay_ratechart;

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
        btn_import=(Button)rootView.findViewById(R.id.btn_import);
        send_mail=(Button)rootView.findViewById(R.id.btn_mail);
        btn_export = (Button) rootView.findViewById(R.id.btn_export);
        btn_excel = (Button) rootView.findViewById(R.id.btn_excel);
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

                importDB(v);
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
                ft.commit();*/
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

                String db_name=milkDBHelpers.DATABASE_NAME;
                File sd = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                        File.separator + "MyData"+
                        File.separator );



                //Log.e("Export", String.valueOf(sd));
                boolean success = true;
                if (!sd.exists()) {
                    success = sd.mkdir();
                }
                if (success) {

                    File data = Environment.getDataDirectory();
                    FileChannel source=null;
                    FileChannel destination=null;
                    String currentDBPath = "/data/"+ getActivity().getPackageName() +"/databases/"+db_name;
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
                        emailIntent .setType("vnd.android.cursor.dir/email");
                        String to[] = {"kishorejangir@gmail.com"};
                        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
// the attachment
                        emailIntent .putExtra(Intent.EXTRA_STREAM, path);
// the mail subject
                        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));


                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        });
        btn_export.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             exportDB(v);
         }
     });


        return rootView;
    }

    public void exportDB(View v)
    {
        String db_name=milkDBHelpers.DATABASE_NAME;
        File sd = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                File.separator + "MyData"+
                File.separator );

        Log.e("Export", String.valueOf(sd));
        boolean success = true;
        if (!sd.exists()) {
            success = sd.mkdir();
        }
        if (success) {


            File data = Environment.getDataDirectory();
            FileChannel source=null;
            FileChannel destination=null;
            String currentDBPath = "/data/"+ getActivity().getPackageName() +"/databases/"+db_name;
            String backupDBPath = db_name;


            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(sd, backupDBPath);
            try {
                source = new FileInputStream(currentDB).getChannel();
                destination = new FileOutputStream(backupDB).getChannel();
                destination.transferFrom(source, 0, source.size());
                source.close();
                destination.close();
                Toast.makeText(getActivity(), "Please wait", Toast.LENGTH_SHORT).show();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void importDB(View v)
    {

        String db_name= milkDBHelpers.DATABASE_NAME;
          File sd = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                File.separator + "MyData"+ File.separator );


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
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(getActivity(), "Please wait", Toast.LENGTH_SHORT).show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void showHideMethod() {
        final SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(MainActivity.instace);
        String rateMethod =  sharedPreferencesUtils.getRateMethodCode();

        if (rateMethod.equals("1")){
            lay_ratechart.setVisibility(View.GONE);
            lay_addrate.setVisibility(View.VISIBLE);
            lay_showrate.setVisibility(View.VISIBLE);
        }else{
            lay_ratechart.setVisibility(View.VISIBLE);
            lay_addrate.setVisibility(View.GONE);
            lay_showrate.setVisibility(View.GONE);
        }
    }
}
