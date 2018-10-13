package com.milk.milkcollection.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.milk.milkcollection.R;
import com.milk.milkcollection.model.LossReport;

import java.util.List;

public class ProfitAdapter extends ArrayAdapter<LossReport> {
    Activity context;
    LossReport paymentReport;

    public ProfitAdapter(Activity context, int resourceId,
                         List<LossReport> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {
        TextView txtno,txtweight,txtamount, textdate,txtSellWt,txtSellAmount,lossProfit,lossProfitAmount;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        paymentReport = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adoptor_lossprofit_report, null);
            holder = new ViewHolder();
            holder.txtno = (TextView) convertView.findViewById(R.id.sra_no);
            holder.txtweight = (TextView) convertView.findViewById(R.id.sra_weight);
            holder.txtamount = (TextView) convertView.findViewById(R.id.sra_amount);
            holder.textdate=(TextView)convertView.findViewById(R.id.sra_date);
            holder.txtSellWt=(TextView)convertView.findViewById(R.id.sell_wt);
            holder.txtSellAmount=(TextView)convertView.findViewById(R.id.sell_amount);
            holder.lossProfitAmount=(TextView)convertView.findViewById(R.id.loss_amount);
            holder.lossProfit=(TextView)convertView.findViewById(R.id.lossprofit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(paymentReport.getAmount()!=null) {
            holder.txtno.setText(String.valueOf(position+1));
            holder.txtweight.setText(paymentReport.getWeight());
            holder.txtamount.setText(paymentReport.getAmount());
            String date =  paymentReport.getDate().substring(0, 5)  + "-" + paymentReport.getShift();
            holder.textdate.setText(date);
            holder.txtSellWt.setText(paymentReport.getSellwt());
            holder.txtSellAmount.setText(paymentReport.getSellAmout());
            holder.lossProfit.setText(paymentReport.getLoss());
            holder.lossProfitAmount.setText(paymentReport.getLossProfitAmount());
        }
        return convertView;
    }
}

