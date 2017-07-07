package com.example.android.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by mhesah on 2017-07-06. LOADER FOR MANAGING INSTANCES
 */

public class DataLoader extends AsyncTaskLoader<List<Data>> {

    // tag for log messages
    private static final String LOG_TAG = DataLoader.class.getName();

    // url query
    private String mKeyword;

    public DataLoader(Context context, String mKeyword) {
        super(context);
        this.mKeyword = mKeyword;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    // background thread
    @Override
    public List<Data> loadInBackground() {
        if (mKeyword == null) {
            return null;
        }

        // network request, response, list extract
        List<Data> data = DataUtils.fetchData(mKeyword);
        return data;
    }
}