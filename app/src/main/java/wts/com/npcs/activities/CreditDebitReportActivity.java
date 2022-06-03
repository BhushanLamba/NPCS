package wts.com.npcs.activities;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import wts.com.npcs.databinding.ActivityCreditDebitReportBinding;
import wts.com.npcs.models.CreditDebitModel;
import wts.com.npcs.retrofit.RetrofitClient;

public class CreditDebitReportActivity extends AppCompatActivity {

    ActivityCreditDebitReportBinding binding;
    ArrayList<CreditDebitModel> creditDebitModelArrayList;
    SharedPreferences sharedPreferences;
    String userKey;
    String toDate, fromDate;
    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    String name, mobile, openingBalance, amount, closingBalance, transactionId, date, serviceType;
    String service, title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_credit_debit_report);

        service = getIntent().getStringExtra("service");
        title = getIntent().getStringExtra("title");

        binding.tvTitle.setText(title);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CreditDebitReportActivity.this);
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
                DatePickerDialog fromDatePicker = new DatePickerDialog(CreditDebitReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                DatePickerDialog fromDatePicker = new DatePickerDialog(CreditDebitReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                new androidx.appcompat.app.AlertDialog.Builder(CreditDebitReportActivity.this).setMessage("Please select both From date and To Date")
                        .setPositiveButton("Ok", null).show();
            } else {
                dialog.dismiss();
                getReport();
            }
        });

        dialog.show();

    }

    private void getReport() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(CreditDebitReportActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().crDrReport(userKey, fromDate, toDate, service);
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
                            creditDebitModelArrayList = new ArrayList<>();

                            for (int i = 0; i < dataArray.length(); i++) {
                                CreditDebitModel creditDebitModel = new CreditDebitModel();
                                JSONObject dataObject = dataArray.getJSONObject(i);

                                name = dataObject.getString("Name");
                                mobile = dataObject.getString("UserMobileNo");
                                openingBalance = dataObject.getString("OpeningBal");
                                amount = dataObject.getString("Amount");
                                closingBalance = dataObject.getString("ClosingBal");
                                transactionId = dataObject.getString("TransactionId");
                                date = dataObject.getString("CreatedOn");
                                serviceType = dataObject.getString("CrDrType");

                                creditDebitModel.setName(name);
                                creditDebitModel.setMobile(mobile);
                                creditDebitModel.setOpeningBalance(openingBalance);
                                creditDebitModel.setAmount(amount);
                                creditDebitModel.setClosingBalance(closingBalance);
                                creditDebitModel.setTransactionId(transactionId);
                                creditDebitModel.setServiceType(serviceType);

                                @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String[] splitDate = date.split("T");
                                try {
                                    Date date = inputDateFormat.parse(splitDate[0]);
                                    Date time = simpleDateFormat.parse(splitDate[1]);
                                    @SuppressLint("SimpleDateFormat") String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                    @SuppressLint("SimpleDateFormat") String outputTime = new SimpleDateFormat("hh:mm a").format(time);

                                    creditDebitModel.setDate(outputDate);
                                    creditDebitModel.setTime(outputTime);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                creditDebitModelArrayList.add(creditDebitModel);

                            }


                            binding.allReportRecycler.setLayoutManager(new LinearLayoutManager(CreditDebitReportActivity.this,
                                    RecyclerView.VERTICAL, false));

                            CrDrReportAdapter crDrReportAdapter = new CrDrReportAdapter(creditDebitModelArrayList, CreditDebitReportActivity.this);
                            binding.allReportRecycler.setAdapter(crDrReportAdapter);
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


}