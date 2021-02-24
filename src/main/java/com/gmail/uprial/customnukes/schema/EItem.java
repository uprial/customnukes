package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.config.ConfigReaderMaterial;
import com.gmail.uprial.customnukes.config.ConfigReaderNumbers;
import com.gmail.uprial.customnukes.config.ConfigReaderSimple;
import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.config.ConfigReaderRecipe;
import com.gmail.uprial.customnukes.config.InvalidConfigException;
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
    private ConfigReaderRecipe recipe = null;
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
    public static EItem getFromConfig(Material defaultMaterial, CustomNukes plugin, FileConfiguration config, CustomLogger customLogger, String key, boolean checkPermissions) throws InvalidConfigException {
        EItem explosive = new EItem(key, checkPermissions);

        explosive.name = ConfigReaderSimple.getString(config,key + ".name",
                String.format("name of explosive-key '%s'", key));
        explosive.material = ConfigReaderMaterial.getMaterial(config, customLogger,
                key + ".service-material", String.format("material of '%s'", explosive.name), defaultMaterial);
        explosive.description = ConfigReaderSimple.getStringList(config, customLogger,
                key + ".description", String.format("description of explosive '%s'", explosive.name));;
        explosive.amount = ConfigReaderNumbers.getInt(config, customLogger,
                key + ".amount", String.format("amount of explosive '%s'", explosive.name), MIN_AMOUNT, MAX_AMOUNT, DEFAULT_AMOUNT);
        explosive.recipe = ConfigReaderRecipe.getFromConfig(plugin, config, key,
                String.format("recipe of explosive '%s'", explosive.name));
        explosive.scenario = EScenario.getFromConfig(config, customLogger, key,
                String.format("scenario of explosive '%s'", explosive.name), true);

        return explosive;
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
