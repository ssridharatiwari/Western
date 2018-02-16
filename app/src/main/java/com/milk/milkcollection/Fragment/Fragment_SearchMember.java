package com.milk.milkcollection.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.milk.milkcollection.Database.MilkDBHelpers;
import com.milk.milkcollection.R;
import com.milk.milkcollection.adapter.ShowmemberAdapter;
import com.milk.milkcollection.adapter.SingleReportAdapter;
import com.milk.milkcollection.model.ShowMember;

import java.util.ArrayList;

import android.widget.TextView;



public class Fragment_SearchMember extends Fragment
{
    ListView memberList;
    ShowmemberAdapter dataAdapter;
    ArrayList<ShowMember> arraymemberList;

    public Fragment_SearchMember() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.searchmember_fragment, container, false);
        //\ memberList =(ListView)rootView.findViewById(R.id.memberlist);
        memberList = (ListView)rootView.findViewById(R.id.saved_listview);
        memberList.setEmptyView(rootView.findViewById(R.id.empty_saved_list));
        memberList.setOnCreateContextMenuListener(this);

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
        return rootView;
    }
}