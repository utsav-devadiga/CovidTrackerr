package com.example.covidtracker.api.newsData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsDataHolder {

    @GET("v2/top-headlines?country=in&q=covid-19")
    Call<NewsData> getNews(@Query("apiKey") String apiKey);

    @GET("v2/top-headlines?language=en&q=covid-19&sortBy=popularity")
    Call<NewsData> getNewsEn(@Query("apiKey") String apiKey);

}
