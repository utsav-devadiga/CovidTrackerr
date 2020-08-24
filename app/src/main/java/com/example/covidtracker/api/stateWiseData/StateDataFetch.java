package com.example.covidtracker.api.stateWiseData;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StateDataFetch {

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.covid19india.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static <S> S createService(Class<S> serviceClass){
        return retrofit.create(serviceClass);
    }
}
