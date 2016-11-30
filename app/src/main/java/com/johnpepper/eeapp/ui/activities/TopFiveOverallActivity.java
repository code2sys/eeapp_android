package com.johnpepper.eeapp.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.adapter.TopFiveAdapter;
import com.johnpepper.eeapp.asynctask.EEApiManager;
import com.johnpepper.eeapp.listener.CompletedListener;
import com.johnpepper.eeapp.model.EEUser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import info.hoang8f.android.segmented.SegmentedGroup;

public class TopFiveOverallActivity extends EEActionBarBaseActivity implements RadioGroup.OnCheckedChangeListener{

    private SegmentedGroup segmentedGroup;
    private ListView mListView;
    TopFiveAdapter mAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    String[] stateIDs = {};

    JSONArray userArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_five_overall);

        getSupportActionBar().setCustomView(R.layout.view_actionbar_topfiveoverall);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        setRightOptionsItemStatus(false);

        RelativeLayout actionBarView = (RelativeLayout) getSupportActionBar().getCustomView();

        segmentedGroup = (SegmentedGroup) actionBarView.findViewById(R.id.segmentedGroup);
        segmentedGroup.setOnCheckedChangeListener(this);

        try {
            RadioButton myStateRadioButton = (RadioButton) segmentedGroup.findViewById(R.id.myStateRadioButton);
            myStateRadioButton.setText(EEUser.getCurrentUser().userInfo.getString("state_description"));
            stateIDs = new String[] {EEUser.getCurrentUser().userInfo.getString("state_id"), "*"};
        } catch (JSONException e) {
            e.printStackTrace();
        }

        refresh();

    }

    @Override
    protected void initUI() {

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mListView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        refresh();
    }

    private void refresh() {
        try {
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("state_id", stateIDs[segmentedGroup.getCheckedRadioButtonId() == R.id.myCompanyRadioButton ? 1 : 0]));
            params.add(new BasicNameValuePair("company_id", EEUser.getCurrentUser().userInfo.getString("company_id")));
            params.add(new BasicNameValuePair("location_id", EEUser.getCurrentUser().userInfo.getString("location_id")));

            new EEApiManager("top_five_overall", params, new CompletedListener() {
                @Override
                public void onCompleted(JSONObject result) {
                    try {
                        if (result.getString("result").equalsIgnoreCase("success")) {
                            userArray = result.getJSONArray("data");
                            if (mAdapter == null) {
                                mAdapter = new TopFiveAdapter(TopFiveOverallActivity.this, userArray);
                                mListView.setAdapter(mAdapter);
                            } else {
                                mAdapter.userArray = userArray;
                                mAdapter.notifyDataSetChanged();
                            }

                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).execute(new String[]{"GET"});
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
