package com.akash.surveryapp.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.akash.surveryapp.R;

import java.util.ArrayList;
import java.util.List;

public class PermissionActivity extends AppCompatActivity {
    PermissionActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_permission);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // only for gingerbread and newer versions
            Splash.permissions = new String[]{

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
            Splash. permissions = new String[]{

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
        alertForPermission();
    }


    private void alertForPermission() {

        TextView but_submt;
        TextView but_cancel;
        LayoutInflater inflater = (activity).getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_permission, null);

        final Dialog alertDialog;
        alertDialog = new Dialog(this);

        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(view);

        alertDialog.setCanceledOnTouchOutside(false);

        TextView title_message4 = (TextView) view.findViewById(R.id.title_message4);


        but_submt = (TextView) view.findViewById(R.id.but_submt);
        but_cancel = (TextView) view.findViewById(R.id.but_cancel);


        title_message4.setText((Html.fromHtml("permission")));

        but_submt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                int result;
                List<String> listPermissionsNeeded = new ArrayList<>();
                for (String p : Splash.permissions) {
                    result = ContextCompat.checkSelfPermission(PermissionActivity.this, p);
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(p);
                    }
                }
                if (!listPermissionsNeeded.isEmpty()) {
                    ActivityCompat.requestPermissions(PermissionActivity.this,
                            listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Splash.MULTIPLE_PERMISSIONS);

                }

              /*  Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                Toast.makeText(activity, "Select Permissions and enable all permissions",
                        Toast.LENGTH_LONG).show();
*/

            }
        });
        but_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();

                PermissionActivity.this.finish();
            }
        });

        alertDialog.show();

    }


    @SuppressLint("Override")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Splash.MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PermissionActivity.this.finish();
                    Intent i = new Intent(PermissionActivity.this, Splash.class);
                    startActivity(i);
                } else {
                    String perStr = "";
                    for (String per : permissions) {
                        perStr += "\n" + per;
                    }
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    Toast.makeText(PermissionActivity.this, "Kindly allow all the Permissions.", Toast.LENGTH_SHORT).show();
                    PermissionActivity.this.finish();

                }
                return;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (checkPermissions()) {
            Intent i = new Intent(PermissionActivity.this, Splash.class);
            startActivity(i);
        }else alertForPermission();


    }


    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : Splash.permissions) {
            result = ContextCompat.checkSelfPermission(PermissionActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            return false;
        }
        return true;
    }


}
