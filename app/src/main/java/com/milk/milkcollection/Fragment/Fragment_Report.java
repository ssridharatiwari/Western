package com.milk.milkcollection.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.milk.milkcollection.R;

/**
 * Created by Er. Arjun on 21-02-2016.
 */
public class Fragment_Report extends Fragment {

    private Button btn_singlememberreport,btn_dailymemberreport,btn_pay_report,btn_sell_report,btn_socity_report,btnLossProfit;
    public TextView toolbartitle;


    public Fragment_Report() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);
        toolbartitle = (TextView)getActivity().findViewById(R.id.titletool);
        btn_singlememberreport=(Button)rootView.findViewById(R.id.btn_singlememberreport);
        btn_dailymemberreport=(Button)rootView.findViewById(R.id.btn_dailymemberreport);
        btn_pay_report=(Button)rootView.findViewById(R.id.btn_pay_report);
        btn_sell_report=(Button)rootView.findViewById(R.id.btn_sell_report);
        btn_socity_report=(Button)rootView.findViewById(R.id.btn_socity_report);
        btnLossProfit=(Button)rootView.findViewById(R.id.btn_sell_loss);




        btn_singlememberreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // startActivity(new Intent(getActivity(), SearchActivity.class));
                toolbartitle.setText(getResources().getString(R.string.rep_mem_lzr));
                Fragment fragment = new Fragment_SingleReport();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack("home");
                ft.commit();
            }
        });



        btn_pay_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toolbartitle.setText(getResources().getString(R.string.payment_report_text));
                Fragment fragment = new FragmentPaymentReport();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack("home");
                ft.commit();
            }
        });

        btn_dailymemberreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbartitle.setText(getResources().getString(R.string.rep_daily_report));
                Fragment fragment = new Fragment_DailyReport();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack("home");
                ft.commit();
            }
        });


        btn_sell_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toolbartitle.setText(getResources().getString(R.string.rep_sell_report));
                Fragment fragment = new Fragment_CellReport();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack("home");
                ft.commit();
            }
        });


        btn_socity_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toolbartitle.setText(getResources().getString(R.string.rep_socity_report));
                Fragment fragment = new Fragment_SocietyReport();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack("home");
                ft.commit();
            }
        });

        btnLossProfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toolbartitle.setText(getResources().getString(R.string.rep_lossprofit_report));
                Fragment fragment = new ReportLossProfit();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack("home");
                ft.commit();
            }
        });

        toolbartitle.setText(getResources().getString(R.string.report));
        return rootView;
    }
}


