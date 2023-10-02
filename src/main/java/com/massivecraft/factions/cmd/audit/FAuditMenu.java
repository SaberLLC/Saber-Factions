package com.massivecraft.factions.cmd.audit;

/*
  @author Saser
 */

import com.google.common.collect.Lists;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.factions.util.serializable.ClickableItemStack;
import com.massivecraft.factions.util.serializable.GUIMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FAuditMenu extends GUIMenu {

    private final Player player;
    private final Faction faction;
    private boolean showTimestamps = true;

    public FAuditMenu(Player player, Faction faction) {
        super("Faction Logs", 18);
        this.player = player;
        this.faction = faction;
    }

    @Override
    public void drawItems() {
        FactionLogs logs = FactionsPlugin.instance.getFlogManager().getFactionLogMap().get(faction.getId());
        if (logs == null) logs = new FactionLogs();

        for (FLogType type : FLogType.values()) {
            if (type.getSlot() == -1) continue;

            if (type != FLogType.F_TNT || FactionsPlugin.getInstance().getConfig().getBoolean("f-points.Enabled")) {
                LinkedList<FactionLogs.FactionLog> recentLogs = logs.getMostRecentLogs().get(type);
                if (recentLogs == null) recentLogs = new LinkedList<>();

                List<String> lore = buildLoreForItem(type, recentLogs);
                setItem(type.getSlot(), buildClickableItem(type, lore));
            }
        }
    }

    private List<String> buildLoreForItem(FLogType type, LinkedList<FactionLogs.FactionLog> recentLogs) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(CC.GreenB + "Recent Logs " + CC.Green + "(" + CC.GreenB + recentLogs.size() + CC.Green + ")");

        int logsPerPage = 20;
        Iterator<FactionLogs.FactionLog> backwards = recentLogs.descendingIterator();
        while (backwards.hasNext() && lore.size() < logsPerPage) {
            String logLine = backwards.next().getLogLine(type, showTimestamps);
            lore.add(" " + CC.Yellow + logLine);
        }

        int logsLeft = recentLogs.size() - logsPerPage;
        if (logsLeft > 0) {
            lore.add(CC.YellowB + logsLeft + CC.Yellow + " more logs...");
            lore.add("");
            lore.add(CC.Yellow + "Left-Click " + CC.Gray + "to view more logs");
        }
        lore.add(CC.Yellow + "Right-Click " + CC.Gray + "to toggle timestamps");

        return lore;
    }

    private ClickableItemStack buildClickableItem(FLogType type, List<String> lore) {
        ItemStack item = new ItemBuilder(type.getMaterial()).name(type.getDisplayName()).lore(lore).build();

        return new ClickableItemStack(item).setClickCallback(click -> {
            click.setCancelled(true);
            if (click.getClick() == ClickType.RIGHT) {
                showTimestamps = !showTimestamps;
                drawItems();
            } else {
                int logsLeft = lore.size() - 20;  // 20 is the logsPerPage as used earlier
                if (logsLeft <= 0) {
                    player.sendMessage(CC.Red + "No extra logs to load.");
                    return;
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(FactionsPlugin.instance,
                        () -> new FAuditLogMenu(player, faction, type).open(player));
            }
        });
    }

    static class FAuditLogMenu extends GUIMenu {
        private final Player player;
        private final Faction faction;
        private final FLogType logType;
        private boolean timeStamp = false;

        public FAuditLogMenu(Player player, Faction faction, FLogType type) {
            super("Faction Logs", 9);
            this.player = player;
            this.faction = faction;
            this.logType = type;
        }

        public void drawItems() {
            FactionLogs logs = FactionsPlugin.instance.getFlogManager().getFactionLogMap().get(faction.getId());
            int perPage = logType == FLogType.F_TNT ? 25 : 20;
            if (logs != null) {
                LinkedList<FactionLogs.FactionLog> log = logs.getMostRecentLogs().get(logType);
                if (log != null) {
                    int slot = logType == FLogType.F_TNT ? 0 : 3;
                    int pagesToShow = (int) Math.max(1.0D, Math.ceil((double) log.size() / (double) perPage));

                    for (int page = 1; page <= pagesToShow; ++page) {
                        int startIndex = log.size() - (page * perPage - perPage);
                        if (startIndex >= log.size()) {
                            startIndex = log.size() - 1;
                        }

                        List<String> lore = Lists.newArrayList("", CC.GreenB + "Logs");

                        for (int i = startIndex; i > startIndex - perPage; --i) {
                            if (i < log.size()) {
                                if (i < 0) {
                                    break;
                                }
                                FactionLogs.FactionLog l = log.get(i);
                                lore.add(" " + CC.Yellow + l.getLogLine(logType, timeStamp));
                            }
                        }
                        lore.add("");
                        lore.add(CC.Gray + "Click to toggle timestamp");
                        setItem(slot++, (new ClickableItemStack((new ItemBuilder(Material.PAPER)).name(CC.GreenB + "Log #" + page).lore(lore).build())).setClickCallback((e) -> {
                            e.setCancelled(true);
                            timeStamp = !timeStamp;
                            drawItems();
                        }));
                    }
                }
            }
            setItem(getSize() - 1, (new ClickableItemStack((new ItemBuilder(Material.ARROW)).name(CC.Green + "Previous Page").lore("", CC.Gray + "Click to view previous page!").build())).setClickCallback((event) -> {
                event.setCancelled(true);
                Bukkit.getScheduler().scheduleSyncDelayedTask(FactionsPlugin.instance, () -> (new FAuditMenu(player, faction)).open(player));
            }));
        }
    }
}
