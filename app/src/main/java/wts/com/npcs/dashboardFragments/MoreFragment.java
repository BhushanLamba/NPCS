package wts.com.npcs.dashboardFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import soup.neumorphism.NeumorphCardView;
import wts.com.npcs.R;
import wts.com.npcs.activities.ViewCustomerActivity;

public class MoreFragment extends Fragment {

    NeumorphCardView viewUserCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_more, container, false);
        initViews(view);


        viewUserCard.setOnClickListener(v->
        {
            startActivity(new Intent(getContext(), ViewCustomerActivity.class));
        });

        return view;
    }

    private void initViews(View view) {
        viewUserCard=view.findViewById(R.id.view_users_card);
    }


}