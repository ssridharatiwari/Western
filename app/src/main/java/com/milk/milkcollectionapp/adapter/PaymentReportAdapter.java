package com.milk.milkcollectionapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.milk.milkcollectionapp.Activity.MainActivity;
import com.milk.milkcollectionapp.R;
import com.milk.milkcollectionapp.model.PaymentReport;

import java.util.List;

/**
 * Created by Er. Arjun on 06-03-2016.
 */
public class PaymentReportAdapter extends ArrayAdapter<PaymentReport> {
    Activity context;
    PaymentReport paymentReport;


    public PaymentReportAdapter(Activity context, int resourceId,
                                List<PaymentReport> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView txtcode,txtweight,txtamount, textname;;
    }



    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        paymentReport = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.payment_report_listview, null);
            holder = new ViewHolder();
            holder.txtcode = (TextView) convertView.findViewById(R.id.lv_payment_code);
            holder.txtweight = (TextView) convertView.findViewById(R.id.lv_payment_weight);
            holder.txtamount = (TextView) convertView.findViewById(R.id.lv_payment_amount);
            holder.textname=(TextView)convertView.findViewById(R.id.lv_name);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        if(paymentReport.getCode()!=null) {
            holder.txtcode.setText(paymentReport.getCode());
            if (paymentReport.getName() == ""){
                holder.textname.setText(paymentReport.cmfund);
            }else{
                holder.textname.setText(paymentReport.getName());
            }
            holder.txtweight.setText(MainActivity.twoDecimal(paymentReport.getWeight()) );
            holder.txtamount.setText(MainActivity.twoDecimal(paymentReport.getAmount())  );
        }
        return convertView;
    }
}

