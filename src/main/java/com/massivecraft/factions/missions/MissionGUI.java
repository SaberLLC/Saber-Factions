package com.massivecraft.factions.missions;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.frame.FactionGUI;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MissionGUI implements FactionGUI {

    /**
     * @author Driftay
     */

    private final FactionsPlugin plugin;
    private final FPlayer fPlayer;
    private final Inventory inventory;
    private final Map<Integer, String> slots;

    BukkitTask updateItemsTask = null;
    BukkitTask cancelTask = null;


    public MissionGUI(FactionsPlugin plugin, FPlayer fPlayer) {
        this.slots = new HashMap<>();
        this.plugin = plugin;
        this.fPlayer = fPlayer;
        this.inventory = plugin.getServer().createInventory(this, plugin.getFileManager().getMissions().getConfig().getInt("MissionGUISize") * 9, CC.translate(plugin.getFileManager().getMissions().getConfig().getString("Missions-GUI-Title")));
    }

    @Override
    public void onClose(HumanEntity player) {
        //onClose is called every time a related inventory instance is closed.
        //This means that every time we use openInventory to show the inventory once again
        //the inventory technically closes and opens up once again, triggering this event each time.
        if (cancelTask != null)
            cancelTask.cancel();
        //Because of what's mentioned before, we check on the next tick if the inventory that the player
        //is currently viewing is the same as this GUI, if it isn't, the updateItemsTask gets cancelled
        cancelTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(player.getOpenInventory().getTopInventory() != inventory)
                if (updateItemsTask != null)
                    updateItemsTask.cancel();
        }, 1);
    }


    @Override
    public void onClick(int slot, ClickType action) {
        String missionName = slots.get(slot);
        if (missionName == null) return;
        ConfigurationSection configurationSection = plugin.getFileManager().getMissions().getConfig().getConfigurationSection("Missions");
        if (configurationSection == null) return;

        if (plugin.getFileManager().getMissions().getConfig().getBoolean("Allow-Cancellation-Of-Missions") && action == ClickType.RIGHT && !fPlayer.getFaction().getMissions().isEmpty()) {

            int cost = FactionsPlugin.getInstance().getFileManager().getMissions().getConfig().getInt("CancelMissionCost");

            if (cost > 0) {
                Faction faction = fPlayer.getFaction();
                if (FactionsPlugin.getInstance().getFileManager().getMissions().getConfig().getBoolean("PayCancelMissionCostWithPoints")) {

                    if (faction.getPoints() >= cost) {
                        faction.setPoints(faction.getPoints() - cost);
                        fPlayer.msg(TL.MISSION_CANCEL_POINTS_TAKEN, cost, faction.getPoints());
                    } else {
                        fPlayer.msg(TL.COMMAND_UPGRADES_NOT_ENOUGH_POINTS);
                        return;
                    }
                } else {
                    EconomyParticipator payee = null;

                    if (Conf.bankEnabled && FactionsPlugin.getInstance().getFileManager().getMissions().getConfig().getBoolean("FactionPaysCancelMissionCost", false)) {
                        payee = faction;
                    } else {
                        payee = fPlayer;
                    }

                    if (!Econ.modifyMoney(payee, -cost, TL.MISSION_TOCANCEL.toString(), TL.MISSION_FORCANCEL.toString())) {
                        return;
                    }
                }
            }

            fPlayer.getFaction().getMissions().remove(missionName);
            fPlayer.msg(TL.MISSION_MISSION_CANCELLED);
            build(false);
            fPlayer.getPlayer().openInventory(inventory);
            return;
        }

        if (plugin.getFileManager().getMissions().getConfig().getBoolean("Randomization.Enabled")) {

            if (missionName.equals(CC.translate(plugin.getFileManager().getMissions().getConfig().getString("Randomization.Start-Item.Allowed.Name")))) {
                Set<String> keys = plugin.getFileManager().getMissions().getConfig().getConfigurationSection("Missions").getKeys(false);

                // Remove un-selectable keys
                keys.remove("FillItem");
                fPlayer.getFaction().getMissions().forEach((mName, miss) -> keys.remove(mName));
                if (plugin.getFileManager().getMissions().getConfig().getBoolean("DenyMissionsMoreThenOnce"))
                    fPlayer.getFaction().getCompletedMissions().forEach(keys::remove);

                Random r = new Random();
                int pick = r.nextInt(keys.size());
                // We override and let the rest of the code handle the rest.
                missionName = keys.toArray()[pick].toString();
            } else if (missionName.equals(CC.translate(plugin.getFileManager().getMissions().getConfig().getString("Randomization.Start-Item.Disallowed.Name")))) {
                return;
            } else {
                fPlayer.msg(TL.MISSION_RANDOM_MODE_DENIED, CC.translate(plugin.getFileManager().getMissions().getConfig().getString("Randomization.Start-Item.Allowed.Name")));
                return;
            }
        }


        int max = plugin.getFileManager().getMissions().getConfig().getInt("MaximumMissionsAllowedAtOnce");
        if (fPlayer.getFaction().getMissions().size() >= max) {
            fPlayer.msg(TL.MISSION_MISSION_MAX_ALLOWED, max);
            return;
        }

        if (fPlayer.getFaction().getMissions().containsKey(missionName)) {
            fPlayer.msg(TL.MISSION_MISSION_ACTIVE);
            return;
        }
        ConfigurationSection section = configurationSection.getConfigurationSection(missionName);
        if (section == null) return;

        if (plugin.getFileManager().getMissions().getConfig().getBoolean("DenyMissionsMoreThenOnce")) {
            if (fPlayer.getFaction().getCompletedMissions().contains(missionName)) {
                fPlayer.msg(TL.MISSION_ALREAD_COMPLETED);
                return;
            }
        }

        ConfigurationSection missionSection = section.getConfigurationSection("Mission");
        if (missionSection == null) return;

        Mission mission = new Mission(missionName, MissionType.fromName(missionSection.getString("Type")), System.currentTimeMillis());

        fPlayer.getFaction().getMissions().put(missionName, mission);
        fPlayer.msg(TL.MISSION_MISSION_STARTED, fPlayer.describeTo(fPlayer.getFaction()), CC.translate(section.getString("Name")));

        long deadlineMillis = plugin.getFileManager().getMissions().getConfig().getLong("MissionDeadline", 0L);

        if(deadlineMillis > 0L) {
            MissionHandler.setDeadlineTask(mission, fPlayer.getFaction(), deadlineMillis);
        }


        build(false);
        fPlayer.getPlayer().openInventory(inventory);
    }

    @Override
    public void build(boolean first) {
        ConfigurationSection configurationSection = plugin.getFileManager().getMissions().getConfig().getConfigurationSection("Missions");
        if (configurationSection == null) {
            return;
        }

        if (!Objects.equals(configurationSection.getString("FillItem.Material"), "AIR")) {
            ItemStack fillItem = XMaterial.matchXMaterial(configurationSection.getString("FillItem.Material")).get().parseItem();
            ItemMeta fillmeta = fillItem.getItemMeta();
            fillmeta.setDisplayName(CC.translate(configurationSection.getString("FillItem.Name")));
            fillmeta.setLore(CC.translate(configurationSection.getStringList("FillItem.Lore")));
            fillItem.setItemMeta(fillmeta);
            for (int fill = 0; fill < configurationSection.getInt("FillItem.Rows") * 9; ++fill) {
                //Why were we generating a new itemstack per slot?????
                inventory.setItem(fill, fillItem);
            }
        }

        for (String missionName : configurationSection.getKeys(false)) {
            if (!missionName.equals("FillItem")) {
                ConfigurationSection section = configurationSection.getConfigurationSection(missionName);

                if(!section.getBoolean("enabled")) {
                    continue;
                }

                int slot = section.getInt("Slot");

                String material = section.getString("Material", "DIRT");

                List<String> loreLines = new ArrayList<>();
                for (String line : section.getStringList("Lore")) {
                    loreLines.add(CC.translate(line));
                }

                if (plugin.getFileManager().getMissions().getConfig().getBoolean("DenyMissionsMoreThenOnce")) {
                    if (fPlayer.getFaction().getCompletedMissions().contains(missionName)) {
                        material = plugin.getFileManager().getMissions().getConfig().getString("DeniedMissionMaterial", material);
                        loreLines.add(CC.translate(plugin.getFileManager().getMissions().getConfig().getString("DeniedMissionExtraLore", "")));
                    }
                }

                ItemStack itemStack = XMaterial.matchXMaterial(material).get().parseItem();
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("Name")));

                if (fPlayer.getFaction().getMissions().containsKey(missionName)) {
                    Mission mission = fPlayer.getFaction().getMissions().get(missionName);
                    itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
                    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    loreLines.add("");
                    loreLines.add(CC.translate(plugin.getFileManager().getMissions().getConfig().getString("Mission-Progress-Format")
                            .replace("{progress}", String.valueOf(mission.getProgress()))
                            .replace("{total}", String.valueOf(section.getConfigurationSection("Mission").get("Amount")))));

                    long deadlineMillis = plugin.getFileManager().getMissions().getConfig().getLong("MissionDeadline", 0L);
                    if (deadlineMillis > 0) {

                        long timeTillDeadline = mission.getStartTime() + deadlineMillis - System.currentTimeMillis();

                        loreLines.add("");
                        loreLines.add(TextUtil.parse(plugin.getFileManager().getMissions().getConfig().getString("DeadlineMissionLore", ""),
                                String.format("%02dh %02dm %02ds",
                                        TimeUnit.MILLISECONDS.toHours(timeTillDeadline),
                                        TimeUnit.MILLISECONDS.toMinutes(timeTillDeadline) -
                                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeTillDeadline)),
                                        TimeUnit.MILLISECONDS.toSeconds(timeTillDeadline) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeTillDeadline)))));


                        if(updateItemsTask == null)
                            updateItemsTask = Bukkit.getScheduler().runTaskTimer(plugin, this::updateGUI, 20L, 20L);
                    }

                    if (plugin.getFileManager().getMissions().getConfig().getBoolean("Allow-Cancellation-Of-Missions")) {
                        loreLines.add("");
                        loreLines.add(CC.translate(plugin.getFileManager().getMissions().getConfig().getString("CancellableMissionLore", "")));
                    }
                }
                itemMeta.setLore(loreLines);
                itemStack.setItemMeta(itemMeta);
                inventory.setItem(slot, itemStack);
                slots.put(slot, missionName);
            }
        }

        if (plugin.getFileManager().getMissions().getConfig().getBoolean("Randomization.Enabled")) {
            String material = plugin.getFileManager().getMissions().getConfig().getString("Randomization.Start-Item.Allowed.Material");
            String displayName = plugin.getFileManager().getMissions().getConfig().getString("Randomization.Start-Item.Allowed.Name");
            List<String> loree = new ArrayList<>();
            for (String string : plugin.getFileManager().getMissions().getConfig().getStringList("Randomization.Start-Item.Allowed.Lore")) {
                loree.add(CC.translate(string));
            }
            // There are no more available missions
            if (plugin.getFileManager().getMissions().getConfig().getBoolean("DenyMissionsMoreThenOnce") &&
                    // Check if the completed missions contain all the available missions,
                    // doing it this way since there might be completed missions that are no longer available
                    fPlayer.getFaction().getCompletedMissions().containsAll(configurationSection.getKeys(false)
                            .stream().filter(key -> !key.equals("FillItem")).collect(Collectors.toSet()))) {
                material = plugin.getFileManager().getMissions().getConfig().getString("Randomization.Start-Item.Disallowed.Material");
                displayName = plugin.getFileManager().getMissions().getConfig().getString("Randomization.Start-Item.Disallowed.Name");

                loree.clear();
                for (String string : plugin.getFileManager().getMissions().getConfig().getStringList("Randomization.Start-Item.Disallowed.Lore")) {
                    loree.add(CC.translate(string).replace("%reason%", TL.MISSION_MISSION_ALL_COMPLETED.toString()));
                }
            }
            if (fPlayer.getFaction().getMissions().size() >= plugin.getFileManager().getMissions().getConfig().getInt("MaximumMissionsAllowedAtOnce")) {
                material = plugin.getFileManager().getMissions().getConfig().getString("Randomization.Start-Item.Disallowed.Material");
                displayName = plugin.getFileManager().getMissions().getConfig().getString("Randomization.Start-Item.Disallowed.Name");

                loree.clear();
                for (String string : plugin.getFileManager().getMissions().getConfig().getStringList("Randomization.Start-Item.Disallowed.Lore")) {
                    loree.add(CC.translate(string).replace("%reason%", TextUtil.parse(TL.MISSION_MISSION_MAX_ALLOWED.toString(), plugin.getFileManager().getMissions().getConfig().getInt("MaximumMissionsAllowedAtOnce"))));
                }
            }

            ItemStack itemStack = XMaterial.matchXMaterial(material).get().parseItem();
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(CC.translate(displayName));
            itemMeta.setLore(loree);
            itemStack.setItemMeta(itemMeta);

            // Place the item in the GUI
            int slot = plugin.getFileManager().getMissions().getConfig().getInt("Randomization.Start-Item.Slot");
            inventory.setItem(slot, itemStack);
            slots.put(slot, itemMeta.getDisplayName());
        }
    }

    private void updateGUI() {
        if (fPlayer.getFaction().getMissions().isEmpty()) {
            updateItemsTask.cancel();
            return;
        }

        build(true);
        fPlayer.getPlayer().openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }
}
