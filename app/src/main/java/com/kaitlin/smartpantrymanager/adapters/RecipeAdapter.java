package com.kaitlin.smartpantrymanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kaitlin.smartpantrymanager.R;
import com.kaitlin.smartpantrymanager.models.Recipe;
import com.kaitlin.smartpantrymanager.models.RecipeIngredient;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> recipes;
    private final Map<Integer, List<RecipeIngredient>> ingredientsByRecipeId;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(List<Recipe> recipes,
                         Map<Integer, List<RecipeIngredient>> ingredientsByRecipeId,
                         OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.ingredientsByRecipeId = ingredientsByRecipeId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.textName.setText(recipe.getName());

        List<RecipeIngredient> ingredients = ingredientsByRecipeId.get(recipe.getId());
        int count = (ingredients == null) ? 0 : ingredients.size();
        holder.textSummary.setText(count + (count == 1 ? " ingredient" : " ingredients"));

        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textSummary;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textRecipeName);
            textSummary = itemView.findViewById(R.id.textRecipeSummary);
        }
    }
}
