package wts.com.npcs.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import soup.neumorphism.NeumorphCardView;
import wts.com.npcs.R;
import wts.com.npcs.dashboardFragments.HomeFragment;
import wts.com.npcs.databinding.ActivityLoginBinding;
import wts.com.npcs.retrofit.RetrofitClient;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_login);

        binding.loginCard.setOnClickListener(v->
        {
            if (!TextUtils.isEmpty(binding.etUserName.getText()))
            {
                if (!TextUtils.isEmpty(binding.etPassword.getText()))
                {
                    loginUser();
                }
                else
                {
                    binding.etPassword.setError("Required");
                }
            }
            else 
            {
                binding.etUserName.setError("Required");
            }
        });

    }

    private void loginUser() {
        final AlertDialog pDialog = new AlertDialog.Builder(LoginActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        String loginUserName = binding.etUserName.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().loginUser(loginUserName,password,
                "s","sd","dhuhg","dsd");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statuscode = jsonObject1.getString("ResponseCode");

                        if (statuscode.equalsIgnoreCase("200")) {

                            JSONArray responseDataArray=jsonObject1.getJSONArray("ResponseData");
                            JSONObject responseDataObject=responseDataArray.getJSONObject(0);

                            String userKey=responseDataObject.getString("UserKey");
                            String parentId=responseDataObject.getString("ParentId");
                            String userId=responseDataObject.getString("UserId");
                            String roleId=responseDataObject.getString("RoleId");
                            String status=responseDataObject.getString("Status");
                            String name=responseDataObject.getString("Name");
                            String mobileNo=responseDataObject.getString("MobileNo");
                            String emailId=responseDataObject.getString("EmailId");
                            String aadharNo=responseDataObject.getString("AadharNo");
                            String panNo=responseDataObject.getString("PanNo");
                            String companyName=responseDataObject.getString("CompanyName");
                            String stateId=responseDataObject.getString("StateId");
                            String cityId=responseDataObject.getString("CityId");
                            String address=responseDataObject.getString("Address");
                            String packageId=responseDataObject.getString("PackageId");
                            String createdOn=responseDataObject.getString("CreatedOn");
                            String pinCode=responseDataObject.getString("PinCode");



                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userKey", userKey);
                            editor.putString("parentId", parentId);
                            editor.putString("userId", userId);
                            editor.putString("roleId", roleId);
                            editor.putString("status", status);
                            editor.putString("name", name);
                            editor.putString("mobileNo", mobileNo);
                            editor.putString("emailId", emailId);
                            editor.putString("aadharNo", aadharNo);
                            editor.putString("panNo", panNo);
                            editor.putString("companyName", companyName);
                            editor.putString("stateId", stateId);
                            editor.putString("cityId", cityId);
                            editor.putString("address", address);
                            editor.putString("packageId", packageId);
                            editor.putString("createdOn", createdOn);
                            editor.putString("pinCode", pinCode);

                            editor.apply();


                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            pDialog.dismiss();

                        } else {
                            pDialog.dismiss();
                            String data = jsonObject1.getString("ResponseMessage");

                            new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this).setTitle("Message")
                                    .setMessage(data)
                                    .setPositiveButton("Ok", null).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Message")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Got it!!!", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Message")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Got it!!!", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("Got it!!!", null)
                        .show();
            }
        });
    }
}