package com.prakshal.qeats.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {

    private String id;

    private String restaurantId;

    private String userId;

    private List<Item> items = new ArrayList<>();

    private int total;

    private String timePlaced;

    private Date placedAt;

    private int rating;

    private Status status;

    private Restaurant restaurant;

}