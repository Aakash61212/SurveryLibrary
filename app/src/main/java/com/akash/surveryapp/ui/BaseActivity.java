package com.akash.surveryapp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.akash.surveryapp.database.DatabaseAdapter;


/**
 * Created by Akash Namdev.
 * Created date 16-03-2019
 * Error  Logging for android
 */
public class BaseActivity extends Activity {private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, final Throwable e) {
                DatabaseAdapter db = new DatabaseAdapter(BaseActivity.this);

            }
        });
    }




    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

  /* public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }*/

}
