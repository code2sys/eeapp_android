package com.johnpepper.eeapp.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.app.Constants;
import com.johnpepper.eeapp.ui.fragments.CategoryStatPageFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class CategoryStatsActivity extends EEActionBarBaseActivity {

    private JSONArray allStats;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment> ();
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_stats);

        setTitle(getString(R.string.activity_categorystats_title));
        setRightOptionsItemStatus(false);

//        ButtonFlat backButton = (ButtonFlat)findViewById(R.id.backButton);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CategoryStatsActivity.this.finish();
//            }
//        });

    }

    @Override
    protected void initUI() {
        try {
            
            allStats = new JSONArray(getIntent().getStringExtra(Constants.EXTRA_KEY_STATS));
            viewPager = (ViewPager) findViewById(R.id.viewPager);
            FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    if (position >= allStats.length()) return null;
                    else return fragmentAtIndex(position);
                }

                @Override
                public int getCount() {
                    return allStats.length();
                }
            };

            viewPager.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private Fragment fragmentAtIndex(int index) {
        try {
            if (fragments.size() == 0) {
                for (int i = 0; i < allStats.length(); i++) {
                    fragments.add(CategoryStatPageFragment.newInstance(i, allStats.getJSONObject(i)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return fragments.get(index);
    }

}
