package com.milk.milkcollection.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.milk.milkcollection.R;
import com.milk.milkcollection.model.ShowMember;

import java.util.ArrayList;

/**
 * Created by hp on 7/16/2016.
 */
public class ShowmemberAdapter extends BaseAdapter {
    Context context;
    ShowMember showMembermodel;
    private LayoutInflater mInflater;
    ArrayList<ShowMember> paymentReports;

    public ShowmemberAdapter(Context context,ArrayList<ShowMember> items) {

        this.context = context;
        this.paymentReports=items;
        mInflater = LayoutInflater.from(context);
    }




    /*private view holder class*/
    private class ViewHolder {
        TextView txtcode,txtweight,txtamount;;
    }

    @Override
    public int getCount() {
        return paymentReports.size();
    }

    @Override
    public Object getItem(int position) {
        return paymentReports.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ShowMember showMember = paymentReports.get(position);
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.searchmember_fragment, null);
            holder = new ViewHolder();
            holder.txtcode = (TextView) convertView.findViewById(R.id.lv_code);
            holder.txtweight = (TextView) convertView.findViewById(R.id.lv_name);
            holder.txtamount = (TextView) convertView.findViewById(R.id.lv_mobile);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();
            if(showMember.getMember_code()!=null) {
            holder.txtcode.setText(showMember.getMember_code());
            holder.txtweight.setText(showMember.getMember_name());
            holder.txtamount.setText(showMember.getMember_contact());
        }
        return convertView;
    }
}
