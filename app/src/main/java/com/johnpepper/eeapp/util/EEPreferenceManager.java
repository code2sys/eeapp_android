package com.johnpepper.eeapp.util;

import android.content.SharedPreferences;

/**
 * Created by borysrosicky on 10/29/15.
 */
public class EEPreferenceManager {

    private static SharedPreferences preferences;

    public static class PreferenceKeys {
        public static String STRING_LAST_SINGED_USER_LOGIN = "string_last_singed_user_login";
        public static String STRING_LAST_SINGED_USER_EMAIL = "string_last_singed_user_email";
        public static String STRING_LAST_SINGED_USER_PASSWORD = "string_last_singed_user_password";
        public static String STRING_USER_TOKEN = "string_user_token";
        public static String INT_EMOJI_ACTIVITY = "int_emoji_activity";
    }

    public static void initializePreferenceManager(SharedPreferences _preferences) {
        preferences = _preferences;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }
    public static void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static int getInteger(String key, int defaultInt) {
        return preferences.getInt(key, defaultInt);
    }
    public static void setInt(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static String getString(String key,String defaultValue) {
        return preferences.getString(key,defaultValue);
    }
    public static void setString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
}