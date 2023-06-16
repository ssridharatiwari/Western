package com.milk.milkcollectionapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.milk.milkcollectionapp.activity.MainActivity;
import com.milk.milkcollectionapp.R;
import com.milk.milkcollectionapp.model.PaymentReport;

import java.util.List;

/**
 * Created by Er. Arjun on 06-03-2016.
 */
public class CMPaymentReportAdapter extends ArrayAdapter<PaymentReport> {
    Activity context;
    PaymentReport paymentReport;


    public CMPaymentReportAdapter(Activity context, int resourceId,
                                  List<PaymentReport> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView txtcode,txtweight,txt_cm_fund,txtamount, txt_dairy_amount;
    }



    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        paymentReport = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.cm_payment_report_listview, null);
            holder = new ViewHolder();
            holder.txtcode = (TextView) convertView.findViewById(R.id.lv_payment_code);
            holder.txtweight = (TextView) convertView.findViewById(R.id.lv_payment_weight);
            holder.txtamount = (TextView) convertView.findViewById(R.id.lv_payment_amount);
            holder.txt_cm_fund = (TextView) convertView.findViewById(R.id.lv_paymet_cm);
            holder.txt_dairy_amount=(TextView)convertView.findViewById(R.id.lv_payment_dairy_amount);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        if(paymentReport.getCode()!=null) {
            holder.txtcode.setText(paymentReport.getCode());

            holder.txt_cm_fund.setText(paymentReport.cmfund);
            holder.txt_dairy_amount.setText(paymentReport.getAmount());

            holder.txtweight.setText(MainActivity.twoDecimal(paymentReport.getWeight()) );
            holder.txtamount.setText(MainActivity.twoDecimal(paymentReport.total)  );
        }
        return convertView;
    }
}

