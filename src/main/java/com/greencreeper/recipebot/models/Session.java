package com.greencreeper.recipebot.models;


import java.util.HashMap;
import java.util.Map;

public class Session {
    private String id;
    private String request;
    private Map<Integer, String> ingredients;

    public String getId() {
        return id;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getIngredient(int index) {
        return ingredients.get(index);
    }

    public Map<Integer, String> getIngredients() {
        return ingredients;
    }

    public void addIngredient(Integer i, String name) {
        this.ingredients.put(i, name);
    }

    public Session(String id, String request) {
        this.id = id;
        this.request = request;
        this.ingredients = new HashMap<>();
    }
}
