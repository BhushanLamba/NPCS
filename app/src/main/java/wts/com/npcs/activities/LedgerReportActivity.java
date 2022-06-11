package wts.com.npcs.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.npcs.R;
import wts.com.npcs.adapters.CrDrReportAdapter;
import wts.com.npcs.adapters.LedgerAdapter;
import wts.com.npcs.databinding.ActivityLedgerReportBinding;
import wts.com.npcs.models.CreditDebitModel;
import wts.com.npcs.models.LedgerModel;
import wts.com.npcs.retrofit.RetrofitClient;

public class LedgerReportActivity extends AppCompatActivity {

    ActivityLedgerReportBinding binding;
    ArrayList<LedgerModel> ledgerModelArrayList;
    SharedPreferences sharedPreferences;
    String userKey;
    String toDate, fromDate;
    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    String openingBal,amount,commission,tds,surcharge,gst,closingBalance,transactionId,transactionType,
            status,remarks,date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ledger_report);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LedgerReportActivity.this);
        userKey = sharedPreferences.getString("userKey", null);

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        Calendar fromCalendar = Calendar.getInstance();
        int year = fromCalendar.get(Calendar.YEAR);
        int month = fromCalendar.get(Calendar.MONTH);
        int day = fromCalendar.get(Calendar.DAY_OF_MONTH);

        Calendar newDate1 = Calendar.getInstance();
        newDate1.set(year, month, day);
        newDate1.add(Calendar.MONTH, -1);
        fromDate = webServiceDateFormat.format(newDate1.getTime());


        Calendar toCalendar = Calendar.getInstance();
        int toYear = toCalendar.get(Calendar.YEAR);
        int toMonth = toCalendar.get(Calendar.MONTH);
        int toDay = toCalendar.get(Calendar.DAY_OF_MONTH);
        Calendar newDate2 = Calendar.getInstance();
        newDate2.set(toYear, toMonth, toDay);
        toDate = webServiceDateFormat.format(newDate2.getTime());


        getReport();

        binding.imgFilter.setOnClickListener(v ->
        {
            showBottomDateDialog();
        });

    }

    private void getReport() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(LedgerReportActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getLedger(userKey, fromDate, toDate, "");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {

                        jsonObject1 = new JSONObject(String.valueOf(response.body()));
                        String statusCode = jsonObject1.getString("ResponseCode");

                        if (statusCode.equalsIgnoreCase("200")) {
                            binding.allReportRecycler.setVisibility(View.VISIBLE);
                            binding.imgNoDataFound.setVisibility(View.GONE);


                            JSONArray dataArray = jsonObject1.getJSONArray("ResponseData");
                            ledgerModelArrayList = new ArrayList<>();

                            for (int i = 0; i < dataArray.length(); i++) {
                                LedgerModel ledgerModel = new LedgerModel();
                                JSONObject dataObject = dataArray.getJSONObject(i);

                                openingBal=dataObject.getString("OpeningBal");
                                amount=dataObject.getString("Amount");
                                commission=dataObject.getString("Commission");
                                tds=dataObject.getString("Tds");
                                surcharge=dataObject.getString("Surcharge");
                                gst=dataObject.getString("Gst");
                                closingBalance=dataObject.getString("ClosingBal");
                                transactionId=dataObject.getString("TransactionId");
                                transactionType=dataObject.getString("TransactionType");
                                status=dataObject.getString("Status");
                                remarks=dataObject.getString("Remarks");
                                date=dataObject.getString("CreatedOn");

                                ledgerModel.setOpeningBal(openingBal);
                                ledgerModel.setAmount(amount);
                                ledgerModel.setCommission(commission);
                                ledgerModel.setTds(tds);
                                ledgerModel.setSurcharge(surcharge);
                                ledgerModel.setGst(gst);
                                ledgerModel.setClosingBalance(closingBalance);
                                ledgerModel.setTransactionId(transactionId);
                                ledgerModel.setTransactionType(transactionType);
                                ledgerModel.setStatus(status);
                                ledgerModel.setRemarks(remarks);

                                @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String[] splitDate = date.split("T");
                                try {
                                    Date date = inputDateFormat.parse(splitDate[0]);
                                    Date time = simpleDateFormat.parse(splitDate[1]);
                                    @SuppressLint("SimpleDateFormat") String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                    @SuppressLint("SimpleDateFormat") String outputTime = new SimpleDateFormat("hh:mm a").format(time);

                                    ledgerModel.setDate(outputDate);
                                    ledgerModel.setTime(outputTime);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                ledgerModelArrayList.add(ledgerModel);

                            }


                            binding.allReportRecycler.setLayoutManager(new LinearLayoutManager(LedgerReportActivity.this,
                                    RecyclerView.VERTICAL, false));

                            LedgerAdapter ledgerAdapter = new LedgerAdapter(ledgerModelArrayList);
                            binding.allReportRecycler.setAdapter(ledgerAdapter);
                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            binding.imgNoDataFound.setVisibility(View.VISIBLE);
                            binding.allReportRecycler.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        binding.imgNoDataFound.setVisibility(View.VISIBLE);
                        binding.allReportRecycler.setVisibility(View.GONE);
                    }
                } else {
                    pDialog.dismiss();
                    binding.imgNoDataFound.setVisibility(View.VISIBLE);
                    binding.allReportRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                binding.imgNoDataFound.setVisibility(View.VISIBLE);
                binding.allReportRecycler.setVisibility(View.GONE);
            }
        });

    }


    private void showBottomDateDialog() {
        View view = getLayoutInflater().inflate(R.layout.filter_view_bottom, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        Button filterBtn = view.findViewById(R.id.filterBtn);
        TextView tvFromDate = view.findViewById(R.id.tv_from_date);
        TextView tvToDate = view.findViewById(R.id.tv_to_date);
        LinearLayout fromDateLayout = view.findViewById(R.id.from_date_layout);
        LinearLayout toDateLayout = view.findViewById(R.id.to_date_layout);

        fromDateLayout.setOnClickListener(new View.OnClickListener() {
            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                DatePickerDialog fromDatePicker = new DatePickerDialog(LedgerReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate1 = Calendar.getInstance();
                        newDate1.set(year, month, dayOfMonth);

                        tvFromDate.setText(simpleDateFormat.format(newDate1.getTime()));
                        fromDate = webServiceDateFormat.format(newDate1.getTime());

                    }
                }, year, month, day);

                fromDatePicker.show();

            }
        });
        toDateLayout.setOnClickListener(new View.OnClickListener() {
            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                DatePickerDialog fromDatePicker = new DatePickerDialog(LedgerReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate1 = Calendar.getInstance();
                        newDate1.set(year, month, dayOfMonth);
                        tvToDate.setText(simpleDateFormat.format(newDate1.getTime()));
                        toDate = webServiceDateFormat.format(newDate1.getTime());
                    }
                }, year, month, day);

                fromDatePicker.show();

            }
        });

        filterBtn.setOnClickListener(v -> {
            if (tvFromDate.getText().toString().equalsIgnoreCase("Select Date") ||
                    tvToDate.getText().toString().equalsIgnoreCase("Select Date")) {
                new androidx.appcompat.app.AlertDialog.Builder(LedgerReportActivity.this).setMessage("Please select both From date and To Date")
                        .setPositiveButton("Ok", null).show();
            } else {
                dialog.dismiss();
                getReport();
            }
        });

        dialog.show();

    }

}