package wts.com.npcs.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.npcs.R;
import wts.com.npcs.classes.MessageDialog;
import wts.com.npcs.databinding.ActivityElectricityBinding;
import wts.com.npcs.retrofit.RetrofitClient;

public class ElectricityActivity extends AppCompatActivity {
    ActivityElectricityBinding binding;

    ArrayList<String> operatorIdList, operatorNameList;
    String selectedOperatorId = "select";
    String userKey, mobileNo;
    SharedPreferences sharedPreferences;

    String responseConsumerNo, consumerName, billDate, dueDate, dueAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_electricity);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ElectricityActivity.this);
        userKey = sharedPreferences.getString("userKey", null);
        mobileNo = sharedPreferences.getString("mobileNo", null);

        getOperators();

        binding.fetchBillCard.setOnClickListener(v ->
        {
            if (!TextUtils.isEmpty(binding.etNumber.getText())) {
                if (!selectedOperatorId.equalsIgnoreCase("select")) {
                    fetchBill();
                } else {
                    Toast.makeText(ElectricityActivity.this, "Please select operator", Toast.LENGTH_SHORT).show();
                }
            } else {
                binding.etNumber.setError("Required");
            }
        });

    }

    private void fetchBill() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(ElectricityActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        String consumerNumber = binding.etNumber.getText().toString();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().fetchBill(userKey, selectedOperatorId, consumerNumber, mobileNo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("ResponseCode");
                        if (responseCode.equalsIgnoreCase("200")) {

                            JSONArray dataArray = responseObject.getJSONArray("ResponseData");
                            JSONObject dataObject = dataArray.getJSONObject(0);

                            responseConsumerNo = dataObject.getString("ConsumerNo");
                            consumerName = dataObject.getString("ConsumerName");
                            billDate = dataObject.getString("BillDate");
                            dueDate = dataObject.getString("DueDate");
                            dueAmount = dataObject.getString("DueAmount");

                            new AlertDialog.Builder(ElectricityActivity.this)
                                    .setTitle("Message")
                                    .setMessage("Consumer No -: " + consumerNumber
                                            + "\nConsumer Name -: " + consumerName +
                                            "\nBill Date -: " + billDate +
                                            "\nDue Date -: " + dueDate +
                                            "\nDue Amount -: " + dueAmount)
                                    .show();
                            pDialog.dismiss();
                        } else {
                            String message = responseObject.getString("ResponseMessage");
                            pDialog.dismiss();
                            MessageDialog.showMessageDialog(message, ElectricityActivity.this, ElectricityActivity.this);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        MessageDialog.showMessageDialog(e.getMessage(), ElectricityActivity.this, ElectricityActivity.this);
                    }
                } else {
                    pDialog.dismiss();
                    MessageDialog.showMessageDialog(response.message(), ElectricityActivity.this, ElectricityActivity.this);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                MessageDialog.showMessageDialog(t.getMessage(), ElectricityActivity.this, ElectricityActivity.this);
            }
        });

    }

    private void getOperators() {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(ElectricityActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getOperators("2009");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseJsonObject = new JSONObject(String.valueOf(response.body()));

                        operatorIdList = new ArrayList<>();
                        operatorNameList = new ArrayList<>();

                        JSONArray dataArray = responseJsonObject.getJSONArray("ResponseData");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataObject = dataArray.getJSONObject(i);

                            String operatorName = dataObject.getString("OperatorName");
                            operatorNameList.add(operatorName);
                            String operatorId = dataObject.getString("OperatorId");
                            operatorIdList.add(operatorId);

                        }

                        binding.operatorLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SpinnerDialog operatorDialog = new SpinnerDialog(ElectricityActivity.this, operatorNameList, "Select Operator", R.style.DialogAnimations_SmileWindow, "Close  ");// With 	Animation
                                operatorDialog.setCancellable(true); // for cancellable
                                operatorDialog.setShowKeyboard(false);// for open keyboard by default
                                operatorDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                                    @Override
                                    public void onClick(String item, int position) {
                                        binding.tvOperator.setText(item);
                                        selectedOperatorId = operatorIdList.get(position);

                                    }
                                });

                                operatorDialog.showSpinerDialog();
                            }
                        });


                        pDialog.dismiss();


                    } catch (JSONException e) {
                        pDialog.dismiss();

                        new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false)
                                .setTitle("Message")
                                .setMessage("Please try after some time.")
                                .setPositiveButton("Ok", (dialog, which) -> finish()).show();
                        e.printStackTrace();
                    }
                } else {
                    pDialog.dismiss();

                    new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false)
                            .setTitle("Message")
                            .setMessage("Please try after some time.")
                            .setPositiveButton("Ok", (dialog, which) -> finish()).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();

                new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false)
                        .setTitle("Message")
                        .setMessage("Please try after some time.")
                        .setPositiveButton("Ok", (dialog, which) -> finish()).show();
            }
        });
    }

}