package com.example.android.newsapp;

/**
 * Created by XXX on 05-Apr-18.
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class NewsUtils {

    private static final String LOG = "LOG";
    private static final String RESPONSE = "response";
    private static final String RESULTS = "results";
    private static final String WEBTITLE = "webTitle";
    private static final String SECTIONNAME = "sectionName";
    private static final String WEBRUBLICATIONDATE = "webPublicationDate";
    private static final String WEBURL = "webUrl";
    private static final String TAGS = "tags";
    private static final String FIELDS = "fields";
    private static final String STARRATING = "starRating";

    private NewsUtils() {
    }

    private static URL createUrl(String stringUrl) {

        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG, "Error building the URL", e);
        }

        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        } else {

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(15000);
                urlConnection.setConnectTimeout(25000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {

                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readfromStream(inputStream);
                } else {

                    Log.e(LOG, "Error response code:  " + urlConnection.getResponseCode());
                }

            } catch (IOException e) {
                Log.e(LOG, "Error retrieving data from server. ", e);

            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        return jsonResponse;
    }

    private static String readfromStream(InputStream inputStream) throws IOException {

        StringBuilder outputString = new StringBuilder();

        if (inputStream != null) {

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = bufferedReader.readLine();
            while (line != null) {
                outputString.append(line);
                line = bufferedReader.readLine();
            }
        }
        return outputString.toString();
    }

    public static List<News> extractFeaturesFromJson(String newsJSON) {

        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<News> news = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // build up a list of Earthquake objects with the corresponding data.
            JSONObject newsJsonObject = new JSONObject(newsJSON);

            JSONObject newsArray = newsJsonObject.getJSONObject(RESPONSE);

            JSONArray results = newsArray.getJSONArray(RESULTS);

            for (int i = 0; i < results.length(); i++) {

                JSONObject currentNews = results.getJSONObject(i);

                String author = " ";
                int rating = 0;

                String title = currentNews.getString(WEBTITLE);

                String section = currentNews.getString(SECTIONNAME);

                String date = currentNews.getString(WEBRUBLICATIONDATE);

                String webUrl = currentNews.getString(WEBURL);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                try {
                    Date newDate = dateFormat.parse(date);
                    dateFormat = new SimpleDateFormat("EEE, dd-MM-yyyy" + " , " + "HH:mm:ss");
                    date = dateFormat.format(newDate).toString();

                } catch (ParseException e) {
                    Log.e(LOG, "Error formatting the date", e);
                }

                if (currentNews.has(TAGS)) {
                    JSONArray tags = newsArray.getJSONArray(TAGS);

                    if (tags.length() != 0) {

                        JSONObject currentNews2 = tags.getJSONObject(i);
                        author = currentNews2.getString(WEBTITLE);
                    }
                }

                if (currentNews.has(FIELDS)) {

                    JSONArray fields = newsArray.getJSONArray(FIELDS);

                    if (fields.length() != 0) {

                        JSONObject currentNews3 = fields.getJSONObject(i);
                        rating = currentNews3.getInt(STARRATING);

                    }
                }

                News newsObject = new News(author, rating, title, section, webUrl, date);
                news.add(newsObject);
            }

        } catch (JSONException e) {

            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG, "Problem parsing the news JSON results", e);

        }

        return news;
    }

    public static List<News> fetchNewsData(String requestUrl) {

        URL url = createUrl(requestUrl);
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {

            Log.e(LOG, "Error making HTTP request.", e);
        }

        List<News> news = extractFeaturesFromJson(jsonResponse);

        Log.e(LOG, "Fetching data.", null);

        return news;

    }

}
