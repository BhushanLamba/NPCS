package wts.com.npcs.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.os.Bundle;

import wts.com.npcs.R;
import wts.com.npcs.databinding.ActivityShareReportBinding;

public class ShareReportActivity extends AppCompatActivity {

    ActivityShareReportBinding binding;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_share_report);

        String operator=getIntent().getStringExtra("operator");
        String number=getIntent().getStringExtra("number");
        String orderId=getIntent().getStringExtra("orderId");
        String openingBalance=getIntent().getStringExtra("openingBalance");
        String amount=getIntent().getStringExtra("amount");
        String commission=getIntent().getStringExtra("commission");
        String tds=getIntent().getStringExtra("Tds");
        String surcharge=getIntent().getStringExtra("surcharge");
        String gst=getIntent().getStringExtra("gst");
        String payableAmt=getIntent().getStringExtra("payableAmt");
        String closingBalance=getIntent().getStringExtra("closingBalance");
        String status=getIntent().getStringExtra("status");
        String date=getIntent().getStringExtra("date");
        String time=getIntent().getStringExtra("time");


        binding.tvOperator.setText(operator);
        binding.tvNumber.setText(number);
        binding.tvOrderId.setText(orderId);
        binding.tvOpeningBal.setText("₹ "+openingBalance);
        binding.tvAmount.setText("₹ "+amount);
        binding.tvCommission.setText("₹ "+commission);
        binding.tvPayableAmount.setText("₹ "+payableAmt);
        binding.tvClosingBal.setText("₹ "+closingBalance);
        binding.tvStatus.setText(status);
        binding.tvDateTime.setText(date+" , "+time);

    }
}