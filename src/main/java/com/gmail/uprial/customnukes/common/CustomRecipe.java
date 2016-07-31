package com.gmail.uprial.customnukes.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class CustomRecipe {
    private String[] recipe;

    public CustomRecipe() {
        recipe = new String[9];
        for(int i = 0; i < 9; i++)
            recipe[i] = Material.AIR.toString();
    }
    
    public void setItem(int i, int j, String material) {
        if((i >= 0) && (i < 3) && (j >= 0) && (j < 3))
            recipe[dim2line(i, j)] = material;
    }

    public String toString() {
        return "[" + StringUtils.join(recipe, ",") + "]"; 
    }
    
    public ShapedRecipe getShapedRecipe(ItemStack result) {
        Map<String, Character> materials2char = new HashMap<String, Character>();
        Map<Character, String> char2materials = new HashMap<Character, String>();
        int chars_count = 0;
        for(int i = 0; i < 9; i++) {
            if(null == materials2char.get(recipe[i])) {
                char c = id2char(chars_count);
                chars_count++;

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
        
        ShapedRecipe shapedRecipe = new ShapedRecipe(result);
        shapedRecipe.shape(shapes[0], shapes[1], shapes[2]);
        for(int i = 0; i < chars_count; i++) {
            char c = id2char(i);
            shapedRecipe.setIngredient(c, Material.getMaterial(char2materials.get(c).toString()));
        }

        return shapedRecipe;
    }
    
    public static CustomRecipe getFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        CustomRecipe recipe = new CustomRecipe();
        
        List<?> rows = config.getList(key + ".recipe");
        if(null == rows) {
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
                if(null == Material.getMaterial(cols[j])) {
                    customLogger.error(String.format("Invalid material '%s' in explosive '%s' at row %d, col %d", cols[j], name, i ,j));
                    return null;
                }
                else
                    recipe.setItem(i, j, cols[j]);
            }
        }
        
        return recipe;
    }    
    
    private static int dim2line(int i, int j) {
        return i * 3 + j;
    }
    
    private static char id2char(int id) {
        return (char)((byte)'A' + id);
    }
}
