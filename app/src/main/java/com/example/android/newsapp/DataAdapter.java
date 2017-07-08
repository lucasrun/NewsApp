package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mhesah on 2017-07-06. DATA OBJECT ADAPTER
 */

public class DataAdapter extends ArrayAdapter<Data> {

    public DataAdapter(Context context, List<Data> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.layout_list, parent, false);
        }

        Data currentData = getItem(position);

        TextView sectionTV = (TextView) listItemView.findViewById(R.id.sectionTV);
        TextView titleTV = (TextView) listItemView.findViewById(R.id.titleTV);

        sectionTV.setText(currentData.getmSection());
        titleTV.setText(currentData.getmTitle());

        return listItemView;
    }
}