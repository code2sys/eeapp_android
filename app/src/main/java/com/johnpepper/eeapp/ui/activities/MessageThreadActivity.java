package com.johnpepper.eeapp.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.adapter.MessageThreadAdapter;
import com.johnpepper.eeapp.app.Constants;
import com.johnpepper.eeapp.asynctask.EEApiManager;
import com.johnpepper.eeapp.listener.CompletedListener;
import com.johnpepper.eeapp.model.EEUser;
import com.johnpepper.eeapp.util.MessageUtil;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageThreadActivity extends EEActionBarBaseActivity{

    private ListView mListView;
    private EditText contentEditText;
    private String mParentMessageID;
    MessageThreadAdapter mAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    JSONArray messageArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_thread);

        setTitle(R.string.activity_messagethread_title);
        setRightOptionsItemStatus(false);
    }


    @Override
    protected void initUI() {

        mParentMessageID = getIntent().getStringExtra(Constants.EXTRA_KEY_PARENT_MESSAGE_ID);

        try {
            TextView companyTextView = (TextView) findViewById(R.id.companyTextView);
            companyTextView.setText(EEUser.getCurrentUser().userInfo.getString("company_description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        contentEditText = (EditText) findViewById(R.id.contentEditText);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mListView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });


//        ButtonFlat backButton = (ButtonFlat) findViewById(R.id.backButton);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MessageThreadActivity.this.finish();
//            }
//        });

        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (contentEditText.getText().toString().equalsIgnoreCase("")) {
                    MessageUtil.showMessage("You can't send empty message", false);
                    return;
                }

                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("content", StringEscapeUtils.escapeJava(contentEditText.getText().toString())));
                try {
                    params.add(new BasicNameValuePair("author_id", EEUser.getCurrentUser().userInfo.getString("id")));
                    params.add(new BasicNameValuePair("company_id", EEUser.getCurrentUser().userInfo.getString("company_id")));
                    params.add(new BasicNameValuePair("parent_id", mParentMessageID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                new EEApiManager("messages", params, new CompletedListener() {
                    @Override
                    public void onCompleted(JSONObject result) {
                        refresh();
                        contentEditText.setText("");
                    }
                }).execute(new String[]{"POST"});
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        try {
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", mParentMessageID));
            params.add(new BasicNameValuePair("user_id", EEUser.getCurrentUser().userInfo.getString("id")));

            new EEApiManager("thread_messages", params, new CompletedListener() {
                @Override
                public void onCompleted(JSONObject result) {
                    try {
                        if (result.getString("result").equalsIgnoreCase("success")) {
                            messageArray = result.getJSONArray("data");
                            if (mAdapter == null) {
                                mAdapter = new MessageThreadAdapter(MessageThreadActivity.this, messageArray);
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
}
