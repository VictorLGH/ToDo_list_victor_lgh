package com.isep.simov.todo.model;


public class TaskLocalization {

    private String placeGoogleId;
    private String placeName;
    private String placeAddress;
    private double lat;
    private double lng;


    public TaskLocalization() {
    }

    public TaskLocalization(String placeGoogleId, String placeName, String placeAddress, double lat, double lng) {
        this.placeGoogleId = placeGoogleId;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.lat = lat;
        this.lng = lng;
    }

    public String getPlaceGoogleId() {
        return placeGoogleId;
    }

    public void setPlaceGoogleId(String placeGoogleId) {
        this.placeGoogleId = placeGoogleId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
