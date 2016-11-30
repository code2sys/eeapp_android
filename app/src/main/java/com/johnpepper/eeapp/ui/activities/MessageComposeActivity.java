package com.johnpepper.eeapp.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.asynctask.EEApiManager;
import com.johnpepper.eeapp.listener.CompletedListener;
import com.johnpepper.eeapp.model.EEUser;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MessageComposeActivity extends EEActionBarBaseActivity {

    private EditText contentEditText;
    private TextView imageAttached;

    int TAKE_PHOTO_CODE = 0;
    int flgImageAttached = 0;
    public static int count = 0;
    private static final int REQUEST_CAMERARESULT=201;

    private File newfile = null;;
    private String stringPath = null;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_compose);

        setTitle(R.string.activity_messagecompose_title);
        setRightOptionsItemStatus(true);

        Button capture = (Button) findViewById(R.id.cameraBtn);
        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (MessageComposeActivity.this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        ///method to get Images
                        takePhoto();
                    } else {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                            Toast.makeText(MessageComposeActivity.this, "Your Permission is needed to get access the camera", Toast.LENGTH_LONG).show();
                        }
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CAMERARESULT);
                    }
                } else {
                    takePhoto();
                }

                // Here, the counter will be incremented each time, and the
                // picture taken by camera will be stored as 1.jpg,2.jpg
                // and likewise.
                
            }
        });
    }

    private void takePhoto() {

        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();

        count++;
        stringPath = dir + "image.jpg";
        newfile = new File(stringPath);
        try {
            newfile.createNewFile();
        } catch (IOException e) {

        }


        Uri outputFileUri = Uri.fromFile(newfile);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {

            Log.d("CameraDemo", "Pic saved");
            imageAttached.setVisibility(View.VISIBLE);
            flgImageAttached = 1;
        }
    }

    @Override
    protected void initUI() {

        final TextView companyTextView = (TextView) findViewById(R.id.companyTextView);
        contentEditText = (EditText) findViewById(R.id.contentEditText);
        imageAttached = (TextView) findViewById(R.id.textImageAttached);
        imageAttached.setVisibility(View.INVISIBLE);
        //imageAttached.setVisibility(View.VISIBLE);

        try {
            companyTextView.setText(EEUser.getCurrentUser().userInfo.getString("company_description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        ButtonFlat backButton = (ButtonFlat) findViewById(R.id.backButton);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MessageComposeActivity.this.finish();
//
//            }
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message_compose, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_send) {

            String content =  StringEscapeUtils.escapeJava(contentEditText.getText().toString());

            if (!content.isEmpty()) {
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("content",content));
                try {
                    params.add(new BasicNameValuePair("author_id", EEUser.getCurrentUser().userInfo.getString("id")));
                    params.add(new BasicNameValuePair("company_id", EEUser.getCurrentUser().userInfo.getString("company_id")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                InputStream inputStream = null;
                if (stringPath != null) {

                    try {
                        inputStream = new FileInputStream(new File(stringPath));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                new EEApiManager("messages", params, inputStream, inputStream != null, new CompletedListener(){
                //new EEApiManager("messages", params, new CompletedListener() {
                    @Override
                    public void onCompleted(JSONObject result) {
                        MessageComposeActivity.this.finish();
                    }
                }).execute(new String[]{"POST"});
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
