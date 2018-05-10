package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private String URL_NEWS = "";
    private static final String GUARDIAN_URL = "https://content.guardianapis.com";

    @BindView(R.id.empty_state_text)
    public TextView emptyState;

    @BindView(R.id.progress_bar)
    public View progressBar;

    @BindView(R.id.swipe_refresh)
    public SwipeRefreshLayout swipeRefreshLayout;

    private static final int LOADER_ID = 0;
    private NewsAdapter adapter;
    public LoaderManager loaderManager;
    boolean connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        connection = networkInfo != null && networkInfo.isConnectedOrConnecting();

        if (connection) {

            loaderManager = getLoaderManager();
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {

            progressBar.setVisibility(View.GONE);
            emptyState.setText(getResources().getString(R.string.no_internet_connection));
        }

        adapter = new NewsAdapter(this, new ArrayList<News>());
        final ListView newsList = findViewById(R.id.list_view);
        newsList.setAdapter(adapter);
        newsList.setScrollbarFadingEnabled(false);

        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                News currentNews = adapter.getItem(i);
                Uri newsUri = Uri.parse(currentNews.getUrl());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(webIntent, 0);
                boolean safeIntent = activities.size() > 0;

                if (safeIntent) {

                    startActivity(webIntent);
                } else {

                    Toast.makeText(MainActivity.this, getResources().getString(R.string.no_web_browser_installed), Toast.LENGTH_SHORT).show();
                }

            }
        });

        newsList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                int topRow = (newsList == null || newsList.getChildCount() == 0) ?
                        0 : newsList.getChildAt(0).getTop();

                swipeRefreshLayout.setEnabled(i == 0 && topRow >= 0);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                onRestartLoader();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        newsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                String currentSearch = "";
                News currentNews = adapter.getItem(i);
                String currentSection = currentNews.getSection().toLowerCase();
                String[] parts = currentSection.split(" ");
                currentSearch = parts[0];

                if (currentSection.contains(" ")) {

                    for (int k = 1; k < parts.length; k++) {

                        currentSearch += "-" + parts[k];
                    }
                }

                URL_NEWS = GUARDIAN_URL + "/" + currentSearch + "?api-key=test";
                adapter.notifyDataSetChanged();
                searchLoader();

                return true;
            }

        });

    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String keywordSearch = sharedPreferences.getString(getResources().getString(R.string.keyword_key),
                getResources().getString(R.string.keyword_default));

        String sectionSearch = sharedPreferences.getString(getResources().getString(R.string.section_key),
                getResources().getString(R.string.section_default));

        Uri guardianUri = Uri.parse(GUARDIAN_URL);
        Uri.Builder uriBuilder = guardianUri.buildUpon();

        if (keywordSearch.isEmpty() && sectionSearch.equalsIgnoreCase(getResources().getString(R.string.section_list_all))) {

            uriBuilder.appendPath(getResources().getString(R.string.search));
            uriBuilder.appendQueryParameter(getResources().getString(R.string.show_fields), getResources().getString(R.string.starrating));
            uriBuilder.appendQueryParameter(getResources().getString(R.string.show_tags), getResources().getString(R.string.contributor));
            uriBuilder.appendQueryParameter(getResources().getString(R.string.api_key), getResources().getString(R.string.api_value));
            return new NewsLoader(this, uriBuilder.toString());
        } else if (!(keywordSearch.isEmpty()) && (sectionSearch.equalsIgnoreCase(getResources().getString(R.string.section_list_all)))) {

            uriBuilder.appendPath(getResources().getString(R.string.search));
            uriBuilder.appendQueryParameter(getResources().getString(R.string.q), keywordSearch);
            uriBuilder.appendQueryParameter(getResources().getString(R.string.show_tags), getResources().getString(R.string.contributor));
            uriBuilder.appendQueryParameter(getResources().getString(R.string.api_key), getResources().getString(R.string.api_value));
            return new NewsLoader(this, uriBuilder.toString());
        } else if ((keywordSearch.isEmpty()) && !(sectionSearch.equalsIgnoreCase(getResources().getString(R.string.section_list_all)))) {

            uriBuilder.appendPath(sectionSearch);
            uriBuilder.appendQueryParameter(getResources().getString(R.string.show_tags), getResources().getString(R.string.contributor));
            uriBuilder.appendQueryParameter(getResources().getString(R.string.api_key), getResources().getString(R.string.api_value));
            return new NewsLoader(this, uriBuilder.toString());
        } else {

            uriBuilder.appendPath(getResources().getString(R.string.search));
            uriBuilder.appendQueryParameter(getResources().getString(R.string.section), sectionSearch);
            uriBuilder.appendQueryParameter(getResources().getString(R.string.q), keywordSearch);
            uriBuilder.appendQueryParameter(getResources().getString(R.string.show_tags), getResources().getString(R.string.contributor));
            uriBuilder.appendQueryParameter(getResources().getString(R.string.api_key), getResources().getString(R.string.api_value));
            return new NewsLoader(this, uriBuilder.toString());
        }

    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

        adapter.clear();
        progressBar.setVisibility(View.GONE);

        if (news != null && !news.isEmpty()) {

            adapter.addAll(news);
        } else {

            emptyState.setText(getResources().getString(R.string.no_news_found));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {

        adapter.clear();
    }

    public void onRestartLoader() {

        if (connection) {

            swipeRefreshLayout.setRefreshing(true);
            emptyState.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            loaderManager.restartLoader(0, null, this);
        } else {

            emptyState.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            emptyState.setText(getResources().getString(R.string.no_internet_connection));
        }

        adapter.notifyDataSetChanged();
    }

    public void searchLoader() {

        adapter.clear();

        if (connection) {

            loaderManager.restartLoader(0, null, this);
        } else {

            progressBar.setVisibility(View.GONE);
            emptyState.setText(getResources().getString(R.string.no_internet_connection));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {

            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
