package com.prakshal.qeats.utils;

import android.util.Log;

import com.prakshal.qeats.model.Item;
import com.prakshal.qeats.model.Order;
import com.prakshal.qeats.model.Restaurant;
import com.prakshal.qeats.model.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Parser {

    public static List<Order> getOrdersFromJson(JSONObject response) throws JSONException {

        List<Order> orders = new ArrayList<>();
        JSONArray ordersJsonArray = response.getJSONArray("orders");
        for (int i = 0; i < ordersJsonArray.length(); i++) {
            JSONObject orderJsonObject = ordersJsonArray.getJSONObject(i);
            Order order = getOrderFromJson(orderJsonObject);
            orders.add(order);
        }
        return orders;
    }

    public static Order getOrderFromJson(JSONObject orderJsonObject) throws JSONException {
        Order order = new Order();
        Map<String, Integer> map = new LinkedHashMap<>();
        order.setId(orderJsonObject.getString("id"));
        order.setRestaurantId(orderJsonObject.getString("restaurantId"));
        JSONArray items = orderJsonObject.getJSONArray("items");
        for (int j = 0; j < items.length(); j++) {
            Item item = getItemFromJson(items.getJSONObject(j));
            Integer itemCount = map.get(item.getId());
            if(itemCount != null){
                map.put(item.getId(), itemCount + 1);
            } else{
                map.put(item.getId(), 1);

                order.getItems().add(item);
            }
        }

        for(Item item : order.getItems()) {
            Integer itemCount = map.get(item.getId());
            if (itemCount != null)
                item.setItemCount(itemCount);
        }
        order.setPlacedAt(new Date(orderJsonObject.getInt("placedAt")));
        order.setRating(orderJsonObject.getInt("rating"));
        order.setTotal(orderJsonObject.getInt("total"));
        order.setRestaurant(getRestaurantFromJson(orderJsonObject.getJSONObject("restaurant")));
        order.setStatus(Status.valueOf(orderJsonObject.getString("status")));
        return order;
    }

    public static List<String> getAtttributesFromJson(JSONArray attributesJsonArray) throws JSONException {
        List<String> attributes = new ArrayList<>();
        for (int j = 0; j < attributesJsonArray.length(); j++) {
            attributes.add(attributesJsonArray.getString(j));
        }
        return attributes;
    }

    public static Restaurant getRestaurantFromJson(JSONObject restaurantJsonObject) throws JSONException {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(restaurantJsonObject.getString("restaurantId"));
        restaurant.setName(restaurantJsonObject.getString("name"));
        restaurant.setImageUrl(restaurantJsonObject.getString("imageUrl"));
        restaurant.setOpensAt(restaurantJsonObject.getString("opensAt"));
        restaurant.setClosesAt(restaurantJsonObject.getString("closesAt"));
        restaurant.setAttributes(getAtttributesFromJson(
                restaurantJsonObject.getJSONArray("attributes")));
        return restaurant;
    }

    public static Item getItemFromJson(JSONObject itemJsonObject) throws JSONException {
        Item item = new Item();
        item.setId(itemJsonObject.getString("itemId"));
        item.setName(itemJsonObject.getString("name"));
        item.setPrice(itemJsonObject.getInt("price"));
        return item;
    }
}
