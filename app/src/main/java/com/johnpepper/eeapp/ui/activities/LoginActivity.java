package com.johnpepper.eeapp.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.app.Constants;
import com.johnpepper.eeapp.asynctask.EEApiManager;
import com.johnpepper.eeapp.listener.CompletedListener;
import com.johnpepper.eeapp.model.EEUser;
import com.johnpepper.eeapp.util.EEPreferenceManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends EEBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initUI() {

        final EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        final CheckBox rememberMeCheckBox = (CheckBox) findViewById(R.id.rememberMeCheckBox);

        // emailEditText.setText(EEPreferenceManager.getString(Constants.PREF_KEY_CURRENT_USER_EMAIL, ""));

        findViewById(R.id.logInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.show();
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("email", emailEditText.getText().toString()));
                params.add(new BasicNameValuePair("password", passwordEditText.getText().toString()));
                new EEApiManager("users", params, new CompletedListener() {

                    @Override
                    public void onCompleted(JSONObject result) {

                        progressDialog.dismiss();
                        try {

                            if (result.getString("result").equalsIgnoreCase("success")) {

                                if (rememberMeCheckBox.isChecked()) {

                                    EEUser.setCurrentUser(result.getJSONObject("data"), true);
                                    EEPreferenceManager.setBoolean(Constants.PREF_KEY_LOGGED_IN, true);
                                }
                                EEPreferenceManager.setString(Constants.PREF_KEY_CURRENT_USER_EMAIL, emailEditText.getText().toString());

                                Intent intent = new Intent(LoginActivity.this, ReviewActivity.class);
                                pushIntent(intent, true);
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        }

                    }

                }).execute(new String[]{"GET"});

            }
        });

        findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                pushIntent(intent);
            }
        });

        findViewById(R.id.forgotButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                intent.putExtra(Constants.EXTRA_KEY_EMAIL, emailEditText.getText().toString());
                pushIntent(intent);
            }
        });
    }

}
