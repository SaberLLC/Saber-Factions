package com.massivecraft.factions.missions;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.FactionGUI;
import com.massivecraft.factions.util.XMaterial;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MissionGUI implements FactionGUI {
    private FactionsPlugin plugin;
    private FPlayer fPlayer;
    private Inventory inventory;
    private Map<Integer, String> slots;

    public MissionGUI(FactionsPlugin plugin, FPlayer fPlayer) {
        this.slots = new HashMap<>();
        this.plugin = plugin;
        this.fPlayer = fPlayer;
        this.inventory = plugin.getServer().createInventory(this, plugin.getConfig().getInt("MissionGUISize") * 9, plugin.color(plugin.getConfig().getString("Missions-GUI-Title")));
    }

    @Override
    public void onClick(int slot, ClickType action) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("Missions");
        if (configurationSection == null) {
            return;
        }
        int max = plugin.getConfig().getInt("MaximumMissionsAllowedAtOnce");
        if (fPlayer.getFaction().getMissions().size() >= max) {
            fPlayer.msg(TL.MISSION_MISSION_MAX_ALLOWED, max);
            return;
        }
        String missionName = slots.get(slot);
        if (missionName == null) {
            return;
        }
        if (fPlayer.getFaction().getMissions().containsKey(missionName)) {
            fPlayer.msg(TL.MISSION_MISSION_ACTIVE);
            return;
        }
        ConfigurationSection section = configurationSection.getConfigurationSection(missionName);
        if (section == null) {
            return;
        }
        if(FactionsPlugin.getInstance().getConfig().getBoolean("DenyMissionsMoreThenOnce")) {
            if (fPlayer.getFaction().getCompletedMissions().contains(missionName)) {
                fPlayer.msg(TL.MISSION_ALREAD_COMPLETED);
                return;
            }
        }

        ConfigurationSection missionSection = section.getConfigurationSection("Mission");
        if (missionSection == null) {
            return;
        }
        Mission mission = new Mission(missionName, missionSection.getString("Type"));
        fPlayer.getFaction().getMissions().put(missionName, mission);
        fPlayer.msg(TL.MISSION_MISSION_STARTED, fPlayer.describeTo(fPlayer.getFaction()), plugin.color(section.getString("Name")));
        build();
        fPlayer.getPlayer().openInventory(inventory);
    }

    @Override
    public void build() {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("Missions");
        if (configurationSection == null) {
            return;
        }
        for (int fill = 0; fill < configurationSection.getInt("FillItem.Rows") * 9; ++fill) {
            ItemStack fillItem = new ItemStack(XMaterial.matchXMaterial(configurationSection.getString("FillItem.Material")).parseItem());
            ItemMeta meta = fillItem.getItemMeta();
            meta.setDisplayName("");
            fillItem.setItemMeta(meta);
            inventory.setItem(fill, fillItem);
        }
        for (String key : configurationSection.getKeys(false)) {
            if (!key.equals("FillItem")) {
                ConfigurationSection section = configurationSection.getConfigurationSection(key);
                int slot = section.getInt("Slot");

                ItemStack itemStack = XMaterial.matchXMaterial(section.getString("Material")).parseItem();
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("Name")));
                List<String> loreLines = new ArrayList<>();
                for (String line : section.getStringList("Lore")) {
                    loreLines.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                if (fPlayer.getFaction().getMissions().containsKey(key)) {
                    Mission mission = fPlayer.getFaction().getMissions().get(key);
                    itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
                    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    loreLines.add("");
                    loreLines.add(plugin.color(plugin.getConfig().getString("Mission-Progress-Format")
                            .replace("{progress}", String.valueOf(mission.getProgress()))
                            .replace("{total}", String.valueOf(section.getConfigurationSection("Mission").get("Amount")))));
                }
                itemMeta.setLore(loreLines);
                itemStack.setItemMeta(itemMeta);
                inventory.setItem(slot, itemStack);
                slots.put(slot, key);
            }
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}
