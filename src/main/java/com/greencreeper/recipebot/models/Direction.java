package com.greencreeper.recipebot.models;


public class Direction {
    String imageUrl;
    String text;

    public Direction(String imageUrl, String text) {
        this.imageUrl = imageUrl;
        this.text = text;
    }

    public Direction() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getText() {
        return text;
    }

}
