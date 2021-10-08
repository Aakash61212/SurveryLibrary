package com.akash.surveryapp.ui;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.akash.surveryapp.R;
import com.akash.surveryapp.database.DatabaseAdapter;
import com.akash.surveryapp.utils.AndroidDatabaseManager;
import com.akash.surveryapp.utils.ImageLoadingUtils;
import com.akash.surveryapp.utils.Preference;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCanceledListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


/**
 * Created by Akash on 14-08-2020.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {


    private static final int RESULT_CANCELED = 0;
    private ImageButton recordBtn;


    private ImageButton records;
    private TextView filenameText;

    Context context;

    private boolean isRecording = false;

    private MediaRecorder mediaRecorder;
    private String recordFile;

    private Chronometer timer;
    ImageView imageView;
    ImageView camera_img;
    TextView txt_attach, txtCaption;
    Button bttn_submit;
    View divider;
    EditText edt_caption;
    String path;
    DatabaseAdapter db;
    private ImageLoadingUtils utils;
    public static Uri fileUri;
    Bitmap bitmap;
    private String TAG = "RecordFrag";
    public String imageName = "";
    public ProgressDialog pd;
    Handler handler;
    private Runnable runnable;
    int x = 1;
    Activity activity;


    public boolean fusedlocationRecieved = false;
    public static int REQUEST_CHECK_SETTINGS = 100;
    private static final int CAMERA_REQUEST = 1888;
    String edt_capt = "";
    CardView card_view;
    boolean GpsEnabled = false;


    private static final long INTERVAL = 10 * 1000;
    private static final long FASTEST_INTERVAL = 5 * 1000;
    private GoogleApiClient googleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
    private double Latitude = 0.0, Longitude = 0.0;

    private long mLastClickTime = 0;


    public boolean isRecordInserted = false;

    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_survey,
                container, false);

        context = getActivity();
        activity = getActivity();
        pd = new ProgressDialog(context);
        divider = view.findViewById(R.id.divider);

        edt_caption = view.findViewById(R.id.edt_caption);
        recordBtn = view.findViewById(R.id.record_btn);
        records = view.findViewById(R.id.btnOpenRecordList);
        timer = view.findViewById(R.id.record_timer);
        filenameText = view.findViewById(R.id.record_filename);
        bttn_submit = view.findViewById(R.id.bttn_submit);
        card_view = view.findViewById(R.id.card_view);
        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity) Objects.requireNonNull(context)).setTabBarSelection(1);

            }
        });
        bttn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //prevent multiple taps on button
                path = recordedpath();

                if (SystemClock.elapsedRealtime() - mLastClickTime < 5000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                isRecordInserted = false;

                if (checkPermissions1()) {
                    edt_capt = edt_caption.getText().toString().trim();
                    //edt_capt = edt_capt.trim();
                    if (edt_capt.equals("") || edt_capt == null) {
                        edt_caption.setError("Please enter notes for Image");
                    } else if (bitmap == null) {
                        Toast.makeText(getActivity(), "Please upload an Image.", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            if (isRecording) {
                                stopRecording();
                                isRecording = false;
                            }
                            storeImage();

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Please upload image before submit.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                recordBtn.setClickable(true);
            }
        });
        card_view.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
        imageView = view.findViewById(R.id.imageView1);
        camera_img = view.findViewById(R.id.camera_img);
        txt_attach = view.findViewById(R.id.txt_attach);
        txtCaption = view.findViewById(R.id.txtCaption);
        recordBtn.setOnClickListener(this);
        txtCaption.setOnClickListener(this);
        camera_img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 5000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (checkPermissions1()) {

                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        fileUri = getOutputMediaFileUri(); // create a file to save the image
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }

                    }/*else alertForPermission();*/
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Kindly allow storage permission to access camera", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getApplicationContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Criteria criteria = new Criteria();
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);


