package wts.com.npcs.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.npcs.R;
import wts.com.npcs.adapters.MyPagerAdapter;
import wts.com.npcs.commissionFragments.DthFragment;
import wts.com.npcs.commissionFragments.MobileFragment;
import wts.com.npcs.commissionFragments.UtiFragment;
import wts.com.npcs.models.MyCommissionModel;
import wts.com.npcs.retrofit.RetrofitClient;

public class MyCommissionActivity extends AppCompatActivity {

    String userKey;
    SharedPreferences sharedPreferences;
    public static ArrayList<MyCommissionModel> mobileCommissionList, dthCommissionList, utiCommissionList;

    ViewPager viewPager;
    TabLayout tabLayout;

    MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_commission);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyCommissionActivity.this);
        userKey = sharedPreferences.getString("userKey", null);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        if (checkInternetState()) {
            getMyCommission();
        } else {
            showSnackbar();
        }


    }

    private void getMyCommission() {
        final AlertDialog pDialog = new AlertDialog.Builder(MyCommissionActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getCommissionSlab(userKey);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("ResponseCode");

                        if (responseCode.equalsIgnoreCase("200")) {
                            mobileCommissionList = new ArrayList<>();
                            dthCommissionList = new ArrayList<>();
                            utiCommissionList = new ArrayList<>();

                            myPagerAdapter.addFragment(new MobileFragment(), "Mobile");
                            myPagerAdapter.addFragment(new DthFragment(), "DTH");
                            myPagerAdapter.addFragment(new UtiFragment(), "UTI");

                            JSONArray transactionArray = responseObject.getJSONArray("ResponseData");
                            for (int i = 0; i < transactionArray.length(); i++) {

                                JSONObject transactionObject = transactionArray.getJSONObject(i);

                                String service = transactionObject.getString("ServiceName");
                                String operator = transactionObject.getString("OperatorName");
                                String commPer = transactionObject.getString("CommPer");
                                String chargePer = transactionObject.getString("ChargePer");


                                if (service.equalsIgnoreCase("Prepaid")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    mobileCommissionList.add(myCommissionModel);

                                }

                                if (service.equalsIgnoreCase("DTH")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    dthCommissionList.add(myCommissionModel);


                                }

                                if (service.equalsIgnoreCase("UTI Pan Card")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    utiCommissionList.add(myCommissionModel);

                                }
                                pDialog.dismiss();
                                viewPager.setAdapter(myPagerAdapter);
                                tabLayout.setupWithViewPager(viewPager);

                            }
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(MyCommissionActivity.this)
                                    .setMessage("Something went wrong.")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                        }


                    } catch (JSONException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                        new AlertDialog.Builder(MyCommissionActivity.this)
                                .setMessage("Something went wrong.")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(MyCommissionActivity.this)
                            .setMessage("Something went wrong.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(MyCommissionActivity.this)
                        .setMessage("Something went wrong.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
            }
        });
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.my_commission_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}