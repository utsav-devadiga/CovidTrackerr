package com.example.covidtracker.api.timeseries;

import android.content.Context;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.covidtracker.StatsFragment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CasesTimeSeriesClient {
    private Context context;


    public  CasesTimeSeriesClient(Context context) {this.context = context; }


}