//API level 9 and up
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        try {
            buildGoogleApiClient();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return view;
    }


    /**
     * Function to start FusedLocation updates
     */
    public void requestLocationUpdate() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    private void locationSettingsRequest() {
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(context);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        LocationSettingsRequest mLocationSettingsRequest = builder.build();

        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(locationSettingsResponse -> {
                    // Start FusedLocation if GPS is enabled
                    requestLocationUpdate();
                })
                .addOnFailureListener(e -> {
                    // Show enable GPS Dialog and handle dialog buttons
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                int REQUEST_CHECK_SETTINGS = 214;
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                Toast.makeText(context, "Unable to Execute Request", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Toast.makeText(context, "Location Settings are Inadequate, and Cannot be fixed here. Fix in Settings", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Toast.makeText(context, "cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onClick(View v) {
        /*  Check, which button is pressed and do the task accordingly
         */
        switch (v.getId()) {


            case R.id.record_btn:

                if (checkPermissions1()) {
                    //Start Recording
                    startRecording();


                    recordBtn.setBackground(getResources().getDrawable(R.drawable.ic_baseline_record_voice_over_24));
                    isRecording = true;
                    recordBtn.setClickable(false);
                }

                break;
            case R.id.txtCaption:
                Intent intent = new Intent(getActivity(), AndroidDatabaseManager.class);
                startActivity(intent);
                break;
        }

    }

    /**
     * Build GoogleApiClient and connect
     */
    private synchronized void buildGoogleApiClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        // Creating a location request
                        mLocationRequest = new LocationRequest();
                        mLocationRequest.setPriority(priority);
                        mLocationRequest.setInterval(INTERVAL);
                        mLocationRequest.setSmallestDisplacement(0f);
                        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
                        mLocationRequest.setSmallestDisplacement(0);

                        // FusedLocation callback
                        mLocationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(final LocationResult locationResult) {
                                super.onLocationResult(locationResult);

                                Latitude = locationResult.getLastLocation().getLatitude();
                                Longitude = locationResult.getLastLocation().getLongitude();

                                if (Latitude == 0.0 && Longitude == 0.0) {
                                    requestLocationUpdate();
                                } else {


                                    ChangeandSetTimer();

                                }
                            }
                        };

                        // Call location settings function to enable gps
//                        locationSettingsRequest();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        connectGoogleClient();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(LocationServices.API)
                .build();

        // Connect googleapiclient after build
        connectGoogleClient();
    }

    /**
     * Function to connect googleapiclient
     */
    private void connectGoogleClient() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(getContext());
        if (resultCode == ConnectionResult.SUCCESS) {
            googleApiClient.connect();
        } else {
            int REQUEST_GOOGLE_PLAY_SERVICE = 988;
            googleAPI.getErrorDialog(this, resultCode, REQUEST_GOOGLE_PLAY_SERVICE);
        }
    }

    @SuppressLint("Override")
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    storeImage();
                } else {
                    String perStr = "";
                    for (String per : permissions) {
                        perStr += "\n" + per;
                    }

                    alertForPermission();
                }
                return;
            }
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CAMERA_REQUEST) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);


                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    camera_img.setVisibility(View.GONE);
                    txt_attach.setVisibility(View.GONE);
                } else {
                    imageView.setImageBitmap(null);
                    camera_img.setVisibility(View.VISIBLE);
                    txt_attach.setVisibility(View.VISIBLE);
                }
            } else if (requestCode == REQUEST_CHECK_SETTINGS) {
                edt_caption.setText(edt_capt);
                if (resultCode == Activity.RESULT_OK) {

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    //  bitmap = BitmapFactory.decodeFile(fileUri.getPath());
                    bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
                    // bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        txt_attach.setVisibility(View.GONE);
                        camera_img.setVisibility(View.GONE);
                    }
                    edt_capt = edt_caption.getText().toString().trim();
                    //edt_capt = edt_capt.trim();
                    if (edt_capt.equals("") || edt_capt == null) {
                        edt_caption.setError("Please enter notes for Image");
                    } else if (bitmap == null) {
                        Toast.makeText(context, "Please upload an Image.", Toast.LENGTH_LONG).show();
                        imageView.setImageBitmap(null);
                        camera_img.setVisibility(View.VISIBLE);
                        txt_attach.setVisibility(View.VISIBLE);
                    } else {
                        try {
                            storeImage();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Please upload image before submit.", Toast.LENGTH_LONG).show();
                        }
                    }

                } else edt_caption.setText(edt_capt);
                ;
            } else if (resultCode == RESULT_CANCELED) {
                imageView.setVisibility(View.GONE);
                edt_caption.setText(edt_capt);
            } else {
                Toast.makeText(context, "picture not taken", Toast.LENGTH_SHORT)
                        .show();
                edt_caption.setText(edt_capt);
                imageView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void storeImage() {

        try {


            LocationManager manager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


                // Store image in DB
                locationSettingsRequest();
                Log.d(TAG, "after fused api call" + Latitude);


            } else {
                locationSettingsRequest();

            }


        } catch (Exception e) {
        }

    }

    public synchronized void StoreImageInDB(String imageName, String lat, String lng, String path) {
        db.open();
        SimpleDateFormat s = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String UploadDate = s.format(new Date());
        if (lat == null || lat.equals("")) {
            lat = "0";
        }
        if (lng == null || lng.equals("")) {
            lng = "0";
        }
        db.addStoreImage(edt_caption.getText().toString(), imageName, UploadDate, lat, lng, path);
        db.close();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void ChangeandSetTimer() {
        db = new DatabaseAdapter(context);

        GpsEnabled = true;
        StartTimer();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public int getLocationMode(Context context) {
        int val = 0;
        try {
            val = Settings.Secure.getInt(activity.getContentResolver(), Settings.Secure.LOCATION_MODE);
            Log.d(TAG, "getLocationMode status: " + val);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return val;

    }

    private synchronized void StartTimer() {
//
        if (!isRecordInserted) {
            isRecordInserted = true;
            StoreImageInDB(imageName, String.valueOf(Latitude), String.valueOf(Longitude), path.toString());
            exitAndShowTAb();

        }
    }



    private void exitAndShowTAb() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }

        recycleBitmap();
        recordFile = "";
        path = "";
        edt_caption.setText("");
        recordBtn.setClickable(true);
        if (mediaRecorder != null) {
            stopRecording();
        }
        timer.setBase(SystemClock.elapsedRealtime());
        filenameText.setText("Press the record button \n to start recording");

        Preference.saveBoolean(context, Preference.KEY_IS_RECORD, true);
        camera_img.setVisibility(View.VISIBLE);
        txt_attach.setVisibility(View.VISIBLE);

        imageView.setVisibility(View.GONE);
        recordBtn.setBackground(getResources().getDrawable(R.drawable.ic_baseline_keyboard_voice_24));
        ((HomeActivity) context).setTabBarSelection(1);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pd != null) {
            pd.dismiss();

        }
        fusedlocationRecieved = false;

        recycleBitmap();

    }

