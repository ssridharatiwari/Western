package com.milk.milkcollection.Fragment;

import android.app.Fragment;
import android.content.ContentProvider;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.milk.milkcollection.R;
import com.milk.milkcollection.helper.SharedPreferencesUtils;

/**
 * Created by Alpha on 13-12-2015.
 */
public class Fragment_Setting extends Fragment {
    private static final int GALLERY_KITKAT_INTENT_CALLED = 0;
    private Button btn_rate_method,btn_select_print;
    private ContentProvider contentResolver;
    SharedPreferencesUtils sharedPreferencesUtils;

    TextView lblRate,lblPrint;
    public Fragment_Setting() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        sharedPreferencesUtils = new SharedPreferencesUtils(getActivity());


        btn_rate_method =(Button)rootView.findViewById(R.id.btn_rate_method);
        btn_select_print =(Button)rootView.findViewById(R.id.btn_select_print);
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
                    // builder.setMessage(messge);
                    // builder.setMessage("Now working with " + messge);
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


                String printBefore = sharedPreferencesUtils.getprintBy();
                String messge = "Change Rate Method";



                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setTitle(messge );
                // builder.setMessage(messge);
                // builder.setMessage("Now working with " + messge);
                builder.setItems(new CharSequence[]
                                {"Manual Fat and SNF Rate", "Fat Snf Rate Chart", "Fat Clr Rate Chart", "Cancel"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {
                                    case 0:
                                        sharedPreferencesUtils.applyRateMethod("1");
                                        updateData();

                                        break;
                                    case 1:
                                        sharedPreferencesUtils.applyRateMethod("2");
                                        updateData();
                                        break;
                                    case 2:
                                        sharedPreferencesUtils.applyRateMethod("3");
                                        updateData();
                                        break;
                                }
                            }
                        });
                builder.create().show();

            }

        });


        return rootView;
    }

    public void updateData(){

        lblRate.setText("Selected : " + sharedPreferencesUtils.getRateMethodText());
        lblPrint.setText("Selected : " + sharedPreferencesUtils.getprintByText());

    }

}
