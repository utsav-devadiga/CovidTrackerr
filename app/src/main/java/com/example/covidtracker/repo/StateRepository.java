package com.example.covidtracker.repo;

import androidx.lifecycle.MutableLiveData;

import com.example.covidtracker.api.stateWiseData.StateDataFetch;
import com.example.covidtracker.api.stateWiseData.StateDataHolder;
import com.example.covidtracker.api.timeseries.CasesTimeSeries;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class StateRepository {

    private static StateRepository stateRepository;

    public  static StateRepository getInstance(){
        if (stateRepository == null){
            stateRepository = new StateRepository();
        }
        return stateRepository;
    }

    private StateDataHolder stateDataHolder;

    private  StateRepository(){
        stateDataHolder = StateDataFetch.createService(StateDataHolder.class);
    }
private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public MutableLiveData<Boolean> getLoading(){
        return isLoading;
    }

    public MutableLiveData<CasesTimeSeries> getStatesData(){
        MutableLiveData<CasesTimeSeries> ProvData = new MutableLiveData<>();
        isLoading.setValue(true);
        stateDataHolder.getALLData().enqueue(new Callback<CasesTimeSeries>() {
            @Override
            public void onResponse(Call<CasesTimeSeries> call, Response<CasesTimeSeries> response) {
                if (response.isSuccessful()){
                    isLoading.setValue(false);
                    ProvData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<CasesTimeSeries> call, Throwable t) {
                isLoading.setValue(false);
                Timber.e(t);
            }
        });

        return ProvData;
    }
}
