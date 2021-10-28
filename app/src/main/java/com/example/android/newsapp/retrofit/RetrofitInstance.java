package com.example.android.newsapp.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit;

    public static GetNews getRetrofitInstance() {

        if (retrofit == null) {

            String URL_NEWS = "https://content.guardianapis.com/";
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL_NEWS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(GetNews.class);
    }
}
