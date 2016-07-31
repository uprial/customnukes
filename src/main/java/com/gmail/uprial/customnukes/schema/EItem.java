package com.gmail.uprial.customnukes.schema;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.uprial.customnukes.ConfigReader;
import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.common.CustomRecipe;

public class EItem {
    private static int defaultAmount = 1;
    private static int minAmount = 1;
    private static int maxAmount = 64;

    private String key;
    private boolean checkPermissions;
    private Material material;
    private String name;
    private List<String> description;
    private CustomRecipe recipe;
    private int amount;
    private EScenario scenario;

    public EItem(String key, boolean checkPermissions) {
        this.key = key;
        this.checkPermissions = checkPermissions;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setRecipe(CustomRecipe recipe) {
        this.recipe = recipe;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setScenario(EScenario scenario) {
        this.scenario = scenario;
    }

    public String toString() {
        String description_string;
        if(null == description)
            description_string = "";
        else
            description_string = " (" + StringUtils.join(description, " ") + ")";

        return material.toString() + "~'" + name + description_string + "'=" + recipe.toString();
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
        return recipe.getShapedRecipe(getItemStack());
    }

    public void explode(CustomNukes plugin, Location location) {
        scenario.execute(plugin, location);
    }

    public boolean hasPermission(Player player) {
        return (!checkPermissions) || (null != player) && (player.hasPermission("customnukes.explosive." + key.toLowerCase()));
    }

    public static EItem getFromConfig(Material defaultMaterial, FileConfiguration config, CustomLogger customLogger, String key, boolean checkPermissions) {
        String name = getNameFromConfig(config, customLogger, key);
        if(null == name)
            return null;

        EItem explosive = new EItem(key, checkPermissions);
        explosive.setMaterial(ConfigReader.getMaterial(config, customLogger, key + ".service-material", String.format("Material of '%s'", name), defaultMaterial));
        explosive.setName(name);
        List<String> description = getDescriptionFromConfig(config, customLogger, key, name);
        if(null != description)
            explosive.setDescription(description);

        explosive.setAmount(getAmountFromConfig(config, customLogger, key, name));

        CustomRecipe recipe = CustomRecipe.getFromConfig(config, customLogger, key, name);
        if(null == recipe)
            return null;

        explosive.setRecipe(recipe);

        EScenario scenario = EScenario.getFromConfig(config, customLogger, key, name, true);
        if(null == scenario)
            return null;

        explosive.setScenario(scenario);

        return explosive;
    }

    private static String getNameFromConfig(FileConfiguration config, CustomLogger customLogger, String key) {
        return ConfigReader.getString(config, customLogger, key + ".name", "name of explosive-key");
    }

    private static List<String> getDescriptionFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        return ConfigReader.getStringList(config, customLogger, key + ".description", "description of explosive", name);
    }

    private static int getAmountFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        return ConfigReader.getInt(config, customLogger, key + ".amount", "Amount of explosive", name, minAmount, maxAmount, defaultAmount);
    }

    private ItemStack getItemStack() {
        ItemStack result = new ItemStack(material);
        ItemMeta meta = result.getItemMeta();

        if(null != description)
            meta.setLore(description);

        meta.setDisplayName(name);
        result.setItemMeta(meta);
        result.setAmount(amount);

        return result;
    }
}
