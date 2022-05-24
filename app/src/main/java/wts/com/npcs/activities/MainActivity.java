package wts.com.npcs.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import soup.neumorphism.NeumorphCardView;
import wts.com.npcs.dashboardFragments.HomeFragment;
import wts.com.npcs.dashboardFragments.MoreFragment;
import wts.com.npcs.dashboardFragments.ProfileFragment;
import wts.com.npcs.R;
import wts.com.npcs.dashboardFragments.ReportsFragment;

public class MainActivity extends AppCompatActivity {

    NeumorphCardView homeCard,reportsCard,profileCard,moreCard;
    ImageView imgHome,imgReports,imgProfile,imgMore;
    TextView tvHome,tvReports,tvProfile,tvMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        //////CHANGE COLOR OF STATUS BAR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.purple_200));
        //////CHANGE COLOR OF STATUS BAR

        loadFragment(new HomeFragment());
        handleBottomBar();
    }

    private void handleBottomBar() {
        homeCard.setOnClickListener(view -> {
            loadFragment(new HomeFragment());
            homeCard.setShapeType(0);
            reportsCard.setShapeType(2);
            profileCard.setShapeType(2);
            moreCard.setShapeType(2);

            imgHome.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.purple_200));
            imgReports.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.grey));
            imgProfile.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.grey));
            imgMore.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.grey));

            tvHome.setTextColor(getResources().getColor(R.color.purple_200));
            tvReports.setTextColor(getResources().getColor(R.color.grey));
            tvProfile.setTextColor(getResources().getColor(R.color.grey));
            tvMore.setTextColor(getResources().getColor(R.color.grey));
        });

        reportsCard.setOnClickListener(view -> {
            loadFragment(new ReportsFragment());
            homeCard.setShapeType(2);
            reportsCard.setShapeType(0);
            profileCard.setShapeType(2);
            moreCard.setShapeType(2);

            imgHome.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.grey));
            imgReports.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.purple_200));
            imgProfile.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.grey));
            imgMore.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.grey));

            tvHome.setTextColor(getResources().getColor(R.color.grey));
            tvReports.setTextColor(getResources().getColor(R.color.purple_200));
            tvProfile.setTextColor(getResources().getColor(R.color.grey));
            tvMore.setTextColor(getResources().getColor(R.color.grey));
        });

        profileCard.setOnClickListener(view -> {
            loadFragment(new ProfileFragment());
            homeCard.setShapeType(2);
            reportsCard.setShapeType(2);
            profileCard.setShapeType(0);
            moreCard.setShapeType(2);

            imgHome.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.grey));
            imgReports.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.grey));
            imgProfile.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.purple_200));
            imgMore.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.grey));

            tvHome.setTextColor(getResources().getColor(R.color.grey));
            tvReports.setTextColor(getResources().getColor(R.color.grey));
            tvProfile.setTextColor(getResources().getColor(R.color.purple_200));
            tvMore.setTextColor(getResources().getColor(R.color.grey));
        });

        moreCard.setOnClickListener(view -> {
            loadFragment(new MoreFragment());
            homeCard.setShapeType(2);
            reportsCard.setShapeType(2);
            profileCard.setShapeType(2);
            moreCard.setShapeType(0);

            imgHome.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.grey));
            imgReports.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.grey));
            imgProfile.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.grey));
            imgMore.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.purple_200));

            tvHome.setTextColor(getResources().getColor(R.color.grey));
            tvReports.setTextColor(getResources().getColor(R.color.grey));
            tvProfile.setTextColor(getResources().getColor(R.color.grey));
            tvMore.setTextColor(getResources().getColor(R.color.purple_200));
        });
    }

    private void initViews() {
        homeCard=findViewById(R.id.home_card);
        reportsCard=findViewById(R.id.reports_card);
        profileCard=findViewById(R.id.profile_card);
        moreCard=findViewById(R.id.more_card);
        imgHome=findViewById(R.id.img_home);
        imgReports=findViewById(R.id.img_report);
        imgProfile=findViewById(R.id.img_profile);
        imgMore=findViewById(R.id.img_more);
        tvHome=findViewById(R.id.tv_home);
        tvReports=findViewById(R.id.tv_reports);
        tvProfile=findViewById(R.id.tv_profile);
        tvMore=findViewById(R.id.tv_more);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, fragment).commit();
    }
}