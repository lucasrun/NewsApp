package com.example.android.booklistingapp;

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
            url = new URL("https://www.googleapis.com/books/v1/volumes?q=" + mKeyword + "&maxResults=10");
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
            JSONObject books = new JSONObject(dataJSON);
            if (books.has("items")) {
                JSONArray items = books.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {

                    String title = "";
                    String authors = "";
                    String link = "";
                    String description = "";

                    // JSON single book
                    JSONObject bookInfo = items.getJSONObject(i);

                    if (bookInfo.has("volumeInfo")) {
                        JSONObject volumeInfo = bookInfo.getJSONObject("volumeInfo");

                        if (volumeInfo.has("title")) {
                            title = volumeInfo.getString("title");
                        } else title = "Title N/A";

                        if (volumeInfo.has("description")) {
                            description = volumeInfo.getString("description");
                        } else description = "Description N/A";

                        if (volumeInfo.has("infoLink")) {
                            link = volumeInfo.getString("infoLink");
                        } else link = "";

                        if (volumeInfo.has("authors")) {
                            JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                            for (int j = 0; j < authorsArray.length(); j++) {
                                if (j == 0) {
                                    authors += authorsArray.getString(j);
                                } else {
                                    authors += ", " + authorsArray.getString(j);
                                }
                            }
                        } else {
                            authors = "Authors N/A";
                        }
                    }
                    data.add(new Data(title, authors, link, description));
                }
            }
        } catch (JSONException e) {
            Log.e("DataUtils", "Problem parsing the data JSON results", e);
        }

        return data;
    }
}