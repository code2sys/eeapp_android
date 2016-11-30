package com.johnpepper.eeapp.model;

import com.johnpepper.eeapp.app.Constants;
import com.johnpepper.eeapp.util.EEPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by borysrosicky on 10/29/15.
 */
public class EEUser {
    public JSONObject userInfo;
    private static EEUser currentUser;

    public static EEUser getCurrentUser() {

        if (currentUser == null) {

            String currentUserJsonInfo = EEPreferenceManager.getString(Constants.PREF_KEY_CURRENT_USER_INFO, "");
            if (currentUserJsonInfo.equalsIgnoreCase("")) {

                currentUser = null;

            } else {

                try {

                    currentUser = new EEUser();
                    currentUser.userInfo = new JSONObject(currentUserJsonInfo);

                } catch (JSONException e) {

                    e.printStackTrace();

                }

            }

        }

        return currentUser;

    }

    public static void setCurrentUser(JSONObject userInfo, boolean isSaving) {
        if (currentUser == null) {
            currentUser = new EEUser();
        }
        currentUser.userInfo = userInfo;
        if (isSaving)
            EEPreferenceManager.setString(Constants.PREF_KEY_CURRENT_USER_INFO, currentUser.userInfo.toString());
    }

    public static void logOut() {
        EEPreferenceManager.setString(Constants.PREF_KEY_CURRENT_USER_INFO, "");
        EEPreferenceManager.setBoolean(Constants.PREF_KEY_LOGGED_IN, false);
    }

    public boolean isEmployee() {
        try {
            if (userInfo.getInt("role_id") == 1) {
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        }



    }
}
