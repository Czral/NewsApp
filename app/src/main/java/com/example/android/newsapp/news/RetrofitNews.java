package com.example.android.newsapp.news;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RetrofitNews {

    @SerializedName("response")
    @Expose
    private RetrofitResponse response;

    public RetrofitResponse getResponse() {
        return response;
    }

    public void setResponse(RetrofitResponse response) {
        this.response = response;
    }
}
