package com.johnpepper.eeapp.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import android.text.TextWatcher;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.app.Constants;
import com.johnpepper.eeapp.asynctask.EEApiManager;
import com.johnpepper.eeapp.listener.CompletedListener;
import com.johnpepper.eeapp.model.EEUser;
import com.johnpepper.eeapp.util.EEImageLoader;
import com.johnpepper.eeapp.util.ImageUtil;
import com.johnpepper.eeapp.util.MessageUtil;
import com.johnpepper.eeapp.util.StringUtil;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.makeramen.roundedimageview.RoundedImageView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//public interface TextWatcher

public class SignUpActivity extends EEActionBarBaseActivity implements ImageChooserListener {

    private JSONArray companies;
    private JSONArray states;
    private JSONArray locations;
    private JSONArray roles;

    private boolean isFriendSignUp;
    private boolean isEditProfile;

    private boolean isSelectedProfileImage;
    private boolean isSelectedCompanyImage;

    //private boolean isFirstKeyDown;
    //private boolean isChangedCompanyText;

    private String  selectedProfileFilePath = null;
    private String  selectedCompanyFilePath = null;

    private ImageChooserManager imageChooserManager;

    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText emailEditText;
    EditText passwordEditText;

    EditText companyEditText;
    EditText stateEditText;
    EditText locationEditText;
    //Spinner  companySpinner;
    //Spinner  stateSpinner;
    //Spinner  locationSpinner;
    Spinner  roleSpinner;

    RoundedImageView profileImageView;
    RoundedImageView companyImageView;

    private LinearLayout companyContainerLayout;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int INVITE_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Intent intent = getIntent();

        isFriendSignUp = intent.getBooleanExtra(Constants.EXTRA_KEY_IS_FRIEND_SIGNUP, false);
        isEditProfile = intent.getBooleanExtra(Constants.EXTRA_KEY_IS_EDIT_PROFILE, false);

        isSelectedProfileImage = false;
        isSelectedCompanyImage = false;

        //isFirstKeyDown = false;
        //isChangedCompanyText = false;

