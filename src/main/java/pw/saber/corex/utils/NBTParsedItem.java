package pw.saber.corex.utils;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.util.CC;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pw.saber.corex.CoreX;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NBTParsedItem {

    public static ItemStack createChunkBusterItem(int amount) {
        ItemStack itemStack = new ItemStack(XMaterial.matchXMaterial(CoreX.getConfig().fetchString("Chunkbuster.Item.Type")).get().parseMaterial());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(CC.translate(CoreX.getConfig().fetchString("Chunkbuster.Item.Display-Name")));
        List<String> configLore = CoreX.getConfig().fetchStringList("Chunkbuster.Item.Lore");
        List<String> lore = new ArrayList<>();
        for (String s : configLore) {
            lore.add(CC.translate(s));
        }

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setBoolean("Chunkbuster", true);
        nbtItem.getItem().setAmount(amount);
        return nbtItem.getItem();
    }


    public static ItemStack createHarvesterHoeItem(int amount) {
        ItemStack itemStack = new ItemStack(XMaterial.matchXMaterial(CoreX.getConfig().fetchString("HarvesterHoe.Item.Type")).get().parseMaterial());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(CC.translate(CoreX.getConfig().fetchString("HarvesterHoe.Item.Display-Name")));
        List<String> configLore = CoreX.getConfig().fetchStringList("HarvesterHoe.Item.Lore");
        List<String> lore = new ArrayList<>();
        for (String s : configLore) {
            lore.add(CC.translate(s));
        }

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setBoolean("Harvester", true);
        nbtItem.setBoolean("AutoSell", false);
        nbtItem.setInteger("Level", 1);
        nbtItem.setInteger("Mined", 0);
        nbtItem.getItem().setAmount(amount);
        return nbtItem.getItem();
    }

    public static int getData(ItemStack itemStack, String nbtKey) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getInteger(nbtKey);
    }

    public static void setAmountOfCaneMined(int amountOfCaneMined, ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setInteger("Mined", amountOfCaneMined);
    }


    public static void setLevelOfHarvesterHoe(int levelToSet, ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setInteger("Level", levelToSet);
    }

    public static void setAutoSell(boolean setSell, ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setBoolean("AutoSell", setSell);
    }

    public static boolean isHarvesterHoe(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.hasKey("Harvester");
    }

    public static Set<String> getSectionForHavesterHoesKeys(String section) {
        return CoreX.getConfig().getConfig().getConfigurationSection(section).getKeys(false);
    }

    public static ConfigurationSection getSectionForHavesterHoes(String section) {
        return CoreX.getConfig().getConfig().getConfigurationSection(section);
    }

}
