package com.example.android.newsapp.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.newsapp.R;
import com.example.android.newsapp.adapters.NewsAdapter;
import com.example.android.newsapp.databinding.ActivityMainBinding;
import com.example.android.newsapp.news.RetrofitNews;
import com.example.android.newsapp.news.RetrofitResult;
import com.example.android.newsapp.retrofit.GetNews;
import com.example.android.newsapp.retrofit.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    // TODO: Fill in your API key
    private static final String API = "";

    private static final String STAR_RATING = "starRating";
    private static final String CONTRIBUTOR = "contributor";
    private static final String SECTION_ALL = "all";

    private NewsAdapter adapter;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String keywordSearch = sharedPreferences.getString(getResources().getString(R.string.keyword_key),
                getResources().getString(R.string.keyword_default));

        String sectionSearch = sharedPreferences.getString(getResources().getString(R.string.section_key),
                getResources().getString(R.string.section_default));

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

            searchNews(keywordSearch, sectionSearch);
        } else {

            binding.progressBar.setVisibility(View.GONE);
            binding.emptyStateText.setText(getResources().getString(R.string.no_internet_connection));
        }

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int topRow = (recyclerView.getChildCount() == 0) ?
                        0 : recyclerView.getChildAt(0).getTop();

                if (topRow == 0) {

                    binding.swipeRefresh.setEnabled(dy == 0);
                }

            }
        });

        binding.swipeRefresh.setOnRefreshListener(() -> {

            searchNews(keywordSearch, sectionSearch);
            binding.swipeRefresh.setRefreshing(false);
        });

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

    private void searchNews(String keyword, String section) {

        GetNews getNews = RetrofitInstance.getRetrofitInstance();

        if (section.equalsIgnoreCase(SECTION_ALL)) {

            if (keyword == null || keyword.isEmpty()) {

                getNews.getNews(STAR_RATING, CONTRIBUTOR, API).enqueue(new Callback<RetrofitNews>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitNews> call, @NonNull Response<RetrofitNews> response) {

                        if (response.body() != null) {

                            List<RetrofitResult> results = response.body().getResponse().getResults();

                            adapter = new NewsAdapter((ArrayList<RetrofitResult>) results);
                            binding.recyclerView.setAdapter(adapter);

                        } else {

                            binding.emptyStateText.setVisibility(View.VISIBLE);
                        }

                        binding.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitNews> call, @NonNull Throwable t) {

                        binding.emptyStateText.setVisibility(View.VISIBLE);
                        Log.d(TAG, t.toString());
                    }
                });
            } else {

                getNews.getKeywordNews(keyword, API).enqueue(new Callback<RetrofitNews>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitNews> call, @NonNull Response<RetrofitNews> response) {

                        if (response.body() != null) {

                            List<RetrofitResult> results = response.body().getResponse().getResults();

                            adapter = new NewsAdapter((ArrayList<RetrofitResult>) results);
                            binding.recyclerView.setAdapter(adapter);

                        } else {

                            binding.emptyStateText.setVisibility(View.VISIBLE);
                        }

                        binding.progressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitNews> call, @NonNull Throwable t) {

                        binding.emptyStateText.setVisibility(View.VISIBLE);
                        Log.d(TAG, t.toString());
                    }
                });
            }

        } else {

            getNews.getSectionNews(section, API).enqueue(new Callback<RetrofitNews>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitNews> call, @NonNull Response<RetrofitNews> response) {

                    if (response.body() != null) {

                        adapter = new NewsAdapter((ArrayList<RetrofitResult>) response.body().getResponse().getResults());
                        binding.recyclerView.setAdapter(adapter);
                    } else {

                        binding.emptyStateText.setVisibility(View.VISIBLE);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitNews> call, @NonNull Throwable t) {

                    binding.emptyStateText.setVisibility(View.VISIBLE);
                    Log.d(TAG, t.toString());
                }
            });

        }

    }

}
