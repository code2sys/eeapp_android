package com.johnpepper.eeapp.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.app.Constants;
import com.johnpepper.eeapp.asynctask.EEApiManager;
import com.johnpepper.eeapp.listener.CompletedListener;
import com.johnpepper.eeapp.util.MessageUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForgotPasswordActivity extends EEActionBarBaseActivity {

    String email;
    EditText txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setTitle(R.string.activity_forgotpassword_title);
        Intent intent = getIntent();
        email = intent.getStringExtra(Constants.EXTRA_KEY_EMAIL);
    }

    @Override
    protected void initUI() {

        txtEmail = (EditText) findViewById(R.id.emailEditText);
        txtEmail.setText(email);

        findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtEmail.getText().toString().equalsIgnoreCase("")) {
                    MessageUtil.showMessage("Email should not be empty!", false);
                    return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
                progressDialog.show();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", txtEmail.getText().toString()));

                new EEApiManager("reset_password", params, new CompletedListener() {
                    @Override
                    public void onCompleted(JSONObject result) {
                        progressDialog.dismiss();
                        try {
                            if (result.getString("result").equalsIgnoreCase("success")) {
                                MessageUtil.showMessage("We've sent an email just now. Please check your inbox.", true);
                            } else {
                                MessageUtil.showMessage(result.getString("message"), true);
                            }
                        } catch (Exception e) {

                        }
                    }
                }).execute(new String[]{"GET"});

            }
        });
    }

}
