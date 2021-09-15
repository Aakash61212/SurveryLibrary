package com.akash.surveryapp.utils;


import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.akash.surveryapp.R;
import com.akash.surveryapp.database.DatabaseAdapter;

import java.io.File;

/**
 * Created by Admin on 22-06-2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ImageZoomDialogFragment extends DialogFragment {

    DatabaseAdapter db;
    ImageView imageview;
    TextView textView_image_caption;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
        //imageNameListFromBundle = new ArrayList<>();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View rootView = inflater.inflate(R.layout.fragment_image_zoom, container);

        imageview = (ImageView) rootView.findViewById(R.id.imageview);
        textView_image_caption = (TextView) rootView.findViewById(R.id.textView_image_caption);
        db = new DatabaseAdapter(getActivity());
        return rootView;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void zoomImageFromThumb(String ImageName, String caption) {

        File mediaStorageDir;
        if (Build.VERSION.SDK_INT >= 30) {

            mediaStorageDir = new File(
                    Environment.getExternalStorageDirectory(), "DCIM/4cliq");


        }else {
             mediaStorageDir = new File(
                    Environment.getExternalStorageDirectory() + "/Android/data/"
                            + getActivity().getPackageName()
                            + "/4cliq");}


        String imagePath = mediaStorageDir.getPath() + File.separator
                + ImageName + ".png";

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap Zoombitmap = BitmapFactory.decodeFile(imagePath, options);
        imageview.setImageBitmap(Zoombitmap);

        if (caption != null) {
            textView_image_caption.setText(caption);
        }


    }
}
