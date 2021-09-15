package com.akash.surveryapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.akash.surveryapp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseAdapter {

    public static final String DB_NAME = "surveyDemo.db";
    public static final int DB_VERSION = (int) 1.0;
    public static int DB_OLD_VERSION = (int) 0;
    private static final String TAG = "Database Adapter";
    public static SharedPreferences sp;
    public static final String MY_PREF = "MyPreferences";
    Context context;


    public static final String DB_TABLE_StoreImage = "CREATE  TABLE  IF NOT EXISTS StoreImage"
            + "(pkstoreimageid INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"
            + "imagename , caption,UploadDate, isUsed INTEGER DEFAULT 0,showImageTemp INTEGER DEFAULT 0,store_imageLattitude,store_imageLongitude,path);";


    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {


            db.execSQL(DB_TABLE_StoreImage);


        }


        public static boolean isColumnExists(SQLiteDatabase sqliteDatabase,
                                             String tableName,
                                             String columnToFind) {
            Cursor cursor = null;

            try {
                cursor = sqliteDatabase.rawQuery(
                        "PRAGMA table_info(" + tableName + ")",
                        null
                );

                int nameColumnIndex = cursor.getColumnIndexOrThrow("name");

                while (cursor.moveToNext()) {
                    String name = cursor.getString(nameColumnIndex);

                    if (name.equals(columnToFind)) {
                        return true;
                    }
                }

                return false;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG, "upgrade: old " + oldVersion);
            DatabaseAdapter.DB_OLD_VERSION = oldVersion;
            Log.d(TAG, "upgrade: new " + newVersion);

            //only if we need upgradation
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
        }
    }

    private DatabaseHelper dbh;
    private SQLiteDatabase db;

    public DatabaseAdapter(Context context) {
        try {
            this.context = context;

            dbh = new DatabaseHelper(context);
            sp = context.getSharedPreferences(MY_PREF, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DatabaseAdapter open() throws SQLException {
        try {
            db = dbh.getWritableDatabase();
            return this;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }


    public void close() {
        dbh.close();
    }

    public String getMyCustomerId(String name) {
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT CustomerGuid,CustomerName FROM UserCustomerMap WHERE CustomerName='" + name + "' ", null);

        if (cursor.moveToFirst() && cursor != null) {
            do {
                return cursor.getString(0);
            } while (cursor.moveToNext());
        }

        return "";
    }


    public long addStoreImage(String caption, String imageName, String UploadDate, String lat, String lng , String path) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("imagename", imageName);
        initialValues.put("caption", caption);
        initialValues.put("UploadDate", UploadDate);
        initialValues.put("store_imageLattitude", lat);
        initialValues.put("store_imageLongitude", lng);
        initialValues.put("path",path);
        return db.insert("StoreImage", null, initialValues);
    }

    public Cursor getStoreImages() {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM StoreImage", null);
        return cursor;
    }

    public Cursor getStoreImagesForMultipleImageSelectGallery() {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM StoreImage WHERE isUsed = 0", null);
        return cursor;
    }

    public Cursor getStoreImagesForCustomGallery() {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM StoreImage WHERE isUsed = 0", null);
        return cursor;
    }

    public int deleteStoreImageId(String pkstoreimageid) {

        return db.delete("StoreImage", "pkstoreimageid = '" + pkstoreimageid + "'", null);
    }


    public int updateIsUsedStatusToZeroForStoreImages() {
        ContentValues initialValues = new ContentValues();

        initialValues.put("isUsed", 0);
        return db.update("StoreImage", initialValues,
                "showImageTemp = '" + 1 + "'", null);
    }

    public int updateIsUsedStatusForStoreImages(String ImageId, int status) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("isUsed", status);
        return db.update("StoreImage", initialValues,
                "imagename = '" + ImageId + "'", null);
    }


    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = dbh.getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }


    public boolean checkIfImageIsUsed(String storeImageId) {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM StoreImage WHERE isUsed = 1 and pkstoreimageid='" + storeImageId + "'", null);
        if (cursor != null && cursor.moveToFirst()) {
            return true;
        }
        if (cursor != null) cursor.close();

        return false;

    }


}
