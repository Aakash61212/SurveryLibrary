package com.akash.surveryapp.ui;


import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akash.surveryapp.R;
import com.akash.surveryapp.database.DatabaseAdapter;
import com.akash.surveryapp.ui.helper.RecordsListAdapter;
import com.akash.surveryapp.utils.CheckBoxAnimated;
import com.akash.surveryapp.utils.Preference;
import com.akash.surveryapp.utils.RecordListModel;
import com.akash.surveryapp.utils.Image;
import com.akash.surveryapp.utils.SlideshowDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by Akash on 14-08-2020.
 */
public class RecordListFragment extends Fragment implements RecordsListAdapter.ItemListener {

    public ArrayList<RecordListModel> CustomListViewValuesArr = new ArrayList<RecordListModel>();
    public ArrayList<Image> imageList = new ArrayList<Image>();
    DatabaseAdapter db;


    public static final String MY_PREF = "MyPreferences";


    RecyclerView list;
    RecordsListAdapter adapter;
    Bitmap bitmap;
    String StoreImageID;
    String ImageName = "";
    String DateTime;
    TextView txtNoUploads;
    TextView txt_selectedCount;
    String finalPath;
    String recordedPath;


    static Boolean isImageZoomed = false;
    ImageView Image_thumb1;
    CheckBoxAnimated cb_delete_all;
    TextView txt_delete;


    ImageView img_refresh;
    RelativeLayout rel_delt_all;
    RelativeLayout rel_records;


    Context context;
    String longi;
    String lati;
    private RelativeLayout playerSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private File fileToPlay = null;

    //UI Elements
    private ImageButton playBtn;
    private TextView playerHeader;
    private TextView playerFilename;

