package com.example.android.newsapp;

/**
 * Created by mhesah on 2017-07-06. QUERY UTILS FOR RECEIVING DATA FROM THE NET
 */

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class DataUtils {

    public static final String SECTION_NAME = "sectionName";
    public static final String SECTION_N_A = "Section N/A";
    public static final String WEB_TITLE = "webTitle";
    public static final String WEB_URL = "webUrl";
    public static final String TITLE_N_A = "Title N/A";
    public static final String PROBLEM_PARSING_THE_DATA_JSON_RESULTS = "Problem parsing the data JSON results";
    public static final String RESULTS = "results";
    public static final String RESPONSE = "response";
    public static final String PROBLEM_RETRIEVING_THE_JSON_RESULTS = "Problem retrieving the JSON results.";
    public static final String ERROR_RESPONSE_CODE = "Error response code: ";
    public static final int TIMEOUT = 10000;
    public static final int CONNECT_TIMEOUT = 15000;
    public static final String REQUEST_METHOD = "GET";
    public static final String HTTP_ENTRY_URL = "http://content.guardianapis.com/search?q=";
    public static final String API_KEY_TEST = "&api-key=test";
    public static final String PROBLEM_BUILDING_THE_URL = "Problem building the URL ";
    public static final String PROBLEM_MAKING_THE_HTTP_REQUEST = "Problem making the HTTP request.";
    public static final String DATA_UTILS = "DataUtils";
    private static final String LOG_TAG = DataUtils.class.getSimpleName();

    private DataUtils() {
    }

    public static List<Data> fetchData(String mKeyword) {
        URL url = createUrl(mKeyword);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, PROBLEM_MAKING_THE_HTTP_REQUEST, e);
        }

        List<Data> data = extractJSON(jsonResponse);
        return data;
    }

    private static URL createUrl(String mKeyword) {
        URL url = null;
        try {
            url = new URL(HTTP_ENTRY_URL + mKeyword + API_KEY_TEST);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, PROBLEM_BUILDING_THE_URL, e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, ERROR_RESPONSE_CODE + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, PROBLEM_RETRIEVING_THE_JSON_RESULTS, e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Data> extractJSON(String dataJSON) {
        if (TextUtils.isEmpty(dataJSON)) {
            return null;
        }

        List<Data> data = new ArrayList<>();

        try {
            JSONObject news = new JSONObject(dataJSON);

            if (news.has(RESPONSE)) {
                JSONObject response = news.getJSONObject(RESPONSE);

                if (response.has(RESULTS)) {
                    JSONArray result = response.getJSONArray(RESULTS);

                    for (int i = 0; i < result.length(); i++) {
                        String section = "";
                        String title = "";
                        String webUrl = "";

                        // single news
                        JSONObject singleNews = result.getJSONObject(i);
                        if (singleNews.has(SECTION_NAME)) {
                            section = singleNews.getString(SECTION_NAME);
                        } else {
                            section = SECTION_N_A;
                        }
                        if (singleNews.has(WEB_TITLE)) {
                            title = singleNews.getString(WEB_TITLE);
                        } else {
                            title = TITLE_N_A;
                        }
                        if (singleNews.has(WEB_URL)) {
                            webUrl = singleNews.getString(WEB_URL);
                        }

                        data.add(new Data(section, title, webUrl));
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(DATA_UTILS, PROBLEM_PARSING_THE_DATA_JSON_RESULTS, e);
        }

        return data;
    }
}