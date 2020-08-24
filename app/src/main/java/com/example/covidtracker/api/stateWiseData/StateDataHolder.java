package com.example.covidtracker.api.stateWiseData;

import com.example.covidtracker.api.timeseries.CasesTimeSeries;

import retrofit2.Call;
import retrofit2.http.GET;

public interface StateDataHolder {
    @GET("data.json")
    Call<CasesTimeSeries> getALLData();

}
