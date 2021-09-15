package com.akash.surveryapp.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.akash.surveryapp.R;
import com.akash.surveryapp.tabnavsupport.NoSwipePager;
import com.akash.surveryapp.tabnavsupport.TopBarAdapter;
import com.akash.surveryapp.utils.Preference;
import com.akash.surveryapp.utils.RecordListModel;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;


    private NoSwipePager viewPager;
    private static AHBottomNavigation topNavigation;
    private TopBarAdapter pagerAdapter;
    Context context;
    final static String TAG = "Home";
    CoordinatorLayout content;

    static AHNotification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.activity_home);
        context = this;
        content = findViewById(R.id.content);

        setupViewPager();


        topNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        setupTopNavBehaviors();
        setupTopNavStyle();


        addTopNavigationItems();
        topNavigation.setCurrentItem(0);


        topNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (!wasSelected)
                    viewPager.setCurrentItem(position);


                return true;
            }
        });


    }


    //Open Tab of perticular id
    public void setTabBarSelection(int i) {

        topNavigation.setCurrentItem(i);
        topNavigation.refresh();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    //Set Count of cart products
    @SuppressLint("ResourceAsColor")
    public void setRecordCount(List<RecordListModel> modelLists) {
        int count = 0;

        if (modelLists != null) {
            count = modelLists.size();
        }

        if (count > 0) {
            notification = new AHNotification.Builder()
                    .setText(count + "")
                    .setBackgroundColor(R.color.colorWhite)
                    .setTextColor(Color.WHITE)
                    .build();


            topNavigation.setNotification(notification, topNavigation.getItemsCount() - 1);


        }
        // Adding notification to last item.


    }


    @Override
    public void onBackPressed() {

        Preference.saveBoolean(context, Preference.KEY_IS_RECORD, false);

        if (topNavigation.getCurrentItem() != 0) {
            int stateNav = topNavigation.getCurrentItem();

            if (stateNav == 1) {


                topNavigation.setCurrentItem(stateNav - 1);
            }
            pagerAdapter.notifyDataSetChanged();
            topNavigation.refresh();
        } else {
            exitOption();
        }

//


    }

    //Exit app Dailog
    private void exitOption() {


        android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Exit");
        alertBuilder.setMessage("Sure to want to close App ?");
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onClick(DialogInterface dialog, int which) {
                alertBuilder.setCancelable(true);

                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        });
        alertBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        android.app.AlertDialog alert = alertBuilder.create();
        alert.show();

    }

    ///View Pager init and Fraagment addition
    private void setupViewPager() {
        viewPager = (NoSwipePager) findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        pagerAdapter = new TopBarAdapter(context, getSupportFragmentManager());

        pagerAdapter.addFragments(createFragment(R.color.colorAboutGreen));
        pagerAdapter.addFragments(createFragment(R.color.colorAboutGreen));


        viewPager.setAdapter(pagerAdapter);

        pagerAdapter.notifyDataSetChanged();


    }

    @NonNull
    private Fragment createFragment(int color) {
        Fragment fragment = new RecordFragment();
        fragment.setArguments(passFragmentArguments(fetchColor(color)));
        return fragment;
    }


    @NonNull
    private Bundle passFragmentArguments(int color) {
        Bundle bundle = new Bundle();
        bundle.putInt("color", color);
        return bundle;
    }


    public void setupTopNavBehaviors() {
        topNavigation.setBehaviorTranslationEnabled(false);


        topNavigation.setTranslucentNavigationEnabled(true);
    }


    @SuppressLint("ResourceAsColor")
    private void setupTopNavStyle() {

        topNavigation.setDefaultBackgroundColor(fetchColor(R.color.colorAboutGreen));
        topNavigation.setAccentColor(fetchColor(R.color.colorAboutYellow));
        topNavigation.setInactiveColor(fetchColor(R.color.colorWhite));

        // Colors for selected (active) and non-selected items.
        topNavigation.setColoredModeColors(R.color.colorAboutGreen,
                fetchColor(R.color.colorAboutYellow));


        //  Enables Reveal effect
        topNavigation.setColored(false);

        //  Displays item Title always (for selected and non-selected items)
        topNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
    }

    //add Navigation view on topbar
    private void addTopNavigationItems() {


        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Survey", R.drawable.ic_baseline_camera_alt_24, R.color.colorAboutGreen);


        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Records", R.drawable.ic_hamberger, R.color.colorAboutYellow);


        topNavigation.addItem(item1);
        topNavigation.addItem(item2);


    }

    //get set compat color
    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }

}


