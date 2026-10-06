package com.kaitlin.smartpantrymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kaitlin.smartpantrymanager.R;
import com.kaitlin.smartpantrymanager.adapters.RecipeAdapter;
import com.kaitlin.smartpantrymanager.database.DatabaseHelper;
import com.kaitlin.smartpantrymanager.models.PantryItem;
import com.kaitlin.smartpantrymanager.models.Recipe;
import com.kaitlin.smartpantrymanager.models.RecipeIngredient;
import com.kaitlin.smartpantrymanager.utils.MatchingUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuggestedRecipesActivity extends AppCompatActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView recyclerView;
    private TextView textNoMatches;
    private RecipeAdapter adapter;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        db = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerViewRecipes);
        textNoMatches = findViewById(R.id.textNoMatches);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadSuggestions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        // load user pantry + every recipe
        List<PantryItem> pantry = db.getAllPantryItems();
        List<Recipe> allRecipes = db.getAllRecipes();

        /*
        // decide which recipes qualify
        List<Recipe> matches = new ArrayList<>();
        Map<Integer, List<RecipeIngredient>> ingredientsByRecipeId = new HashMap<>();

        for (Recipe recipe : allRecipes) {
            List<RecipeIngredient> required = db.getIngredientsForRecipe(recipe.getId());
            ingredientsByRecipeId.put(recipe.getId(), required);

            // Strict matching rule
            if (MatchingUtils.canMakeRecipe(required, pantry)) {
                matches.add(recipe);
            }
        }

        // show matches or empty state message
        if (matches.isEmpty()) {
            textNoMatches.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textNoMatches.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        // Bind to adapter
        if (adapter == null) {
            adapter = new RecipeAdapter(matches, ingredientsByRecipeId, this);
            recyclerView.setAdapter(adapter);
        } else {
            // recreate adapter on refresh
            adapter = new RecipeAdapter(matches, ingredientsByRecipeId, this);
            recyclerView.setAdapter(adapter);
        } */

        // --- DEBUG: print everything we know ---
        android.util.Log.d("MATCH_DEBUG", "=== PANTRY (" + pantry.size() + " items) ===");
        for (PantryItem p : pantry) {
            android.util.Log.d("MATCH_DEBUG",
                    "  pantry: '" + p.getName() + "' qty=" + p.getQuantity() + " unit='" + p.getUnit() + "'");
        }

        List<Recipe> matches = new ArrayList<>();
        Map<Integer, List<RecipeIngredient>> ingredientsByRecipeId = new HashMap<>();

        for (Recipe recipe : allRecipes) {
            List<RecipeIngredient> required = db.getIngredientsForRecipe(recipe.getId());
            ingredientsByRecipeId.put(recipe.getId(), required);

            android.util.Log.d("MATCH_DEBUG", "=== RECIPE: " + recipe.getName() + " ===");
            for (RecipeIngredient r : required) {
                android.util.Log.d("MATCH_DEBUG",
                        "  requires: '" + r.getIngredientName() + "' qty=" + r.getQuantity() + " unit='" + r.getUnit() + "'");
            }

            boolean canMake = MatchingUtils.canMakeRecipe(required, pantry);
            android.util.Log.d("MATCH_DEBUG", "  --> canMake = " + canMake);

            if (canMake) {
                matches.add(recipe);
            }
        }

        if (matches.isEmpty()) {
            textNoMatches.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textNoMatches.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        adapter = new RecipeAdapter(matches, ingredientsByRecipeId, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        // Intent intent = new Intent(this, RecipeDetailActivity.class);
        // intent.putExtra("recipe_id", recipe.getId());
        // startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }
}
