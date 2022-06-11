package wts.com.npcs.activities;

import static org.json.JSONObject.wrap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.npcs.R;
import wts.com.npcs.classes.MessageDialog;
import wts.com.npcs.databinding.ActivityAddMoneyBinding;
import wts.com.npcs.retrofit.RetrofitClient;

public class AddMoneyActivity extends AppCompatActivity {

    ActivityAddMoneyBinding binding;

    SharedPreferences sharedPreferences;
    String userKey, userName,mobileNo;
    String amount, appName;
    public static final int PAYMENT_REQUEST = 4400;
    static final String GOOGLE_PAY = "com.google.android.apps.nbu.paisa.user";
    static final String PAYTM = "net.one97.paytm";

    String uniqueId;
    String upiId;
    String request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_add_money);

        //////CHANGE COLOR OF STATUS BAR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(AddMoneyActivity.this, R.color.white));
        //////CHANGE COLOR OF STATUS BAR
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AddMoneyActivity.this);
        userKey = sharedPreferences.getString("userKey", null);
        userName = sharedPreferences.getString("name", null);
        mobileNo = sharedPreferences.getString("mobileNo", null);

        binding.tvUsername.setText(userName);

        getUpiId();
        getBalance();

        binding.imgGpay.setOnClickListener(v ->
        {
                amount = binding.etAmount.getText().toString();
                if (!amount.equalsIgnoreCase("")) {
                    appName = "Google Pay";
                    String packageName = GOOGLE_PAY;
                    insertUpiPaymentInfo(packageName);
                } else {
                    binding.etAmount.setError("Required");
                }

        });

        binding.imgPaytm.setOnClickListener(v ->
        {
                amount = binding.etAmount.getText().toString();
                if (!amount.equalsIgnoreCase("")) {
                    appName = "Pay Tm";
                    String packageName = PAYTM;
                    insertUpiPaymentInfo(packageName);
                } else {
                    binding.etAmount.setError("Required");
                }

        });

    }

    private void getUpiId() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(AddMoneyActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getUpiId(userKey);
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
                            upiId = dataObject.getString("UpiId");
                            pDialog.dismiss();
                        } else {
                            String message = responseObject.getString("data");
                            pDialog.dismiss();
                            new AlertDialog.Builder(AddMoneyActivity.this)
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(AddMoneyActivity.this)
                                .setMessage("Please try after sometime.")
                                .setCancelable(false)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(AddMoneyActivity.this)
                            .setMessage("Please try after sometime.")
                            .setCancelable(false)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(AddMoneyActivity.this)
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void getBalance() {


        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getBalance(userKey);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("ResponseCode");

                        if (responseCode.equalsIgnoreCase("200")) {
                            String userBalance = responseObject.getString("ResponseData");
                            binding.tvBalance.setText("₹ " + userBalance);
                        } else {
                            binding.tvBalance.setText("₹ 00.00");

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        binding.tvBalance.setText("₹ 00.00");

                    }
                } else {
                    binding.tvBalance.setText("₹ 00.00");

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                binding.tvBalance.setText("₹ 00.00");
            }
        });
    }

    private void insertUpiPaymentInfo(String packageName) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(AddMoneyActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().insertUpiTransaction(userKey,amount,mobileNo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("ResponseCode");
                        if (responseCode.equalsIgnoreCase("200")) {
                            pDialog.dismiss();
                            uniqueId = responseObject.getString("ResponseData");
                            payNow(packageName);
                        } else {
                            pDialog.dismiss();
                            String message=responseObject.getString("ResponseMessage");
                            MessageDialog.showMessageDialog(message,AddMoneyActivity.this,AddMoneyActivity.this);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        MessageDialog.showMessageDialog("Please try after sometime.",
                                AddMoneyActivity.this,AddMoneyActivity.this);
                    }
                } else {
                    pDialog.dismiss();
                    MessageDialog.showMessageDialog(response.message(),
                            AddMoneyActivity.this,AddMoneyActivity.this);                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                MessageDialog.showMessageDialog(t.getMessage(),
                        AddMoneyActivity.this,AddMoneyActivity.this);            }
        });

    }

    private void payNow(String packageName) {
        try {
            Uri uri = new Uri.Builder()
                    .scheme("upi")
                    .authority("pay")
                    .appendQueryParameter("pa", upiId)
                    .appendQueryParameter("pn", "CS Pay")
                    .appendQueryParameter("mc", "")
                    .appendQueryParameter("tr", uniqueId)
                    .appendQueryParameter("tn", "Top Up wallet")
                    .appendQueryParameter("am", amount)
                    .appendQueryParameter("cu", "INR")
                    .build();

            request=uri.toString();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(packageName);
            startActivityForResult(intent, PAYMENT_REQUEST);
        } catch (Exception e) {
            appNotInstall();
        }
    }

    @SuppressLint("SetTextI18n")
    private void appNotInstall() {
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(AddMoneyActivity.this).create();
        final LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.device_not_connected, null);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        AppCompatButton btnOK = convertView.findViewById(R.id.btn_ok);

        btnOK.setOnClickListener(v ->
        {
            alertDialog.dismiss();
        });

        alertDialog.setView(convertView);
        alertDialog.show();
        alertDialog.setCancelable(false);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYMENT_REQUEST) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                String response = data.getStringExtra("response");
                String responseData = getJson(bundle);
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("Status");
                    if (status.equalsIgnoreCase("Success")) {
                        String txnId = jsonObject.getString("txnId");
                        updateBalanceUPI("Success", txnId,response);
                    } else if (status.equalsIgnoreCase("Failed")) {
                        updateBalanceUPI("Failed", "NA",response);
                    } else {
                        updateBalanceUPI("Failed", "NA",response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getJson(final Bundle bundle) {
        if (bundle == null) return null;
        JSONObject jsonObject = new JSONObject();

        for (String key : bundle.keySet()) {
            Object obj = bundle.get(key);
            try {
                jsonObject.put(key, wrap(bundle.get(key)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    private void updateBalanceUPI(String status, String upiTxnId,String response) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().updateUpiTransaction(userKey,status,uniqueId,upiTxnId,
                request,response);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));

                        String message=jsonObject.getString("ResponseMessage");

                        new AlertDialog.Builder(AddMoneyActivity.this)
                                .setCancelable(false)
                                .setMessage(message)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).show();

                    } catch (Exception e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                        showSnackbar("Something went wrong");
                    }
                } else {
                    pDialog.dismiss();
                    showSnackbar("" + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                showSnackbar("Something went wrong");
            }
        });
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.add_money_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}