package com.kaitlin.smartpantrymanager.utils;

import com.kaitlin.smartpantrymanager.models.PantryItem;
import com.kaitlin.smartpantrymanager.models.RecipeIngredient;

import java.util.List;
import java.util.Locale;

public final class MatchingUtils {

    // prevent instantiation
    private MatchingUtils() {

    }

    public static boolean canMakeRecipe(List<RecipeIngredient> requiredIngredients,
                                        List<PantryItem> pantryItems) {
        if (requiredIngredients == null || requiredIngredients.isEmpty()) {
            return false; // recipe with no ingredient is treated as "cannot make"
        }
        if (pantryItems == null || pantryItems.isEmpty()) {
            return false; // Empty pantry = no recipe can be made
        }

        for (RecipeIngredient required : requiredIngredients) {
            if (!hasEnoughInPantry(required, pantryItems)) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasEnoughInPantry(RecipeIngredient required,
                                             List<PantryItem> pantryItems) {
        String requiredName = normaliseName(required.getIngredientName());
        String requiredUnit = normaliseUnit(required.getUnit());

        double totalInPantry = 0.0;
        boolean foundAny = false;

        for (PantryItem item : pantryItems) {
            if (namesMatch(requiredName, item.getName())
                    && unitsMatch(requiredUnit, normaliseUnit(item.getUnit()))) {
                totalInPantry += item.getQuantity();
                foundAny = true;
            }
        }

        return foundAny && totalInPantry >= required.getQuantity();
    }

    // NORMALISATION HELPERS
    static String normaliseName(String name) {
        if (name == null) return "";
        String n = name.trim().toLowerCase(Locale.ROOT);

        // Strip a trailing "es"
        if (n.endsWith("es") && n.length() > 2) {
            n = n.substring(0, n.length() - 2);
        }
        // Strip a trailing "s"
        else if (n.endsWith("s") && n.length() > 1) {
            n = n.substring(0, n.length() - 1);
        }
        return n;
    }

    static String normaliseUnit(String unit) {
        if (unit == null) return "";
        String u = unit.trim().toLowerCase(Locale.ROOT);

        switch (u) {
            case "g":
            case "gram":
            case "grams":
                return "gram";
            case "kg":
            case "kilogram":
            case "kilograms":
                return "kg";
            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "ml";
            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return "litre";
            case "piece":
            case "pieces":
                return "piece";
            case "tbsp":
            case "tablespoon":
            case "tablespoons":
                return "tablespoon";
            case "tsp":
            case "teaspoon":
            case "teaspoons":
                return "teaspoon";
            case "clove":
            case "cloves":
                return "clove";
            case "slice":
            case "slices":
                return "slice";
            default:
                return u;
        }
    }

    // 2 ingredients match if normalised forms are equal
    static boolean namesMatch(String normalisedRequired, String pantryName) {
        return normalisedRequired.equals(normaliseName(pantryName));
    }

    // units match if their normalised forms are equal
    static boolean unitsMatch(String normalisedRequiredUnit, String normalisedPantryUnit) {
        return normalisedRequiredUnit.equals(normalisedPantryUnit);
    }
}
