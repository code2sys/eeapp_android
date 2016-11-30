package com.johnpepper.eeapp.ui.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.app.Constants;
import com.johnpepper.eeapp.asynctask.EEApiManager;
import com.johnpepper.eeapp.listener.CompletedListener;
import com.johnpepper.eeapp.model.EEUser;
import com.johnpepper.eeapp.ui.fragments.ReviewPageFragment;
import com.johnpepper.eeapp.util.DateTimeUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReviewActivity extends EEActionBarBaseActivity {

    private JSONArray reviewAttributes;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment> ();
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        getSupportActionBar().setCustomView(R.layout.view_actionbar_review);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        setRightOptionsItemStatus(true);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            Button btnChat = (Button)actionBar.getCustomView().findViewById(R.id.btnChat);
            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ReviewActivity.this, MessageBoardActivity.class);
                    pushIntent(intent);
                }
            });
        }
    }

    @Override
    protected void initUI() {

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        try {

            // Get review attributes and initialize viewPager
            Date today = new Date();
            String startDateString = DateTimeUtil.dateToStringInUTC(DateTimeUtil.twoPMOfDate(today));
            Date tomorrow = DateTimeUtil.addOneDayToDate(today);
            String endDateString = DateTimeUtil.dateToStringInUTC(DateTimeUtil.twoPMOfDate(tomorrow));

            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("start_datetime", startDateString));
            params.add(new BasicNameValuePair("end_datetime", endDateString));
            params.add(new BasicNameValuePair("user_id", EEUser.getCurrentUser().userInfo.getString("id")));
            params.add(new BasicNameValuePair("company_id", EEUser.getCurrentUser().userInfo.getString("company_id")));
            params.add(new BasicNameValuePair("location_id", EEUser.getCurrentUser().userInfo.getString("location_id")));
            params.add(new BasicNameValuePair("role_id", EEUser.getCurrentUser().userInfo.getString("role_id")));

            new EEApiManager("next_possible_ratings", params, new CompletedListener() {
                @Override
                public void onCompleted(JSONObject result) {
                    try {
                        if (result.getString("result").equalsIgnoreCase("success")) {
                            reviewAttributes = result.getJSONArray("data");

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new Date());

                            String categoryIDOfFirstQuestion = reviewAttributes.getJSONObject(0).getString("category_id");
                            if (categoryIDOfFirstQuestion.equalsIgnoreCase("") && calendar.get(Calendar.HOUR_OF_DAY) > 14) {
                                reviewAttributes.remove(0);
                            }

                            FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
                                @Override
                                public Fragment getItem(int position) {
                                    if (position >= reviewAttributes.length()) return null;
                                    else return fragmentAtIndex(position);
                                }

                                @Override
                                public int getCount() {
                                    return reviewAttributes.length();
                                }
                            };

                            viewPager.setAdapter(adapter);

                        }
                        else{
                            reviewAttributes = result.getJSONArray("data");

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new Date());

                            String categoryIDOfFirstQuestion = reviewAttributes.getJSONObject(0).getString("category_id");
                            if (categoryIDOfFirstQuestion.equalsIgnoreCase("") && calendar.get(Calendar.HOUR_OF_DAY) > 14) {
                                reviewAttributes.remove(0);
                            }

                            FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
                                @Override
                                public Fragment getItem(int position) {
                                    if (position >= reviewAttributes.length()) return null;
                                    else return fragmentAtIndex(position);
                                }

                                @Override
                                public int getCount() {
                                    return reviewAttributes.length();
                                }
                            };

                            viewPager.setAdapter(adapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(new String[]{"POST"});
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_review, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_done) {
            Intent intent = new Intent(ReviewActivity.this, ResultActivity.class);
            ReviewActivity.this.finish();
            startActivity(intent);
            overridePendingTransition(R.anim.flip_point_from_middle, R.anim.flip_point_to_middle);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }


    public void didPostRate(int index, int rating, String ratingID, int categoryID, String userName) {

        boolean didGoToComposeComment = false;
        if (rating < 3 && categoryID > 0 ) {

            // Go to compose comment
            Intent intent = new Intent(this, CommentComposeActivity.class);
            intent.putExtra(Constants.EXTRA_KEY_USER_NAME, userName);
            intent.putExtra(Constants.EXTRA_KEY_RATING_ID, ratingID);
            pushIntent(intent);

            didGoToComposeComment = true;
        }

        if (index + 1 < reviewAttributes.length()) {
            viewPager.setCurrentItem(index + 1, true);
        } else if (!didGoToComposeComment) {
            Intent intent = new Intent(this, ResultActivity.class);
            this.finish();
            startActivity(intent);
            overridePendingTransition(R.anim.flip_point_from_middle, R.anim.flip_point_to_middle);
        }
    }

    private Fragment fragmentAtIndex(int index) {
        try {
            if (fragments.size() == 0) {
                for (int i = 0; i < reviewAttributes.length(); i++) {
                fragments.add(ReviewPageFragment.newInstance(i, reviewAttributes.getJSONObject(i)));
            }
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return fragments.get(index);
    }

}
