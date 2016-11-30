package com.johnpepper.eeapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.model.EEUser;
import com.johnpepper.eeapp.util.EEImageLoader;
import com.johnpepper.eeapp.util.StringUtil;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by borysrosicky on 10/28/15.
 */


public class TopFiveAdapter extends ArrayAdapter<String> {
    private final Activity context;
    public JSONArray userArray;


    public TopFiveAdapter(Activity context, JSONArray userArray) {

        super(context, R.layout.row_top_five);
        this.context = context;
        this.userArray = userArray;

    }
    @Override
    public int getCount() {
        return userArray.length();
    }
    static class ViewContainer {
        public TextView rankingTextView;
        public TextView companyTextView;
        public RoundedImageView profileImageView;
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
            rowView= inflater.inflate(R.layout.row_top_five, null, true);

            viewContainer = new ViewContainer();

            //---get a reference to all the views on the xml layout---

            viewContainer.rankingTextView = (TextView)rowView.findViewById(R.id.rankingTextView);
            viewContainer.companyTextView = (TextView)rowView.findViewById(R.id.companyTextView);
            viewContainer.profileImageView = (RoundedImageView)rowView.findViewById(R.id.profileImageView);


            rowView.setTag(viewContainer);
        }
        else
        {

            viewContainer = (ViewContainer)rowView.getTag();
        }

        try
        {
            JSONObject userObject = (JSONObject)userArray.get(position);

            String initialForLastName = userObject.getString("last_name").equalsIgnoreCase("") ? "" : userObject.getString("last_name").substring(0,1).toUpperCase();
            viewContainer.rankingTextView.setText(userObject.getString("rank") + ". " + userObject.getString("first_name") + " " + initialForLastName);
            viewContainer.companyTextView.setText(userObject.getString("company_name") + "  -  " + userObject.getString("location_name"));

            EEImageLoader.showImage(viewContainer.profileImageView, StringUtil.userImageURLFromUserID(userObject.getString("id")));


        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return rowView;
    }
}