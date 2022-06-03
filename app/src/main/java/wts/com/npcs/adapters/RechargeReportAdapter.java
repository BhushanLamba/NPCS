package wts.com.npcs.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.npcs.R;
import wts.com.npcs.activities.ShareReportActivity;
import wts.com.npcs.models.RechargeReportModel;

public class RechargeReportAdapter extends RecyclerView.Adapter<RechargeReportAdapter.MyReportViewHolder> {

    ArrayList<RechargeReportModel> rechargeReportModelArrayList;
    Context context;
    Activity activity;


    public RechargeReportAdapter(ArrayList<RechargeReportModel> rechargeReportModelArrayList, Context context, Activity activity) {
        this.rechargeReportModelArrayList = rechargeReportModelArrayList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recharge_report, parent, false);

        return new MyReportViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyReportViewHolder holder, int position) {
        String number = rechargeReportModelArrayList.get(position).getNumber();
        String amount = rechargeReportModelArrayList.get(position).getAmount();
        String status = rechargeReportModelArrayList.get(position).getStatus();
        String date = rechargeReportModelArrayList.get(position).getDate();
        String time = rechargeReportModelArrayList.get(position).getTime();


        holder.tvNumber.setText(number);
        holder.tvAmount.setText("₹ " + amount);
        holder.tvStatus.setText(status);
        holder.tvDateTime.setText(date + " " + time);

        holder.tvMore.setOnClickListener(v ->
        {
            Intent intent = new Intent(activity, ShareReportActivity.class);
            intent.putExtra("operator", rechargeReportModelArrayList.get(position).getOperator());
            intent.putExtra("number", rechargeReportModelArrayList.get(position).getNumber());
            intent.putExtra("orderId", rechargeReportModelArrayList.get(position).getOrderId());
            intent.putExtra("openingBalance", rechargeReportModelArrayList.get(position).getOpeningBalance());
            intent.putExtra("amount", rechargeReportModelArrayList.get(position).getAmount());
            intent.putExtra("commission", rechargeReportModelArrayList.get(position).getCommission());
            intent.putExtra("payableAmt", rechargeReportModelArrayList.get(position).getPayableAmt());
            intent.putExtra("closingBalance", rechargeReportModelArrayList.get(position).getClosingBalance());
            intent.putExtra("status", rechargeReportModelArrayList.get(position).getStatus());
            intent.putExtra("date", rechargeReportModelArrayList.get(position).getDate());
            intent.putExtra("time", rechargeReportModelArrayList.get(position).getTime());
            activity.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return rechargeReportModelArrayList.size();
    }

    public static class MyReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber, tvAmount, tvStatus, tvDateTime, tvMore;

        public MyReportViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNumber = itemView.findViewById(R.id.tv_number);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvMore = itemView.findViewById(R.id.tv_more);
        }
    }
}
