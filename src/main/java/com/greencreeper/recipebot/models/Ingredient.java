package com.greencreeper.recipebot.models;

public class Ingredient {
    String name;
    String quantity;

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }

    public Ingredient(String name, String quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public Ingredient() {
    }
}
