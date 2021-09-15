package com.akash.surveryapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.akash.surveryapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohit on 8/10/16.
 */
public class CardViewSingleSelectAdapter extends RecyclerView.Adapter<CardViewSingleSelectAdapter.DataObjectHolder> {
    Context context;
    int isSelected;
    int row_index = -1;
    private String LOG_TAG = "MyRecyclerViewAdapter";

    private List<String> mList = new ArrayList<>();


    public CardViewSingleSelectAdapter(int isSelected, List<String> getstatelist, Context context) {

        this.mList = getstatelist;

        this.context = context;
        this.isSelected = isSelected;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_dialog, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.name.setText(mList.get(position));


        holder.linearMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = position;
                notifyDataSetChanged();
            }
        });

        if (row_index != -1) {
            if (row_index == position) {
                holder.linearMaster.setBackgroundColor(context.getResources().getColor(R.color.cardview_shadow_start_color));
                holder.name.setTextColor(context.getResources().getColor(R.color.cardview_shadow_end_color));
            } else {
                holder.linearMaster.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
                holder.name.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    @Override
    public int getItemCount() {

        return mList.size();


    }

    public class DataObjectHolder extends RecyclerView.ViewHolder {

        TextView name;
        LinearLayout linearMaster;
        android.widget.RadioButton RadioButton;

        public DataObjectHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            RadioButton = (android.widget.RadioButton) itemView.findViewById(R.id.RadioButton);
            linearMaster = (LinearLayout) itemView.findViewById(R.id.linearMaster);
        }
    }


}

