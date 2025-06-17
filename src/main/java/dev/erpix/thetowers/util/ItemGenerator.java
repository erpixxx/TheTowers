package dev.erpix.thetowers.util;

import dev.erpix.thetowers.Key;
import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.model.game.GameTeam;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public final class ItemGenerator {

    private ItemGenerator() { }

    /**
     * A map of item names to their corresponding item stack suppliers.
     */
    public static final Map<String, Supplier<ItemStack>> ITEMS = new HashMap<>() {{
        put("tower_teleport",       ItemGenerator::getTowerTeleport);
        put("crossbow",             ItemGenerator::getCrossbow);
        put("bow",                  ItemGenerator::getBow);
        put("stone_axe",            ItemGenerator::getStoneAxe);
        put("stone_sword",          ItemGenerator::getStoneSword);
        put("stone_pickaxe",        ItemGenerator::getStonePickaxe);
        put("iron_axe",             ItemGenerator::getIronAxe);
        put("iron_sword",           ItemGenerator::getIronSword);
        put("iron_pickaxe",         ItemGenerator::getIronPickaxe);
        put("iron_helmet",          ItemGenerator::getIronHelmet);
        put("iron_chestplate",      ItemGenerator::getIronChestplate);
        put("iron_leggings",        ItemGenerator::getIronLeggings);
        put("iron_boots",           ItemGenerator::getIronBoots);
        put("diamond_axe",          ItemGenerator::getDiamondAxe);
        put("diamond_sword",        ItemGenerator::getDiamondSword);
        put("diamond_pickaxe",      ItemGenerator::getDiamondPickaxe);
        put("diamond_helmet",       ItemGenerator::getDiamondHelmet);
        put("diamond_chestplate",   ItemGenerator::getDiamondChestplate);
        put("diamond_leggings",     ItemGenerator::getDiamondLeggings);
        put("diamond_boots",        ItemGenerator::getDiamondBoots);
        put("netherite_axe",        ItemGenerator::getNetheriteAxe);
        put("netherite_sword",      ItemGenerator::getNetheriteSword);
        put("netherite_pickaxe",    ItemGenerator::getNetheritePickaxe);
        put("netherite_helmet",     ItemGenerator::getNetheriteHelmet);
        put("netherite_chestplate", ItemGenerator::getNetheriteChestplate);
        put("netherite_leggings",   ItemGenerator::getNetheriteLeggings);
        put("netherite_boots",      ItemGenerator::getNetheriteBoots);
        put("blue_helmet",          () -> getLeatherHelmet(Color.fromRGB(Integer.parseInt(GameTeam.Color.BLUE.getColorHex(), 16))));
        put("red_helmet",           () -> getLeatherHelmet(Color.fromRGB(Integer.parseInt(GameTeam.Color.RED.getColorHex(), 16))));
        put("green_helmet",         () -> getLeatherHelmet(Color.fromRGB(Integer.parseInt(GameTeam.Color.GREEN.getColorHex(), 16))));
        put("yellow_helmet",        () -> getLeatherHelmet(Color.fromRGB(Integer.parseInt(GameTeam.Color.YELLOW.getColorHex(), 16))));
        put("orange_helmet",        () -> getLeatherHelmet(Color.fromRGB(Integer.parseInt(GameTeam.Color.ORANGE.getColorHex(), 16))));
        put("purple_helmet",        () -> getLeatherHelmet(Color.fromRGB(Integer.parseInt(GameTeam.Color.PURPLE.getColorHex(), 16))));
        put("blue_chestplate",      () -> getLeatherChestplate(Color.fromRGB(Integer.parseInt(GameTeam.Color.BLUE.getColorHex(), 16))));
        put("red_chestplate",       () -> getLeatherChestplate(Color.fromRGB(Integer.parseInt(GameTeam.Color.RED.getColorHex(), 16))));
        put("green_chestplate",     () -> getLeatherChestplate(Color.fromRGB(Integer.parseInt(GameTeam.Color.GREEN.getColorHex(), 16))));
        put("yellow_chestplate",    () -> getLeatherChestplate(Color.fromRGB(Integer.parseInt(GameTeam.Color.YELLOW.getColorHex(), 16))));
        put("orange_chestplate",    () -> getLeatherChestplate(Color.fromRGB(Integer.parseInt(GameTeam.Color.ORANGE.getColorHex(), 16))));
        put("purple_chestplate",    () -> getLeatherChestplate(Color.fromRGB(Integer.parseInt(GameTeam.Color.PURPLE.getColorHex(), 16))));
        put("blue_leggings",        () -> getLeatherLeggings(Color.fromRGB(Integer.parseInt(GameTeam.Color.BLUE.getColorHex(), 16))));
        put("red_leggings",         () -> getLeatherLeggings(Color.fromRGB(Integer.parseInt(GameTeam.Color.RED.getColorHex(), 16))));
        put("green_leggings",       () -> getLeatherLeggings(Color.fromRGB(Integer.parseInt(GameTeam.Color.GREEN.getColorHex(), 16))));
        put("yellow_leggings",      () -> getLeatherLeggings(Color.fromRGB(Integer.parseInt(GameTeam.Color.YELLOW.getColorHex(), 16))));
        put("orange_leggings",      () -> getLeatherLeggings(Color.fromRGB(Integer.parseInt(GameTeam.Color.ORANGE.getColorHex(), 16))));
        put("purple_leggings",      () -> getLeatherLeggings(Color.fromRGB(Integer.parseInt(GameTeam.Color.PURPLE.getColorHex(), 16))));
        put("blue_boots",           () -> getLeatherBoots(Color.fromRGB(Integer.parseInt(GameTeam.Color.BLUE.getColorHex(), 16))));
        put("red_boots",            () -> getLeatherBoots(Color.fromRGB(Integer.parseInt(GameTeam.Color.RED.getColorHex(), 16))));
        put("green_boots",          () -> getLeatherBoots(Color.fromRGB(Integer.parseInt(GameTeam.Color.GREEN.getColorHex(), 16))));
        put("yellow_boots",         () -> getLeatherBoots(Color.fromRGB(Integer.parseInt(GameTeam.Color.YELLOW.getColorHex(), 16))));
        put("orange_boots",         () -> getLeatherBoots(Color.fromRGB(Integer.parseInt(GameTeam.Color.ORANGE.getColorHex(), 16))));
        put("purple_boots",         () -> getLeatherBoots(Color.fromRGB(Integer.parseInt(GameTeam.Color.PURPLE.getColorHex(), 16))));
    }};

    /**
     * The base attack speed for hand and items without a specific attack speed.
     */
    private static final float BASE_ATTACK_SPEED = 4.0f;

    /**
     * Creates a dummy modifier that allows hiding attributes from the item.
     *
     * @return An {@link AttributeModifier} that hides attributes.
     */
    private static @NotNull AttributeModifier hideAttributesModifier() {
        return new AttributeModifier(NamespacedKey.minecraft("hide"), 0.0, AttributeModifier.Operation.ADD_NUMBER);
    }

    /**
     * An enum representing different types of items with their associated colors.
     */
    private enum ItemType {
        STONE("<gray>"),
        IRON("<white>"),
        DIAMOND("<#5bc4fc>"),
        NETHERITE("<#7f2de3>");

        private final String color;

        ItemType(@NotNull String color) {
            this.color = color;
        }

        public @NotNull String getColor() {
            return color;
        }
    }

    private static @NotNull ItemStack generateItem(@NotNull ItemType type, @NotNull Material material, @NotNull String name) {
        ItemStack item = new ItemStack(material, 1);
        item.editMeta(meta -> {
            meta.displayName(Components.standard("<i:false>" + type.getColor() + name));
            meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, hideAttributesModifier());
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        });
        return item;
    }

    private static void applyMiningSpeed(@NotNull ItemStack item, float speed) {
        item.editMeta(meta -> {
            ToolComponent tool = meta.getTool();
            tool.setDefaultMiningSpeed(speed);
            meta.setTool(tool);
        });
    }

    private static void setItemDamage(@NotNull ItemStack item, double value) {
        item.editPersistentDataContainer(data ->
                data.set(TheTowers.key("damage"), PersistentDataType.DOUBLE, value));
    }

    private static void setItemDefense(@NotNull ItemStack item, double value) {
        item.editPersistentDataContainer(data ->
                data.set(TheTowers.key("defense"), PersistentDataType.DOUBLE, value));
    }

    private static void setCooldown(@NotNull ItemStack item, int ticks) {
        double cooldown = (double) 20 / ticks;
        item.editMeta(meta -> {
            meta.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(
                    TheTowers.key("attack_speed"),
                    -(BASE_ATTACK_SPEED - cooldown),
                    AttributeModifier.Operation.ADD_NUMBER
            ));
        });
    }

    private static @NotNull ItemStack generatePrimaryArmor(@NotNull Material material, @NotNull String name, @NotNull Color color) {
        ItemStack item = new ItemStack(material);
        item.editMeta( meta -> {
            LeatherArmorMeta armor = (LeatherArmorMeta) meta;
            armor.setColor(color);
            String hex = Integer.toHexString(color.asRGB());
            armor.displayName(Components.standard("<i:false><#"+ hex +">" + name));
        });
        return item;
    }

    public static @NotNull ItemStack getCrossbow() {
        ItemStack item = new ItemStack(Material.CROSSBOW);
        item.editMeta(meta -> {
            meta.displayName(Components.standard("<i:false><#fcec5b>Kusza"));
            meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, hideAttributesModifier());
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        });
        item.editPersistentDataContainer(data ->
                data.set(Key.PROJECTILE_DAMAGE.key(), PersistentDataType.DOUBLE, 3.0));
        return item;
    }

    public static @NotNull ItemStack getBow() {
        ItemStack item = new ItemStack(Material.BOW);
        item.editMeta(meta -> {
            meta.displayName(Components.standard("<i:false><#fcec5b>Łuk"));
            meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, hideAttributesModifier());
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        });
        item.editPersistentDataContainer(data ->
                data.set(Key.PROJECTILE_DAMAGE.key(), PersistentDataType.DOUBLE, 3.0));
        return item;
    }

    public static @NotNull ItemStack getLeatherHelmet(@NotNull Color color) {
        ItemStack item = generatePrimaryArmor(Material.LEATHER_HELMET, "Podstawowy Hełm", color);
        setItemDefense(item, 1.0);
        return item;
    }

    public static @NotNull ItemStack getLeatherChestplate(@NotNull Color color) {
        ItemStack item = generatePrimaryArmor(Material.LEATHER_CHESTPLATE, "Podstawowa Zbroja", color);
        setItemDefense(item, 2.0);
        return item;
    }

    public static @NotNull ItemStack getLeatherLeggings(@NotNull Color color) {
        ItemStack item = generatePrimaryArmor(Material.LEATHER_LEGGINGS, "Podstawowe spodnie", color);
        setItemDefense(item, 1.5);
        return item;
    }

    public static @NotNull ItemStack getLeatherBoots(@NotNull Color color) {
        ItemStack item = generatePrimaryArmor(Material.LEATHER_BOOTS, "Podstawowe buty", color);
        setItemDefense(item, 1.0);
        return item;
    }

    public static @NotNull ItemStack getIronHelmet() {
        ItemStack item = generateItem(ItemType.IRON, Material.IRON_HELMET, "Żelazny Hełm");
        setItemDefense(item, 2.5);
        return item;
    }

    public static @NotNull ItemStack getIronChestplate() {
        ItemStack item = generateItem(ItemType.IRON, Material.IRON_CHESTPLATE, "Żelazny Napierśnik");
        setItemDefense(item, 3.5);
        return item;
    }

    public static @NotNull ItemStack getIronLeggings() {
        ItemStack item = generateItem(ItemType.IRON, Material.IRON_LEGGINGS, "Żelazne Spodnie");
        setItemDefense(item, 3.0);
        return item;
    }

    public static @NotNull ItemStack getIronBoots() {
        ItemStack item = generateItem(ItemType.IRON, Material.IRON_BOOTS, "Żelazne Buty");
        setItemDefense(item, 2.5);
        return item;
    }

    public static @NotNull ItemStack getDiamondHelmet() {
        ItemStack item = generateItem(ItemType.DIAMOND, Material.DIAMOND_HELMET, "Diamentowy Hełm");
        setItemDefense(item, 4.0);
        return item;
    }

    public static @NotNull ItemStack getDiamondChestplate() {
        ItemStack item = generateItem(ItemType.DIAMOND, Material.DIAMOND_CHESTPLATE, "Diamentowy Napierśnik");
        setItemDefense(item, 5.0);
        return item;
    }

    public static @NotNull ItemStack getDiamondLeggings() {
        ItemStack item = generateItem(ItemType.DIAMOND, Material.DIAMOND_LEGGINGS, "Diamentowe Spodnie");
        setItemDefense(item, 4.5);
        return item;
    }

    public static @NotNull ItemStack getDiamondBoots() {
        ItemStack item = generateItem(ItemType.DIAMOND, Material.DIAMOND_BOOTS, "Diamentowe Buty");
        setItemDefense(item, 4.0);
        return item;
    }

    public static @NotNull ItemStack getNetheriteHelmet() {
        ItemStack item = generateItem(ItemType.NETHERITE, Material.NETHERITE_HELMET, "Netheritowy Hełm");
        setItemDefense(item, 5.5);
        return item;
    }

    public static @NotNull ItemStack getNetheriteChestplate() {
        ItemStack item = generateItem(ItemType.NETHERITE, Material.NETHERITE_CHESTPLATE, "Netheritowy Napierśnik");
        setItemDefense(item, 6.5);
        return item;
    }

    public static @NotNull ItemStack getNetheriteLeggings() {
        ItemStack item = generateItem(ItemType.NETHERITE, Material.NETHERITE_LEGGINGS, "Netheritowe Spodnie");
        setItemDefense(item, 6.0);
        return item;
    }

    public static @NotNull ItemStack getNetheriteBoots() {
        ItemStack item = generateItem(ItemType.NETHERITE, Material.NETHERITE_BOOTS, "Netheritowe Buty");
        setItemDefense(item, 5.5);
        return item;
    }

    public static @NotNull ItemStack getStoneAxe() {
        ItemStack item = generateItem(ItemType.STONE, Material.STONE_AXE, "Kamienna Siekiera");
        applyMiningSpeed(item, 5.0f);
        setItemDamage(item, 6.5);
        setCooldown(item, 25);
        return item;
    }

    public static @NotNull ItemStack getIronAxe() {
        ItemStack item = generateItem(ItemType.IRON, Material.IRON_AXE, "Żelazna Siekiera");
        applyMiningSpeed(item, 10.0f);
        setItemDamage(item, 8.0);
        setCooldown(item, 22);
        return item;
    }

    public static @NotNull ItemStack getDiamondAxe() {
        ItemStack item = generateItem(ItemType.DIAMOND, Material.DIAMOND_AXE, "Diamentowa Siekiera");
        applyMiningSpeed(item, 20.0f);
        setItemDamage(item, 9.5);
        setCooldown(item, 20);
        return item;
    }

    public static @NotNull ItemStack getNetheriteAxe() {
        ItemStack item = generateItem(ItemType.NETHERITE, Material.NETHERITE_AXE, "Netheritowa Siekiera");
        applyMiningSpeed(item, 40.0f);
        setItemDamage(item, 11.0);
        setCooldown(item, 20);
        return item;
    }

    public static @NotNull ItemStack getStoneSword() {
        ItemStack item = generateItem(ItemType.STONE, Material.STONE_SWORD, "Kamienny Miecz");
        setItemDamage(item, 5.0);
        setCooldown(item, 12);
        return item;
    }

    public static @NotNull ItemStack getIronSword() {
        ItemStack item = generateItem(ItemType.IRON, Material.IRON_SWORD, "Żelazny Miecz");
        setItemDamage(item, 6.5);
        setCooldown(item, 12);
        return item;
    }

    public static @NotNull ItemStack getDiamondSword() {
        ItemStack item = generateItem(ItemType.DIAMOND, Material.DIAMOND_SWORD, "Diamentowy Miecz");
        setItemDamage(item, 8.0);
        setCooldown(item, 12);
        return item;
    }

    public static @NotNull ItemStack getNetheriteSword() {
        ItemStack item = generateItem(ItemType.NETHERITE, Material.NETHERITE_SWORD, "Netheritowy Miecz");
        setItemDamage(item, 9.5);
        setCooldown(item, 12);
        return item;
    }

    public static @NotNull ItemStack getStonePickaxe() {
        ItemStack item = generateItem(ItemType.STONE, Material.STONE_PICKAXE, "Kamienny Kilof");
        applyMiningSpeed(item, 5.0f);
        setItemDamage(item, 3.0);
        setCooldown(item, 17);
        return item;
    }

    public static @NotNull ItemStack getIronPickaxe() {
        ItemStack item = generateItem(ItemType.IRON, Material.IRON_PICKAXE, "Żelazny Kilof");
        applyMiningSpeed(item, 10.0f);
        setItemDamage(item, 4.0);
        setCooldown(item, 17);
        return item;
    }

    public static @NotNull ItemStack getDiamondPickaxe() {
        ItemStack item = generateItem(ItemType.DIAMOND, Material.DIAMOND_PICKAXE, "Diamentowy Kilof");
        applyMiningSpeed(item, 20.0f);
        setItemDamage(item, 5.0);
        setCooldown(item, 17);
        return item;
    }

    public static @NotNull ItemStack getNetheritePickaxe() {
        ItemStack item = generateItem(ItemType.NETHERITE, Material.NETHERITE_PICKAXE, "Netheritowy Kilof");
        applyMiningSpeed(item, 40.0f);
        setItemDamage(item, 6.0);
        setCooldown(item, 17);
        return item;
    }

    public static @NotNull ItemStack getTowerTeleport() {
        ItemStack item = new ItemStack(Material.NETHER_STAR, 1);
        item.editMeta(meta -> {
            meta.displayName(Components.standard("<i:false><b><#a563db>Teleport do Wieży"));
            meta.lore(List.of(
                    Components.standard("<i:false><gray>Teleportuje Cię do twojej wieży"),
                    Components.standard("<i:false><gray>w przeciągu 5 sekund."),
                    Component.space(),
                    Components.standard("<i:false><gray>Kliknij <white>[PPM]</white> aby użyć teleportu.")
            ));
        });
        return item;
    }

}
