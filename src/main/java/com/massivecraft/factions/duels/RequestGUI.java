package com.massivecraft.factions.duels;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.StaticGUI;
import com.massivecraft.factions.util.Sync.SyncExecutor;
import com.massivecraft.factions.util.Sync.SyncTask;
import com.massivecraft.factions.util.XMaterial;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * @author droppinganvil
 */
public class RequestGUI implements StaticGUI {
    private static ItemStack fillItem;
    private static RequestResponse fallback;
    FileConfiguration config = FactionsPlugin.getInstance().getConfig();
    public static HashMap<Integer, RequestResponse> responseMap = new HashMap<>();
    public static Inventory inv;
    private static RequestGUI instance;
    public static RequestGUI getInstance() {
        return instance;
    }

    public RequestGUI() {
        if (instance == null) {
            instance = this;
        }
        if (fillItem == null) {
            fillItem = XMaterial.matchXMaterial(config.getString("Duels.GUI.Fill-Item.Material")).get().parseItem();
            ItemMeta fillMeta = fillItem.getItemMeta();
            fillMeta.setDisplayName(" ");
            fillItem.setItemMeta(fillMeta);
        }
        if (fallback == null) {
            try {
                fallback = RequestResponse.valueOf(config.getString("Duels.GUI.Fill-Item.Response"));
            } catch (EnumConstantNotPresentException e) {
                FactionsPlugin.getInstance().getLogger().log(Level.WARNING, "FallbackResponse is invalid! Using Ignored");
                fallback = RequestResponse.Ignored;
            }
        }
        if (inv == null) {
            inv = Bukkit.createInventory(this, 27, "Join Faction Duel");
            int fi = 0;
            while (fi != 26) {
                inv.setItem(fi, fillItem);
                fi++;
            }
            for (String key : config.getConfigurationSection("Duels.GUI.Items").getKeys(false)) {
                ItemStack item = XMaterial.matchXMaterial(config.getString("Duels.GUI.Items." + key + ".Material")).get().parseItem();
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("Duels.GUI.Items." + key + ".Name")));
                List<String> lore = new ArrayList<>();
                for (String loreEntry : config.getStringList("Duels.GUI.Items." + key + ".Lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', loreEntry));
                }
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
                int slot = Integer.parseInt(key);
                responseMap.put(slot, RequestResponse.valueOf(config.getString("Duels.GUI.Items." + key + ".Response")));
                inv.setItem(slot, item);
            }
        }
    }

    @Override
    public void click(int slot, ClickType action, Player player) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (action != ClickType.LEFT) {
            processResponse(fallback, fPlayer);
        } else {
            processResponse(responseMap.get(slot), fPlayer);
        }
    }

    public static void processResponse(RequestResponse response, FPlayer fPlayer) {
        switch (response) {
            case Accepted:
                fPlayer.getFaction().broadcast(TL.DUEL_REQUEST_ACCEPTED_PLAYER.format(fPlayer.getNameAndTitle()));
                Duels.acceptedDuel.add(fPlayer);
                close(false, fPlayer);
                break;
            case Rejected:
                fPlayer.getFaction().broadcast(TL.DUEL_REQUEST_REJECTED_PLAYER.format(fPlayer.getNameAndTitle()));
                close(false, fPlayer);
                break;
        }
    }

    public static void close(Boolean reject, FPlayer fPlayer) {
        if (reject) {
            processResponse(RequestResponse.Rejected, fPlayer);
        }
        fPlayer.getPlayer().closeInventory();
        Duels.guiMap.remove(fPlayer);
    }

    public static void closeSync(Boolean reject, FPlayer fPlayer) {
        SyncExecutor.taskQueue.add(new SyncTask(RequestGUI.getInstance(), "close", reject, fPlayer));
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
