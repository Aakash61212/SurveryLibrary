package com.akash.surveryapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.akash.surveryapp.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import uk.co.senab.photoview.PhotoViewAttacher;


public class SlideshowDialogFragment extends DialogFragment {
    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private ArrayList<RecordListModel> images;
    private HackyViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblTitle, lblDate;
    ImageView img_back;
    private int selectedPosition = 0;

    public static SlideshowDialogFragment newInstance() {

        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = (HackyViewPager) v.findViewById(R.id.viewpager);
        lblCount = (TextView) v.findViewById(R.id.lbl_count);
        lblTitle = (TextView) v.findViewById(R.id.title);
        lblDate = (TextView) v.findViewById(R.id.date);
        img_back = (ImageView) v.findViewById(R.id.img_back);

        images = (ArrayList<RecordListModel>) getArguments().getSerializable("images");
        selectedPosition = getArguments().getInt("position");
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {/*
                UploadPhotosActivity.tabChange = true;
                Intent i = new Intent(getActivity(), TabActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(i);*/

            }
        });

       /* viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });*/
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        if (images != null) {


            Log.e(TAG, "position: " + selectedPosition);
            Log.e(TAG, "images size: " + images.size());

            myViewPagerAdapter = new MyViewPagerAdapter();
            viewPager.setAdapter(myViewPagerAdapter);
            viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

            setCurrentItem(selectedPosition);
        }
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        lblCount.setText((position + 1) + " of " + images.size());

        RecordListModel image = images.get(position);
        lblTitle.setText(image.getCaption());
        String captureTime = "";
        captureTime = getDateTime(image);
        captureTime = captureTime == null ? "" : captureTime;
        lblDate.setText(captureTime);
    }

    private String getDateTime(RecordListModel image) {
        String captureTime = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
            Date date = sdf.parse(image.getDateAndTime());
            sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            captureTime = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (captureTime.equals("")) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
                Date date = sdf.parse(image.getDateAndTime());
                sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                captureTime = sdf.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return captureTime;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_NoActionBar);
    }

    //	adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.d(TAG, "instantiateItem: ");
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.image_preview);

            RecordListModel image = images.get(position);

            if (image != null) {

                if (Build.VERSION.SDK_INT >= 30) {

                    File mediaStorageDir = new File(
                            Environment.getExternalStorageDirectory(), "DCIM/surveyApp");


                    String imagePath = mediaStorageDir.getPath() + File.separator
                            + image.getMyImageName() + ".png";


                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath + "/");
                    imageViewPreview.setImageBitmap(bitmap);


                } else {


                    File mediaStorageDir = new File(
                            Environment.getExternalStorageDirectory() + "/Android/data/"
                                    + getContext().getPackageName()
                                    + "/surveyApp");


                    String imagePath = mediaStorageDir.getPath() + File.separator
                            + image.getMyImageName() + ".png";
                    imageViewPreview.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                }

          /*  Glide.with(getActivity()).load(image.getLarge())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewPreview);
*/

                container.addView(view);
                try {
                    PhotoViewAttacher pAttacher;
                    pAttacher = new PhotoViewAttacher(imageViewPreview);
                    pAttacher.update();
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
            return view;
        }

        @Override
        public int getCount() {
            Log.d(TAG, "getCount: ");
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            Log.d(TAG, "isViewFromObject: ");
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.d(TAG, "destroyItem: ");
            container.removeView((View) object);
        }
    }
}
