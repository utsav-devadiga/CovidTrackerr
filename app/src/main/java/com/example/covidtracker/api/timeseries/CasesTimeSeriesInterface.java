package com.example.covidtracker.api.timeseries;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CasesTimeSeriesInterface {
    @GET("data.json")
   public Call<CasesTimeSeries> getDailyData();
}
