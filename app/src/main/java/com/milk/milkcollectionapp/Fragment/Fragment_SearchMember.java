package com.milk.milkcollectionapp.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import com.milk.milkcollectionapp.Activity.MainActivity;
import com.milk.milkcollectionapp.Database.MilkDBHelpers;
import com.milk.milkcollectionapp.R;
import com.milk.milkcollectionapp.adapter.ShowmemberAdapter;
import com.milk.milkcollectionapp.model.Member;
import java.util.ArrayList;
import android.widget.TextView;
import android.widget.Toast;


public class Fragment_SearchMember extends Fragment
{
    ListView memberList;
    ShowmemberAdapter dataAdapter;
    ArrayList<Member> arraymemberList;
    ImageView imgShare;
    String printString = "";

    public Fragment_SearchMember() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.searchmember_fragment, container, false);
        memberList = (ListView)rootView.findViewById(R.id.saved_listview);
        memberList.setEmptyView(rootView.findViewById(R.id.empty_saved_list));
        memberList.setOnCreateContextMenuListener(this);
        imgShare =(ImageView)rootView.findViewById(R.id.imgShare);

        arraymemberList = new ArrayList<>();
        MilkDBHelpers milkDBHelpers = new MilkDBHelpers(getActivity());
        arraymemberList = milkDBHelpers.getMembers();

        Log.e(" arraymemberList ",arraymemberList+"");

        dataAdapter = new ShowmemberAdapter(getActivity(),arraymemberList )
        {
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {

                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setBackground(getResources().getDrawable(R.drawable.text_underline_spinner));
                return v;
            }
        };

        memberList.setAdapter(dataAdapter);


        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("Print String" , printString);

                if(arraymemberList.size() > 0){

                    MainActivity.getInstace().shareDialog(printString,"Member List");

                } else {
                    Toast.makeText(getActivity(), "Please Search Data First", Toast.LENGTH_LONG).show();
                }
            }
        });

        getPrintStrng();

        return rootView;
    }

    private void getPrintStrng() {

        printString = String.format("%4s %-10s %10s " , "Code" , "Name" , "Mobile");

        for (int i = 0; i< arraymemberList.size() ;i++) {
            Member member = arraymemberList.get(i);

            String name =  member.getName();
            String mobile =  member.getMobile();

            if (name.length() > 10){
                name = name.substring(0,10);
            }
            if (mobile.length() < 10){
                mobile = "--";
            }

            printString = printString + "\n" + String.format("%4s %-10s %-10s " , member.getCode() ,name, mobile);

        }

    }



}