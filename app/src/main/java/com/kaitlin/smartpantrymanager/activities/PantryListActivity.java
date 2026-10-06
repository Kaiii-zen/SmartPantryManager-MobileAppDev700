package com.kaitlin.smartpantrymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kaitlin.smartpantrymanager.R;
import com.kaitlin.smartpantrymanager.adapters.PantryAdapter;
import com.kaitlin.smartpantrymanager.database.DatabaseHelper;
import com.kaitlin.smartpantrymanager.models.PantryItem;

import java.util.List;
import java.util.ArrayList;
public class PantryListActivity extends AppCompatActivity implements PantryAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private PantryAdapter adapter;
    private TextView textEmpty;
    private DatabaseHelper db;
    private List<PantryItem> pantryItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        db = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerViewPantry);
        textEmpty = findViewById(R.id.textEmptyPantry);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadPantryItems();

        // FAB opens Add/edit screen
        FloatingActionButton fab = findViewById(R.id.fabAddIngredient);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(PantryListActivity.this,
                   AddEditIngredientActivity.class);
            startActivity(intent);
        });

        com.google.android.material.button.MaterialButton findRecipes =
                findViewById(R.id.buttonFindRecipes);
        findRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(PantryListActivity.this,
                    SuggestedRecipesActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // refresh items immediately
        android.util.Log.d("PANTRY_DEBUG", "onResume fired, loading items");
        loadPantryItems();
    }

    private void loadPantryItems() {
        List<PantryItem> fresh = db.getAllPantryItems();

        // rebuild the adapters data source in place
        if (pantryItems == null) {
            pantryItems = fresh;
        } else {
            pantryItems.clear();
            pantryItems.addAll(fresh);
        }


        if (adapter == null) {
            adapter = new PantryAdapter(pantryItems, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        // show or hide the empty state
        if (textEmpty != null) {
            if (pantryItems.isEmpty()) {
                textEmpty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                textEmpty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    // row tap: open edit screen with items id
    @Override
    public void onItemClick(PantryItem item) {
         Intent intent = new Intent(this, AddEditIngredientActivity.class);
         intent.putExtra("pantry_item_id", item.getId());
         startActivity(intent);
    }

    // row long-press: confirm delete
    @Override
    public void onItemLongClick(PantryItem item) {
        new AlertDialog.Builder(this).setTitle("Delete " + item.getName() + "?")
                .setMessage("This ingredient will be removed from your pantry.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.deletePantryItem(item.getId());
                    loadPantryItems();
                })
                .setNegativeButton("Cancel", null).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }
}