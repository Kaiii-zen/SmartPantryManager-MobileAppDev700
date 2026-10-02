package com.kaitlin.smartpantrymanager.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.kaitlin.smartpantrymanager.R;
import com.kaitlin.smartpantrymanager.database.DatabaseHelper;
import com.kaitlin.smartpantrymanager.models.PantryItem;

public class AddEditIngredientActivity extends AppCompatActivity {
    // constant for the intent extra key
    public static final String EXTRA_PANTRY_ID = "pantry_item_id";

    private TextInputEditText editName, editQuantity, editUnit, editExpiry;
    private DatabaseHelper db;
    // null in ADD mode; the existing id in EDIT mode
    private Integer editingItemId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        db = new DatabaseHelper(this);

        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editUnit = findViewById(R.id.editUnit);
        editExpiry = findViewById(R.id.editExpiry);
        TextView screenTitle = findViewById(R.id.textScreenTitle);
        MaterialButton buttonSave = findViewById(R.id.buttonSave);
        MaterialButton buttonCancel = findViewById(R.id.buttonCancel);

        if (getIntent().hasExtra(EXTRA_PANTRY_ID)) {
            editingItemId = getIntent().getIntExtra(EXTRA_PANTRY_ID, -1);
            screenTitle.setText("Edit Ingredient");
            loadExistingItem();
        } else {
            screenTitle.setText("Add ingredient");
        }

        buttonSave.setOnClickListener(v -> saveItem());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void loadExistingItem() {
        PantryItem item = db.getPantryItemById(editingItemId);
        if (item == null) {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        editName.setText(item.getName());
        editQuantity.setText(String.valueOf(item.getQuantity()));
        editUnit.setText(item.getUnit());
        editExpiry.setText(item.getExpiryDate());
    }

    private void saveItem() {
        String name = editName.getText() == null ? "" : editName.getText().toString().trim();
        String quantityStr = editQuantity.getText() == null ? "" : editQuantity.getText().toString().trim();
        String unit = editUnit.getText() == null ? "" : editUnit.getText().toString().trim();
        String expiry = editExpiry.getText() == null ? "" : editExpiry.getText().toString().trim();

        // Input Validation
        if (TextUtils.isEmpty(name)) {
            editName.setError("Name is required");
            editName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(quantityStr)) {
            editQuantity.setError("Quantity is required");
            editQuantity.requestFocus();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(quantityStr);
        } catch (NumberFormatException e) {
            editQuantity.setError("Quantity must be a number");
            editQuantity.requestFocus();
            return;
        }

        if (quantity <= 0) {
            editQuantity.setError("Quantity must be greater than 0");
            editQuantity.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(unit)) {
            editUnit.setError("Unit is required");
            editUnit.requestFocus();
            return;
        }

        // Save to DB
        if (editingItemId == null) {
            // ADD mode
            PantryItem newItem = new PantryItem(name, quantity, unit, expiry);
            long newId = db.insertPantryItem(newItem);
            if (newId > 0) {
                Toast.makeText(this, "Added " + name, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        } else {
            // EDIT MODE
            PantryItem updated = new PantryItem(editingItemId, name, quantity, unit, expiry);
            int rows = db.updatePantryItem(updated);
            if (rows > 0) {
                Toast.makeText(this, "Updated " + name, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update item", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }
}

