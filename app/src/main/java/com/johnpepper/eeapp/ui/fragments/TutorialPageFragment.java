package com.johnpepper.eeapp.ui.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.johnpepper.eeapp.R;

public class TutorialPageFragment extends Fragment {

    private static final int[] imageResourceArray = new int[] {R.mipmap.tour_bg_1, R.mipmap.tour_bg_2,R.mipmap.tour_bg_3, R.mipmap.tour_bg_4};
    private static final String ARG_INDEX = "index";
    private int mIndex;

    public static TutorialPageFragment newInstance(int index) {
        TutorialPageFragment fragment = new TutorialPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIndex = getArguments().getInt(ARG_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle s) {

        View view = inflater.inflate(
                R.layout.fragment_tutorial_page,
                container,
                false
        );

        ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
        imageView.setImageResource(imageResourceArray[mIndex]);

        return view;

    }

}
