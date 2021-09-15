package com.akash.surveryapp.utils;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.akash.surveryapp.R;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Admin on 14-07-2016.
 */
public class Utils {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 124;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    static double PI_RAD = Math.PI / 180.0;

    public static void clearPreferences() {

        try {
            // clearing app data
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear com.pe.tmi.fourcliqs");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void adjustFontScale(Context context, Configuration configuration) {
        if (configuration.fontScale != 1) {
            configuration.fontScale = 1;
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            context.getResources().updateConfiguration(configuration, metrics);
        }
    }
    public static boolean Check_FINE_LOCATION(Activity act)
    {
        int result = ContextCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }




    public static void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c))
                return false;
        }
        return true;
    }

    // Used to convert 24hr format to 12hr format with AM/PM values
    public static String updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        String hrs = "";
        if (hours < 10)
            hrs = "0" + hours;
        else
            hrs = String.valueOf(hours);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hrs).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        return aTime;
    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c.getTime());
    }

    public static String getPreviousDate() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = Calendar.getInstance();
        cal = Calendar.getInstance();

        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    public static String getSubtractedAddedMonthORDateByDate(boolean isMonthVal, int value, String date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("d MMM, yy");
        Calendar cal = Calendar.getInstance();
        cal = Calendar.getInstance();
        cal.setTime(dateFormat.parse(date));
        if (isMonthVal)
            cal.add(Calendar.MONTH, value);
        else
            cal.add(Calendar.DATE, value);
        return dateFormat.format(cal.getTime());
    }

    public static String getSubtractedAddedMonthORDate(boolean isMonthVal, int value) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("d MMM, yy");
        Calendar cal = Calendar.getInstance();
        cal = Calendar.getInstance();

        if (isMonthVal)
            cal.add(Calendar.MONTH, value);
        else
            cal.add(Calendar.DATE, value);
        return dateFormat.format(cal.getTime());
    }

    public static String getDateinYY(int val) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, val);
        return dateFormat.format(cal.getTime());
    }

    public static String getPreviousDateInDD() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    public static String getDateDD() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public static String getDateFancy() {
        DateFormat dateFormat = new SimpleDateFormat("d MMM, yy");
        Calendar cal = Calendar.getInstance();
        cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public static String getDayByDate(String date) {
        String input = getDate(date);
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
        Date dt1 = null;
        try {
            dt1 = format1.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat format2 = new SimpleDateFormat("EEE");
        return format2.format(dt1);

    }

    public static String getDateOnly(String getdate) {
        String reformattedStr = "";
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat myFormat = new SimpleDateFormat("d");

        try {
            if (!getdate.equals("")) {
                reformattedStr = myFormat.format(fromUser.parse(getdate));
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }

    public static String getCurrentDateInMM() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = Calendar.getInstance();
        cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public static String getDateFromTimeStamp(Long timestamp) {
        //get date from time stamp
        SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Timestamp stamp = new Timestamp(timestamp);
        Date date = new Date(stamp.getTime());
        String formatteddate = myFormat.format(date);
        //  System.out.println("getDateFromTimeStamp  " + date);
        return formatteddate;
    }

    private Date addorDeleteDays(int dayCount, Date incomingDate, boolean add) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(incomingDate);
        if (add) {
            cal.add(Calendar.DATE, +dayCount); //minus number would decrement the days
        } else {
            cal.add(Calendar.DATE, -dayCount); //minus number would decrement the days
        }
        return cal.getTime();
    }

    public static String GetDateinDDMMYYYY(String getdate) {
        String reformattedStr = "";
        SimpleDateFormat fromUser = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            if (!getdate.equals("")) {
                reformattedStr = myFormat.format(fromUser.parse(getdate));
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }

    public static String getDate(String getdate) {
        String reformattedStr = "";
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            if (!getdate.equals("")) {
                reformattedStr = myFormat.format(fromUser.parse(getdate));
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }

    public static String GetDateinDDampm(String getdate) {
        String reformattedStr = "";
        SimpleDateFormat fromUser = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

        try {
            if (!getdate.equals("")) {
                reformattedStr = myFormat.format(fromUser.parse(getdate));
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }

    public static String GetDateMonthinDDampm(String getdate) {
        String reformattedStr = "";
        SimpleDateFormat fromUser = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        SimpleDateFormat myFormat = new SimpleDateFormat("d MMM yyyy, hh:mm a");

        try {
            if (!getdate.equals("")) {
                reformattedStr = myFormat.format(fromUser.parse(getdate));
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }

    public static String getDateinLetters(String getdate) {
        String reformattedStr = "";
        SimpleDateFormat fromUser = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("d MMM yyyy,");

        try {
            if (!getdate.equals("")) {
                reformattedStr = myFormat.format(fromUser.parse(getdate));
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }

    public static String getDateinMMDDYYYY(String getdate) {
        String reformattedStr = "";
        SimpleDateFormat fromUser = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");

        try {
            Log.d("exce", "getDateinMMDDYYYY: " + getdate);
            //    if (!getdate.equals("") && (!getdate.equals("From Date") || !getdate.equals("To Date"))) {
            if (!getdate.equals("") && getdate.contains("/")) {
                reformattedStr = myFormat.format(fromUser.parse(getdate));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        return reformattedStr;
    }

    public static String getDateinMMDDYY(String getdate) {
        String reformattedStr = "";
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");

        try {
            //  Log.d("exce", "getDateinMMDDYYYY: " + getdate);
            //    if (!getdate.equals("") && (!getdate.equals("From Date") || !getdate.equals("To Date"))) {
            if (!getdate.equals("")) {
                reformattedStr = myFormat.format(fromUser.parse(getdate));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        return reformattedStr;
    }

    public static String getDateTimeinDDMMYYYY(String getdate) {
        String reformattedStr = "";
        SimpleDateFormat fromUser = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {

            reformattedStr = myFormat.format(fromUser.parse(getdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }


    public static String getDateTimeinMMDDYYYY(String getdate) {
        String reformattedStr = "";
        SimpleDateFormat fromUser = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

        try {
            if (!getdate.equals(""))
                reformattedStr = myFormat.format(fromUser.parse(getdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }

    public static String getDateTimeinDD(String getdate) {
        String reformattedStr = "";
        SimpleDateFormat fromUser = new SimpleDateFormat("MM/dd/yyyy HH:mm a");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm a");

        try {
            if (!getdate.equals(""))
                reformattedStr = myFormat.format(fromUser.parse(getdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }

    public static String getCurrentDateinMM() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.format(c.getTime());
    }

    public static String getcurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal = Calendar.getInstance();
        // cal.add(Calendar.HOUR, +1);
        return dateFormat.format(cal.getTime());
    }

    public static String getTimeByHour() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        Calendar cal = Calendar.getInstance();
        cal = Calendar.getInstance();
       //  cal.add(Calendar.HOUR, +1);
        return dateFormat.format(cal.getTime());
    }


    public static String getSubtractedDateTimeMins(int val) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm a");
        Calendar cal = Calendar.getInstance();
        cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, val);
        return dateFormat.format(cal.getTime());
    }

    public static String getcurrentDateTimeMM() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm a");
        Calendar cal = Calendar.getInstance();
        cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public static String getcurrentDateTimeDD() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        Calendar cal = Calendar.getInstance();
        cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public static String getsubtractedDateTimeInMMByMins(int value) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm a");
        Calendar cal = Calendar.getInstance();
        cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, value);
        return dateFormat.format(cal.getTime());
    }
    public double greatDistanceInKilometers(double lat1, double long1, double lat2, double long2) {
        double phi1 = lat1 * PI_RAD;
        double phi2 = lat2 * PI_RAD;
        double lam1 = long1 * PI_RAD;
        double lam2 = long2 * PI_RAD;

        return 6371.01 * acos(sin(phi1) * sin(phi2) + cos(phi1) * cos(phi2) * cos(lam2 - lam1));
    }
    public static String getsubtractedDate(int value) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, value);
        return dateFormat.format(cal.getTime());
    }

    public static String getsubtractedDateTimeMMHHBymins(String datetime, int value) {
        String reformattedStr = "";
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm a");
        Calendar cal = Calendar.getInstance();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            if (!datetime.equals("")) {
                Date d = simpleDateFormat.parse(datetime);
                cal = Calendar.getInstance();
                cal.setTime(d);
                cal.add(Calendar.MINUTE, value);
                reformattedStr = dateFormat.format(cal.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }

    public static boolean TimeDiffInTwoinMMDD(String FirsRange, String LastRange, String TimeInFirstAndLast) {
        boolean IsTimeExist = false;
        try {
            // String str1 = "07/20/2017 21:19:17";
            Date t1 = new SimpleDateFormat("MM/dd/yyyy HH:mm a").parse(FirsRange);
            Calendar c1 = Calendar.getInstance();
            c1.setTime(t1);
            //   Log.d(TAG, "DateDif1: "+c1.getTimeByHour());
            String str2 = "07/23/2017 12:32:00";
            Date t2 = new SimpleDateFormat("MM/dd/yyyy HH:mm a").parse(LastRange);
            Calendar c2 = Calendar.getInstance();
            c2.setTime(t2);
            // Log.d(TAG, "DateDif2: "+c2.getTimeByHour());
            //String currenttime = "07/21/2017 12:32:00";
            Date d = new SimpleDateFormat("MM/dd/yyyy HH:mm a").parse(TimeInFirstAndLast);
            Calendar c3 = Calendar.getInstance();
            c3.setTime(d);
            // Log.d(TAG, "DateDif3: "+c3.getTimeByHour());
            Date x = c3.getTime();
            if (x.after(c1.getTime()) && x.before(c2.getTime())) {
                IsTimeExist = true;
                System.out.println("Yes it is in the range");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return IsTimeExist;
    }

    public static int getBatteryPercentage(Context context) throws Exception {

        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);


        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        float batteryPct = level / (float) scale;

        return (int) (batteryPct * 100);
    }

    public static boolean checkPermissionLocation(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= 23) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission needed");
                    alertBuilder.setMessage("Please give permissions");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        }
                    });
                    alertBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static String convertTo24Hour(String Time) {
        SimpleDateFormat f1 = new SimpleDateFormat("hh:mm a", Locale.US); //11:00 pm
        Date d = null;
        String x = "";
        try {
            d = f1.parse(Time);
            SimpleDateFormat f2 = new SimpleDateFormat("HH:mm", Locale.US);
            x = f2.format(d); // "23:00"
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return x;
    }


    //Compare time
    public static int compareTime(String time1, String time2, Context c) {
        String pattern = "HH:mm a";
        //  String pattern = "MM/dd/yyyy HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        int comparisonResult = 0;
        try {
            Date date1 = sdf.parse(time1);
            Date date2 = sdf.parse(time2);
            comparisonResult = date1.compareTo(date2);

        } catch (ParseException e) {
        }
        return comparisonResult;
    }

    public static int compareTimeDiff(String time1, String time2) {
        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        int comparisonResult = 0;
        try {
            Date date1 = sdf.parse(convertTo24Hour(time1));
            Date date2 = sdf.parse(convertTo24Hour(time2));

            comparisonResult = date1.compareTo(date2);

        } catch (ParseException e) {
        }
        return comparisonResult;
    }


    public static Map<String, String> getHeaderValue() {
        Map<String, String> headerValue = new HashMap<String, String>();

        return headerValue;
    }


    public static int compareDateTime(String datetime1, String datetime2) {
        String pattern = "dd-MM-yyyy HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        int comparisonResult = 0;
        try {
            Date date1 = sdf.parse(datetime1);
            Date date2 = sdf.parse(datetime2);
            comparisonResult = date1.compareTo(date2);
        } catch (ParseException e) {
            // Exception handling goes here
        }
        return comparisonResult;
    }

    public static int compareDateAndTime(String datetime1, String datetime2) {
        String pattern = "MM/dd/yyyy HH:mm";
        Date date2 = null;
        Date date1 = null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        int comparisonResult = 0;
        try {
            if (!datetime2.equals("")) {
                date2 = sdf.parse(datetime2);
            } else return 1;
            if (!datetime1.equals("")) {
                date1 = sdf.parse(datetime1);
            } else return -1;
            comparisonResult = date1.compareTo(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return comparisonResult;
    }

    public static String parseDateChange(String time, String inputPattern, String outputPattern) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            if (!time.equals("")) {

                date = inputFormat.parse(time);
                str = outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        return str;
    }

    public static String parseDateTimeChange(String time, String inputPattern, String outputPattern) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        return str;
    }

    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static boolean checkPermissionstorage(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        //if (currentAPIVersion >= Build.VERSION_CODES.LOLLIPOP) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Permission necessary");
                alertBuilder.setMessage("External storage permission is necessary");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();

            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
            return false;
        } else {
            return true;
        }
        // } else {
        //    return true;
        //}
    }

    public void setBlinkingTextview(TextView tv, long milliseconds, long offset) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(milliseconds); //You can manage the blinking time with this parameter
        anim.setStartOffset(offset);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        tv.startAnimation(anim);
    }

    public static String convertFilterDate(String time) {
        String outputPattern = "";
        String inputPattern = "";
        inputPattern = "d/MM/yyyy hh:mm a";
        outputPattern = "hh:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            if (!time.equals("")) {
                date = inputFormat.parse(time);
                str = outputFormat.format(date.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();

        }

        return str;
    }

    public static void setCustomTypeFaceRegular(Context context, TextView txtView) {

        Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        txtView.setTypeface(custom_font);
    }

    public static Bitmap convertImageToBitmap(Context mContext, String ImageName) {

        File mediaStorageDir;
        if (Build.VERSION.SDK_INT >= 30) {

            mediaStorageDir = new File(
                    Environment.getExternalStorageDirectory(), "DCIM/4cliq");


        }else {
            mediaStorageDir = new File(
                    Environment.getExternalStorageDirectory() + "/Android/data/"
                            + mContext.getPackageName()
                            + "/4cliq");
        }

        String filePath = mediaStorageDir.getPath() + File.separator
                + ImageName + ".png";


        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scaledBitmap;

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }


    public static <T> Object getGsonObject(String s, Class<T> clazz) {
        Object object = new Gson().fromJson(s, clazz);
        return object;
    }

    public static <T> ArrayList<T> getGsonArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return new ArrayList<T>(Arrays.asList(arr));
    }

    public static String getStringFromArray(ArrayList<?> arrayList) {
        String jsonString = "";

        jsonString = new Gson().toJson(arrayList);
        return jsonString;
    }

    public static String getStringFromArray(List<?> arrayList) {
        String jsonString = "";

        jsonString = new Gson().toJson(arrayList);
        return jsonString;
    }

    public static void largeLog(String TAG, String message) {


        int maxLogSize = 2000;
        for (int i = 0; i <= message.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > message.length() ? message.length() : end;
            Log.d(TAG, message.substring(start, end));
        }
    }

    public String getConnectionSpeed(int paramInt1, int paramInt2, Context context) {
        if (paramInt1 == 1) {
            Log.i(getClass().getName(), "Wifi State");

            Object localObject = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
            Integer localInteger = Integer.valueOf(0);
            if (localObject != null) {
                localInteger = Integer.valueOf(((WifiInfo) localObject).getLinkSpeed());
            }
            localObject = new StringBuilder();
            ((StringBuilder) localObject).append("WIFI ");
            ((StringBuilder) localObject).append(localInteger);
            ((StringBuilder) localObject).append("Mbps");
            return ((StringBuilder) localObject).toString();
        }
        if (paramInt1 == 0) {
            switch (paramInt2) {
                default:
                    return "UNKNOWN";
                case 15:
                    Log.i(getClass().getName(), "10 - 20 Mbps");
                    return "HSPAP ~ 10-20 Mbps";
                case 14:
                    Log.i(getClass().getName(), "1 - 2 Mbps");
                    return "EHRPD ~ 1-2 Mbps";
                case 13:
                    Log.i(getClass().getName(), "10+ Mbps");
                    return "LTE ~ 10+ Mbps";
                case 12:
                    Log.i(getClass().getName(), "5 Mbps");
                    return "EVDO_B  ~ 5 Mbps";
                case 11:
                    Log.i(getClass().getName(), "25 kbps");
                    return "IDEN  ~ 25 kbps";
                case 10:
                    Log.i(getClass().getName(), "700 - 1700 kbps");
                    return "HSPA  ~ 700-1700 kbps";
                case 9:
                    Log.i(getClass().getName(), "1 - 23 Mbps");
                    return "HSUPA 1 - 23 Mbps";
                case 8:
                    Log.i(getClass().getName(), "2 - 14 Mbps");
                    return "HSDPA  ~ 2-14 Mbps";
                case 7:
                    Log.i(getClass().getName(), "50 - 100 kbps");
                    return "1xRTT ~ 50-100 kbps";
                case 6:
                    Log.i(getClass().getName(), "600 - 1400 kbps");
                    return "EVDO_A  ~ 600-1400 kbps";
                case 5:
                    Log.i(getClass().getName(), "400 - 1000 kbps");
                    return "EVDO_0 ~ 400-1000 kbps";
                case 4:
                    Log.i(getClass().getName(), "14 - 64 kbps");
                    return "CDMA ~ 14-64 kbps";
                case 3:
                    Log.i(getClass().getName(), "400 - 7000 kbps");
                    return "UMTS  ~ 400-7000 kbps";
                case 2:
                    Log.i(getClass().getName(), "50 - 100 kbps");
                    return "EDGE ~ 50-100 kbps";
            }
            //Log.i(getClass().getName(), " 100 kbps");
            //return "GPRS  ~ 100 kbps";
        }
        return "UNKNOWN";
    }
   /* public long freeRamMemorySize(Context context)
    {
        ActivityManager.MemoryInfo localMemoryInfo = new ActivityManager.MemoryInfo();
        ((ActivityManager)context.getSystemService("activity")).getMemoryInfo(localMemoryInfo);
        return availMem / 1048576L;
    }*/

   /* public long getAvailableExternalMemorySize()
    {
        if (externalMemoryAvailable())
        {
            StatFs localStatFs = new StatFs( Environment.getExternalStorageDirectory().getPath());
            long l = localStatFs.getBlockSize();
            return localStatFs.getAvailableBlocks() * l;
        }
        return 0L;
    }*/

    public long getAvailableInternalMemorySize() {
        StatFs localStatFs = new StatFs(Environment.getDataDirectory().getPath());
        long l = localStatFs.getBlockSize();
        return localStatFs.getAvailableBlocks() * l;
    }

   /* public long getTotalExternalMemorySize()
    {
        if (externalMemoryAvailable())
        {
            StatFs localStatFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long l = localStatFs.getBlockSize();
            return localStatFs.getBlockCount() * l;
        }
        return 0L;
    }*/

    public long getTotalInternalMemorySize() {
        StatFs localStatFs = new StatFs(Environment.getDataDirectory().getPath());
        long l = localStatFs.getBlockSize();
        return localStatFs.getBlockCount() * l;
    }

    /*
        public String getTotalRAM()
        {
            DecimalFormat localDecimalFormat = new DecimalFormat("#.##");
            try
            {
                RandomAccessFile localRandomAccessFile = new RandomAccessFile("/proc/meminfo", "r");
                try
                {
                    String str1 = localRandomAccessFile.readLine();
                    try
                    {
                        Matcher localMatcher = Pattern.compile("(\\d+)").matcher(str1);
                        for (str1 = "";; str1 = localMatcher.group(1))
                        {
                            boolean bool = localMatcher.find();
                            if (!bool) {
                                break;
                            }
                        }
                        localRandomAccessFile.close();
                        d1 = Double.parseDouble(str1);
                        d2 = d1 / 1024.0D;
                        double d3 = d1 / 1048576.0D;
                        double d4 = d1 / 1.073741824E9D;
                        if (d4 > 1.0D)
                        {
                            try
                            {
                                str1 = localDecimalFormat.format(d4);
                                try
                                {
                                    str1 = str1.concat(" TB");
                                    return str1;
                                }
                                catch (Throwable localThrowable1)
                                {
                                    break label241;
                                }
                                catch (IOException localIOException1) {}
                                if (d3 <= 1.0D) {
                                    break label179;
                                }
                            }
                            catch (Throwable localThrowable2)
                            {
                                break label259;
                            }
                            catch (IOException localIOException2) {}
                        }
                        else
                        {
                            str2 = localDecimalFormat.format(d3).concat(" GB");
                            return str2;
                        }
                    }
                    catch (Throwable localThrowable3)
                    {
                        double d1;
                        double d2;
                        String str2;
                        label179:
                        break label259;
                    }
                    catch (IOException localIOException3) {}
                    if (d2 > 1.0D)
                    {
                        str2 = localDecimalFormat.format(d2).concat(" MB");
                        return str2;
                    }
                    str2 = localDecimalFormat.format(d1).concat(" KB");
                    return str2;
                }
                catch (Throwable localThrowable4)
                {
                    break label259;
                }
                catch (IOException localIOException4) {}
                try
                {
                    label241:
                    ((IOException)localIOException5).printStackTrace();
                    return "";
                }
                catch (Throwable localThrowable6) {}
            }
            catch (Throwable localThrowable5) {}catch (IOException localIOException5) {}
            label259:
            throw localThrowable6;
        }
        public long totalRamMemorySize(Context context)
        {
            ActivityManager.MemoryInfo localMemoryInfo = new ActivityManager.MemoryInfo();
            ((ActivityManager)context.getSystemService("activity")).getMemoryInfo(localMemoryInfo);
            return totalMem / 1048576L;
        }*/
    public static List<String> getDates(String dateString1, String dateString2) {
        ArrayList<String> dates = new ArrayList<>();
        DateFormat df1 = new SimpleDateFormat("d MMM, yy");
        DateFormat df2 = new SimpleDateFormat("EEE");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while (!cal1.after(cal2)) {
            dates.add(df2.format(cal1.getTime()));
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public static List<String> getMonths(String dateString1, String dateString2) {
        ArrayList<String> dates = new ArrayList<>();
        DateFormat df1 = new SimpleDateFormat("d MMM, yy");
        DateFormat df2 = new SimpleDateFormat("MMM");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while (!cal1.after(cal2)) {
            dates.add(df2.format(cal1.getTime()));
            cal1.add(Calendar.MONTH, 1);
        }
        return dates;
    }
}
