package com.example.android.newsapp;

/**
 * Created by mhesah on 2017-07-06. DATA OBJECT
 */

public class Data {
    String mSection;
    String mTitle;
    String mUrl;

    public Data(String mSection, String mTitle, String mUrl) {
        this.mSection = mSection;
        this.mTitle = mTitle;
        this.mUrl = mUrl;
    }

    public String getmSection() {
        return mSection;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmUrl() {
        return mUrl;
    }
}