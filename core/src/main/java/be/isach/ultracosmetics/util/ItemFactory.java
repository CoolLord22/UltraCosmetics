package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.CustomConfiguration;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.SmartLogger.LogLevel;
import be.isach.ultracosmetics.version.ServerVersion;
import com.cryptomorin.xseries.XAttribute;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.Vector;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by sacha on 03/08/15.
 */
public class ItemFactory {
    // for some reason I don't understand, there's no Tag or XTag for dyes
    private static final List<XMaterial> DYES = new ArrayList<>(16);
    private static final List<XMaterial> STAINED_GLASS = new ArrayList<>(16);
    private static final FixedMetadataValue UNPICKABLE_META = new FixedMetadataValue(UltraCosmeticsData.get().getPlugin(), true);

    static {
        for (XMaterial mat : XMaterial.VALUES) {
            if (mat.name().endsWith("_DYE")) {
                DYES.add(mat);
            } else if (mat.name().endsWith("_STAINED_GLASS")) {
                STAINED_GLASS.add(mat);
            }
        }
    }

    private ItemFactory() {
    }

    private static boolean noticePrinted = false;

    public static ItemStack create(XMaterial material, String displayName, String... lore) {
        return rename(material.parseItem(), displayName, lore);
    }

    public static ItemStack create(XMaterial material, Component displayName, String... lore) {
        return create(material, MessageManager.toLegacy(displayName), lore);
    }

    public static ItemStack rename(ItemStack itemStack, Component displayName, String... lore) {
        return rename(itemStack, MessageManager.toLegacy(displayName), lore);
    }

    public static ItemStack rename(ItemStack itemstack, String displayName, String... lore) {
        ItemMeta meta = itemstack.getItemMeta();
        meta.setDisplayName(displayName);
        if (lore != null && lore.length > 0) {
            List<String> finalLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            for (String s : lore) {
                if (s == null) continue;
                for (String line : s.split("\n")) {
                    finalLore.add(line);
                }
            }
            meta.setLore(finalLore);
        }
        itemstack.setItemMeta(meta);
        return ItemFactory.hideAttributes(itemstack);
    }

    public static Item setUnpickable(Item item) {
        item.setMetadata("UNPICKABLEUP", UNPICKABLE_META);
        item.setPersistent(false);
        return item;
    }

    public static Item spawnUnpickableItem(ItemStack stack, Location loc, Vector velocity) {
        Item item = loc.getWorld().dropItem(loc, stack);
        item.setVelocity(velocity);
        setUnpickable(item);
        return item;
    }

    public static Item createUnpickableItemDirectional(XMaterial material, Player player, double scale) {
        return spawnUnpickableItem(material.parseItem(), player.getEyeLocation(), player.getLocation().getDirection().multiply(scale));
    }

    public static Item createUnpickableItemVariance(XMaterial material, Location loc, Random random, double variance) {
        return spawnUnpickableItem(material.parseItem(), loc, new Vector(random.nextDouble() - 0.5, random.nextDouble() / 2.0, random.nextDouble() - 0.5).multiply(variance));
    }

    public static void applyCosmeticMarker(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        // Do not cache this in a field, it doesn't exist on versions below 1.12
        NamespacedKey marker = new NamespacedKey(UltraCosmeticsData.get().getPlugin(), "marker");
        meta.getPersistentDataContainer().set(marker, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
    }

    public static ItemStack getItemStackFromConfig(String path) {
        XMaterial mat = getFromConfigInternal(path);
        if (mat != null) return mat.parseItem();
        return create(XMaterial.BEDROCK, "&cError parsing material", "&cFailed to parse material");
    }

    public static XMaterial getXMaterialFromConfig(String path) {
        XMaterial mat = getFromConfigInternal(path);
        return mat == null ? XMaterial.BEDROCK : mat;
    }

    public static XMaterial getNullableXMaterialFromConfig(String path) {
        return getFromConfigInternal(path);
    }

    public static List<XMaterial> getXMaterialListFromConfig(String path) {
        List<XMaterial> mats = new ArrayList<>();
        CustomConfiguration cc = UltraCosmeticsData.get().getPlugin().getConfig();
        for (String matString : cc.getStringList(path)) {
            XMaterial.matchXMaterial(matString).ifPresent(mats::add);
        }
        return mats;
    }

    private static XMaterial getFromConfigInternal(String path) {
        String fromConfig = UltraCosmeticsData.get().getPlugin().getConfig().getString(path);
        if (fromConfig == null) return null;
        if (MathUtils.isInteger(fromConfig) || fromConfig.contains(":")) {
            if (!noticePrinted) {
                UltraCosmeticsData.get().getPlugin().getSmartLogger().write(LogLevel.ERROR, "UltraCosmetics no longer supports numeric IDs, please replace it with a material name.");
                noticePrinted = true;
            }
            UltraCosmeticsData.get().getPlugin().getSmartLogger().write(LogLevel.ERROR, "Offending config path: " + path);
            return null;
        }
        // null if not found
        return XMaterial.matchXMaterial(fromConfig).orElse(null);
    }

    private static ItemStack createMenuItem() {
        ConfigurationSection section = SettingsManager.getConfig().getConfigurationSection("Menu-Item");
        MiniMessage mm = MessageManager.getMiniMessage();
        String name = MessageManager.toLegacy(mm.deserialize(section.getString("Displayname")));
        int model = section.getInt("Custom-Model-Data");
        ItemStack stack = ItemFactory.rename(ItemFactory.getItemStackFromConfig("Menu-Item.Type"), name);
        ItemMeta meta = stack.getItemMeta();
        String rawLore = section.getString("Lore", "");
        if (!rawLore.equals("")) {
            List<String> lore = new ArrayList<>();
            for (String line : rawLore.split("\n")) {
                lore.add(MessageManager.toLegacy(mm.deserialize(line)));
            }
            meta.setLore(lore);
        }
        if (model != 0) {
            meta.setCustomModelData(model);
        }

        stack.setItemMeta(meta);
        applyCosmeticMarker(stack);
        return ItemFactory.hideAttributes(stack);
    }

    public static ItemStack getMenuItem() {
        if (!SettingsManager.getConfig().getBoolean("Menu-Item.Enabled")) {
            return null;
        }
        return createMenuItem();
    }


    public static ItemStack createSkull(String url, String name) {
        ItemStack head = create(XMaterial.PLAYER_HEAD, name);
        if (UltraCosmeticsData.get().getServerVersion().isAtLeast(ServerVersion.v1_18)) {
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.nameUUIDFromBytes(url.getBytes()));
            PlayerTextures textures = profile.getTextures();
            try {
                textures.setSkin(new URL("https://textures.minecraft.net/texture/" + url));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return head;
            }
            profile.setTextures(textures);
            meta.setOwnerProfile(profile);
            head.setItemMeta(meta);
        } else {
            XSkull.of(head).profile(Profileable.of(ProfileInputType.TEXTURE_HASH, url)).apply();
        }
        return head;
    }

