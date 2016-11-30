package com.johnpepper.eeapp.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.app.Constants;
import com.johnpepper.eeapp.util.EEImageLoader;
import com.johnpepper.eeapp.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class KudosDetailActivity extends EEBaseActivity{


    JSONObject kudos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kudos_detail);

    }

    @Override
    protected void initUI() {

        try {
            kudos = new JSONObject(getIntent().getStringExtra(Constants.EXTRA_KEY_KUDOS));

            TextView kudosAuthorNameTextView = (TextView) findViewById(R.id.kudosAuthorNameTextView);
            TextView kudosMessageTextView = (TextView) findViewById(R.id.kudosMessageTextView);
            TextView dateTextView = (TextView) findViewById(R.id.dateTextView);

            ImageView kudosAuthorProfileImageView = (ImageView) findViewById(R.id.kudosAuthorImageView);

            kudosAuthorNameTextView.setText("From " + kudos.getString("first_name") + " " + kudos.getString("last_name"));
            kudosMessageTextView.setText(kudos.getString("kudos_message"));
            dateTextView.setText(kudos.getString("created_at"));
            EEImageLoader.showImage(kudosAuthorProfileImageView, StringUtil.userImageURLFromUserID(kudos.getString("user_id")));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button closeButton = (Button) findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KudosDetailActivity.this.finish();
            }
        });
    }


}
