package com.prakshal.qeats.model;

import java.util.Arrays;

public class Restaurant {
    private String restaurantId;
    private String name;
    private String city;
    private String imageUrl;
    private double latitude;
    private double longitude;
    private String opensAt;
    private String closesAt;
    private String[] attributes;

    public Restaurant() {
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getOpensAt() {
        return opensAt;
    }

    public void setOpensAt(String opensAt) {
        this.opensAt = opensAt;
    }

    public String getClosesAt() {
        return closesAt;
    }

    public void setClosesAt(String closesAt) {
        this.closesAt = closesAt;
    }

    public String[] getAttributes() {
        return Arrays.copyOf(attributes,attributes.length);
    }

    public void setAttributes(String[] attributes) {
        this.attributes = Arrays.copyOf(attributes,attributes.length);
    }

    public String toString() {
        return getName();
    }

}