    private SeekBar playerSeekbar;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;
    boolean isFromRecord;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_records_list,
                container, false);
        context = getActivity();
        db = new DatabaseAdapter(context);

        isFromRecord = Preference.getBoolean(context, Preference.KEY_IS_RECORD);

        rel_delt_all = view.findViewById(R.id.rel_delt_all);
        rel_records = view.findViewById(R.id.rel_records);
        cb_delete_all = view.findViewById(R.id.cb_delete_all);
        txt_delete = view.findViewById(R.id.txt_delete);
        list = view.findViewById(R.id.list);
        LinearLayoutManager lllm = new LinearLayoutManager(getActivity());
        lllm.setOrientation(LinearLayoutManager.VERTICAL);
        list.setHasFixedSize(true);
        list.setLayoutManager(lllm);
        playerSheet = view.findViewById(R.id.player_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(playerSheet);
        playBtn = view.findViewById(R.id.player_play_btn);
        playerHeader = view.findViewById(R.id.player_header_title);
        playerFilename = view.findViewById(R.id.player_filename);
        playerSeekbar = view.findViewById(R.id.player_seekbar);


        txtNoUploads = view.findViewById(R.id.No_uploads);
        txt_selectedCount = view.findViewById(R.id.txt_selectedCount);
        img_refresh = view.findViewById(R.id.img_refresh);
        //initswipeDown();
        txt_delete = view.findViewById(R.id.txt_delete);
        cb_delete_all = view.findViewById(R.id.cb_delete_all);
        rel_delt_all = view.findViewById(R.id.rel_delt_all);

        if (isImageZoomed) {
            Image_thumb1 = view.findViewById(R.id.img_storeimages);
            Image_thumb1.setClickable(false);
        }

        txt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkUsedStatusForAllSelectedImage(CustomListViewValuesArr)) {


                    Toast.makeText(getActivity(), "Images with Used status can not be deleted", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(txt_selectedCount.getText().toString()) > 0) {
//               alertDialog("Delete images", "", "Are you sure you want to delete selected images?");
                }

            }
        });

        setListData();


        if (CustomListViewValuesArr.size() > 0) {
            img_refresh.setVisibility(View.VISIBLE);
            rel_records.setVisibility(View.VISIBLE);
            txtNoUploads.setVisibility(View.GONE);


        } else {
            rel_records.setVisibility(View.GONE);
            txtNoUploads.setVisibility(View.VISIBLE);
            img_refresh.setVisibility(View.GONE);

        }


        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //We cant do anything here for this app
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPlaying) {
                    pauseAudio();
                } else {
                    if (fileToPlay != null) {
                        resumeAudio();
                    }
                }
            }
        });

        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                resumeAudio();
            }
        });


        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setListData();
            }
        });

        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            setListData();
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    private void deleteImageFromFile(String imagePath, String fileName) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        // Toast.makeText(activity, "Deleted Successfully", Toast.LENGTH_LONG).show();

        File mediaStorageDir = new File(imagePath);

        String mImageName = fileName + ".png";

        finalPath = mediaStorageDir.getPath() + File.separator
                + mImageName;


        File mediaFile = new File(finalPath);

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (mediaFile.exists()) {
            // delete image from file system.
            mediaFile.delete();
        }
    }

    private void deleteMultpleImages() {
        File mediaStorageDir;
        ArrayList<RecordListModel> mTempList = new ArrayList<>();
        mTempList.addAll(CustomListViewValuesArr);
        for (int i = 0; i < CustomListViewValuesArr.size(); i++) {
            if (CustomListViewValuesArr.get(i).isChecked()) {

                deleteImageById(mTempList, CustomListViewValuesArr.get(i).getMyImageName());
            }
        }
        CustomListViewValuesArr.clear();
        CustomListViewValuesArr.addAll(mTempList);

    }

    private void deleteImageById(ArrayList<RecordListModel> CustomListViewValuesArr, String imgId) {
        File mediaStorageDir;

        for (int i = 0; i < CustomListViewValuesArr.size(); i++) {
            String imagePathdelete;
            if (CustomListViewValuesArr.get(i).getMyImageName().equals(imgId)) {

                if (Build.VERSION.SDK_INT >= 30) {

                    mediaStorageDir = new File(
                            Environment.getExternalStorageDirectory(), "DCIM/surveyApp");


                } else {
                    mediaStorageDir = new
                            File(Environment.getExternalStorageDirectory()
                            + "/Android/data/" + context.getApplicationContext().getPackageName() + "/surveyApp");
                }
                imagePathdelete = mediaStorageDir.getPath() + File.separator;
                deleteImageFromFile(imagePathdelete, CustomListViewValuesArr.get(i).getMyImageName());
                CustomListViewValuesArr.remove(i);
            }


        }

    }


    private void pauseAudio() {
        mediaPlayer.pause();
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn, null));
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void resumeAudio() {
        mediaPlayer.start();
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn, null));
        isPlaying = true;

        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);

    }

    private void stopAudio() {
        //Stop The Audio
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn, null));
        playerHeader.setText("Stopped");
        isPlaying = false;
        mediaPlayer.stop();
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void playAudio(File fileToPlay) {

        mediaPlayer = new MediaPlayer();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn, null));
        playerFilename.setText(fileToPlay.getName());
        playerHeader.setText("Playing");
        //Play the audio
        isPlaying = true;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
                playerHeader.setText("Finished");
            }
        });

        playerSeekbar.setMax(mediaPlayer.getDuration());

        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);

    }

    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 500);
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isPlaying) {
            stopAudio();
        }
    }

    private boolean checkUsedStatusForMultipleImages(RecordListModel tempValues) {

        return false;
    }

    private boolean checkUsedStatusForAllSelectedImage(ArrayList<RecordListModel> tempValues) {
        boolean isAnyImageUsed = false;
        for (int i = 0; i < CustomListViewValuesArr.size(); i++) {
            if (CustomListViewValuesArr.get(i).isChecked()) {
                isAnyImageUsed = checkUsedStatusForMultipleImages(CustomListViewValuesArr.get(i));
                if (isAnyImageUsed)
                    return true;
            }
        }
        return false;

    }


    public void setListData() {
        CustomListViewValuesArr.clear();


        db.open();
        //update isUsed =0 to show images in
        db.updateIsUsedStatusToZeroForStoreImages();
        Cursor cursor = db.getStoreImages();
        String Caption = "";
        if (cursor != null && cursor.moveToFirst()) {
            int i = 0;
            do {
                StoreImageID = cursor.getString(0);
                ImageName = cursor.getString(1);
                Caption = cursor.getString(2);
                DateTime = cursor.getString(3);
                lati = cursor.getString(6);
                longi = cursor.getString(7);
                recordedPath = cursor.getString(8);


                if (Build.VERSION.SDK_INT >= 30) {

                    File mediaStorageDir = new File(
                            Environment.getExternalStorageDirectory(), "DCIM/surveyApp");


                    String imagePath = mediaStorageDir.getPath() + File.separator
                            + ImageName + ".png";


                    bitmap = BitmapFactory.decodeFile(imagePath + "/");


                } else {

                    File mediaStorageDir = new File(
                            Environment.getExternalStorageDirectory() + "/Android/data/"
                                    + context.getApplicationContext().getPackageName()
                                    + "/surveyApp");

                    String imagePath = mediaStorageDir.getPath() + File.separator
                            + ImageName + ".png";

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    bitmap = BitmapFactory.decodeFile(imagePath, options);
                }

                RecordListModel sched = new RecordListModel();
                if (bitmap != null) {
                    sched.setImage(bitmap);
                    sched.setCaption(Caption);
                    sched.setStoreImageID(StoreImageID);
                    sched.setMyImageName(ImageName);
                    sched.setDateAndTime(DateTime);
                    sched.setStore_imageLattitude(lati);
                    sched.setStore_imageLongitude(longi);
                    sched.setRecordingpath(recordedPath);


                    CustomListViewValuesArr.add(sched);
                }
            } while (cursor.moveToNext());

        }
        if (cursor != null)
            cursor.close();
        db.close();
        Resources res = getResources();


        //comparision done in Gallery model based on Time of Image taken
        Collections.sort(CustomListViewValuesArr);
        setRecordListAndAdapter(CustomListViewValuesArr);


    }


    /**
     * set Record List to adapter ////
     */


    private void setRecordListAndAdapter(ArrayList<RecordListModel> recordListModelArrayList) {


        if (recordListModelArrayList != null && recordListModelArrayList.size() > 0) {


            if (recordListModelArrayList != null && recordListModelArrayList.size() > 0) {


                img_refresh.setVisibility(View.VISIBLE);
                rel_records.setVisibility(View.VISIBLE);
                txtNoUploads.setVisibility(View.GONE);


                if (adapter == null) {
                    adapter = new RecordsListAdapter(CustomListViewValuesArr, getActivity(),
                            RecordListFragment.this, this.getFragmentManager(), getActivity(), this);
                    list.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }


                ((HomeActivity) context).setRecordCount(CustomListViewValuesArr);
            }
        } else {

            rel_records.setVisibility(View.GONE);
            txtNoUploads.setVisibility(View.VISIBLE);
            img_refresh.setVisibility(View.GONE);

        }

    }


    public int isAnyItemSelected() {
        int counter = 0;
        for (int i = 0; i < CustomListViewValuesArr.size(); i++) {
            if (CustomListViewValuesArr.get(i).isChecked()) {
                counter++;
            }
        }
        return counter;
    }


    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onClickImage(int position, RecordListModel item) {

    }

    @Override
    public void onRemoveRecord(int position, RecordListModel item) {

    }

    @Override
    public void onPlayAudio(int position, File item) {
        playAudio(item);
    }

    @Override
    public int isAnyItemSelected(ArrayList<RecordListModel> item) {
        return 0;
    }


    private class OnItemLongClickListener implements View.OnLongClickListener {
        private int mPosition;

        OnItemLongClickListener(int position, View view) {
            mPosition = position;
        }


        @Override
        public boolean onLongClick(View v) {


            RecordListModel bean = CustomListViewValuesArr.get(mPosition);
            bean.setChecked(!bean.isChecked());
            CheckBoxAnimated checkBox = (CheckBoxAnimated) v.findViewById(R.id.scb);
            checkBox.setChecked(bean.isChecked(), true);

            int count = isAnyItemSelected();

            if (count > 0) {
                txt_selectedCount.setText("" + count);
                rel_delt_all.setVisibility(View.VISIBLE);
            } else {
                rel_delt_all.setVisibility(View.GONE);
            }

            Log.v("long clicked", "pos: " + mPosition);
            return false;
        }
    }

    private class OnItemClickListener implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position, View view) {
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            callFragment(mPosition);
        }


    }


    public void callFragment(int position) {
        try {
            Bundle bundle = new Bundle();
            bundle.putSerializable("images", CustomListViewValuesArr);
            bundle.putInt("position", position);


            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
            newFragment.setArguments(bundle);
            newFragment.show(ft, "slideshow");

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}