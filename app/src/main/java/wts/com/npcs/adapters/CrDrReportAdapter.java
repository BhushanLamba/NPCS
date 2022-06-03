package wts.com.npcs.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.npcs.R;
import wts.com.npcs.models.CreditDebitModel;

public class CrDrReportAdapter extends RecyclerView.Adapter<CrDrReportAdapter.CrDrViewHolder> {

    ArrayList<CreditDebitModel> creditDebitModelArrayList;
    Context context;

    public CrDrReportAdapter(ArrayList<CreditDebitModel> creditDebitModelArrayList, Context context) {
        this.creditDebitModelArrayList = creditDebitModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CrDrViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_credit_debit_report,parent,false);

        return new CrDrViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CrDrViewHolder holder, int position) {

        String name=creditDebitModelArrayList.get(position).getName();
        String transactionId=creditDebitModelArrayList.get(position).getTransactionId();
        String date=creditDebitModelArrayList.get(position).getDate();
        String time=creditDebitModelArrayList.get(position).getTime();
        String amount=creditDebitModelArrayList.get(position).getAmount();
        String serviceType=creditDebitModelArrayList.get(position).getServiceType();


        holder.tvName.setText(name);
        holder.tvTransactionId.setText(transactionId);
        holder.tvDateTime.setText(date+","+time);
        holder.tvAmount.setText("₹ "+amount);

        if (serviceType.equalsIgnoreCase("cr"))
        {
            holder.imgServiceType.setImageResource(R.drawable.credit);
            holder.tvAmount.setTextColor(context.getResources().getColor(R.color.green));
        }
        else if (serviceType.equalsIgnoreCase("dr"))
        {
            holder.imgServiceType.setImageResource(R.drawable.debit);
            holder.tvAmount.setTextColor(context.getResources().getColor(R.color.red));


        }

    }

    @Override
    public int getItemCount() {
        return creditDebitModelArrayList.size();
    }

    public static class CrDrViewHolder extends RecyclerView.ViewHolder{

        ImageView imgServiceType,imgStatus;
        TextView tvName,tvTransactionId,tvDateTime,tvAmount;
        public CrDrViewHolder(@NonNull View itemView) {
            super(itemView);

            imgServiceType=itemView.findViewById(R.id.img_service_type);
            imgStatus=itemView.findViewById(R.id.img_status);
            tvName=itemView.findViewById(R.id.tv_name);
            tvTransactionId=itemView.findViewById(R.id.tv_transaction_id);
            tvDateTime=itemView.findViewById(R.id.tv_date_time);
            tvAmount=itemView.findViewById(R.id.tv_amount);

        }
    }
}
