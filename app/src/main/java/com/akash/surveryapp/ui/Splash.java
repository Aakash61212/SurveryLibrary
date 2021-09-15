package com.akash.surveryapp.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.content.ContextCompat;

import com.akash.surveryapp.R;
import com.akash.surveryapp.utils.CheckConnection;
import com.akash.surveryapp.utils.Preference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PRATITI on 01-05-2015.
 */
public class Splash extends BaseActivity {

    public static SharedPreferences sp;
    public static final String MY_PREF = "MyPreferences";
    private String userId;
    private String TAG = "Splash";
    private boolean LoginStatus;
    private String IsCheckedUserValidate = "0";
    Context context;
    Dialog mDialog;
    ConnectivityManager mConnectivity;
    NetworkInfo info;

    /**
     * Called when the activity is firs`t created.
     */
    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.
    public static String[] permissions;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        sp = getSharedPreferences(MY_PREF, 0);
        mDialog = new Dialog(this, R.style.AlertDialogCustom);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        context = this;
        if (sp != null) {
            userId = sp.getString("userId", "");
            LoginStatus = sp.getBoolean("LoginStatus", false);
            IsCheckedUserValidate = sp.getString("IsCheckedUserValidate", "0");

        }
        mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        info = mConnectivity.getActiveNetworkInfo();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // only for gingerbread and newer versions
            permissions = new String[]{

                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.RECORD_AUDIO,



            };
        } else {
            permissions = new String[]{

                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.RECORD_AUDIO,



            };
        }

//        Toast.makeText(context, "" + Build.MODEL
//                , Toast.LENGTH_SHORT).show();

        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.splash);

        // Add stuf


        Thread splashThread = new Thread() {
            @Override
            public void run() {

                moveTonextScreen();

            }
        };
        splashThread.start();

    }


    private void moveTonextScreen() {

        Intent i = new Intent();

        if (checkPermissions()) {

            i.setClass(Splash.this, HomeActivity.class);


        } else {
            i.setClass(Splash.this, PermissionActivity.class);
        }
        startActivity(i);
        Splash.this.finish();
    }


    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(context, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            //   ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @SuppressLint("Override")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    String perStr = "";
                    for (String per : permissions) {
                        perStr += "\n" + per;
                    }
                   /* Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);*/
                    Toast.makeText(Splash.this, "Kindly allow all the Permissions.", Toast.LENGTH_SHORT).show();
                    Splash.this.finish();
                    //   Toast.makeText(context, "Not granted:"+perStr, Toast.LENGTH_SHORT).show();
                    // permissions list of don't granted permission
                }
                //    Toast.makeText(context, "no permit", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

}
