package wts.com.npcs.dashboardFragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import soup.neumorphism.NeumorphCardView;
import wts.com.npcs.R;
import wts.com.npcs.activities.RechargeActivity;
import wts.com.npcs.retrofit.RetrofitClient;

public class HomeFragment extends Fragment {

    ImageSlider imageSlider;
    TextView tvBalance;
    String userKey;
    SharedPreferences sharedPreferences;
    
    NeumorphCardView prepaidCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userKey = sharedPreferences.getString("userKey", null);

        setImageSlider();
        
        handleClickEvents();

        return view;
    }

    private void handleClickEvents() {
        prepaidCard.setOnClickListener(v->
        {
            Intent intent=new Intent(getContext(),RechargeActivity.class);
            intent.putExtra("service","Prepaid");
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getBalance();
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
                            tvBalance.setText("₹ " + userBalance);
                        } else {
                            tvBalance.setText("₹ 00.00");

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        tvBalance.setText("₹ 00.00");

                    }
                } else {
                    tvBalance.setText("₹ 00.00");

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                tvBalance.setText("₹ 00.00");
            }
        });
    }

    private void setImageSlider() {
        ArrayList<SlideModel> mySliderList;
        mySliderList = new ArrayList<>();
        mySliderList.add(new SlideModel(R.drawable.slider1, ScaleTypes.FIT));
        mySliderList.add(new SlideModel(R.drawable.slider2, ScaleTypes.FIT));
        mySliderList.add(new SlideModel(R.drawable.slider3, ScaleTypes.FIT));

        imageSlider.setImageList(mySliderList, ScaleTypes.FIT);
    }

    private void initViews(View view) {
        imageSlider = view.findViewById(R.id.image_slider);
        tvBalance = view.findViewById(R.id.tv_balance);
        prepaidCard = view.findViewById(R.id.prepaid_card);
    }
}