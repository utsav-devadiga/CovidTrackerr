package com.example.covidtracker.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.covidtracker.api.timeseries.CasesTimeSeries;
import com.example.covidtracker.repo.StateRepository;

public class StateData extends ViewModel {

    private MutableLiveData<CasesTimeSeries> casesTimeSeriesMutableLiveData;
    private MutableLiveData<Boolean> isLoading;
    private StateRepository stateRepository;

    public void init(){
        if(casesTimeSeriesMutableLiveData != null){
            return;
        }

        stateRepository = StateRepository.getInstance();
        casesTimeSeriesMutableLiveData = stateRepository.getStatesData();
        isLoading = stateRepository.getLoading();
    }

    public LiveData<CasesTimeSeries> getRegulardatas(){return casesTimeSeriesMutableLiveData;}

    public LiveData<Boolean> getLoading(){
        return  isLoading;
    }
}
