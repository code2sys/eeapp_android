package com.johnpepper.eeapp.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.app.Constants;
import com.johnpepper.eeapp.util.EEPreferenceManager;

public class SplashActivity extends EEBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void initUI() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Boolean isLoggedIn = EEPreferenceManager.getBoolean(Constants.PREF_KEY_LOGGED_IN, false);

                Intent intent;
                if (isLoggedIn) {
                    intent = new Intent(SplashActivity.this, ReviewActivity.class);
                } else {
                    Boolean isTutorialMode = EEPreferenceManager.getBoolean(Constants.PREF_KEY_TUTORIAL_MODE, false);
                    if (isTutorialMode) {
                        intent = new Intent(SplashActivity.this, IntroActivity.class);
                    } else {
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                    }
                }
                SplashActivity.this.finish();
                startActivity(intent);

            }
        }, 4000);
    }

}
