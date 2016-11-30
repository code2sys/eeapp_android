package com.johnpepper.eeapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.app.Constants;
import com.johnpepper.eeapp.asynctask.EEApiManager;
import com.johnpepper.eeapp.listener.CompletedListener;
import com.johnpepper.eeapp.model.EEUser;
import com.johnpepper.eeapp.util.EEImageLoader;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * Created by borysrosicky on 10/28/15.
 */
public class MessageAdapter extends ArrayAdapter<String> {
    private final Activity context;
    public JSONArray messages;

    public MessageAdapter(Activity context, JSONArray messages) {

        super(context, R.layout.row_message);
        this.context = context;
        this.messages = messages;

    }
    @Override
    public int getCount() {
        return messages.length();
    }
    static class ViewContainer {
        public TextView interestValueTextView;
        public TextView timeTextView;
        public TextView contentTextView;
        public TextView repliesCountTextView;
        public RelativeLayout upvoteButton;
        public RelativeLayout downvoteButton;
        public ImageView upvoteImageView;
        public ImageView downvoteImageView;
        public ImageView contentImageView;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View view, ViewGroup parent) {


        View rowView = view;
        ViewContainer viewContainer;

        if (rowView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(R.layout.row_message, null, true);

            viewContainer = new ViewContainer();

            //---get a reference to all the views on the xml layout---

            viewContainer.interestValueTextView = (TextView)rowView.findViewById(R.id.interestValueTextView);
            viewContainer.timeTextView = (TextView)rowView.findViewById(R.id.timeTextView);
            viewContainer.contentTextView = (TextView)rowView.findViewById(R.id.contentTextView);
            viewContainer.repliesCountTextView = (TextView)rowView.findViewById(R.id.repliesCountTextView);
            viewContainer.upvoteButton = (RelativeLayout)rowView.findViewById(R.id.upvoteButton);
            viewContainer.downvoteButton = (RelativeLayout)rowView.findViewById(R.id.downvoteButton);
            viewContainer.upvoteImageView = (ImageView)rowView.findViewById(R.id.upvoteImageView);
            viewContainer.downvoteImageView = (ImageView)rowView.findViewById(R.id.downvoteImageView);
            viewContainer.contentImageView = (ImageView)rowView.findViewById(R.id.contentImageView);

            rowView.setTag(viewContainer);
        }
        else
        {

            viewContainer = (ViewContainer)rowView.getTag();
        }

        try
        {
            //viewContainer.contentImageView.getLayoutParams().height = 0;
            //viewContainer.contentImageView.getLayoutParams().width = 0;

            JSONObject message = (JSONObject)messages.get(position);

            int repliesCount = message.getInt("replies_count");

            if (message.getInt("parent_id") != -1 || repliesCount == 0) {
                viewContainer.repliesCountTextView.setVisibility(View.INVISIBLE);
            }else {
                viewContainer.repliesCountTextView.setVisibility(View.VISIBLE);
                String prefixForRepliesCount = (repliesCount > 1) ? "replies" : "reply";
                viewContainer.repliesCountTextView.setText(repliesCount + " " + prefixForRepliesCount);
            }

            // Show message content
            viewContainer.contentTextView.setText(StringEscapeUtils.unescapeJava(message.getString("content")));

            // Show the created time of message
            String dateString = message.getString("created_at");
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            TimeZone serverTimeZone = TimeZone.getTimeZone("UTC");
            sf.setTimeZone(serverTimeZone);

            Date date = sf.parse(dateString);

            long curMillis = date.getTime();
            dateString = (String) DateUtils.getRelativeDateTimeString(this.context, curMillis, DateUtils.DAY_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_12HOUR | DateUtils.FORMAT_CAP_AMPM);
            viewContainer.timeTextView.setText(dateString);

            // Initialize the status of upvote and downvote buttons
            configureVoteButtonStatus(message, viewContainer);

            String contentString = Constants.BASE_RESOURCE_URL_STRING + message.getString("photo_url");
            if (message.getString("photo_url").compareTo("") == 0){ // same with empty string
                viewContainer.contentImageView.getLayoutParams().height = 0;
                viewContainer.contentImageView.getLayoutParams().width = 0;
            }
            else{
                viewContainer.contentImageView.getLayoutParams().height = 280;
                viewContainer.contentImageView.getLayoutParams().width = 280;

                EEImageLoader.showImage(viewContainer.contentImageView, contentString);
            }

            //viewContainer.contentImageView.setImage

            viewContainer.upvoteButton.setOnClickListener(new onMyClick(position, viewContainer, true));
            viewContainer.downvoteButton.setOnClickListener(new onMyClick(position, viewContainer, false));

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return rowView;
    }

    public void configureVoteButtonStatus(JSONObject message, ViewContainer viewContainer) {
        initVoteButtons(viewContainer);

        try {
            int aggregateInterestValue = message.getInt("aggregate_interest_value");
            viewContainer.interestValueTextView.setText(aggregateInterestValue > 0 ? "+" + aggregateInterestValue : String.valueOf(aggregateInterestValue));

            if (message.getString("author_id").equalsIgnoreCase(EEUser.getCurrentUser().userInfo.getString("id"))) {
                viewContainer.downvoteButton.setVisibility(View.INVISIBLE);
                viewContainer.upvoteButton.setVisibility(View.INVISIBLE);
            } else {
                viewContainer.downvoteButton.setVisibility(View.VISIBLE);
                viewContainer.upvoteButton.setVisibility(View.VISIBLE);

                int personalInterestValue = message.getInt("personal_interest_value");

                if (personalInterestValue == 0) {
                    viewContainer.upvoteButton.setEnabled(true);
                    viewContainer.downvoteButton.setEnabled(true);
                    viewContainer.upvoteImageView.setImageResource(R.mipmap.upvote_button_normal);
                    viewContainer.downvoteImageView.setImageResource(R.mipmap.downvote_button_normal);
                } else if (personalInterestValue > 0) {
                    viewContainer.upvoteButton.setEnabled(false);
                    viewContainer.downvoteButton.setEnabled(true);
                    viewContainer.upvoteImageView.setImageResource(R.mipmap.upvote_button_highlighted);
                    viewContainer.downvoteImageView.setImageResource(R.mipmap.downvote_button_normal);
                } else {
                    viewContainer.upvoteButton.setEnabled(true);
                    viewContainer.downvoteButton.setEnabled(false);
                    viewContainer.upvoteImageView.setImageResource(R.mipmap.upvote_button_normal);
                    viewContainer.downvoteImageView.setImageResource(R.mipmap.downvote_button_highlighted);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void initVoteButtons(ViewContainer viewContainer) {

        viewContainer.upvoteButton.setEnabled(true);
        viewContainer.downvoteButton.setEnabled(true);
        viewContainer.upvoteImageView.setImageResource(R.mipmap.upvote_button_normal);
        viewContainer.downvoteImageView.setImageResource(R.mipmap.downvote_button_normal);

    }

    public class onMyClick implements View.OnClickListener {

        private final int pos;
        private final ViewContainer viewContainer;
        private boolean isUpvote;

        public onMyClick(int pos, ViewContainer viewContainer, Boolean isUpvote) {
            this.pos = pos;
            this.viewContainer = viewContainer;
            this.isUpvote = isUpvote;
        }

        @Override
        public void onClick(View v) {
            try {

                final JSONObject message = messages.getJSONObject(pos);
                final int offset;
                if (isUpvote) {
                    viewContainer.upvoteButton.setEnabled(false);
                    offset = 1;

                } else {
                    viewContainer.downvoteButton.setEnabled(false);
                    offset = -1;
                }
                message.put("personal_interest_value", message.getInt("personal_interest_value") + offset);

                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("message_id", message.getString("id")));
                params.add(new BasicNameValuePair("value", String.valueOf(message.getInt("personal_interest_value"))));
                params.add(new BasicNameValuePair("user_id", EEUser.getCurrentUser().userInfo.getString("id")));


                new EEApiManager("vote_on_message", params, new CompletedListener() {
                    @Override
                    public void onCompleted(JSONObject result) {
                        try {

                            if (isUpvote) {
                                viewContainer.upvoteButton.setEnabled(true);

                            } else {
                                viewContainer.downvoteButton.setEnabled(true);
                            }
                            if (result.getString("result").equalsIgnoreCase("success")) {
                                message.put("aggregate_interest_value", message.getInt("aggregate_interest_value") + offset);
                                configureVoteButtonStatus(messages.getJSONObject(pos), onMyClick.this.viewContainer);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).execute(new String[]{"POST"});

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}