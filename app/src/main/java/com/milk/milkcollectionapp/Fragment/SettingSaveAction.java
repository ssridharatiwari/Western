package com.milk.milkcollectionapp.Fragment;

import android.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.milk.milkcollectionapp.Activity.MainActivity;
import com.milk.milkcollectionapp.R;
import com.milk.milkcollectionapp.helper.SharedPreferencesUtils;


public class SettingSaveAction extends Fragment {
    SharedPreferencesUtils sharedPreferencesUtils;

    public SettingSaveAction() {}
    Switch switchSms,switchSave,switchPrint ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.home_save, container, false);
        sharedPreferencesUtils = new SharedPreferencesUtils(getActivity());


        switchSms = (Switch)rootView.findViewById(R.id.switch_sms);
        switchSave = (Switch)rootView.findViewById(R.id.switch_save);
        switchPrint = (Switch)rootView.findViewById(R.id.switch_print);





        switchPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String statusSwitch1, statusSwitch2;
                if (switchPrint.isChecked()){
                    sharedPreferencesUtils.setSavePrint("1");
                    MainActivity.makeToast("on");
                }else{
                    sharedPreferencesUtils.setSavePrint("0");
                    MainActivity.makeToast("off");
                }

            }
        });
        switchSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String statusSwitch1, statusSwitch2;
                if (switchSms.isChecked()){
                    sharedPreferencesUtils.setSaveSms("1");
                    MainActivity.makeToast("on");
                }else{
                    sharedPreferencesUtils.setSaveSms("0");
                    MainActivity.makeToast("off");
                }

            }
        });

        if (sharedPreferencesUtils.getSavePrint().equals("1")){
            switchPrint.setChecked(true);
        }else{
            switchPrint.setChecked(false);
        }


        if (sharedPreferencesUtils.getSaveSms().equals("1")){
            switchSms.setChecked(true);
        }else{
            switchSms.setChecked(false);
        }

        return rootView;
    }


}

