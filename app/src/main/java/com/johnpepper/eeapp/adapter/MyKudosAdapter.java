package com.johnpepper.eeapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.util.EEImageLoader;
import com.johnpepper.eeapp.util.StringUtil;
import com.makeramen.roundedimageview.RoundedImageView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

    /**
     * Created by borysrosicky on 10/28/15.
     *
     *
     */
    public class MyKudosAdapter extends ArrayAdapter<String> {
        private final Activity context;
        public JSONArray kudosArray;

    public MyKudosAdapter(Activity context, JSONArray kudosArray) {

        super(context, R.layout.row_kudos);
        this.context = context;
        this.kudosArray = kudosArray;

    }
    @Override
    public int getCount() {
        return kudosArray.length();
    }
    static class ViewContainer {
        public RoundedImageView profileImageView;
        public TextView nameTextView;
        public TextView kudosTextView;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View rowView = view;
        ViewContainer viewContainer;

        if (rowView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(R.layout.row_kudos, null, true);

            viewContainer = new ViewContainer();

            //---get a reference to all the views on the xml layout---

            viewContainer.profileImageView = (RoundedImageView)rowView.findViewById(R.id.profileImageView);
            viewContainer.nameTextView = (TextView)rowView.findViewById(R.id.nameTextView);
            viewContainer.kudosTextView = (TextView)rowView.findViewById(R.id.kudosTextView);

            rowView.setTag(viewContainer);
        }
        else
        {
            viewContainer = (ViewContainer)rowView.getTag();
        }

        try
        {
            JSONObject kudosObject = (JSONObject)kudosArray.get(position);
            viewContainer.nameTextView.setText(kudosObject.getString("first_name") + " " + kudosObject.getString("last_name"));
            viewContainer.kudosTextView.setText(kudosObject.getString("kudos_message"));

            EEImageLoader.showImage(viewContainer.profileImageView, StringUtil.userImageURLFromUserID(kudosObject.getString("user_id")));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return rowView;
    }
}