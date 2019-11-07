package com.rozvi14.facialrecognition.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import static com.rozvi14.facialrecognition.utils.PreferencesUtility.*;

public class SaveSharedPreference {
    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    //Set the Login Status
    public static void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED_IN_PREF, loggedIn);
        editor.apply();
    }


    //Get the Login Status
    public static boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean(LOGGED_IN_PREF,false);
    }

    //set token
    public static void setToken(Context context, String token) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(TOKEN_IN_PREF, token);
        editor.apply();
    }

    //Get token
    public static String getToken(Context context) {
        return getPreferences(context).getString(TOKEN_IN_PREF,"");
    }

    //set username
    public static void setUserName(Context context, String username) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(USERNAME_IN_PREF, username);
        editor.apply();
    }


    //Get username
    public static String getUserName(Context context) {
        return getPreferences(context).getString(USERNAME_IN_PREF,"");
    }

    //set idClient
    public static void setIdClient(Context context, String idClient) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(IDCLIENT_IN_PREF, idClient);
        editor.apply();
    }

    //Get idClient
    public static String getIdClient(Context context) {
        return getPreferences(context).getString(IDCLIENT_IN_PREF,"");
    }
}
