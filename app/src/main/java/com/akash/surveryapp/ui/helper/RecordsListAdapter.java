package com.akash.surveryapp.ui.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.akash.surveryapp.R;
import com.akash.surveryapp.database.DatabaseAdapter;
import com.akash.surveryapp.ui.HomeActivity;
import com.akash.surveryapp.ui.RecordListFragment;
import com.akash.surveryapp.utils.CheckBoxAnimated;
import com.akash.surveryapp.utils.RecordListModel;
import com.akash.surveryapp.utils.SlideshowDialogFragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Akash on 02/20/21.
 */
public class RecordsListAdapter extends RecyclerView.Adapter<RecordsListAdapter.DataObjectHolder> {
    Context context;
    private ArrayList<RecordListModel> recordListModels = new ArrayList<>();


    ItemListener mListener;
    FragmentManager fragmentManager;
    RecordListFragment fragment;
    Activity activity;
    LayoutInflater layoutInflater;
    DatabaseAdapter db;

    double multiple;

    public RecordsListAdapter(ArrayList<RecordListModel> recordListModelArrayList,
                              Context context, RecordListFragment fragment, FragmentManager fragmentManager, Activity activity, ItemListener itemListener) {

        this.recordListModels = recordListModelArrayList;
        this.fragmentManager = fragmentManager;
        this.fragment = fragment;
        this.context = context;
        this.activity = activity;
        mListener = itemListener;
        db = new DatabaseAdapter(context);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater
                .inflate(R.layout.tab_recorditem, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(final DataObjectHolder holder, @SuppressLint("RecyclerView") final int position) {


        String captureTime = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
            Date date = sdf.parse(recordListModels.get(position).getDateAndTime());
            sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            captureTime = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (captureTime.equals("")) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
                Date date = sdf.parse(recordListModels.get(position).getDateAndTime());
                sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                captureTime = sdf.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        holder.list_title.setText(recordListModels.get(position).getRecordingpath());

        holder.txtlat.setText(recordListModels.get(position).getStore_imageLattitude());
        holder.txtlong.setText(recordListModels.get(position).getStore_imageLongitude());
        if (checkImageUsedStatus(recordListModels.get(position))) {
            holder.tv_image_used.setVisibility(View.VISIBLE);
        } else {
            holder.tv_image_used.setVisibility(View.GONE);
        }

        holder.txtDatetime.setText(captureTime);
        holder.text.setText(recordListModels.get(position).getCaption());
        holder.image.setImageBitmap(recordListModels.get(position).getImage());


        holder.list_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recordListModels.get(position).getRecordingpath() != null && !recordListModels.get(position).getRecordingpath().isEmpty()) {
                    String path = context.getExternalFilesDir("/").getAbsolutePath() + "/" +recordListModels.get(position).getRecordingpath();
                    File filepath = new File(path);
                    mListener.onPlayAudio(position, filepath);


                }
            }
        });
        holder.image.setOnClickListener(new View.OnClickListener() {
            int mposition = position;

            @Override
            public void onClick(View arg0) {


                callFragment(mposition);

            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();

                    }
                }).setTitle("Are you sure do you want to delete ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onRemoveRecord(position, recordListModels.get(position));

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        if (recordListModels != null && recordListModels.size() > 0) {
            final RecordListModel bean = recordListModels.get(position);
            holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    bean.setChecked(b);
                    int count = mListener.isAnyItemSelected(recordListModels);


                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return recordListModels.size();
    }


    public class DataObjectHolder extends RecyclerView.ViewHolder {

        public TextView text, datetime, tv_image_used, lat, longi, txtlat, txtlong, txtDatetime;
        public ImageView image;
        public ImageView btnDelete;
        public CardView card_view;
        public RelativeLayout rel_top;
        public AppCompatCheckBox cb;
        private ImageView list_image;
        private TextView list_title, list_date;

        public DataObjectHolder(View itemView) {
            super(itemView);


            text = (TextView) itemView.findViewById(R.id.text);
            btnDelete = (ImageView) itemView.findViewById(R.id.bttn_delete);
            image = (ImageView) itemView.findViewById(R.id.img_storeimages);
            cb = (AppCompatCheckBox) itemView.findViewById(R.id.scb);
            datetime = (TextView) itemView.findViewById(R.id.datatime);
            txtDatetime = (TextView) itemView.findViewById(R.id.txtDatetime);
            tv_image_used = (TextView) itemView.findViewById(R.id.tv_image_used);
            card_view = (CardView) itemView.findViewById(R.id.card_view);
            rel_top = (RelativeLayout) itemView.findViewById(R.id.rel_top);
            lat = (TextView) itemView.findViewById(R.id.lat);
            longi = (TextView) itemView.findViewById(R.id.longi);
            txtlat = (TextView) itemView.findViewById(R.id.txtLat);
            txtlong = (TextView) itemView.findViewById(R.id.txtLongi);
            card_view.setCardBackgroundColor(context.getResources().getColor(R.color.cardview_light_background));
            list_image = (ImageView) itemView.findViewById(R.id.play_image_icon);
            list_title = (TextView) itemView.findViewById(R.id.list_title);
            list_date = (TextView) itemView.findViewById(R.id.list_date);


        }


        private int getIndex(Spinner spinner, String myString) {
            for (int i = 0; i < spinner.getCount(); i++) {
                if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                    return i;
                }
            }

            return 0;
        }

    }


    private boolean checkImageUsedStatus(RecordListModel recordListModel) {
        db.open();
        if (db.checkIfImageIsUsed(recordListModel.getStoreImageID())) {
            db.close();
            return true;
        }
        db.close();
        return false;
    }


    public interface ItemListener {
        void onClickImage(int position, RecordListModel item);

        void onRemoveRecord(int position, RecordListModel item);

        void onPlayAudio(int position, File file);

        int isAnyItemSelected(ArrayList<RecordListModel> item);


    }


    public void callFragment(int position) {
        try {
            Bundle bundle = new Bundle();
            bundle.putSerializable("images", recordListModels);
            bundle.putInt("position", position);

            FragmentTransaction ft = ((HomeActivity) context).getSupportFragmentManager().beginTransaction();
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

