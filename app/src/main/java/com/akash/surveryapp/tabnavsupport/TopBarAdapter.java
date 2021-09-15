package com.akash.surveryapp.tabnavsupport;


import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.akash.surveryapp.ui.RecordFragment;
import com.akash.surveryapp.ui.RecordListFragment;

import java.util.ArrayList;
import java.util.List;


public class TopBarAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragments = new ArrayList<>();
    Context context;

    public TopBarAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
    }

    public void addFragments(Fragment fragment) {
        fragments.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {


        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = Fragment.instantiate(context, RecordFragment.class.getName());

                break;

            case 1:


                fragment = Fragment.instantiate(context, RecordListFragment.class.getName());


                break;


        }
        return fragment;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