//

    public void recycleBitmap() {


        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (pd != null) {
            pd.dismiss();

        }


    }


    @Override
    public void onStop() {
        super.onStop();

        if (isRecording) {
           stopRecording();
        }
    }

    /**
     * Create a file Uri for saving an image or video
     */
    private Uri getOutputMediaFileUri() {
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        imageName = s.format(new Date());
        return Uri.fromFile(getOutputMediaFile(imageName));
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(String imageName) {


        File mediaStorageDir;

        if (Build.VERSION.SDK_INT >= 30) {

            mediaStorageDir = new File(
                    Environment.getExternalStorageDirectory(), "DCIM/surveyApp");


        } else {


            mediaStorageDir = new File(
                    Environment.getExternalStorageDirectory() + "/Android/data/"
                            + activity.getPackageName()
                            + "/surveyApp");

        }

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        File mediaFile;
        if (Build.VERSION.SDK_INT >= 30) {

            mediaStorageDir = new File(
                    Environment.getExternalStorageDirectory(), "DCIM/surveyApp");


            String imagePath = mediaStorageDir.getPath() + File.separator
                    + imageName + ".png";


            mediaFile = new File(imagePath);
        } else {


            String mImageName = imageName + ".png";
            String finalPath = mediaStorageDir.getPath() + File.separator
                    + mImageName;

            mediaFile = new File(finalPath);
        }
        return mediaFile;

    }


    private String recordedpath() {
        if (recordFile != null && !recordFile.isEmpty()) {
            filenameText.setText("Recording Stopped, File Saved : " + recordFile);
        } else {
            filenameText.setText("Please Start Recording First");


        }
        return recordFile;
    }

    private String stopRecording() {
        //Stop Timer, very obvious
        timer.stop();

        //Change text on page to file saved
        filenameText.setText("Recording Stopped, File Saved : " + recordFile);

        //Stop media recorder and set it to null for further use to record new audio
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        return recordFile;
    }

    private void startRecording() {
        //Start timer from 0
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        //Get app external directory path
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();

        //Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();

        //initialize filename variable with date and time at the end to ensure the new file wont overwrite previous file
        recordFile = "Recording_" + formatter.format(now) + ".3gp";

        filenameText.setText("Recording, File Name : " + recordFile);

        //Setup Media Recorder for recording
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Start Recording
        mediaRecorder.start();
    }


    private boolean checkPermissions1() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void alertForPermission() {

        TextView but_submt;
        TextView but_cancel;
        LayoutInflater inflater = (activity).getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_permission, null);

        final Dialog alertDialog;
        alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(view);

        alertDialog.setCanceledOnTouchOutside(false);

        TextView title_message4 = (TextView) view.findViewById(R.id.title_message4);


        but_submt = (TextView) view.findViewById(R.id.but_submt);
        but_cancel = (TextView) view.findViewById(R.id.but_cancel);


        title_message4.setText((Html.fromHtml("Permissions")));

        but_submt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                Toast.makeText(activity, "Select Permissions and enable all permissions",
                        Toast.LENGTH_LONG).show();


            }
        });
        but_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();


            }
        });

        alertDialog.show();

    }


}
