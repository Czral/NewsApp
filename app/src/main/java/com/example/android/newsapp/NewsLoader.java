package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by XXX on 05-Apr-18.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String mUrl;

    public NewsLoader(Context context, String url) {

        super(context);
        mUrl = url;
    }

    @Override
    public List<News> loadInBackground() {

        if (mUrl == null) {

            return null;
        } else {

            List<News> news = NewsUtils.fetchNewsData(mUrl);
            return news;
        }

    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

}
