package com.example.android.newsapp.retrofit;

import com.example.android.newsapp.news.RetrofitNews;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetNews {

    @GET("search")
    Call<RetrofitNews> getNews(@Query("show-fields") String starRating,
                               @Query("show-tags") String tags,
                               @Query("api-key") String api);

    @GET("{path}")
    Call<RetrofitNews> getSectionNews(@Path("path") String path,
                                      @Query("api-key") String api);

    @GET("search")
    Call<RetrofitNews> getKeywordNews(@Query("q") String keyword,
                                      @Query("api-key") String api);

}
