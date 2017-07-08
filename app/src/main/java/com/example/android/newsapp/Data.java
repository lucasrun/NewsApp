package com.example.android.newsapp;

/**
 * Created by mhesah on 2017-07-06. DATA OBJECT
 */

public class Data {
    String mAuthor;
    String mTitle;
    String mUrl;
    String mDescription;

    public Data(String mAuthor, String mTitle, String mUrl, String mDescription) {
        this.mAuthor = mAuthor;
        this.mTitle = mTitle;
        this.mUrl = mUrl;
        this.mDescription = mDescription;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmUrl() {
        return mUrl;
    }

    public String getmDescription() {
        return mDescription;
    }
}