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
import wts.com.npcs.adapters.RechargeReportAdapter;
import wts.com.npcs.databinding.ActivityRechargeReportBinding;
import wts.com.npcs.databinding.ActivityShareReportBinding;
import wts.com.npcs.models.RechargeReportModel;
import wts.com.npcs.retrofit.RetrofitClient;

public class RechargeReportActivity extends AppCompatActivity {

    ActivityRechargeReportBinding binding;


    ArrayList<RechargeReportModel> rechargeReportModelArrayList;
    SharedPreferences sharedPreferences;
    String userKey;
    String toDate, fromDate;
    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    String operator,number,orderId,openingBalance,amount,commission,payableAmt,closingBalance,status,date,time;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_recharge_report);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RechargeReportActivity.this);
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
                DatePickerDialog fromDatePicker = new DatePickerDialog(RechargeReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                DatePickerDialog fromDatePicker = new DatePickerDialog(RechargeReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                new androidx.appcompat.app.AlertDialog.Builder(RechargeReportActivity.this).setMessage("Please select both From date and To Date")
                        .setPositiveButton("Ok", null).show();
            } else {
                dialog.dismiss();
                getReport();
            }
        });

        dialog.show();

    }

    private void getReport() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(RechargeReportActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getReport(userKey,fromDate,toDate,
                "","","","","","");
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


                            JSONArray jsonArray = jsonObject1.getJSONArray("ResponseData");
                            rechargeReportModelArrayList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                RechargeReportModel rechargeReportModel = new RechargeReportModel();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                operator=jsonObject.getString("OperatorName");
                                number=jsonObject.getString("CustMobileNo");
                                orderId=jsonObject.getString("OrderId");
                                openingBalance=jsonObject.getString("OpeningBal");
                                amount=jsonObject.getString("Amount");
                                commission=jsonObject.getString("Commission");
                                payableAmt=jsonObject.getString("PayableAmt");
                                closingBalance=jsonObject.getString("ClosingBal");
                                status=jsonObject.getString("Status");
                                date=jsonObject.getString("CreatedOn");

                                rechargeReportModel.setOperator(operator);
                                rechargeReportModel.setNumber(number);
                                rechargeReportModel.setOrderId(orderId);
                                rechargeReportModel.setOpeningBalance(openingBalance);
                                rechargeReportModel.setAmount(amount);
                                rechargeReportModel.setCommission(commission);
                                rechargeReportModel.setPayableAmt(payableAmt);
                                rechargeReportModel.setClosingBalance(closingBalance);
                                rechargeReportModel.setStatus(status);

                                @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String[] splitDate = date.split("T");
                                try {
                                    Date date = inputDateFormat.parse(splitDate[0]);
                                    Date time = simpleDateFormat.parse(splitDate[1]);
                                    @SuppressLint("SimpleDateFormat") String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                    @SuppressLint("SimpleDateFormat") String outputTime = new SimpleDateFormat("hh:mm a").format(time);

                                    rechargeReportModel.setDate(outputDate);
                                    rechargeReportModel.setTime(outputTime);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }




                                rechargeReportModelArrayList.add(rechargeReportModel);

                            }


                            binding.allReportRecycler.setLayoutManager(new LinearLayoutManager(RechargeReportActivity.this,
                                    RecyclerView.VERTICAL, false));

                            RechargeReportAdapter rechargeReportAdapter = new RechargeReportAdapter(rechargeReportModelArrayList,
                                    RechargeReportActivity.this, RechargeReportActivity.this);
                            binding.allReportRecycler.setAdapter(rechargeReportAdapter);
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