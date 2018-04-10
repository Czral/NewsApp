package com.example.android.newsapp;

/**
 * Created by XXX on 05-Apr-18.
 */

public class News {

    private String mName;

    private int mRating;

    private String mTitle;

    private String mSection;

    private String mUrl;

    private String mDate;

    public News(String name, int rating, String title, String section, String url, String date) {

        mName = name;
        mRating = rating;
        mTitle = title;
        mSection = section;
        mUrl = url;
        mDate = date;

    }

    public String getName() {
        return mName;
    }

    public int getRating() {
        return mRating;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSection() {
        return mSection;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getDate() {
        return mDate;
    }

}
