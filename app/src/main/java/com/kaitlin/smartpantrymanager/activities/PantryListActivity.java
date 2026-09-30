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


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kaitlin.smartpantrymanager.R;

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
        loadPantryItems();

        // FAB opens Add/edit screen
        FloatingActionButton fab = findViewById(R.id.fabAddIngredient);
        fab.setOnClickListener(v -> {
           //  Intent intent = new Intent(PantryListActivity.this,
           //         AddEditIngredientActivity.class);
          //   startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // refresh items immediately
        loadPantryItems();
    }

    private void loadPantryItems() {
        pantryItems = db.getAllPantryItems();

        if (adapter == null) {
            adapter = new PantryAdapter(pantryItems, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        // show or hide the empty state
        if (pantryItems.isEmpty()) {
            textEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // row tap: open edit screen with items id
    @Override
    public void onItemClick(PantryItem item) {
        // Intent intent = new Intent(this, AddEditIngredientActivity.class);
       // intent.putExtra("pantry_item_id", item.getId());
        // startActivity(intent);
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