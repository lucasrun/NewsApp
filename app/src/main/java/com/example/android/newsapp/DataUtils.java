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

    // tag for log messages
    private static final String LOG_TAG = DataUtils.class.getSimpleName();

    private DataUtils() {
    }

    public static List<Data> fetchData(String mKeyword) {
        URL url = createUrl(mKeyword);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Data> data = extractJSON(jsonResponse);
        return data;
    }

    private static URL createUrl(String mKeyword) {
        URL url = null;
        try {
            url = new URL("http://content.guardianapis.com/search?q=" + mKeyword + "&api-key=test");
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
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
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
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

            if (news.has("response")) {
                JSONObject response = news.getJSONObject("response");

                if (response.has("results")) {
                    JSONArray result = response.getJSONArray("results");

                    for (int i = 0; i < result.length(); i++) {
                        String section = "";
                        String title = "";
                        String webUrl = "";

                        // single news
                        JSONObject singleNews = result.getJSONObject(i);
                        if (singleNews.has("sectionName")) {
                            section = singleNews.getString("sectionName");
                        } else {
                            section = "Section N/A";
                        }
                        if (singleNews.has("webTitle")) {
                            title = singleNews.getString("webTitle");
                        } else {
                            title = "Title N/A";
                        }
                        if (singleNews.has("webUrl")) {
                            webUrl = singleNews.getString("webUrl");
                        }

                        data.add(new Data(section, title, webUrl));
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("DataUtils", "Problem parsing the data JSON results", e);
        }

        return data;
    }
}