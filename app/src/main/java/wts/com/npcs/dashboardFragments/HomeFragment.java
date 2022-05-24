package wts.com.npcs.dashboardFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

import wts.com.npcs.R;

public class HomeFragment extends Fragment {

    ImageSlider imageSlider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        setImageSlider();

        return view;
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
    }
}