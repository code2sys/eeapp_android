package com.johnpepper.eeapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.app.Constants;
import com.johnpepper.eeapp.asynctask.EEApiManager;
import com.johnpepper.eeapp.listener.CompletedListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommentComposeActivity extends EEActionBarBaseActivity {

    private String userName;
    private String ratingID;
    private EditText commentContentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_compose);

        setTitle(R.string.activity_commentcompose_title);
        setRightOptionsItemStatus(true);
    }

    @Override
    protected void initUI() {

        Intent intent = getIntent();
        userName = intent.getStringExtra(Constants.EXTRA_KEY_USER_NAME);
        ratingID = intent.getStringExtra(Constants.EXTRA_KEY_RATING_ID);

        final TextView userNameTextView = (TextView) findViewById(R.id.userNameTextView);
        commentContentEditText = (EditText) findViewById(R.id.commentContentEditText);

        userNameTextView.setText(userName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment_compose, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_done) {

            String content = commentContentEditText.getText().toString();

            if (!content.isEmpty()) {
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("content", content));
                params.add(new BasicNameValuePair("rating_id", ratingID));

                new EEApiManager("comments", params, new CompletedListener() {
                    @Override
                    public void onCompleted(JSONObject result) {
                        CommentComposeActivity.this.finish();
                    }
                }).execute(new String[]{"POST"});
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