    public static ItemStack createColouredLeather(Material armourPart, int red, int green, int blue) {
        ItemStack itemStack = new ItemStack(armourPart);
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        leatherArmorMeta.setColor(Color.fromRGB(red, green, blue));
        itemStack.setItemMeta(leatherArmorMeta);
        return itemStack;
    }

    public static void addGlow(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addEnchant(Enchantment.MENDING, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
    }

    public static void setFlags(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.values());
        try {
            meta.removeItemFlags(ItemFlag.valueOf("HIDE_LORE"));
        } catch (IllegalArgumentException e) {
            // ignored
        }
        if (!meta.hasAttributeModifiers()) {
            // Add a dummy attribute modifier. If the only attribute modifiers present are the default ones, it won't
            // actually hide them when we ask using ItemFlags.
            AttributeModifier modifier = createAttributeModifier("itemflags", 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
            meta.addAttributeModifier(XAttribute.KNOCKBACK_RESISTANCE.get(), modifier);
        }
        item.setItemMeta(meta);
    }

    @SuppressWarnings({"UnstableApiUsage", "removal"})
    public static AttributeModifier createAttributeModifier(String modName, double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        NamespacedKey key = new NamespacedKey(UltraCosmeticsData.get().getPlugin(), modName);
        try {
            return new AttributeModifier(key, amount, operation, slot == null ? EquipmentSlotGroup.ANY : slot.getGroup());
        } catch (NoSuchMethodError error) {
            return new AttributeModifier(UUID.randomUUID(), key.toString(), amount, operation, slot);
        }
    }

    public static boolean haveSameName(ItemStack a, ItemStack b) {
        if (a.hasItemMeta() && b.hasItemMeta()) {
            if (a.getItemMeta().hasDisplayName() && b.getItemMeta().hasDisplayName()) {
                return a.getItemMeta().getDisplayName().equals(b.getItemMeta().getDisplayName());
            }
        }
        return false;
    }

    private static XMaterial randomXMaterial(List<XMaterial> mats) {
        return mats.get(ThreadLocalRandom.current().nextInt(mats.size()));
    }

    private static ItemStack randomStack(List<XMaterial> mats) {
        return randomXMaterial(mats).parseItem();
    }

    public static ItemStack getRandomDye() {
        return randomStack(DYES);
    }

    public static ItemStack getRandomStainedGlass() {
        return randomStack(STAINED_GLASS);
    }

    public static ItemStack randomItemFromTag(XTag<XMaterial> tag) {
        return randomFromTag(tag).parseItem();
    }

    public static XMaterial randomFromTag(XTag<XMaterial> tag) {
        // copy tag values into temporary ArrayList because getting random values from a Set is hard
        return randomXMaterial(new ArrayList<>(tag.getValues()));
    }

    public static boolean isSimilar(ItemStack a, ItemStack b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.getType() != b.getType()) return false;
        if (a.getItemMeta() instanceof BlockStateMeta aMeta && b.getItemMeta() instanceof BlockStateMeta bMeta) {
            // Block state meta spontaneously creates "internal" data that causes it to not be equal.
            // So, we set them to have the same state part and then compare them.
            aMeta.setBlockState(bMeta.getBlockState());
            return aMeta.equals(bMeta);
        }
        return a.isSimilar(b);
    }

    public static ItemStack hideAttributes(ItemStack itemstack) {
        ItemMeta itemMeta = itemstack.getItemMeta();
        if(itemMeta != null) {
            for (Attribute attribute : Attribute.values()) {
                itemMeta.removeAttributeModifier(attribute);
            }
            itemMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(NamespacedKey.minecraft("generic.knockback_resistance"), 0.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }
        itemstack.setItemMeta(itemMeta);
        return itemstack;
    }
}
