package com.akash.surveryapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.akash.surveryapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 05-07-2016.
 */
public class Preference {

    public static final String APP_PREF = "MyPreferences";
    public static SharedPreferences sp;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public static String KEY_SUBMIT_REPORT_DATE = "Report";
    public static String KEY_LICENSE_KEY = "License key";
    public static String KEY_ACTIVE_USER_CHECK_DATE = "date";
    public static String KEY_LOCATION_ALERT_DATE = "locationAlertDate";
    public static String KEY_LOCATION_ALERT_CLOSE = "locationAlertClose";
    public static String KEY_MUTE_NOTIFICATION_STATUS = "MuteNotification";
    public static String KEY_VERSION_CODE = "Version_code";
    public static String KEY_CURRENCY_SYMBOL = "currencySymbol";
    public static String KEY_VALUE_RECEIVED = "IsValueReceived";
    public static String KEY_PID_CHANGED = "ProcessIdChanged";
    public static String KEY_LAST_BEAT_SYNCED = "beatPlanSyncTime";

    public static String KEY_IS_RECORD = "isfromRecord";
    public static String KEY_COMPANY_ID = "companyId";
    public static String KEY_ONDUTY_SYNC_STATUS = "OnDutySyncStatus";
    public static String KEY_LATITUDE = "latitude";
    public static String KEY_LONGITUDE = "longitude";
    public static String KEY_APPVERSION_FIREBASE = "appVersionFirebase";
    public static String KEY_APPVERSION_NAME_FIREBASE = "appVersionNameFirebase";
    public static String USERNAME = "username";
    public static String PASSWORD = "password";
    public static String AUTHTOKEN = "authtoken";
    public static String TOKENTIMESTAMP = "tokentimestamp";
    public static String MOBILENUMBER = "usermobilenumber";
    public static String BATTERYSTATUS = "batterystatus";
    public static String KEY_LOGO = "CompanyLogo";
    public static String DisableLock = "testlock";
    public static String KEY_APP_VERSION_CODE = "APP_Version_code";
    public static String KEY_LAST_TIME_SWICH = "LastTimeSwitchEntry";
    public static String KEY_LAST_PRODUCT_SYNCDATE = "LastTimeProductsEntry";
    public static String KEY_LAST_OUTLET_SYNCDATE = "LastTimeOutletEntry";
    public static String KEY_LAST_BEATPLAN_SYNCDATE = "LastTimeBeatPlanEntry";
    public static String KEY_PREFFERED_LOCATION_SETTINGS = "prefferedLocationSetting";
    public static String KEY_CUSTOM_FIELD_LIST = "custom_field_list";
    public static String KEY_GPS_STATUS = "gpsStatus";
    public static String KEY_TIME_SWITCH_STATUS = "timeSwitchStatus";
    public static String KEY_LOCATION_TIMER_VALUE = "locationTimerValue";
    public static String KEY_OLD_LOCATION_SPEED_VALUE = "locationOldSpeedValue";
    public static String KEY_NEW_LOCATION_SPEED_VALUE = "locationNewSpeedValue";
    public static String KEY_LOCATION_SPEED_VALUE = "locationSpeedValue";
    public static String KEY_LOCATION_SPEED_LAST_SAVED = "locationSpeedBool";
    public static String KEY_LOCATION_PERM_EVENT = "locationPermEvent";
    public static String KEY_CAMERA_PERM_EVENT = "cameraPermEvent";
    public static String KEY_READ_PERM_EVENT = "readPermEvent";
    public static String KEY_WRITE_PERM_EVENT = "writePermEvent";
    public static String KEY_BADGE_COUNT = "badge";
    public static String KEY_IMG_COMP_PERCENTAGE = "imageCompressionPercent";

    public Preference(Context context) {
        sharedPreferences = context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    private static Gson GSON = new Gson();
    Type typeOfObject = new TypeToken<Object>() {
    }.getType();



    public static void save(Context context, String key, String value) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }




    public static String get(Context context, String key) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        return sp.getString(key, "");
    }

    public static String getString(Context context, String key, String defaultValue) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        return sp.getString(key, defaultValue);
    }

    public static String getCurrencySymbol(Context context) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        return sp.getString(KEY_CURRENCY_SYMBOL, "Rupee");
    }

    public static void saveBoolean(Context context, String key, boolean value) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public static boolean getBoolean(Context context, String key) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        return sp.getBoolean(key, false);
    }

    public static void saveInteger(Context context, String key, int value) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public static int getInteger(Context context, String key) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        return sp.getInt(key, 0);
    }

    public static void saveFloat(Context context, String key, float value) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putFloat(key, value);
        edit.commit();
    }

    public static float getFloat(Context context, String key) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        return sp.getFloat(key, -1f);
    }

    public static void clearPreference(Context context) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.clear();
        edit.commit();
    }

    public void setListValue(String key, List<String> value) {
        Gson gson = new Gson();
        String jsonData = gson.toJson(value);
        editor.putString(key, jsonData);
        editor.commit();
    }

    public void setArrayList(String key, ArrayList<JSONObject> value) {
        Gson gson = new Gson();
        String jsonData = gson.toJson(value);
        editor.putString(key, jsonData);
        editor.commit();
    }

    public ArrayList<JSONObject> getArrayList(String key) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<JSONObject> arrayList = gson.fromJson(json, type);
        return arrayList;
    }

    public ArrayList<String> getListValue(String key) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> arrayList = gson.fromJson(json, type);
        return arrayList;
    }

   /* public static <T> void saveGenericList(Context context, String key, List<T> value) {
        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray = gson.toJsonTree(value).getAsJsonArray();
        Preference.save(context, key, jsonArray.toString());
    }*/

    public static <T> List<T> getGenericList(Context context, String key, TypeToken<List<T>> tt) {
        Gson gson = new Gson();
        return (gson.fromJson(Preference.getString(context, key, "[]"), tt.getType()));
    }




    // to save object in prefrence
    public void saveObject(String key, Object object) {
        if (object == null) {
            throw new IllegalArgumentException("object is null");
        }

        if (key.equals("") || key == null) {
            throw new IllegalArgumentException("key is empty or null");
        }

        editor.putString(key, GSON.toJson(object)).apply();
    }

    // To get object from prefrences

    public <T> T getObject(String key, Class<T> a) {

        String gson = sharedPreferences.getString(key, null);
        if (gson == null) {
            return null;
        } else {
            try {
                return GSON.fromJson(gson, a);
            } catch (Exception e) {
                throw new IllegalArgumentException("Object storaged with key "
                        + key + " is instanceof other class");
            }
        }
    }
}
