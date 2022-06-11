package wts.com.npcs.dashboardFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import soup.neumorphism.NeumorphCardView;
import wts.com.npcs.R;
import wts.com.npcs.activities.CreditDebitReportActivity;
import wts.com.npcs.activities.LedgerReportActivity;
import wts.com.npcs.activities.MyCommissionActivity;
import wts.com.npcs.activities.RechargeActivity;
import wts.com.npcs.activities.RechargeReportActivity;

public class ReportsFragment extends Fragment {

    NeumorphCardView prepaidReportLayout,creditReportLayout,debitReportLayout,myCommissionLayout,ledgerCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        initViews(view);

        prepaidReportLayout.setOnClickListener(v ->
        {
            Intent intent = new Intent(getContext(), RechargeReportActivity.class);
            startActivity(intent);
        });

        creditReportLayout.setOnClickListener(v->
        {
            Intent intent=new Intent(getContext(), CreditDebitReportActivity.class);
            intent.putExtra("service","cr");
            intent.putExtra("title","Credit Report");
            startActivity(intent);
        });

        debitReportLayout.setOnClickListener(v->
        {
            Intent intent=new Intent(getContext(), CreditDebitReportActivity.class);
            intent.putExtra("service","dr");
            intent.putExtra("title","Debit Report");
            startActivity(intent);
        });

        myCommissionLayout.setOnClickListener(v->
        {
            startActivity(new Intent(getContext(), MyCommissionActivity.class));
        });

        ledgerCard.setOnClickListener(v->
        {
            startActivity(new Intent(getContext(), LedgerReportActivity.class));
        });

        return view;
    }

    private void initViews(View view) {
        prepaidReportLayout = view.findViewById(R.id.prepaid_report_layout);
        creditReportLayout = view.findViewById(R.id.credit_report_layout);
        debitReportLayout = view.findViewById(R.id.debit_report_layout);
        myCommissionLayout = view.findViewById(R.id.my_commission_card);
        ledgerCard = view.findViewById(R.id.ledger_card);
    }
}