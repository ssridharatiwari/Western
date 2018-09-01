package com.milk.milkcollection.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.milk.milkcollection.Activity.MainActivity;
import com.milk.milkcollection.R;
import com.milk.milkcollection.helper.FSSession;
import com.milk.milkcollection.model.PaymentReport;
import com.milk.milkcollection.model.SocietyReport;

import java.util.List;

/**
 * Created by Er. Arjun on 06-03-2016.
 */
public class SocietyReportAdapter extends ArrayAdapter<SocietyReport> {
    Activity context;
    SocietyReport paymentReport;


    public SocietyReportAdapter(Activity context, int resourceId,
                                List<SocietyReport> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView txtno,txtweight,txtamount, textdate,textCount;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        paymentReport = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adoptor_society_report, null);
            holder = new ViewHolder();
            holder.txtno = (TextView) convertView.findViewById(R.id.sra_no);
            holder.txtweight = (TextView) convertView.findViewById(R.id.sra_weight);
            holder.txtamount = (TextView) convertView.findViewById(R.id.sra_amount);
            holder.textdate=(TextView)convertView.findViewById(R.id.sra_date);
            holder.textCount=(TextView)convertView.findViewById(R.id.sra_count);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        if(paymentReport.getAmount()!=null) {
            holder.txtno.setText(String.valueOf(position+1));
            holder.textCount.setText(paymentReport.getCount());

            holder.txtweight.setText(paymentReport.getWeight());
            holder.txtamount.setText(paymentReport.getAmount());

            String date =  paymentReport.getDate().substring(0, 5)  + "-" + paymentReport.getShift();

            holder.textdate.setText(date);
        }
        return convertView;
    }
}

