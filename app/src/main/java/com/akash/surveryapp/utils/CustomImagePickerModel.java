package com.akash.surveryapp.utils;

import android.graphics.Bitmap;

/**
 * Created by PRATITI on 13-05-2015.
 */
public class CustomImagePickerModel {

    String storeImageId, imageCaption, imageName, imageDateTime;
    Bitmap imageBitmap;

    public int position;

    public CustomImagePickerModel() {
    }

    public CustomImagePickerModel(String storeImageId, String imageCaption, String imageDateTime, String imageName, Bitmap imageBitmap) {
        this.storeImageId = storeImageId;
        this.imageCaption = imageCaption;
        this.imageDateTime = imageDateTime;
        this.imageName = imageName;
        this.imageBitmap = imageBitmap;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public String getStoreImageId() {
        return storeImageId;
    }

    public void setStoreImageId(String storeImageId) {
        this.storeImageId = storeImageId;
    }

    public String getImageCaption() {
        return imageCaption;
    }

    public void setImageCaption(String imageCaption) {
        this.imageCaption = imageCaption;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageDateTime() {
        return imageDateTime;
    }

    public void setImageDateTime(String imageDateTime) {
        this.imageDateTime = imageDateTime;
    }
}
