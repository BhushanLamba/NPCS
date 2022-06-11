package wts.com.npcs.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.npcs.R;
import wts.com.npcs.models.LedgerModel;

public class LedgerAdapter extends RecyclerView.Adapter<LedgerAdapter.LedgerViewHolder> {

    ArrayList<LedgerModel> ledgerModelArrayList;

    public LedgerAdapter(ArrayList<LedgerModel> ledgerModelArrayList) {
        this.ledgerModelArrayList = ledgerModelArrayList;
    }

    @NonNull
    @Override
    public LedgerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ledger_report,parent,false);

        return new LedgerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LedgerViewHolder holder, int position) {
        String transactionId=ledgerModelArrayList.get(position).getTransactionId();
        String date=ledgerModelArrayList.get(position).getDate();
        String time=ledgerModelArrayList.get(position).getTime();
        String amount=ledgerModelArrayList.get(position).getAmount();
        String status=ledgerModelArrayList.get(position).getStatus();


        holder.tvTransactionId.setText(transactionId);
        holder.tvDateTime.setText(date+","+time);
        holder.tvAmount.setText("₹ "+amount);

        if (status.equalsIgnoreCase("SUCCESS"))
        {
            holder.imgStatus.setImageResource(R.drawable.success);
        }
        else
        {
            holder.imgStatus.setImageResource(R.drawable.failed);
        }

    }

    @Override
    public int getItemCount() {
        return ledgerModelArrayList.size();
    }

    public static class LedgerViewHolder extends RecyclerView.ViewHolder{
        TextView tvTransactionId,tvDateTime,tvAmount;
        ImageView imgStatus;

        public LedgerViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTransactionId=itemView.findViewById(R.id.tv_transaction_id);
            tvDateTime=itemView.findViewById(R.id.tv_date_time);
            tvAmount=itemView.findViewById(R.id.tv_amount);
            imgStatus=itemView.findViewById(R.id.img_status);

        }
    }
}
