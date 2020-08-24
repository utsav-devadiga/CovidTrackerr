package com.example.covidtracker.api.stateWiseData;

import com.google.android.gms.maps.model.LatLng;

public class Cordinates {
    Double lat;
    Double lng;
    String title;

    public Cordinates(Double lat, Double lng, String title) {
        this.lat = lat;
        this.lng = lng;
        this.title = title;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
