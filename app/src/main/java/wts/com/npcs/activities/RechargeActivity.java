package wts.com.npcs.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.npcs.R;
import wts.com.npcs.adapters.OperatorAdapter;
import wts.com.npcs.databinding.ActivityRechargeBinding;
import wts.com.npcs.models.OperatorModel;
import wts.com.npcs.myInterface.OperatorInterface;
import wts.com.npcs.retrofit.RetrofitClient;

public class RechargeActivity extends AppCompatActivity {

    ActivityRechargeBinding binding;
    String service;
    Dialog operatorDialog;
    String selectedOperatorId,selectedOperatorName = "Select Operator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recharge);

        service = getIntent().getStringExtra("service");

        getService();

        binding.operatorLayout.setOnClickListener(view -> operatorDialog.show());

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


}