package com.kaitlin.smartpantrymanager.models;

public class Recipe {
    private int id;
    private String name;
    private String steps;

    public Recipe() {}

    // constructor for inserting new recipe
    public Recipe(String name, String steps) {
        this.name = name;
        this.steps = steps;
    }

    // full constructor
    public Recipe(int id, String name, String steps) {
        this.id = id;
         this.name = name;
         this.steps = steps;
    }

    // getters
    public int getId() {return id;}
    public String getName() {return name;}
    public String getSteps() {return steps;}

    // setters
    public void setId(int id) { this.id = id;}
    public void setName(String name) { this.name = name;}
    public void setSteps(String steps) {this.steps = steps;}

    @Override
    public String toString() {
        return name;
    }
}
