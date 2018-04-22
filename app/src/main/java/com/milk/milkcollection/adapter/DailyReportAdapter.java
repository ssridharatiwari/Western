package com.milk.milkcollection.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.milk.milkcollection.R;
import com.milk.milkcollection.model.DailyReport;
import java.util.List;

/**
 * Created by Er. Arjun on 02-03-2016.
 */
public class DailyReportAdapter  extends ArrayAdapter<DailyReport> {
    Activity context;
    DailyReport dailyReport;


    public DailyReportAdapter(Activity context, int resourceId,
                             List<DailyReport> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView txtcode,txtweight,txtfat,txtsnf,txtrate,txtamount,txtSno;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        dailyReport = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.dailyreport_listview, null);
            holder = new ViewHolder();
            holder.txtSno = (TextView) convertView.findViewById(R.id.lv_sno);

            holder.txtcode = (TextView) convertView.findViewById(R.id.lv_code);
            holder.txtweight = (TextView) convertView.findViewById(R.id.lv_weight);
            holder.txtfat = (TextView) convertView.findViewById(R.id.lv_fat);
            holder.txtsnf = (TextView) convertView.findViewById(R.id.lv_snf);
            holder.txtrate = (TextView) convertView.findViewById(R.id.lv_rate);
            holder.txtamount = (TextView) convertView.findViewById(R.id.lv_amount);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        if(dailyReport.getCode()!=null) {

            holder.txtSno.setText(String.valueOf(position+1)+ ".");
            holder.txtcode.setText(dailyReport.getCode());
            holder.txtweight.setText(dailyReport.getWeight());
            holder.txtfat.setText(dailyReport.getFat());
            holder.txtsnf.setText(dailyReport.getSnf());
            holder.txtrate.setText(dailyReport.getRate());
            holder.txtamount.setText(dailyReport.getAmount());
        }
        return convertView;
    }
}
