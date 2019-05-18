package com.gmail.uprial.customnukes.common;

import com.gmail.uprial.customnukes.CustomNukes;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CustomRecipe {
    private final String[] recipe;
    private final CustomNukes plugin;

    private CustomRecipe(CustomNukes plugin) {
        this.plugin = plugin;
        recipe = new String[9];
        for(int i = 0; i < 9; i++) {
            recipe[i] = Material.AIR.toString();
        }
    }

    private void setItem(int i, int j, String material) {
        if((i >= 0) && (i < 3) && (j >= 0) && (j < 3)) {
            recipe[dim2line(i, j)] = material;
        }
    }

    public String toString() {
        return '[' + StringUtils.join(recipe, ",") + ']';
    }

    public ShapedRecipe getShapedRecipe(String keyLC, ItemStack result) {
        Map<String, Character> materials2char = new HashMap<>();
        Map<Character, String> char2materials = new HashMap<>();
        int charsCount = 0;
        for(int i = 0; i < 9; i++) {
            if(materials2char.get(recipe[i]) == null) {
                char c = id2char(charsCount);
                charsCount++;

                materials2char.put(recipe[i], c);
                char2materials.put(c, recipe[i]);
            }
        }

        String[] shapes = new String[3];
        for(int i = 0; i < 3; i++) {
            shapes[i] = "";
            for(int j = 0; j < 3; j++) {
                shapes[i] += materials2char.get(recipe[dim2line(i, j)]).toString();
            }
        }

        NamespacedKey namespacedKey = new NamespacedKey(plugin, keyLC);
        ShapedRecipe shapedRecipe = new ShapedRecipe(namespacedKey, result);
        shapedRecipe.shape(shapes[0], shapes[1], shapes[2]);
        for(int i = 0; i < charsCount; i++) {
            char c = id2char(i);
            shapedRecipe.setIngredient(c, Material.getMaterial(char2materials.get(c)));
        }

        return shapedRecipe;
    }

    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static CustomRecipe getFromConfig(CustomNukes plugin, FileConfiguration config, CustomLogger customLogger, String key, String name) {
        CustomRecipe recipe = new CustomRecipe(plugin);

        List<?> rows = config.getList(key + ".recipe");
        if(rows == null) {
            customLogger.error(String.format("Empty recipe of item '%s'", name));
            return null;
        }
        if(rows.size() != 3) {
            customLogger.error(String.format("Recipe of item '%s' should have 3 rows", name));
            return null;
        }
        for(int i = 0; i < 3; i++) {
            String[] cols = rows.get(i).toString().split(" ");
            if(cols.length != 3) {
                customLogger.error(String.format("Recipe of item '%s' should have 3 cols at row %s", name, i));
                return null;
            }
            for(int j = 0; j < 3; j++) {
                if(Material.getMaterial(cols[j]) == null) {
                    customLogger.error(String.format("Invalid material '%s' in explosive '%s' at row %d, col %d", cols[j], name, i ,j));
                    return null;
                }
                else {
                    recipe.setItem(i, j, cols[j]);
                }
            }
        }

        return recipe;
    }

    private static int dim2line(int i, int j) {
        return (i * 3) + j;
    }

    private static char id2char(int id) {
        return (char)((byte)'A' + id);
    }
}
