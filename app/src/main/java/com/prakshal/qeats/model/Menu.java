package com.prakshal.qeats.model;

import java.util.ArrayList;
import java.util.List;

public class Menu {

    private List<Item> items = new ArrayList<>();

    private String restaurantId;


    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}
