package com.example.covidtracker.api.timeseries;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CasesTimeSeries implements Parcelable {

    @SerializedName("cases_time_series")
    @Expose
    private List<CasesTimeSeriesData> casesTimeSeries = null;
    @SerializedName("statewise")
    @Expose
    private List<Statewise> statewise = null;


    public List<CasesTimeSeriesData> getCasesTimeSeries() {
        return casesTimeSeries;
    }

    public void setCasesTimeSeries(List<CasesTimeSeriesData> casesTimeSeries) {
        this.casesTimeSeries = casesTimeSeries;
    }

    public List<Statewise> getStatewise() {
        return statewise;
    }

    public void setStatewise(List<Statewise> statewise) {
        this.statewise = statewise;
    }




    public static class Statewise {
        @SerializedName("active")
        @Expose
        private String active;
        @SerializedName("confirmed")
        @Expose
        private String confirmed;
        @SerializedName("deaths")
        @Expose
        private String deaths;
        @SerializedName("deltaconfirmed")
        @Expose
        private String deltaconfirmed;
        @SerializedName("deltadeaths")
        @Expose
        private String deltadeaths;
        @SerializedName("deltarecovered")
        @Expose
        private String deltarecovered;
        @SerializedName("lastupdatedtime")
        @Expose
        private String lastupdatedtime;
        @SerializedName("migratedother")
        @Expose
        private String migratedother;
        @SerializedName("recovered")
        @Expose
        private String recovered;
        @SerializedName("state")
        @Expose
        private String state;
        @SerializedName("statecode")
        @Expose
        private String statecode;
        @SerializedName("statenotes")
        @Expose
        private String statenotes;

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }

        public String getConfirmed() {
            return confirmed;
        }

        public void setConfirmed(String confirmed) {
            this.confirmed = confirmed;
        }

        public String getDeaths() {
            return deaths;
        }

        public void setDeaths(String deaths) {
            this.deaths = deaths;
        }

        public String getDeltaconfirmed() {
            return deltaconfirmed;
        }

        public void setDeltaconfirmed(String deltaconfirmed) {
            this.deltaconfirmed = deltaconfirmed;
        }

        public String getDeltadeaths() {
            return deltadeaths;
        }

        public void setDeltadeaths(String deltadeaths) {
            this.deltadeaths = deltadeaths;
        }

        public String getDeltarecovered() {
            return deltarecovered;
        }

        public void setDeltarecovered(String deltarecovered) {
            this.deltarecovered = deltarecovered;
        }

        public String getLastupdatedtime() {
            return lastupdatedtime;
        }

        public void setLastupdatedtime(String lastupdatedtime) {
            this.lastupdatedtime = lastupdatedtime;
        }

        public String getMigratedother() {
            return migratedother;
        }

        public void setMigratedother(String migratedother) {
            this.migratedother = migratedother;
        }

        public String getRecovered() {
            return recovered;
        }

        public void setRecovered(String recovered) {
            this.recovered = recovered;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getStatecode() {
            return statecode;
        }

        public void setStatecode(String statecode) {
            this.statecode = statecode;
        }

        public String getStatenotes() {
            return statenotes;
        }

        public void setStatenotes(String statenotes) {
            this.statenotes = statenotes;
        }


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {

        dest.writeList(this.statewise);

    }
    public CasesTimeSeries() {
    }

    protected CasesTimeSeries (Parcel in){
        this.statewise = new ArrayList<Statewise>();
        in.readList(this.statewise,Statewise.class.getClassLoader());
    }

    public static final Parcelable.Creator<CasesTimeSeries> CREATOR = new Parcelable.Creator<CasesTimeSeries>(){

        @Override
        public CasesTimeSeries createFromParcel(Parcel source) {
            return new CasesTimeSeries(source);
        }

        @Override
        public CasesTimeSeries[] newArray(int size) {
            return new CasesTimeSeries[size];
        }
    };

}