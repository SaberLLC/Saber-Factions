package pw.saber.corex.utils;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.util.CC;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pw.saber.corex.CoreX;

import java.util.ArrayList;
import java.util.List;

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

}
