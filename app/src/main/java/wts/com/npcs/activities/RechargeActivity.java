package wts.com.npcs.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.npcs.R;
import wts.com.npcs.adapters.OperatorAdapter;
import wts.com.npcs.classes.MessageDialog;
import wts.com.npcs.databinding.ActivityRechargeBinding;
import wts.com.npcs.models.OperatorModel;
import wts.com.npcs.myInterface.OperatorInterface;
import wts.com.npcs.retrofit.RetrofitClient;

public class RechargeActivity extends AppCompatActivity {

    ActivityRechargeBinding binding;
    String service;
    Dialog operatorDialog;
    String selectedOperatorId,selectedOperatorName = "Select Operator";
    String userKey;
    SharedPreferences sharedPreferences;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recharge);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(RechargeActivity.this);
        userKey=sharedPreferences.getString("userKey",null);

        service = getIntent().getStringExtra("service");
        binding.tvTitle.setText(service+" Recharge");

        getService();

        binding.operatorLayout.setOnClickListener(view -> operatorDialog.show());

        binding.proceedCard.setOnClickListener(v->
        {
            if (checkInputs())
            {
                showRechargeConfirmationDialog();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void showRechargeConfirmationDialog() {
        final android.app.AlertDialog confirmationDialog = new android.app.AlertDialog.Builder(RechargeActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.recharge_confirmation_dialog, null);
        confirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        confirmationDialog.setView(convertView);
        confirmationDialog.setCancelable(false);
        confirmationDialog.show();

        AppCompatButton btnCancel=convertView.findViewById(R.id.btn_cancel);
        AppCompatButton btnProceed=convertView.findViewById(R.id.btn_proceed);
        TextView tvData=convertView.findViewById(R.id.tv_data);

        String number=binding.etNumber.getText().toString().trim();

        tvData.setText("Are you sure you want to recharge for "+number+" of "+ selectedOperatorName+" ?");

        btnCancel.setOnClickListener(v->
        {
            confirmationDialog.dismiss();
        });

        btnProceed.setOnClickListener(v->
        {
            doRecharge(number);
            confirmationDialog.dismiss();

        });

    }

    private void doRecharge(String number) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(RechargeActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();


        String amount=binding.etAmount.getText().toString().trim();
        Call<JsonObject> call=RetrofitClient.getInstance().getApi().doRecharge(userKey,amount,service,number,selectedOperatorId,"APP");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String responseCode=responseObject.getString("ResponseCode");

                        if (responseCode.equalsIgnoreCase("200"))
                        {
                            JSONArray responseDataArray= responseObject.getJSONArray("ResponseData");
                            JSONObject responseDataObject=responseDataArray.getJSONObject(0);

                            String operatorName=responseDataObject.getString("OperatorName");
                            String number=responseDataObject.getString("CustMobileNo");
                            String orderId=responseDataObject.getString("OrderId");
                            String openingBalance=responseDataObject.getString("OpeningBal");
                            String amount=responseDataObject.getString("Amount");
                            String commission=responseDataObject.getString("Commission");
                            String Tds=responseDataObject.getString("Tds");
                            String surcharge=responseDataObject.getString("Surcharge");
                            String gst=responseDataObject.getString("Gst");
                            String payableAmt=responseDataObject.getString("PayableAmt");
                            String closingBalance=responseDataObject.getString("ClosingBal");
                            String status=responseDataObject.getString("Status");
                            String dateTime=responseDataObject.getString("CreatedOn");

                            String outputDate="",outputTime="";

                            @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                            String[] splitDate = dateTime.split("T");
                            try {
                                Date date = inputDateFormat.parse(splitDate[0]);
                                Date time = simpleDateFormat.parse(splitDate[1]);
                                outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                outputTime = new SimpleDateFormat("hh:mm a").format(time);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Intent intent=new Intent(RechargeActivity.this,ShareReportActivity.class);
                            intent.putExtra("operator",operatorName);
                            intent.putExtra("number",number);
                            intent.putExtra("orderId",orderId);
                            intent.putExtra("openingBalance",openingBalance);
                            intent.putExtra("amount",amount);
                            intent.putExtra("commission",commission);
                            intent.putExtra("Tds",Tds);
                            intent.putExtra("surcharge",surcharge);
                            intent.putExtra("gst",gst);
                            intent.putExtra("payableAmt",payableAmt);
                            intent.putExtra("closingBalance",closingBalance);
                            intent.putExtra("status",status);
                            intent.putExtra("date",outputDate);
                            intent.putExtra("time",outputTime);

                            startActivity(intent);
                            pDialog.dismiss();

                            finish();


                        }
                        else
                        {
                            pDialog.dismiss();
                            String message=responseObject.getString("ResponseMessage");
                            MessageDialog.showMessageDialog(message,
                                    RechargeActivity.this,RechargeActivity.this);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        MessageDialog.showMessageDialog("Please try after sometime.",
                                RechargeActivity.this,RechargeActivity.this);
                    }
                }
                else
                {
                    pDialog.dismiss();
                    MessageDialog.showMessageDialog(response.message(),RechargeActivity.this,RechargeActivity.this);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                MessageDialog.showMessageDialog(t.getMessage(),RechargeActivity.this,RechargeActivity.this);
            }
        });

    }

    private void getService() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(RechargeActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getService();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statuscode = jsonObject1.getString("ResponseCode");

                        if (statuscode.equalsIgnoreCase("200")) {

                            JSONArray jsonArray = jsonObject1.getJSONArray("ResponseData");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String serviceid = jsonObject.getString("ServiceId");
                                String responseServiceName = jsonObject.getString("ServiceName");

                                if (responseServiceName.equalsIgnoreCase(service)) {

                                    pDialog.dismiss();
                                    getOperators(serviceid);
                                    break;
                                }
                            }
                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();

                            String errorMessage = jsonObject1.getString("ResponseMessage");

                            new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
                                    .setTitle("Message")
                                    .setMessage(errorMessage)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        pDialog.dismiss();

                        new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
                                .setTitle("Message")
                                .setMessage("Something went wrong.\nPlease try after sometime.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
                            .setTitle("Message")
                            .setMessage("Something went wrong.\nPlease try after sometime.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
                        .setTitle("Message")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();

            }
        });

    }

    private void getOperators(String serviceid) {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(RechargeActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getOperators(serviceid);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        JSONArray jsonArray = jsonObject1.getJSONArray("ResponseData");

                        ArrayList<OperatorModel> operatorModelArrayList = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            OperatorModel operatorModel = new OperatorModel();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);


                            String operatorName = jsonObject.getString("OperatorName");

                            String operatorId = jsonObject.getString("OperatorId");
                            String operatorImage = jsonObject.getString("Image");

                            operatorModel.setOperatorName(operatorName);
                            operatorModel.setOperatorId(operatorId);
                            operatorModel.setOperatorImg(operatorImage);

                            operatorModelArrayList.add(operatorModel);

                        }

                        operatorDialog = new Dialog(RechargeActivity.this, R.style.DialogTheme);
                        operatorDialog.setContentView(R.layout.operator_dialog);

                        int width = (int) (getResources().getDisplayMetrics().widthPixels * 1.0);
                        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);

                        operatorDialog.getWindow().setLayout(width, height);

                        operatorDialog.getWindow().setGravity(Gravity.BOTTOM);
                        operatorDialog.getWindow().setBackgroundDrawableResource(R.drawable.card_back_white);
                        operatorDialog.getWindow().setWindowAnimations(R.style.SlidingDialog);


                        RecyclerView rv = operatorDialog.findViewById(R.id.recyclerView);
                        ImageView cancelImg = operatorDialog.findViewById(R.id.cancelImg);
                        cancelImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                operatorDialog.dismiss();
                            }
                        });
                        OperatorAdapter recyclerViewItemAdapter = new OperatorAdapter(operatorModelArrayList, RechargeActivity.this);
                        rv.setLayoutManager(new LinearLayoutManager(RechargeActivity.this, RecyclerView.VERTICAL, false));
                        rv.setAdapter(recyclerViewItemAdapter);

                        recyclerViewItemAdapter.setMyInterface(new OperatorInterface() {
                            @Override
                            public void operatorData(String operatorName, String operatorId) {
                                operatorDialog.dismiss();
                                selectedOperatorId = operatorId;
                                selectedOperatorName = operatorName;

                                binding.tvOperator.setText(operatorName);

                            }
                        });

                        pDialog.dismiss();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(RechargeActivity.this).setTitle("Alert")
                                .setMessage("Something went wrong.")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                        e.printStackTrace();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(RechargeActivity.this).setTitle("Alert")
                            .setMessage("Something went wrong.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(RechargeActivity.this).setTitle("Failed")
                        .setCancelable(false)
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });

    }

    private boolean checkInputs() {

        if (service.equalsIgnoreCase("Prepaid") || service.equalsIgnoreCase("Postpaid")) {
            if (binding.etNumber.getText().toString().length() == 10) {
                if (!TextUtils.isEmpty(binding.etAmount.getText())) {
                    if (!selectedOperatorName.equalsIgnoreCase("Select Operator"))
                    return true;
                    else {
                        Toast.makeText(RechargeActivity.this, "Please select Operator", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    binding.etAmount.setError("Required");
                    return false;
                }
            } else {
                binding.etNumber.setError("Invalid Number");
                return false;
            }
        } else {
            if (!TextUtils.isEmpty(binding.etNumber.getText())) {
                if (!TextUtils.isEmpty(binding.etAmount.getText())) {
                    if (!selectedOperatorName.equalsIgnoreCase("Select Operator"))
                        return true;
                    else {
                        Toast.makeText(RechargeActivity.this, "Please select Operator", Toast.LENGTH_SHORT).show();
                        return false;
                    }                } else {
                    binding.etAmount.setError("Required");
                    return false;
                }
            } else {
                binding.etNumber.setError("Invalid");
                return false;
            }
        }

    }

}