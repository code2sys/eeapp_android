package com.johnpepper.eeapp.asynctask;

import android.os.AsyncTask;

import com.johnpepper.eeapp.api.EEApi;
import com.johnpepper.eeapp.listener.CompletedListener;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by borysrosicky on 10/29/15.
 */
public final class EEApiManager extends AsyncTask<String, Void, JSONObject> {
    private CompletedListener listener;
    private String subURL;
    private List<NameValuePair> params;
    private InputStream fileInputStream;
    private Boolean hasMultipartBody;

    public EEApiManager(String subURL, List<NameValuePair> params, CompletedListener listener) {
        this.listener = listener;
        this.subURL = subURL;
        this.params = params;
        this.fileInputStream = null;
        this.hasMultipartBody = false;
    }
    public EEApiManager(String subURL, List<NameValuePair> params, InputStream inputStream, Boolean hasMultipart, CompletedListener listener) {
        this.listener = listener;
        this.subURL = subURL;
        this.params = params;
        this.fileInputStream = inputStream;
        this.hasMultipartBody = hasMultipart;
    }
    @Override

    protected void onPreExecute() {
        //homeActivity.showLoading();
    }

    protected JSONObject doInBackground(String... params) {

        if (this.hasMultipartBody) {
            return new EEApi(params[0], subURL, this.params, this.fileInputStream, this.hasMultipartBody).getAPICallResult();
        } else {
            return new EEApi(params[0], subURL, this.params).getAPICallResult();
        }

    }

    @Override
    protected void onPostExecute(JSONObject json) {
        listener.onCompleted(json);
    }
}
