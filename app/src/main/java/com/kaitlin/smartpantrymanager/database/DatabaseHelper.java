package com.kaitlin.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.kaitlin.smartpantrymanager.models.PantryItem;
import com.kaitlin.smartpantrymanager.models.Recipe;
import com.kaitlin.smartpantrymanager.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    // DB identity
    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 2;

    // pantry_items
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COL_PANTRY_ID = "id";
    public static final String COL_PANTRY_NAME = "name";
    public static final String COL_PANTRY_QUANTITY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiry_date";

    // recipes
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_STEPS = "steps";

    // recipe_ingredients
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RI_ID = "id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";

    public static final String COL_RI_INGREDIENT_NAME = "ingredient_name";

    public static final String COL_RI_QUANTITY = "quantity";
    public static final String COL_RI_UNIT = "unit";

    // constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        // pantry items table
        String createPantry = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PANTRY_NAME + " TEXT NOT NULL, " +
                COL_PANTRY_QUANTITY + " REAL NOT NULL, " +
                COL_PANTRY_UNIT + " TEXT NOT NULL, " +
                COL_PANTRY_EXPIRY + " TEXT" + ")";
        db.execSQL(createPantry);

        // recipes table
        String createRecipes = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT NOT NULL, " +
                COL_RECIPE_STEPS + " TEXT NOT NULL" + ")";
        db.execSQL(createRecipes);

        // recipe ingredients table (FK to recipes)
        String createRecipeIngredients = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                COL_RI_INGREDIENT_NAME + " TEXT NOT NULL, " +
                COL_RI_QUANTITY + " REAL NOT NULL, " +
                COL_RI_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES " +
                TABLE_RECIPES + "(" + COL_RECIPE_ID + ") ON DELETE CASCADE)";
        db.execSQL(createRecipeIngredients);

        seedRecipes(db);

    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }


    // ----- PANTRY ITEM CRUD ------
    // insert
    public long insertPantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, item.getName());
        values.put(COL_PANTRY_QUANTITY, item.getQuantity());
        values.put(COL_PANTRY_UNIT, item.getUnit());
        values.put(COL_PANTRY_EXPIRY, item.getExpiryDate() == null ? "" : item.getExpiryDate());

        long newId = db.insert(TABLE_PANTRY, null, values);
        return newId;
    }

    // read
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_PANTRY + " ORDER BY " + COL_PANTRY_NAME + " ASC", null
        );

        if (cursor.moveToFirst()) {
            do {
                PantryItem item = new PantryItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_PANTRY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QUANTITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY))
                );
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public PantryItem getPantryItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_PANTRY + " WHERE " + COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)});

        PantryItem item = null;
        if (cursor.moveToFirst()) {
            item = new PantryItem(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_PANTRY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QUANTITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY))
            );
        }
        cursor.close();
        return item;
    }

    // update and delete
    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, item.getName());
        values.put(COL_PANTRY_QUANTITY, item.getQuantity());
        values.put(COL_PANTRY_UNIT, item.getUnit());
        values.put(COL_PANTRY_EXPIRY, item.getExpiryDate() == null ? "" : item.getExpiryDate());

        int rows = db.update(
                TABLE_PANTRY,
                values,
                COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(item.getId())}
        );
        return rows;
    }

    public int deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(
                TABLE_PANTRY,
                COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        return rows;
    }

    // RECIPE READS (read only)
    // return all recipes
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_RECIPES + " ORDER BY " + COL_RECIPE_NAME + " ASC",
                null);

        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = new Recipe(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_STEPS))
                );
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recipes;
    }

    // return recipe by id
    public Recipe getRecipeById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_RECIPES + " WHERE " + COL_RECIPE_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        Recipe recipe = null;
        if (cursor.moveToFirst()) {
            recipe = new Recipe(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_STEPS))
            );
        }
        cursor.close();
        return recipe;
    }

    // method for strict-matching
    public List<RecipeIngredient> getIngredientsForRecipe(int recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_RECIPE_INGREDIENTS + " WHERE " + COL_RI_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)}
        );

        if (cursor.moveToFirst()) {
            do {
                RecipeIngredient ri = new RecipeIngredient(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_RI_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_RI_RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_INGREDIENT_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RI_QUANTITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_UNIT))
                );
                ingredients.add(ri);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ingredients;
    }

    // RECIPE SEEDING (runs once, inside onCreate)
    private void seedRecipes(SQLiteDatabase db) {
        // Recipe 1: Tomato Pasta
        long r1 = insertRecipe(db, "Tomato Pasta",
                "1. Boil pasta until al dente.\n" +
                        "2. Heat olive oil, add garlic and chopped tomatoes.\n" +
                        "3. Simmer 5 minutes, season, and toss with pasta.");
        insertIngredient(db, r1, "Pasta", 200, "grams");
        insertIngredient(db, r1, "Tomato", 2, "pieces");
        insertIngredient(db, r1, "Garlic", 2, "cloves");
        insertIngredient(db, r1, "Olive Oil", 2, "tablespoons");

        // Recipe 2: Egg Fried Rice
        long r2 = insertRecipe(db, "Egg Fried Rice",
                "1. Cook rice and let cool.\n" +
                        "2. Scramble eggs in a hot pan.\n" +
                        "3. Add rice, soy sauce, and peas. Stir-fry 3 minutes.");
        insertIngredient(db, r2, "Rice", 200, "grams");
        insertIngredient(db, r2, "Egg", 2, "pieces");
        insertIngredient(db, r2, "Soy Sauce", 2, "tablespoons");
        insertIngredient(db, r2, "Peas", 100, "grams");

        // Recipe 3: Cheese Omelette
        long r3 = insertRecipe(db, "Cheese Omelette",
                "1. Beat eggs with a pinch of salt.\n" +
                        "2. Pour into a hot buttered pan.\n" +
                        "3. Add grated cheese, fold, and serve.");
        insertIngredient(db, r3, "Egg", 3, "pieces");
        insertIngredient(db, r3, "Cheese", 50, "grams");
        insertIngredient(db, r3, "Butter", 1, "tablespoon");

        // Recipe 4: Garlic Bread
        long r4 = insertRecipe(db, "Garlic Bread",
                "1. Slice bread.\n" +
                        "2. Mix butter with crushed garlic and parsley.\n" +
                        "3. Spread on bread and bake 10 minutes at 200C.");
        insertIngredient(db, r4, "Bread", 4, "slices");
        insertIngredient(db, r4, "Butter", 3, "tablespoons");
        insertIngredient(db, r4, "Garlic", 3, "cloves");

        // -------- Recipe 5: Onion Soup --------
        long r5 = insertRecipe(db, "Onion Soup",
                "1. Slice onions and caramelise in butter.\n" +
                        "2. Add stock and simmer 20 minutes.\n" +
                        "3. Season and serve hot.");
        insertIngredient(db, r5, "Onion", 3, "pieces");
        insertIngredient(db, r5, "Butter", 2, "tablespoons");
        insertIngredient(db, r5, "Stock", 500, "ml");

        // Recipe 6: Tomato Salad
        long r6 = insertRecipe(db, "Tomato Salad",
                "1. Chop tomatoes and onion.\n" +
                        "2. Drizzle with olive oil and vinegar.\n" +
                        "3. Season with salt and pepper.");
        insertIngredient(db, r6, "Tomato", 3, "pieces");
        insertIngredient(db, r6, "Onion", 1, "piece");
        insertIngredient(db, r6, "Olive Oil", 1, "tablespoon");

        // Recipe 7: Pancakes
        long r7 = insertRecipe(db, "Pancakes",
                "1. Whisk flour, milk, egg, and sugar.\n" +
                        "2. Pour batter into a hot pan.\n" +
                        "3. Flip when bubbles form. Serve with syrup.");
        insertIngredient(db, r7, "Flour", 200, "grams");
        insertIngredient(db, r7, "Milk", 300, "ml");
        insertIngredient(db, r7, "Egg", 2, "pieces");
        insertIngredient(db, r7, "Sugar", 2, "tablespoons");

        // Recipe 8: Vegetable Stir-Fry
        long r8 = insertRecipe(db, "Vegetable Stir-Fry",
                "1. Chop vegetables.\n" +
                        "2. Heat oil in a wok.\n" +
                        "3. Stir-fry vegetables 5 minutes with soy sauce.");
        insertIngredient(db, r8, "Carrot", 2, "pieces");
        insertIngredient(db, r8, "Broccoli", 200, "grams");
        insertIngredient(db, r8, "Soy Sauce", 2, "tablespoons");
        insertIngredient(db, r8, "Olive Oil", 1, "tablespoon");

        // Recipe 9: Chicken Rice Bowl
        long r9 = insertRecipe(db, "Chicken Rice Bowl",
                "1. Cook rice.\n" +
                        "2. Pan-fry chicken until golden.\n" +
                        "3. Serve chicken over rice with soy sauce.");
        insertIngredient(db, r9, "Rice", 200, "grams");
        insertIngredient(db, r9, "Chicken", 300, "grams");
        insertIngredient(db, r9, "Soy Sauce", 2, "tablespoons");

        // Recipe 10: Cheese Toastie
        long r10 = insertRecipe(db, "Cheese Toastie",
                "1. Butter two slices of bread.\n" +
                        "2. Add cheese between slices.\n" +
                        "3. Toast in a pan until golden.");
        insertIngredient(db, r10, "Bread", 2, "slices");
        insertIngredient(db, r10, "Cheese", 50, "grams");
        insertIngredient(db, r10, "Butter", 1, "tablespoon");

        // Recipe 11: Mashed Potatoes
        long r11 = insertRecipe(db, "Mashed Potatoes",
                "1. Boil peeled potatoes until soft.\n" +
                        "2. Mash with butter and milk.\n" +
                        "3. Season to taste.");
        insertIngredient(db, r11, "Potato", 4, "pieces");
        insertIngredient(db, r11, "Butter", 2, "tablespoons");
        insertIngredient(db, r11, "Milk", 100, "ml");

        // Recipe 12: Garlic Butter Shrimp
        long r12 = insertRecipe(db, "Garlic Butter Shrimp",
                "1. Melt butter with garlic.\n" +
                        "2. Add shrimp and cook 3 minutes.\n" +
                        "3. Finish with lemon juice.");
        insertIngredient(db, r12, "Shrimp", 300, "grams");
        insertIngredient(db, r12, "Garlic", 3, "cloves");
        insertIngredient(db, r12, "Butter", 2, "tablespoons");

        // Recipe 13: Banana Smoothie
        long r13 = insertRecipe(db, "Banana Smoothie",
                "1. Peel bananas.\n" +
                        "2. Blend with milk and sugar.\n" +
                        "3. Serve chilled.");
        insertIngredient(db, r13, "Banana", 2, "pieces");
        insertIngredient(db, r13, "Milk", 200, "ml");
        insertIngredient(db, r13, "Sugar", 1, "tablespoon");

        // Recipe 14: Veggie Omelette
        long r14 = insertRecipe(db, "Veggie Omelette",
                "1. Beat eggs.\n" +
                        "2. Add chopped onion and tomato.\n" +
                        "3. Cook in buttered pan until set.");
        insertIngredient(db, r14, "Egg", 3, "pieces");
        insertIngredient(db, r14, "Onion", 1, "piece");
        insertIngredient(db, r14, "Tomato", 1, "piece");
        insertIngredient(db, r14, "Butter", 1, "tablespoon");

        // Recipe 15: Simple Fried Rice
        long r15 = insertRecipe(db, "Simple Fried Rice",
                "1. Heat oil in a pan.\n" +
                        "2. Add cooked rice, soy sauce, and peas.\n" +
                        "3. Stir-fry 5 minutes.");
        insertIngredient(db, r15, "Rice", 250, "grams");
        insertIngredient(db, r15, "Soy Sauce", 2, "tablespoons");
        insertIngredient(db, r15, "Peas", 100, "grams");
        insertIngredient(db, r15, "Olive Oil", 1, "tablespoon");

        // Recipe 16: Creamy Tomato Soup
        long r16 = insertRecipe(db, "Creamy Tomato Soup",
                "1. Saute onion in butter.\n" +
                        "2. Add tomatoes and stock.\n" +
                        "3. Simmer 15 minutes, blend, and add milk.");
        insertIngredient(db, r16, "Tomato", 4, "pieces");
        insertIngredient(db, r16, "Onion", 1, "piece");
        insertIngredient(db, r16, "Stock", 400, "ml");
        insertIngredient(db, r16, "Milk", 100, "ml");
        insertIngredient(db, r16, "Butter", 1, "tablespoon");

        //  Recipe 17: Potato Wedges
        long r17 = insertRecipe(db, "Potato Wedges",
                "1. Cut potatoes into wedges.\n" +
                        "2. Toss in oil, salt, and pepper.\n" +
                        "3. Bake 25 minutes at 200C.");
        insertIngredient(db, r17, "Potato", 4, "pieces");
        insertIngredient(db, r17, "Olive Oil", 2, "tablespoons");

        //  Recipe 18: Carrot Soup
        long r18 = insertRecipe(db, "Carrot Soup",
                "1. Chop carrots and onion.\n" +
                        "2. Saute in butter, then add stock.\n" +
                        "3. Simmer 20 minutes and blend.");
        insertIngredient(db, r18, "Carrot", 4, "pieces");
        insertIngredient(db, r18, "Onion", 1, "piece");
        insertIngredient(db, r18, "Stock", 500, "ml");
        insertIngredient(db, r18, "Butter", 1, "tablespoon");
    }


    private long insertRecipe(SQLiteDatabase db, String name, String steps) {
        ContentValues values = new ContentValues();
        values.put(COL_RECIPE_NAME, name);
        values.put(COL_RECIPE_STEPS, steps);
        return db.insert(TABLE_RECIPES, null, values);
    }

    private void insertIngredient(SQLiteDatabase db, long recipeId, String name, double quantity, String unit) {
        ContentValues values = new ContentValues();
        values.put(COL_RI_RECIPE_ID, recipeId);
        values.put(COL_RI_INGREDIENT_NAME, name);
        values.put(COL_RI_QUANTITY, quantity);
        values.put(COL_RI_UNIT, unit);
        long result = db.insert(TABLE_RECIPE_INGREDIENTS, null, values);
        if (result == -1) {
            android.util.Log.e("DB_ERROR", "Failed to insert ingredient: " + name);
        }
    }
}
