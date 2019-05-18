package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.ConfigReader;
import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.common.CustomRecipe;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Locale;

public final class EItem {
    @SuppressWarnings("FieldCanBeLocal")
    private static final int DEFAULT_AMOUNT = 1;
    @SuppressWarnings("FieldCanBeLocal")
    private static final int MIN_AMOUNT = 1;
    @SuppressWarnings("FieldCanBeLocal")
    private static final int MAX_AMOUNT = 64;

    private final String keyLC;
    private boolean skipPermissions = true;
    private Material material = null;
    private String name = null;
    private List<String> description = null;
    private CustomRecipe recipe = null;
    private int amount = 0;
    private EScenario scenario = null;

    @SuppressWarnings("BooleanParameter")
    private EItem(String key, boolean skipPermissions) {
        this.keyLC = key.toLowerCase(Locale.getDefault());
        this.skipPermissions = !skipPermissions;
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        String descriptionString = (description == null) ? "" : (" (" + StringUtils.join(description, " ") + ')');

        return material + "~'" + name + descriptionString + "'=" + recipe;
    }

    public ItemStack getDroppedItemStack() {
        ItemStack itemStack = getItemStack();
        itemStack.setAmount(1);

        return itemStack;
    }

    public ItemStack getCustomItemStack(int amount) {
        ItemStack itemStack = getItemStack();
        itemStack.setAmount(amount);

        return itemStack;
    }

    public ShapedRecipe getShapedRecipe() {
        return recipe.getShapedRecipe(keyLC, getItemStack());
    }

    public void explode(CustomNukes plugin, Location location) {
        scenario.execute(plugin, location);
    }

    public boolean hasPermission(Player player) {
        return (skipPermissions) || ((player != null) && (player.hasPermission("customnukes.explosive." + keyLC)));
    }

    @SuppressWarnings({"BooleanParameter", "AccessingNonPublicFieldOfAnotherObject"})
    public static EItem getFromConfig(Material defaultMaterial, CustomNukes plugin, FileConfiguration config, CustomLogger customLogger, String key, boolean checkPermissions) {
        String name = getNameFromConfig(config, customLogger, key);
        if(name == null) {
            return null;
        }

        EItem explosive = new EItem(key, checkPermissions);
        explosive.material = ConfigReader.getMaterial(config, customLogger, key + ".service-material", String.format("Material of '%s'", name), defaultMaterial);
        explosive.name = name;
        List<String> description = getDescriptionFromConfig(config, customLogger, key, name);
        if(description != null) {
            explosive.description = description;
        }

        explosive.amount = getAmountFromConfig(config, customLogger, key, name);

        CustomRecipe recipe = CustomRecipe.getFromConfig(plugin, config, customLogger, key, name);
        if(recipe == null) {
            return null;
        }

        explosive.recipe = recipe;

        EScenario scenario = EScenario.getFromConfig(config, customLogger, key, name, true);
        if(scenario == null) {
            return null;
        }

        explosive.scenario = scenario;

        return explosive;
    }

    private static String getNameFromConfig(FileConfiguration config, CustomLogger customLogger, String key) {
        return ConfigReader.getString(config, customLogger, key + ".name", "name of explosive-key");
    }

    private static List<String> getDescriptionFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        return ConfigReader.getStringList(config, customLogger, key + ".description", "description of explosive", name);
    }

    private static int getAmountFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        return ConfigReader.getInt(config, customLogger, key + ".amount", "Amount of explosive", name, MIN_AMOUNT, MAX_AMOUNT, DEFAULT_AMOUNT);
    }

    private ItemStack getItemStack() {
        ItemStack result = new ItemStack(material);
        ItemMeta meta = result.getItemMeta();

        if(description != null) {
            meta.setLore(description);
        }

        meta.setDisplayName(name);
        result.setItemMeta(meta);
        result.setAmount(amount);

        return result;
    }

}
