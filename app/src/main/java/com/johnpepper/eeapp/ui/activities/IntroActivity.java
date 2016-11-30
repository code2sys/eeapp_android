package com.johnpepper.eeapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.gc.materialdesign.views.ButtonFlat;
import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.app.Constants;
import com.johnpepper.eeapp.ui.fragments.TutorialPageFragment;
import com.johnpepper.eeapp.util.EEPreferenceManager;

import me.relex.circleindicator.CircleIndicator;

public class IntroActivity extends EEBaseActivity {

    private ButtonFlat skipButton;
    private ButtonFlat nextButton;

    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }

    @Override
    protected void initUI() {

        // Initialize View Pager and Circle Indicator
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        CircleIndicator circleIndicator = (CircleIndicator) findViewById(R.id.circleIndicator);
        skipButton = (ButtonFlat) findViewById(R.id.skipButton);
        nextButton = (ButtonFlat) findViewById(R.id.nextButton);

        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position > 3) return null;
                else return TutorialPageFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return 4;
            }
        };

        viewPager.setAdapter(adapter);
        circleIndicator.setViewPager(viewPager);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishOnboarding();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == 3) { // The last screen
                    finishOnboarding();
                } else {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 3) {
                    skipButton.setVisibility(View.GONE);
                    nextButton.setText("Done");
                } else {
                    skipButton.setVisibility(View.VISIBLE);
                    nextButton.setText("Next");
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void finishOnboarding() {

        EEPreferenceManager.setBoolean(Constants.PREF_KEY_TUTORIAL_MODE, true);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();

    }


}
