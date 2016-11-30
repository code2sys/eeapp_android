package com.johnpepper.eeapp.api;

import android.util.Log;

import com.johnpepper.eeapp.app.Constants;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by borysrosicky on 10/29/15.
 */
public class EEApi {
    private List<NameValuePair> postData = new ArrayList<NameValuePair>();
    private String method;
    private String subURL;
    private InputStream fileInputStream;
    private Boolean hasMultipartBody;

    public EEApi(String methodParam, String subURLParam, List<NameValuePair> postDataParam, InputStream inputStream, Boolean hasMultipart)
    {
        method = methodParam;
        subURL = subURLParam;
        postData = postDataParam;
        hasMultipartBody = hasMultipart;
        fileInputStream = inputStream;
    }

    public EEApi(String methodParam, String subURLParam, List<NameValuePair> postDataParam)
    {
        method = methodParam;
        subURL = subURLParam;
        postData = postDataParam;
        hasMultipartBody = false;
        fileInputStream = null;
    }

    public JSONObject getAPICallResult() {
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("success", false);
            jObj.put("error_message", "Unable to connect to API.");
        } catch (JSONException e) {
        }
        try {
            String baseURL = Constants.BASE_URL_STRING;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            String Tag = "3rd";

            URL url;
            if (method == "GET" && postData != null) {
                url = new URL(baseURL + subURL + "/format/json" + "?" + URLEncodedUtils.format(postData, "UTF-8"));
            } else {
                url = new URL(baseURL + subURL + "/format/json");
            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            conn.setDoOutput(false);

            if (method == "POST") {

                if (hasMultipartBody) {
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Content-Type",
                            "multipart/form-data;boundary=" + boundary);

                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    for (NameValuePair param : postData) {
                        dos.writeBytes("Content-Disposition: form-data; name=\"" + param.getName() + "\""
                                + lineEnd);
                        dos.writeBytes("Content-Type: text/plain;charset=UTF-8" + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(param.getValue() + lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                    }

                    dos.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"photo.jpg\"" + lineEnd);
                    dos.writeBytes("Content-type: image/jpeg;" + lineEnd);
                    dos.writeBytes(lineEnd);

                    int bytesAvailable = fileInputStream.available();
                    int maxBufferSize = 1024;
                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    byte[] buffer = new byte[bufferSize];

                    int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    Log.e(Tag, "File is written");
                    fileInputStream.close();
                    dos.flush();

                } else {

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getQuery(postData));
                    writer.flush();
                    writer.close();
                    os.close();
                }
            }

            conn.connect();
            InputStream is = conn.getInputStream();


            String str= "";

            str = convertInputStreamToString(is);

            jObj = new JSONObject(str);
            jObj.put("success", true);

        } catch (UnsupportedEncodingException e) {
//   Log.e("TURBO LIKEZ UE ERROR", e.toString());
        } catch (ClientProtocolException e) {
//   Log.e("TURBO LIKEZ CPE ERROR", e.toString());
        } catch (IOException e) {
           Log.e("TURBO LIKEZ IO ERROR", e.toString());
        } catch (JSONException e) {
//   Log.e("TURBO LIKEZ JSON ERROR", e.toString());
        }

        return jObj;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream, "UTF-8"), 8);
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }


    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
