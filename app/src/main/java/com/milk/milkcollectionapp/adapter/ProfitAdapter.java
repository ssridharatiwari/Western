package com.milk.milkcollectionapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.milk.milkcollectionapp.activity.MainActivity;
import com.milk.milkcollectionapp.R;
import com.milk.milkcollectionapp.model.LossReport;

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
            holder.txtweight.setText(MainActivity.oneDecimal(paymentReport.getWeight()));
            holder.txtamount.setText(MainActivity.oneDecimal(paymentReport.getAmount()));
            String date =  paymentReport.getDate().substring(0, 5)  + "-" + paymentReport.getShift();
            holder.textdate.setText(date);
            holder.txtSellWt.setText(MainActivity.oneDecimal(paymentReport.getSellwt()));
            holder.txtSellAmount.setText(MainActivity.twoDecimal(paymentReport.getSellAmout()) );
            holder.lossProfit.setText(MainActivity.oneDecimal(paymentReport.getLoss()));
            holder.lossProfitAmount.setText(MainActivity.twoDecimal(paymentReport.getLossProfitAmount()));
        }
        return convertView;
    }
}

