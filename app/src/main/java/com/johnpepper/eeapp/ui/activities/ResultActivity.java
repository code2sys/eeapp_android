package com.johnpepper.eeapp.ui.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.app.Constants;
import com.johnpepper.eeapp.asynctask.EEApiManager;
import com.johnpepper.eeapp.listener.CompletedListener;
import com.johnpepper.eeapp.model.EEUser;
import com.johnpepper.eeapp.util.DateTimeUtil;
import com.johnpepper.eeapp.util.EEImageLoader;
import com.johnpepper.eeapp.util.StringUtil;
import com.makeramen.roundedimageview.RoundedImageView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class ResultActivity extends EEActionBarBaseActivity implements View.OnClickListener {

    private JSONArray myStats = new JSONArray();
    RoundedImageView profileImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        getSupportActionBar().setCustomView(R.layout.view_actionbar_result);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        setRightOptionsItemStatus(true);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            Button btnEditProfile = (Button)actionBar.getCustomView().findViewById(R.id.btnEditProfile);
            btnEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ResultActivity.this, SignUpActivity.class);
                    intent.putExtra(Constants.EXTRA_KEY_IS_EDIT_PROFILE, true);
                    pushIntent(intent);
                }
            });
        }
    }

    @Override
    protected void initUI() {

        findViewById(R.id.chatButton).setOnClickListener(this);
        findViewById(R.id.moreReviewsButton).setOnClickListener(this);
        findViewById(R.id.topFiveUsersButton).setOnClickListener(this);
        findViewById(R.id.kudosButton).setOnClickListener(this);
        findViewById(R.id.supportButton).setOnClickListener(this);
        findViewById(R.id.logoutButton).setOnClickListener(this);
        findViewById(R.id.showBarChartLayout).setOnClickListener(this);

        final TextView participantsNumberTextView = (TextView) findViewById(R.id.participantsNumberTextView);
        final TextView yourScoreTextView = (TextView) findViewById(R.id.yourScoreTextView);
        profileImageView = (RoundedImageView) findViewById(R.id.profileImageView);

        try {

            // Get review attributes and initialize viewPager
            Date today = new Date();
            String startDateString = DateTimeUtil.dateToStringInUTC(DateTimeUtil.lastSixMonthOfDate(today));
            String endDateString = DateTimeUtil.dateToStringInUTC(today);

            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("start_date_for_today", startDateString));
            params.add(new BasicNameValuePair("end_date_for_today", endDateString));
            params.add(new BasicNameValuePair("start_date_for_yesterday", startDateString));
            params.add(new BasicNameValuePair("end_date_for_yesterday", endDateString));
            params.add(new BasicNameValuePair("start_date_for_last6months", startDateString));
            params.add(new BasicNameValuePair("end_date_for_last6months", endDateString));
            params.add(new BasicNameValuePair("user_id", EEUser.getCurrentUser().userInfo.getString("id")));
            params.add(new BasicNameValuePair("company_id", EEUser.getCurrentUser().userInfo.getString("company_id")));
            params.add(new BasicNameValuePair("location_id", EEUser.getCurrentUser().userInfo.getString("location_id")));
            params.add(new BasicNameValuePair("role_id", EEUser.getCurrentUser().userInfo.getString("role_id")));

            new EEApiManager("stats", params, new CompletedListener() {
                @Override
                public void onCompleted(JSONObject result) {
                    try {
                        result = result.getJSONObject("result");
                        DecimalFormat df2 = new DecimalFormat(".0");
                        yourScoreTextView.setText(df2.format(Math.round(result.getDouble("your_score") * 10) / 10.0));
                        participantsNumberTextView.setText(String.valueOf(result.getInt("participants_count")) + " Reviews");

                        myStats = result.getJSONArray("my_stats");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(new String[]{"GET"});
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            EEImageLoader.showImage(profileImageView, StringUtil.userImageURLFromUserID(EEUser.getCurrentUser().userInfo.getString("id")));
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_friend_signup) {

            Intent intent = new Intent(ResultActivity.this, SignUpActivity.class);
            intent.putExtra(Constants.EXTRA_KEY_IS_FRIEND_SIGNUP, true);
            pushIntent(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // OnClickListener method
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.showBarChartLayout:
                if (myStats.length() > 0) {
                    intent = new Intent(this, CategoryStatsActivity.class);
                    intent.putExtra(Constants.EXTRA_KEY_STATS, myStats.toString());
                    pushIntent(intent);
                }
                break;
            case R.id.chatButton:
                intent = new Intent(this, MessageBoardActivity.class);
                pushIntent(intent);
                break;
            case R.id.moreReviewsButton:
                intent = new Intent(this, ReviewActivity.class);
                this.finish();
                startActivity(intent);
                overridePendingTransition(R.anim.flip_point_to_middle, R.anim.flip_point_from_middle);
                break;
            case R.id.topFiveUsersButton:
                intent = new Intent(this, TopFiveOverallActivity.class);
                pushIntent(intent);
                break;
            case R.id.kudosButton:
                intent = new Intent(this, KudosActivity.class);
                pushIntent(intent);
                break;
            case R.id.supportButton:
                intent=new Intent(Intent.ACTION_SEND);
                String[] recipients={"alpha@eeapp.co"};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT,"EEApp User Comment");
                intent.putExtra(Intent.EXTRA_TITLE,"Comment");
                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    String version = "app version: " + pInfo.versionName + "(" + pInfo.versionCode + ")";
                    intent.putExtra(Intent.EXTRA_TEXT, version);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                intent.setType("text/html");
                startActivity(Intent.createChooser(intent, "Send mail"));
                break;
            case R.id.logoutButton:
                EEUser.logOut();
                intent = new Intent(this, LoginActivity.class);
                this.finish();
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
