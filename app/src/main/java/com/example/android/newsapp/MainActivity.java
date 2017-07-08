package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Data>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    // loader id
    private static final int DATA_LOADER_ID = 1;
    // connection vars
    ConnectivityManager connectManager;
    NetworkInfo networkInfo;
    LoaderManager loaderManager;
    // query keyword
    private String mKeyword = "";
    // list adapter
    private DataAdapter mAdapter;
    // different views
    private TextView mEmptyStateTextView;
    private EditText mEditText;
    private Button mButton;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.editTV);
        mButton = (Button) findViewById(R.id.buttonTV);
        mListView = (ListView) findViewById(R.id.listTV);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mListView.setEmptyView(mEmptyStateTextView);

        mAdapter = new DataAdapter(this, new ArrayList<Data>());
        mListView.setAdapter(mAdapter);

        initConnectionManager();

        initButtonClickListener();

        initListClickListener();
    }

    private void initConnectionManager() {
        connectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            loaderManager = getLoaderManager();
            loaderManager.initLoader(DATA_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    private void initButtonClickListener() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                networkInfo = connectManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    getLoaderManager().restartLoader(DATA_LOADER_ID, null, MainActivity.this);
                } else {
                    mAdapter.clear();
                    View loadingIndicator = findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.GONE);
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }
            }
        });
    }

    private void initListClickListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                connectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                networkInfo = connectManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    Data currentData = mAdapter.getItem(position);
                    Uri dataUri = Uri.parse(currentData.getmUrl());
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, dataUri);
                    startActivity(websiteIntent);
                }
            }
        });
    }

    // loader instances
    @Override
    public Loader<List<Data>> onCreateLoader(int i, Bundle bundle) {
        mKeyword = mEditText.getText().toString();
        DataLoader loader = new DataLoader(this, mKeyword);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Data>> loader, List<Data> data) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // clearing adapter
        mEmptyStateTextView.setText(R.string.no_news);
        mAdapter.clear();

        // filling adapter
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Data>> loader) {
        mAdapter.clear();
    }
}