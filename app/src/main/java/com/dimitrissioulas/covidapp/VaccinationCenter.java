package com.dimitrissioulas.covidapp;

import android.location.Location;

public class VaccinationCenter {
    private String id;
    private String latitude;
    private String longitude;
    private String name;
    private Location centerLocation;
    private float distance;


    public VaccinationCenter(){

    }

    public VaccinationCenter(String id, String latitude, String longitude, String name) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        centerLocation = new Location("xx");
        centerLocation.setLatitude(Double.parseDouble(latitude));
        centerLocation.setLongitude(Double.parseDouble(longitude));
    }

    public float getDistance(Location usersLocation){
        return usersLocation.distanceTo(this.getCenterLocation());
    }

    public Location getCenterLocation() {
        return centerLocation;
    }

    public String getId() {
        return id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }
}