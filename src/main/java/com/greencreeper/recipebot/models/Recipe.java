package com.greencreeper.recipebot.models;


import java.util.List;

public class Recipe {
    String name;
    String link;
    String description;
    List<Ingredient> ingredients;
    List<Direction> directions;
    String backQuote;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Direction> getDirections() {
        return directions;
    }

    public void setDirections(List<Direction> directions) {
        this.directions = directions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBackQuote() {
        return backQuote;
    }

    public void setBackQuote(String backQuote) {
        this.backQuote = backQuote==null?"":backQuote;
    }
}
