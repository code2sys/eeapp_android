package com.johnpepper.eeapp.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
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

public class ResetPasswordActivity extends EEActionBarBaseActivity {

    String resetCode;
    EditText txtPassword;
    EditText txtConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        setTitle(R.string.activity_forgotpassword_title);

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        List<String> paths = data.getPathSegments();
        String lastpath = null;
        if (paths.size() > 0)
            lastpath = paths.get(paths.size() - 1);

        if (lastpath != null && !lastpath.equals("reset_code")) {
            // http://server/reset_code/<reset_code>
            resetCode = lastpath;
        } else {
            // eeapp://reset_code?<reset_code>
            resetCode = data.getQuery();
        }
    }

    @Override
    protected void initUI() {

        txtPassword = (EditText) findViewById(R.id.passwordEditText);
        txtConfirm = (EditText) findViewById(R.id.confirmEditText);

        findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (resetCode == null || resetCode.length() == 0) {
                    MessageUtil.showMessage("Invalid reset token.", false);
                    return;
                }

                if (txtPassword.getText().toString().equalsIgnoreCase("") ||
                        txtConfirm.getText().toString().equalsIgnoreCase("")) {
                    MessageUtil.showMessage("Fill in all the fields.", false);
                    return;
                }

                if (!txtPassword.getText().toString().equals(txtConfirm.getText().toString())) {
                    MessageUtil.showMessage("Passwords do not match.", false);
                    return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(ResetPasswordActivity.this);
                progressDialog.show();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("password", txtPassword.getText().toString()));
                params.add(new BasicNameValuePair("code", resetCode));

                new EEApiManager("reset_password", params, new CompletedListener() {
                    @Override
                    public void onCompleted(JSONObject result) {
                        progressDialog.dismiss();
                        try {
                            if (result.getString("result").equalsIgnoreCase("success")) {
                                MessageUtil.showMessage("Your password has been changed successfully", true);
                            } else {
                                int errorCode = result.getInt("code");
                                if (errorCode == 510) {
                                    MessageUtil.showMessage("Invalid access.", true);
                                } else if (errorCode == 511) {
                                    MessageUtil.showMessage("The session is expired. Please try again later.", true);
                                } else if (errorCode == 512) {
                                    MessageUtil.showMessage("Something went wrong, please contact support team.", true);
                                }
                            }
                        } catch (Exception e) {
                            MessageUtil.showMessage("Something went wrong, please contact support team.", true);
                        }
                    }
                }).execute(new String[]{"POST"});

            }
        });
    }
}
