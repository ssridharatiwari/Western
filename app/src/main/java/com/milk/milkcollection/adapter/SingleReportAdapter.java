package com.milk.milkcollection.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.milk.milkcollection.R;
import com.milk.milkcollection.model.ShowMember;
import com.milk.milkcollection.model.SingleReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Er. Arjun on 06-03-2016.
 */
public class SingleReportAdapter extends ArrayAdapter<SingleReport> {
    Activity context;
    SingleReport singleReport;


    public SingleReportAdapter(Activity context, int resourceId,
                               ArrayList<SingleReport> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView txtcode,txtdate,txtsift,txtweight,txtrate,txtamount;;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        singleReport = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.single_member_report, null);
            holder = new ViewHolder();
            holder.txtcode = (TextView) convertView.findViewById(R.id.lv_code);
            holder.txtdate = (TextView) convertView.findViewById(R.id.lv_date);
            holder.txtsift = (TextView) convertView.findViewById(R.id.lv_sift);
            holder.txtweight = (TextView) convertView.findViewById(R.id.lv_weigh);
            holder.txtrate = (TextView) convertView.findViewById(R.id.lv_rateltr);
            holder.txtamount = (TextView) convertView.findViewById(R.id.amount);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        if(singleReport.getDate()!=null) {
            holder.txtcode.setText(singleReport.getCode());
            holder.txtdate.setText(singleReport.getDate());
            holder.txtsift.setText(singleReport.getSift());
            holder.txtweight.setText(singleReport.getWeight());
            holder.txtrate.setText(singleReport.getRate());
            holder.txtamount.setText(singleReport.getAmount());
        }
        return convertView;
    }
}

