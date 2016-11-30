package com.johnpepper.eeapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.adapter.MessageAdapter;
import com.johnpepper.eeapp.app.Constants;
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

public class MessageBoardActivity extends EEActionBarBaseActivity implements RadioGroup.OnCheckedChangeListener{

    private SegmentedGroup segmentedGroup;
    private ListView mListView;
    MessageAdapter mAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    String[] sortModes = {"created_at", "aggregate_interest_value"};

    JSONArray messageArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board);

        getSupportActionBar().setCustomView(R.layout.view_actionbar_messageboard);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        setRightOptionsItemStatus(true);

        RelativeLayout actionBarView = (RelativeLayout) getSupportActionBar().getCustomView();

        segmentedGroup = (SegmentedGroup) actionBarView.findViewById(R.id.segmentedGroup);
        segmentedGroup.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initUI() {

        try {
            TextView companyTextView = (TextView) findViewById(R.id.companyTextView);
            companyTextView.setText(EEUser.getCurrentUser().userInfo.getString("company_description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mListView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent = new Intent(MessageBoardActivity.this, MessageThreadActivity.class);
                    JSONObject message = messageArray.getJSONObject(position);
                    intent.putExtra(Constants.EXTRA_KEY_PARENT_MESSAGE_ID, message.getString("id"));
                    pushIntent(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        refresh();
    }

    private void refresh() {
        try {
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("order_by", sortModes[segmentedGroup.getCheckedRadioButtonId() == R.id.popularMessagesRadioButton ? 1 : 0]));
            params.add(new BasicNameValuePair("company_id", EEUser.getCurrentUser().userInfo.getString("company_id")));
            params.add(new BasicNameValuePair("user_id", EEUser.getCurrentUser().userInfo.getString("id")));

            new EEApiManager("all_messages", params, new CompletedListener() {
                @Override
                public void onCompleted(JSONObject result) {
                    try {
                        if (result.getString("result").equalsIgnoreCase("success")) {
                            messageArray = result.getJSONArray("data");
                            if (mAdapter == null) {
                                mAdapter = new MessageAdapter(MessageBoardActivity.this, messageArray);
                                mListView.setAdapter(mAdapter);
                            } else {
                                mAdapter.messages = messageArray;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message_board, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_compose) {

            Intent intent = new Intent(MessageBoardActivity.this, MessageComposeActivity.class);
            pushIntent(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
