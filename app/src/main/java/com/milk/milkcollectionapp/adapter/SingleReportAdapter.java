package com.milk.milkcollectionapp.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.milk.milkcollectionapp.activity.MainActivity;
import com.milk.milkcollectionapp.R;
import com.milk.milkcollectionapp.model.SingleEntry;

import java.util.ArrayList;

/**
 * Created by Er. Arjun on 06-03-2016.
 */
public class SingleReportAdapter extends ArrayAdapter<SingleEntry> {
    Activity context;
    SingleEntry singleReport;


    public SingleReportAdapter(Activity context, int resourceId,
                               ArrayList<SingleEntry> items) {
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
        if(singleReport.getDatesave()!=null) {

            Log.e("value",singleReport.getDatesave());

            holder.txtcode.setText(String.valueOf(position+1) + ".");
            holder.txtsift.setText(singleReport.getSift());
            holder.txtweight.setText(MainActivity.twoDecimal(singleReport.getWeight()));
            holder.txtrate.setText(MainActivity.oneDecimal(singleReport.getRate()));
            holder.txtamount.setText(MainActivity.twoDecimal(singleReport.getAmount()));
            holder.txtdate.setText(singleReport.getDatesave());
        }
        return convertView;
    }
}

