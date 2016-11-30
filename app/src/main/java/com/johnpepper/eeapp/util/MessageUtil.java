package com.johnpepper.eeapp.util;

import android.content.Context;
import android.os.Environment;
import android.view.Gravity;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by borysrosicky on 10/29/15.
 */
public class MessageUtil {
    private static Context context;
    private static final boolean LOG_ENABLED = false;


    //  ############################################################################
    //  #                                MessageUtil                               #
    //  ############################################################################
    // Mandatory: We need to initialize this class when the application starts.
    public static void initializeMesageUtil(Context applicationContext) {
        context = applicationContext;
    }

    public static void showMessage(String message, boolean displayedByLongTime) {
        if (displayedByLongTime) {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        } else {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }

    public static void showMessage(int res_id, boolean displayedByLongTime) {
        if (displayedByLongTime) {
            Toast toast = Toast.makeText(context, res_id, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        } else {
            Toast toast = Toast.makeText(context, res_id, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }

    public static void showMessage(List<String> message_array, boolean displayedByLongTime) {
        if (message_array == null)
            return;

        String message = "";
        for (int i = 0; i < message_array.size(); i++) {
            message += message_array.get(i) + "\n";
        }
        if (displayedByLongTime) {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        } else {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }

    private static String currentDateTime() {
        SimpleDateFormat sFmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss Z");
        return sFmt.format(new java.util.Date());
    }

    public static void logMessage(String message, boolean displayByLongTime) {
        //if (!LOG_ENABLED) return;

        File root = null;
        File logfile = null;
        FileWriter writer = null;
        boolean externalStorageAvailable = false;
        boolean externalStorageWriteable = false;
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                externalStorageAvailable = externalStorageWriteable = true;
            } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                externalStorageAvailable = true;
                externalStorageWriteable = false;
            } else {
                externalStorageAvailable = externalStorageWriteable = false;
            }
            if (externalStorageAvailable && externalStorageWriteable) {
                root = new File(Environment.getExternalStorageDirectory(), "healthchatLogs");
                if (!root.exists()) {
                    root.mkdirs();
                }
                logfile = new File(root, "Location.log");
                writer = new FileWriter(logfile, true);
                StringBuffer buf = new StringBuffer();
                buf.append(currentDateTime());
                buf.append(' ');
                buf.append(message);
                buf.append('\n');
                System.out.println("###### Writing to sdcard ########### "+buf.toString());
                writer.append(buf.toString());
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //  ############################################################################
}