        if (isEditProfile) {
            //setRightOptionsItemStatus(false);
            setTitle(R.string.common_edit_profile);
        } else if (isFriendSignUp) {
            setTitle(R.string.common_friend_signup);
        } else {
            setTitle(R.string.activity_signup_title);
        }
    }


    @Override
    protected void initUI() {

        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        //companySpinner = (Spinner) findViewById(R.id.companySpinner);
        //stateSpinner = (Spinner) findViewById(R.id.stateSpinner);
        //locationSpinner = (Spinner) findViewById(R.id.locationSpinner);

        companyEditText = (EditText) findViewById(R.id.companyNameEditText);
        stateEditText = (EditText) findViewById(R.id.stateNameEditText);
        locationEditText = (EditText) findViewById(R.id.locationNameEditText);

        roleSpinner = (Spinner) findViewById(R.id.roleSpinner);

        profileImageView = (RoundedImageView) findViewById(R.id.profileImageView);
        companyImageView = (RoundedImageView) findViewById(R.id.companyImageView);

        companyContainerLayout = (LinearLayout) findViewById(R.id.companyImageViewLayout);

//        ButtonFlat backButton = (ButtonFlat) findViewById(R.id.backButton);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SignUpActivity.this.finish();
//            }
//        });

        new EEApiManager("general_data_for_signup", null, new CompletedListener() {
            @Override
            public void onCompleted(JSONObject result) {
                try {
                    if (result.getString("result").equalsIgnoreCase("success")) {
                        JSONObject data = result.getJSONObject("data");
                        companies = data.getJSONArray("companies");

                        ArrayList<String> companyNames = new ArrayList<String>();
                        for (int i = 0; i < companies.length(); i++) {
                            companyNames.add(companies.getJSONObject(i).getString("description"));
                        }

                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(SignUpActivity.this, android.R.layout.simple_spinner_item, companyNames);
                        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_expandable_list_item_1);
                        //.simple_spinner_dropdown_item );

                        //companySpinner.setAdapter(spinnerArrayAdapter);

                        if (isEditProfile) {
                            initViewsForEdit();
                        }
                        if (isFriendSignUp){
                            initViewsForFriendSignup();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute(new String[]{"GET"});

        /*
        companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String selectedCompanyID = companies.getJSONObject(position).getString("id");

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("company_id", selectedCompanyID));
                    new EEApiManager("states_for_company", params, new CompletedListener() {
                        @Override
                        public void onCompleted(JSONObject result) {
                            try {
                                states = result.getJSONArray("data");
                                ArrayList<String> names = new ArrayList<String>();
                                for (int i = 0; i < states.length(); i++) {
                                    names.add(states.getJSONObject(i).getString("description"));
                                }
                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(SignUpActivity.this, android.R.layout.simple_spinner_item, names);
                                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                stateSpinner.setAdapter(spinnerArrayAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).execute(new String[]{"GET"});

                    new EEApiManager("roles_for_company", params, new CompletedListener() {
                        @Override
                        public void onCompleted(JSONObject result) {
                            try {
                                roles = result.getJSONArray("data");
                                ArrayList<String> names = new ArrayList<String>();
                                for (int i = 0; i < roles.length(); i++) {
                                    names.add(roles.getJSONObject(i).getString("description"));
                                }
                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(SignUpActivity.this, android.R.layout.simple_spinner_item, names);
                                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                roleSpinner.setAdapter(spinnerArrayAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).execute(new String[]{"GET"});

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }); */

        /*
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String selectedCompanyID = companies.getJSONObject(companySpinner.getSelectedItemPosition()).getString("id");
                    String selectedStateID = states.getJSONObject(position).getString("id");

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("company_id", selectedCompanyID));
                    params.add(new BasicNameValuePair("state_id", selectedStateID));
                    new EEApiManager("locations_for_company_and_state", params, new CompletedListener() {
                        @Override
                        public void onCompleted(JSONObject result) {
                            try {
                                locations = result.getJSONArray("data");
                                ArrayList<String> names = new ArrayList<String>();
                                for (int i = 0; i < locations.length(); i++) {
                                    names.add(locations.getJSONObject(i).getString("description"));
                                }
                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(SignUpActivity.this, android.R.layout.simple_spinner_item, names);
                                spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
                                locationSpinner.setAdapter(spinnerArrayAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).execute(new String[]{"GET"});
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        */

        final EditText Field1 = (EditText)findViewById(R.id.companyNameEditText);

        Field1.setOnFocusChangeListener(new View.OnFocusChangeListener() {


            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                            getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

                    /*
                    * The following code example shows setting an AutocompleteFilter on a PlaceAutocompleteFragment to
                    * set a filter returning only results with a precise address.
                    */
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                            .build();
                    autocompleteFragment.setFilter(typeFilter);

                    autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                        @Override
                        public void onPlaceSelected(Place place) {
                            // TODO: Get info about the selected place.
                            Log.i(TAG, "Place: " + place.getName());//get place details here
                        }

                        @Override
                        public void onError(Status status) {
                            // TODO: Handle the error.
                            Log.i(TAG, "An error occurred: " + status);
                        }
                    });

                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                        .build(SignUpActivity.this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                        Log.i(TAG, "An error occured : " + e.getMessage());
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                        Log.i(TAG, "An error occured : " + e.getMessage());
                    }
                } else {

                }
            }
        });



        Field1.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                if (isEditProfile ){ //&& !isFirstKeyDown){

                    //if (isChangedCompanyText && !isFirstKeyDown){
                    //    return;
                    //}

                    //isFirstKeyDown = true;

                    boolean flgCompanyFound = false;

                    for (int i = 0; i < companies.length(); i++) {
                        try {
                            if (companies.getJSONObject(i).getString("description").equalsIgnoreCase(s.toString())) {
                                flgCompanyFound = true;
                                //selectedRow = i;
                                //companyEditText.setText(companies.getJSONObject(i).getString("description"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if(flgCompanyFound == true){
                        companyContainerLayout.getLayoutParams().height = 0;
                    }
                    else{
                        companyContainerLayout.getLayoutParams().height = 240;
                    }
                }
                else{
                    companyContainerLayout.getLayoutParams().height = 0;
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {


            }
        });

        findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (firstNameEditText.getText().toString().equalsIgnoreCase("")) {
                    MessageUtil.showMessage("First Name should not be empty!", false);
                    return;
                }
                if (emailEditText.getText().toString().equalsIgnoreCase("")) {
                    MessageUtil.showMessage("Email should not be empty!", false);
                    return;
                }

                try {

                    String companyID = "-1";
                    String locationID = "-1";
                    String stateID = "-1";

                    if (!isFriendSignUp){
                        for (int i = 0; i < companies.length(); i++) {
                            if (companies.getJSONObject(i).getString("description").equalsIgnoreCase(companyEditText.getText().toString())) {
                                //companyID = String.valueOf(i);
                                companyID = companies.getJSONObject(i).getString("id");
                            }
                        }

                        for (int i = 0; i < locations.length(); i++) {
                            if (locations.getJSONObject(i).getString("description").equalsIgnoreCase(locationEditText.getText().toString())) {
                                //locationID = String.valueOf(i);
                                locationID = locations.getJSONObject(i).getString("id");
                            }
                        }

                        for (int i = 0; i < states.length(); i++) {
                            if (states.getJSONObject(i).getString("description").equalsIgnoreCase(stateEditText.getText().toString())) {
                                //stateID = String.valueOf(i);
                                stateID = states.getJSONObject(i).getString("id");
                            }
                        }
                    }

                    String roleID = roles.getJSONObject(roleSpinner.getSelectedItemPosition()).getString("id");
                    String roleString = roles.getJSONObject(roleSpinner.getSelectedItemPosition()).getString("description");

                    String companyDescription = companyEditText.getText().toString();
                            //companies.getJSONObject(companySpinner.getSelectedItemPosition()).getString("description");
                    String locationDescription = locationEditText.getText().toString();
                            //locations.getJSONObject(locationSpinner.getSelectedItemPosition()).getString("description");
                    String stateDescription = stateEditText.getText().toString();
                            //states.getJSONObject(stateSpinner.getSelectedItemPosition()).getString("description");

                    final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
                    progressDialog.show();
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    List<NameValuePair> paramsInvite = new ArrayList<NameValuePair>();

                    params.add(new BasicNameValuePair("first_name", firstNameEditText.getText().toString()));
                    params.add(new BasicNameValuePair("last_name", lastNameEditText.getText().toString()));
                    params.add(new BasicNameValuePair("email", emailEditText.getText().toString()));
                    params.add(new BasicNameValuePair("company_id", companyID));
                    params.add(new BasicNameValuePair("location_id", locationID));
                    params.add(new BasicNameValuePair("state_id", stateID));
                    params.add(new BasicNameValuePair("role_id", roleID));

                    params.add(new BasicNameValuePair("company_description", companyDescription));
                    params.add(new BasicNameValuePair("location_description", locationDescription));
                    params.add(new BasicNameValuePair("state_description", stateDescription));

                    String subURL;
                    if (isEditProfile) {
                        subURL = "update_user";
                        params.add(new BasicNameValuePair("id", EEUser.getCurrentUser().userInfo.getString("id")));

                        if (!passwordEditText.getText().toString().equalsIgnoreCase(""))
                            params.add(new BasicNameValuePair("password", passwordEditText.getText().toString()));

                    }
                    else if (isFriendSignUp){
                        subURL = "invite";

                        paramsInvite.add(new BasicNameValuePair("first_name", firstNameEditText.getText().toString()));
                        paramsInvite.add(new BasicNameValuePair("last_name", lastNameEditText.getText().toString()));
                        paramsInvite.add(new BasicNameValuePair("email", emailEditText.getText().toString()));
                        paramsInvite.add(new BasicNameValuePair("company", companyDescription));
                        paramsInvite.add(new BasicNameValuePair("location", locationDescription));
                        paramsInvite.add(new BasicNameValuePair("state", stateDescription));

                        paramsInvite.add(new BasicNameValuePair("role", roleString));
                        paramsInvite.add(new BasicNameValuePair("inviter_id", EEUser.getCurrentUser().userInfo.getString("id")));
                    }
                    else {
                        subURL = "users";
                        params.add(new BasicNameValuePair("password", passwordEditText.getText().toString()));
                    }

                    InputStream inputStream = null;
                    if (selectedProfileFilePath != null) {
                        inputStream = new FileInputStream(new File(selectedProfileFilePath));
                    }
                    else if (selectedCompanyFilePath != null){
                        inputStream = new FileInputStream(new File(selectedCompanyFilePath));
                    }
                    else if (!isEditProfile) {
                        inputStream = getResources().openRawResource(+ R.mipmap.default_profile_placeholder);
                    }

                    if (isEditProfile){
                        new EEApiManager(subURL, params, inputStream, inputStream != null, new CompletedListener() {

                            @Override
                            public void onCompleted(JSONObject result) {

                                progressDialog.dismiss();
                                try {

                                    if (result.getString("result").equalsIgnoreCase("success")) {
                                        if (isEditProfile || isFriendSignUp) {
                                            if (isEditProfile) {
                                                EEUser.setCurrentUser(result.getJSONObject("data"), true);
                                            }
                                            if (selectedProfileFilePath != null) {
                                                EEImageLoader.clearImageFromCache(StringUtil.userImageURLFromUserID(EEUser.getCurrentUser().userInfo.getString("id")));
                                            }
//                                        SignUpActivity.this.finish();
                                        } else {
//                                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
//                                        SignUpActivity.this.finish();
//                                        startActivity(intent);
                                        }
                                        popIntent();

                                    } else {
                                        MessageUtil.showMessage(result.getString("message"), true);
                                    }

                                } catch (JSONException e) {

                                    e.printStackTrace();

                                }

                            }

                        }).execute(new String[]{"POST"});
                    }

                    if (isFriendSignUp){
                        new EEApiManager(subURL, paramsInvite, inputStream, inputStream != null, new CompletedListener() {

                            @Override
                            public void onCompleted(JSONObject result) {

                                progressDialog.dismiss();
                                try {

                                    if (result.getString("result").equalsIgnoreCase("success")) {
                                        if (isEditProfile || isFriendSignUp) {

                                            if (selectedProfileFilePath != null) {
                                                EEImageLoader.clearImageFromCache(StringUtil.userImageURLFromUserID(EEUser.getCurrentUser().userInfo.getString("id")));
                                            }
//                                        SignUpActivity.this.finish();
                                        } else {
//                                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
//                                        SignUpActivity.this.finish();
//                                        startActivity(intent);
                                        }
                                        //popIntent();

                                        String url = result.getJSONObject("data").getString("url");
                                        //String url = data.getString("url");

                                        Intent intent=new Intent(Intent.ACTION_SEND);
                                        String[] recipients={ emailEditText.getText().toString() };

                                        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                                        intent.putExtra(Intent.EXTRA_SUBJECT,"EEApp Sign Up");
                                        //intent.putExtra(Intent.EXTRA_TEXT, "Your friend has invited you to join EEApp. Click <a href='%s'>here</a> to sign up. Thanks.");

                                        String body = String.format("Your friend %s %s has invited you to join EEApp. Click <a href='%s'> here </a> to sign up", EEUser.getCurrentUser().userInfo.getString("first_name"), EEUser.getCurrentUser().userInfo.getString("last_name"), url);
                                        intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
                                        intent.setType("text/html");

                                        //startActivity(Intent.createChooser(intent, "Send mail"));

                                        startActivityForResult(Intent.createChooser(intent, "Send mail"), INVITE_REQUEST_CODE);

                                    } else {
                                        MessageUtil.showMessage(result.getString("message"), true);
                                    }

                                } catch (JSONException e) {

                                    e.printStackTrace();

                                }

                            }

                        }).execute(new String[]{"POST"});
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(SignUpActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                dialog.setContentView(R.layout.dialog_image_picker);
                RelativeLayout camera = (RelativeLayout) dialog.findViewById(R.id.camera_dialog_btn);
                RelativeLayout gallery = (RelativeLayout) dialog.findViewById(R.id.gallery_dialog_btn);
                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageChooserManager = new ImageChooserManager(SignUpActivity.this, ChooserType.REQUEST_PICK_PICTURE);
                        imageChooserManager.setImageChooserListener(SignUpActivity.this);
                        try {
                            isSelectedProfileImage = true;
                            imageChooserManager.choose();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });

                camera.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        imageChooserManager = new ImageChooserManager(SignUpActivity.this, ChooserType.REQUEST_CAPTURE_PICTURE);
                        imageChooserManager.setImageChooserListener(SignUpActivity.this);
                        try {
                            isSelectedProfileImage = true;
                            imageChooserManager.choose();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        dialog.dismiss();

                    }

                    private String getImageName() {

                        StringBuffer sbf = new StringBuffer();
                        Calendar c = Calendar.getInstance();
                        int year = c.get(Calendar.YEAR);
                        int month = c.get(Calendar.MONTH);
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        int seconds = c.get(Calendar.SECOND);
                        sbf.append("image").append(1).append(year).append(month).append(hour).append(minute).append(seconds).append(".jpg");
                        return sbf.toString();
                    }
                });

                dialog.show();
            }
        });

        companyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(SignUpActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                dialog.setContentView(R.layout.dialog_image_picker);
                RelativeLayout camera = (RelativeLayout) dialog.findViewById(R.id.camera_dialog_btn);
                RelativeLayout gallery = (RelativeLayout) dialog.findViewById(R.id.gallery_dialog_btn);
                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageChooserManager = new ImageChooserManager(SignUpActivity.this, ChooserType.REQUEST_PICK_PICTURE);
                        imageChooserManager.setImageChooserListener(SignUpActivity.this);
                        try {
                            isSelectedCompanyImage = true;
                            imageChooserManager.choose();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });

                camera.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        imageChooserManager = new ImageChooserManager(SignUpActivity.this, ChooserType.REQUEST_CAPTURE_PICTURE);
                        imageChooserManager.setImageChooserListener(SignUpActivity.this);
                        try {
                            isSelectedCompanyImage = true;
                            imageChooserManager.choose();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        dialog.dismiss();

                    }

                    private String getImageName() {

                        StringBuffer sbf = new StringBuffer();
                        Calendar c = Calendar.getInstance();
                        int year = c.get(Calendar.YEAR);
                        int month = c.get(Calendar.MONTH);
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        int seconds = c.get(Calendar.SECOND);
                        sbf.append("image").append(1).append(year).append(month).append(hour).append(minute).append(seconds).append(".jpg");
                        return sbf.toString();
                    }
                });

                dialog.show();
            }
        });
    }

    private void initViewsForFriendSignup() throws JSONException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("company_id", EEUser.getCurrentUser().userInfo.getString("company_id")));

        new EEApiManager("roles_for_company", params, new CompletedListener() {
            @Override
            public void onCompleted(JSONObject result) {
                try {
                    roles = result.getJSONArray("data");
                    ArrayList<String> names = new ArrayList<String>();
                    for (int i = 0; i < roles.length(); i++) {
                        names.add(roles.getJSONObject(i).getString("description"));
                    }
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(SignUpActivity.this, android.R.layout.simple_spinner_item, names);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    roleSpinner.setAdapter(spinnerArrayAdapter);

                    int selectedRow = 0;
                    for (int i = 0; i < roles.length(); i++) {
                        if (roles.getJSONObject(i).getString("id").equalsIgnoreCase(EEUser.getCurrentUser().userInfo.getString("role_id"))) {
                            selectedRow = i;
                        }
                    }
                    roleSpinner.setSelection(selectedRow);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute(new String[]{"GET"});

        companyContainerLayout.getLayoutParams().height = 0;

    }

    private void initViewsForEdit() {

        try {

            firstNameEditText.setText(EEUser.getCurrentUser().userInfo.getString("first_name"));
            lastNameEditText.setText(EEUser.getCurrentUser().userInfo.getString("last_name"));
            emailEditText.setText(EEUser.getCurrentUser().userInfo.getString("email"));

            int selectedRow = 0;
            for (int i = 0; i < companies.length(); i++) {
                if (companies.getJSONObject(i).getString("id").equalsIgnoreCase(EEUser.getCurrentUser().userInfo.getString("company_id"))) {
                    selectedRow = i;
                    //isChangedCompanyText = true;
                    companyEditText.setText(companies.getJSONObject(i).getString("description"));
                }
            }
            //companySpinner.setSelection(selectedRow);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("company_id", EEUser.getCurrentUser().userInfo.getString("company_id")));
            new EEApiManager("states_for_company", params, new CompletedListener() {
                @Override
                public void onCompleted(JSONObject result) {
                    try {
                        states = result.getJSONArray("data");
                        ArrayList<String> names = new ArrayList<String>();
                        for (int i = 0; i < states.length(); i++) {
                            names.add(states.getJSONObject(i).getString("description"));
                        }
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(SignUpActivity.this, android.R.layout.simple_spinner_item, names);
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //stateSpinner.setAdapter(spinnerArrayAdapter);

                        int selectedRow = 0;
                        for (int i = 0; i < states.length(); i++) {
                            if (states.getJSONObject(i).getString("id").equalsIgnoreCase(EEUser.getCurrentUser().userInfo.getString("state_id"))) {
                                selectedRow = i;
                                stateEditText.setText(states.getJSONObject(i).getString("description"));
                            }
                        }
                        //stateSpinner.setSelection(selectedRow);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(new String[]{"GET"});

            new EEApiManager("roles_for_company", params, new CompletedListener() {
                @Override
                public void onCompleted(JSONObject result) {
                    try {
                        roles = result.getJSONArray("data");
                        ArrayList<String> names = new ArrayList<String>();
                        for (int i = 0; i < roles.length(); i++) {
                            names.add(roles.getJSONObject(i).getString("description"));
                        }
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(SignUpActivity.this, android.R.layout.simple_spinner_item, names);
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        roleSpinner.setAdapter(spinnerArrayAdapter);

                        int selectedRow = 0;
                        for (int i = 0; i < roles.length(); i++) {
                            if (roles.getJSONObject(i).getString("id").equalsIgnoreCase(EEUser.getCurrentUser().userInfo.getString("role_id"))) {
                                selectedRow = i;
                            }
                        }
                        roleSpinner.setSelection(selectedRow);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(new String[]{"GET"});

            params.add(new BasicNameValuePair("state_id", EEUser.getCurrentUser().userInfo.getString("state_id")));
            new EEApiManager("locations_for_company_and_state", params, new CompletedListener() {
                @Override
                public void onCompleted(JSONObject result) {
                    try {
                        locations = result.getJSONArray("data");
                        ArrayList<String> names = new ArrayList<String>();
                        for (int i = 0; i < locations.length(); i++) {
                            names.add(locations.getJSONObject(i).getString("description"));
                        }
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(SignUpActivity.this, android.R.layout.simple_spinner_item, names);
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item );
                        //locationSpinner.setAdapter(spinnerArrayAdapter);

                        int selectedRow = 0;
                        for (int i = 0; i < locations.length(); i++) {
                            if (locations.getJSONObject(i).getString("id").equalsIgnoreCase(EEUser.getCurrentUser().userInfo.getString("location_id"))) {
                                selectedRow = i;
                                locationEditText.setText(locations.getJSONObject(i).getString("description"));
                            }
                        }

                        //locationSpinner.setSelection(selectedRow);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(new String[]{"GET"});

            EEImageLoader.showImage(profileImageView, StringUtil.userImageURLFromUserID(EEUser.getCurrentUser().userInfo.getString("id")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {

            //isFirstKeyDown = false;

            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                String address = place.getAddress().toString();
                String[] separated = address.split(",");
                String state = "";

                String name = place.getName().toString();

                for (int i = 0; i < separated.length; i++) {
                    separated[i] = separated[i].trim();
                }

                if (separated.length > 4){

                    address = "";
                    for (int i=0; i < separated.length - 2; i++){
                        if (separated[i].compareTo("") == 1){
                                //(separated[i].compareTo("") == true){
                            continue;
                        }
                        if (i == separated.length - 3){
                            address += separated[i];
                        }
                        else{
                            address += separated[i] + ", ";
                        }
                    }
                    //address = separated[0] + ", " + separated[1] + ", " + separated[2];

                    String[] states_separated = separated[separated.length - 2].split(" ");

                    if (Character.isDigit(states_separated[states_separated.length - 1].charAt(0)) &&
                            Character.isDigit(states_separated[states_separated.length - 1].charAt(states_separated[states_separated.length - 1].length() - 1)) ){

                        for (int i=0; i < states_separated.length - 1; i++){
                            states_separated[i] = states_separated[i].trim();
                            state += states_separated[i] + " ";
                        }
                        //state = states_separated[0];
                    }
                    else{
                        for (int i=0; i < states_separated.length; i++){
                            states_separated[i] = states_separated[i].trim();
                            state += states_separated[i] + " ";
                        }
                    }
                }

                if (separated.length == 4){
                    address = separated[0] + ", " + separated[1];

                    String[] states_separated = separated[2].split(" ");

                    if (Character.isDigit(states_separated[1].charAt(0)) &&
                            Character.isDigit(states_separated[1].charAt(states_separated[1].length() - 1)) ){
                        state = states_separated[0];
                    }
                }

                if (separated.length == 3){
                    address = separated[0];

                    String[] states_separated = separated[1].split(" ");

                    if (Character.isDigit(states_separated[1].charAt(0)) &&
                            Character.isDigit(states_separated[1].charAt(states_separated[1].length() - 1)) ){
                        state = states_separated[0];
                    }
                }

                String add = PlaceAutocomplete.getPlace(this, data).getAddress().toString();

                //isChangedCompanyText = true;
                //isFirstKeyDown = true;

                boolean flgCompanyFound = false;

                for (int i = 0; i < companies.length(); i++) {
                    try {
                        if (companies.getJSONObject(i).getString("description").equalsIgnoreCase(name)) {
                            flgCompanyFound = true;
                            //selectedRow = i;
                            //companyEditText.setText(companies.getJSONObject(i).getString("description"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(flgCompanyFound == true){
                    companyContainerLayout.getLayoutParams().height = 0;
                }
                else{
                    companyContainerLayout.getLayoutParams().height = 240;
                }

                companyEditText.setText(name);
                stateEditText.setText(state);
                locationEditText.setText(address);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (requestCode == INVITE_REQUEST_CODE){
            //if (resultCode == RESULT_OK){
                popIntent();
            //}
        }

       if (resultCode == Activity.RESULT_OK &&
                (requestCode == ChooserType.REQUEST_PICK_PICTURE||
                        requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            imageChooserManager.submit(requestCode, data);
        }


    }

    @Override
    public void onImageChosen(final ChosenImage image) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (image != null) {
                    String path = image.getFileThumbnail();
                    Bitmap bmp = ImageUtil.decodeSampledBitmapFromResource(path, 100, 100);

                    if (isSelectedProfileImage == true) {
                        profileImageView.setImageBitmap(bmp);
                        selectedProfileFilePath = path;
                    } else if (isSelectedCompanyImage == true) {
                        companyImageView.setImageBitmap(bmp);
                        selectedCompanyFilePath = path;
                    }

                }

            }

        });
    }

    @Override
    public void onError(String s) {

    }

    @Override
    public void onBackPressed() {
        popIntent();
    }
}
