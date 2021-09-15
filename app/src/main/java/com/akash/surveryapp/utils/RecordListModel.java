package com.akash.surveryapp.utils;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordListModel implements Comparable<RecordListModel>,Serializable {
    private String Caption = "";
    private Bitmap Image;
    private String StoreImageID;
    private String MyImageName;
    private String DateAndTime;
    private String store_imageLattitude;
    private String store_imageLongitude;

    public String getRecordingpath() {
        return recordingpath;
    }

    public void setRecordingpath(String recordingpath) {
        this.recordingpath = recordingpath;
    }

    public String getRecordingName() {
        return recordingName;
    }

    public void setRecordingName(String recordingName) {
        this.recordingName = recordingName;
    }

    private String recordingpath;
    private String recordingName;



    public String getStore_imageLattitude() {
        return store_imageLattitude;
    }

    public void setStore_imageLattitude(String store_imageLattitude) {
        this.store_imageLattitude = store_imageLattitude;
    }

    public String getStore_imageLongitude() {
        return store_imageLongitude;
    }

    public void setStore_imageLongitude(String store_imageLongitude) {
        this.store_imageLongitude = store_imageLongitude;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    boolean isChecked;

    public String getDateAndTime() {
        return DateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        DateAndTime = dateAndTime;
    }

    public String getMyImageName() {
        return MyImageName;
    }

    public void setMyImageName(String myImageName) {
        MyImageName = myImageName;
    }

    public String getStoreImageID() {
        return StoreImageID;
    }

    public void setStoreImageID(String storeImageID) {
        StoreImageID = storeImageID;
    }

    /*********** Set Methods ******************/
    public void setCaption(String Caption) {
        this.Caption = Caption;
    }

    public void setImage(Bitmap Image) {
        this.Image = Image;
    }

    /*********** Get Methods ****************/
    public String getCaption() {
        return this.Caption;
    }

    public Bitmap getImage() {
        return this.Image;
    }



    @Override
    public int compareTo(RecordListModel o) {

        return o.getDateAndTime().compareTo(getDateAndTime());
      //  return getTimeStamp(o.getDateAndTime()).compareTo(getTimeStamp(getDateAndTime()));
    }

    private Timestamp getTimeStamp(String dateAndTime) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        Date date = null;
        long time = 0;
        try {
            date = dateFormat.parse(dateAndTime);

            time = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Timestamp(time);
    }
}
