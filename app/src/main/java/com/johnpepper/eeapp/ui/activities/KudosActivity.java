package com.johnpepper.eeapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.gc.materialdesign.views.ButtonFlat;
import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.adapter.MyKudosAdapter;
import com.johnpepper.eeapp.app.Constants;
import com.johnpepper.eeapp.asynctask.EEApiManager;
import com.johnpepper.eeapp.listener.CompletedListener;
import com.johnpepper.eeapp.model.EEUser;
import com.johnpepper.eeapp.util.EEImageLoader;
import com.johnpepper.eeapp.util.MessageUtil;
import com.johnpepper.eeapp.util.StringUtil;
import com.makeramen.roundedimageview.RoundedImageView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;

public class KudosActivity extends EEActionBarBaseActivity implements RadioGroup.OnCheckedChangeListener{

    private SegmentedGroup segmentedGroup;
    private ListView myKudosListView;
    private ScrollView newKudosScrollView;
    MyKudosAdapter mAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    Spinner employeeSpinner;
    Spinner categorySpinner;
    EditText kudosEditText;

    JSONArray employees;
    JSONArray categories;
    JSONArray kudosArray;

    RoundedImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kudos);

        getSupportActionBar().setCustomView(R.layout.view_actionbar_kudos);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        setRightOptionsItemStatus(false);

        RelativeLayout actionBarView = (RelativeLayout) getSupportActionBar().getCustomView();

        segmentedGroup = (SegmentedGroup) actionBarView.findViewById(R.id.segmentedGroup);
        segmentedGroup.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initUI() {

        refreshMyKudos();
        newKudosScrollView = (ScrollView) findViewById(R.id.newKudosScrollView);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        myKudosListView = (ListView) findViewById(R.id.myKudosListView);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMyKudos();
            }
        });

        myKudosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject kudos = kudosArray.getJSONObject(position);
                    Intent intent = new Intent(KudosActivity.this, KudosDetailActivity.class);
                    intent.putExtra(Constants.EXTRA_KEY_KUDOS, kudos.toString());
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        employeeSpinner = (Spinner) findViewById(R.id.employeeSpinner);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        kudosEditText = (EditText) findViewById(R.id.kudosEditText);

        profileImageView = (RoundedImageView) findViewById(R.id.imageProfile);

//        ButtonFlat backButton = (ButtonFlat) findViewById(R.id.backButton);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                KudosActivity.this.finish();
//
//            }
//        });



        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("user_id", EEUser.getCurrentUser().userInfo.getString("id")));
                    params.add(new BasicNameValuePair("target_user_id", employees.getJSONObject(employeeSpinner.getSelectedItemPosition()).getString("id")));
                    params.add(new BasicNameValuePair("category_id", categories.getJSONObject(categorySpinner.getSelectedItemPosition()).getString("id")));
                    params.add(new BasicNameValuePair("kudos_message", kudosEditText.getText().toString()));

                    new EEApiManager("kudos", params, new CompletedListener() {
                        @Override
                        public void onCompleted(JSONObject result) {
                            MessageUtil.showMessage("Your Kudos is successfully submitted", false);
                            KudosActivity.this.finish();
                        }
                    }).execute(new String[]{"POST"});
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


        // Load Employees from API
        try {
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_id", EEUser.getCurrentUser().userInfo.getString("id")));
            params.add(new BasicNameValuePair("company_id", EEUser.getCurrentUser().userInfo.getString("company_id")));
            params.add(new BasicNameValuePair("location_id", EEUser.getCurrentUser().userInfo.getString("location_id")));

            new EEApiManager("general_data_for_kudos", params, new CompletedListener() {
                @Override
                public void onCompleted(JSONObject result) {
                    try {
                        employees = result.getJSONObject("data").getJSONArray("employees");
                        categories = result.getJSONObject("data").getJSONArray("categories");

                        ArrayList<String> employeeNames = new ArrayList<String>();
                        for (int i = 0; i < employees.length(); i++) {
                            employeeNames.add(employees.getJSONObject(i).getString("first_name") + " " + employees.getJSONObject(i).getString("last_name"));
                        }

                        ArrayList<String> categoryNames = new ArrayList<String>();
                        for (int i = 0; i < categories.length(); i++) {
                            categoryNames.add(categories.getJSONObject(i).getString("name"));
                        }

                        ArrayAdapter<String> employeeSpinnerArrayAdapter = new ArrayAdapter<String>(KudosActivity.this, android.R.layout.simple_spinner_item, employeeNames);
                        employeeSpinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
                        employeeSpinner.setAdapter(employeeSpinnerArrayAdapter);

                        ArrayAdapter<String> categorySpinnerArrayAdapter = new ArrayAdapter<String>(KudosActivity.this, android.R.layout.simple_expandable_list_item_1, categoryNames);
                        employeeSpinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_expandable_list_item_1 );
                        categorySpinner.setAdapter(categorySpinnerArrayAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).execute(new String[]{"GET"});

            employeeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        String selectedEmployeeID = employees.getJSONObject(position).getString("id");

                        EEImageLoader.showImage(profileImageView, StringUtil.userImageURLFromUserID(selectedEmployeeID));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.newKudosRadioButton:
                newKudosScrollView.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.myKudosRadioButton:
                refreshMyKudos();
                newKudosScrollView.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void refreshMyKudos() {
        try {
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_id", EEUser.getCurrentUser().userInfo.getString("id")));

            new EEApiManager("received_kudos", params, new CompletedListener() {
                @Override
                public void onCompleted(JSONObject result) {
                    try {
                        if (result.getString("result").equalsIgnoreCase("success")) {
                            kudosArray = result.getJSONArray("data");
                            if (mAdapter == null) {
                                mAdapter = new MyKudosAdapter(KudosActivity.this, kudosArray);

                                myKudosListView.setAdapter(mAdapter);
                            } else {
                                mAdapter.kudosArray = kudosArray;
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
