package com.milk.milkcollectionapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;

import com.milk.milkcollectionapp.activity.MainActivity;
import com.milk.milkcollectionapp.R;
import com.milk.milkcollectionapp.utils.SharedPreferencesUtils;


public class SecureSetiing extends Fragment {
    private static final int GALLERY_KITKAT_INTENT_CALLED = 0;

    SharedPreferencesUtils sharedPreferencesUtils;
    Switch switchManual,push_weight;
    EditText welcomeText,cumfund;
    public SecureSetiing() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.secure_setting, container, false);
        sharedPreferencesUtils = new SharedPreferencesUtils(getActivity());


        switchManual = (Switch)rootView.findViewById(R.id.switch_manual_rate);
        push_weight = (Switch)rootView.findViewById(R.id.push_weight);
        welcomeText = (EditText) rootView.findViewById(R.id.welcometext);
        cumfund = (EditText) rootView.findViewById(R.id.cm_fund_txt);

        welcomeText.setText(sharedPreferencesUtils.getWelcomeText());
        cumfund.setText(sharedPreferencesUtils.getCMF());

        welcomeText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (welcomeText.getText().length() > 0) {
                    sharedPreferencesUtils.setWelcomeText(welcomeText.getText().toString());
                } else{

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });




        cumfund.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (cumfund.getText().length() > 0) {
                    sharedPreferencesUtils.setCMF(cumfund.getText().toString());
                }else{
                    sharedPreferencesUtils.setCMF("0");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        switchManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchManual.isChecked()){
                    sharedPreferencesUtils.setManualRate(true);
                    MainActivity.makeToast("on");
                }else{
                    sharedPreferencesUtils.setManualRate(false);
                    MainActivity.makeToast("off");
                }
            }
        });

        push_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (push_weight.isChecked()){
                    sharedPreferencesUtils.setPushWeight(true);
                    MainActivity.makeToast("on");
                }else{
                    sharedPreferencesUtils.setPushWeight(false);
                    MainActivity.makeToast("off");
                }

                Log.e("push weight ", String.valueOf(sharedPreferencesUtils.isPushWeight()));

            }
        });


        if (sharedPreferencesUtils.isPushWeight()){
            push_weight.setChecked(true);
        }else{
            push_weight.setChecked(false);
        }



        if (sharedPreferencesUtils.isManualRate()){
            switchManual.setChecked(true);
        }else{
            switchManual.setChecked(false);
        }

        return rootView;
    }




}

