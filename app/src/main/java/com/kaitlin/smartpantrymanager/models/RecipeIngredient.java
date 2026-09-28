package com.kaitlin.smartpantrymanager.models;

public class RecipeIngredient {
    private int id;
    private int recipeId;
    private String ingredientName;
    private double quantity;
    private String unit;

    public RecipeIngredient() {}

    // constructor for new ingredients
    public RecipeIngredient(int recipeId, String ingredientName, double quantity, String unit) {
        this.recipeId = recipeId;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unit = unit;
    }

    // full constructor
    public RecipeIngredient(int id, int recipeId, String ingredientName, double quantity, String unit) {
        this.id = id;
        this.recipeId = recipeId;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unit = unit;
    }

    // getters
    public int getId() {return id;}
    public int getRecipeId() {return recipeId;}
    public String getIngredientName() {return ingredientName;}
    public double getQuantity() {return quantity;}
    public String getUnit() {return unit;}

    // setters
    public void setId(int id) {this.id = id;}
    public void setRecipeId(int recipeId) {this.recipeId = recipeId;}
    public void setIngredientName(String ingredientName) {this.ingredientName = ingredientName;}
    public void setQuantity(double quantity) {this.quantity = quantity;}
    public void setUnit(String unit) {this.unit = unit;}

    @Override
    public String toString() {
        return ingredientName + " (" + quantity + " " + unit + ")";
    }
}
