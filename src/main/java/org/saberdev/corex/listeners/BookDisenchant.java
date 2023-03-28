package org.saberdev.corex.listeners;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class BookDisenchant implements Listener {

    private static ItemStack EMPTY_BOOK = new ItemStack(Material.BOOK, 1);

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if ((event.getAction() == Action.LEFT_CLICK_BLOCK) && (event.hasItem())) {
            Player player = event.getPlayer();
            if ((event.getClickedBlock().getType() == XMaterial.ENCHANTING_TABLE.parseMaterial()) && (player.getGameMode() != GameMode.CREATIVE)) {
                ItemStack stack = event.getItem();
                if ((stack != null) && (stack.getType() == Material.ENCHANTED_BOOK)) {
                    ItemMeta meta = stack.getItemMeta();
                    if ((meta instanceof EnchantmentStorageMeta)) {
                        EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;
                        for (Enchantment enchantment : enchantmentStorageMeta.getStoredEnchants().keySet()) {
                            enchantmentStorageMeta.removeStoredEnchant(enchantment);
                        }
                        event.setCancelled(true);
                        player.setItemInHand(EMPTY_BOOK);
                        player.sendMessage(ChatColor.GREEN + "You have cleared all enchantments from this book.");
                    }
                }
            }
        }
    }
}