package com.milk.milkcollectionapp.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.milk.milkcollectionapp.R;
import com.milk.milkcollectionapp.activity.MainActivity;

/**
 * Created by Er. Arjun on 21-02-2016.
 */
public class Fragment_Report extends Fragment {
    private Button btn_singlememberreport,btn_dailymemberreport,btn_pay_report,btn_sell_report,btn_socity_report,btnLossProfit,btn_cm_report;
    public TextView toolbartitle;
    public Fragment_Report() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);
        toolbartitle = (TextView)getActivity().findViewById(R.id.titletool);
        btn_singlememberreport=(Button)rootView.findViewById(R.id.btn_singlememberreport);
        btn_dailymemberreport=(Button)rootView.findViewById(R.id.btn_dailymemberreport);
        btn_pay_report=(Button)rootView.findViewById(R.id.btn_pay_report);
        btn_cm_report=(Button)rootView.findViewById(R.id.btn_cm_report);
        btn_sell_report=(Button)rootView.findViewById(R.id.btn_sell_report);
        btn_socity_report=(Button)rootView.findViewById(R.id.btn_socity_report);
        btnLossProfit=(Button)rootView.findViewById(R.id.btn_sell_loss);

        btn_singlememberreport.setOnClickListener(v -> {
            toolbartitle.setText(getResources().getString(R.string.rep_mem_lzr));
            LoadFragment(new Fragment_SingleReport(), "home");
        });

        btn_pay_report.setOnClickListener(v -> {
            toolbartitle.setText(getResources().getString(R.string.payment_report_text));
            LoadFragment(new FragmentPaymentReport(), "home");
        });

        btn_cm_report.setOnClickListener(v -> {
            toolbartitle.setText(getResources().getString(R.string.payment_report_text));
            LoadFragment(new FragmentCMPaymentReport(), "home");
        });


        btn_dailymemberreport.setOnClickListener(v -> {
            toolbartitle.setText(getResources().getString(R.string.rep_daily_report));
            LoadFragment(new Fragment_DailyReport(), "home");
        });


        btn_sell_report.setOnClickListener(v -> {
            toolbartitle.setText(getResources().getString(R.string.rep_sell_report));
            LoadFragment(new Fragment_CellReport(), "home");
        });


        btn_socity_report.setOnClickListener(v -> {
            toolbartitle.setText(getResources().getString(R.string.rep_socity_report));
            LoadFragment(new Fragment_SocietyReport(), "home");
        });

        btnLossProfit.setOnClickListener(v -> {
            toolbartitle.setText(getResources().getString(R.string.rep_lossprofit_report));
            LoadFragment(new ReportLossProfit(), "home");
        });

        toolbartitle.setText(getResources().getString(R.string.report));
        return rootView;
    }

    private void LoadFragment(Fragment fragment, String backstack){
        (new MainActivity()).ChangeIconToBack();
        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(backstack);
        ft.commit();
    }


}


