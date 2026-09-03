package com.massivecraft.factions.cmd.audit;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.factions.util.SaberGUI;
import com.massivecraft.factions.util.serializable.InventoryItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class FAuditMenu extends SaberGUI {

    private final Player player;
    private final Faction faction;
    private boolean showTimestamps;

    public FAuditMenu(Player player, Faction faction) {
        this(player, faction, settings().isTimestampShownByDefault());
    }

    public FAuditMenu(Player player, Faction faction, boolean showTimestamps) {
        super(player, settings().getOverviewTitle(faction), settings().getOverviewSize());
        this.player = player;
        this.faction = faction;
        this.showTimestamps = showTimestamps;
    }

    @Override
    public void redraw() {
        FLogManager logManager = logManager();
        FLogManager.AuditSettings settings = logManager.getSettings();

        if (settings.isFillerEnabled()) {
            InventoryItem filler = new InventoryItem(new ItemBuilder(settings.getFillerMaterial())
                    .name(settings.getFillerName())
                    .build());
            for (int slot = 0; slot < this.size; slot++) {
                this.setItem(slot, filler);
            }
        } else {
            for (int slot = 0; slot < this.size; slot++) {
                this.removeItem(slot);
            }
        }

        for (FLogType type : FLogType.values()) {
            if (!settings.isTypeEnabled(type)) {
                continue;
            }

            int slot = settings.getTypeSlot(type);
            if (slot < 0 || slot >= this.size) {
                continue;
            }

            List<FactionLogs.FactionLog> logs = logManager.getLogSnapshot(this.faction, type);
            this.setItem(slot, new InventoryItem(new ItemBuilder(settings.getTypeMaterial(type))
                    .name(settings.getTypeDisplayName(type))
                    .lore(buildOverviewLore(settings, type, logs))
                    .build()).click(ClickType.RIGHT, () -> {
                this.showTimestamps = !this.showTimestamps;
                redraw();
            }).click(ClickType.LEFT, () -> {
                if (logs.isEmpty()) {
                    this.player.sendMessage(CC.Red + "No logs recorded for this category.");
                    return;
                }

                new FAuditLogMenu(this.player, this.faction, type, 0, this.showTimestamps)
                        .setParentGUI(this)
                        .openGUI(FactionsPlugin.getInstance());
            }));
        }
    }

    private List<String> buildOverviewLore(FLogManager.AuditSettings settings, FLogType type, List<FactionLogs.FactionLog> logs) {
        int previewLines = settings.getOverviewPreviewLines();
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(settings.getOverviewCountLine(logs.size()));

        if (logs.isEmpty()) {
            lore.add(settings.getOverviewNoLogsLine());
        } else {
            int shown = 0;
            for (int index = logs.size() - 1; index >= 0 && shown < previewLines; index--) {
                lore.add(settings.getOverviewPreviewPrefix() + settings.renderLogLine(type, logs.get(index), this.showTimestamps));
                shown++;
            }

            int remaining = logs.size() - shown;
            if (remaining > 0) {
                lore.add(settings.getOverviewMoreLine(remaining));
            }
        }

        lore.add("");
        lore.add(settings.getOverviewOpenHint());
        lore.add(settings.getOverviewToggleHint());
        return lore;
    }

    private static FLogManager logManager() {
        return FactionsPlugin.getInstance().getFlogManager();
    }

    private static FLogManager.AuditSettings settings() {
        return logManager().getSettings();
    }

    static class FAuditLogMenu extends SaberGUI {
        private final Player player;
        private final Faction faction;
        private final FLogType type;
        private final int page;
        private final boolean showTimestamps;

        FAuditLogMenu(Player player, Faction faction, FLogType type, int page, boolean showTimestamps) {
            super(player, settings().getDetailTitle(faction, type), settings().getDetailSize());
            this.player = player;
            this.faction = faction;
            this.type = type;
            this.page = page;
            this.showTimestamps = showTimestamps;
        }

        @Override
        public void redraw() {
            FLogManager logManager = logManager();
            FLogManager.AuditSettings settings = logManager.getSettings();

            if (settings.isFillerEnabled()) {
                InventoryItem filler = new InventoryItem(new ItemBuilder(settings.getFillerMaterial())
                        .name(settings.getFillerName())
                        .build());
                for (int slot = 0; slot < this.size; slot++) {
                    this.setItem(slot, filler);
                }
            } else {
                for (int slot = 0; slot < this.size; slot++) {
                    this.removeItem(slot);
                }
            }

            List<FactionLogs.FactionLog> logs = logManager.getLogSnapshot(this.faction, this.type);
            int perPage = settings.getDetailEntriesPerPage();
            int totalPages = Math.max(1, (int) Math.ceil(logs.size() / (double) perPage));
            int currentPage = Math.max(0, Math.min(this.page, totalPages - 1));

            if (logs.isEmpty()) {
                setItem(Math.min(22, this.size - 1), new InventoryItem(new ItemBuilder(settings.getDetailEmptyMaterial())
                        .name(settings.getDetailEmptyName())
                        .lore(settings.getDetailEmptyLore())
                        .build()));
            } else {
                int startIndex = logs.size() - 1 - (currentPage * perPage);

                for (int slot = 0; slot < perPage && startIndex - slot >= 0; slot++) {
                    FactionLogs.FactionLog log = logs.get(startIndex - slot);
                    int logNumber = startIndex - slot + 1;

                    setItem(slot, new InventoryItem(new ItemBuilder(settings.getDetailEntryMaterial())
                            .name(settings.getDetailEntryName(this.type, logNumber, this.showTimestamps))
                            .lore(settings.getDetailEntryLore(this.type, log, this.showTimestamps))
                            .build()));
                }
            }

            setItem(settings.getBackSlot(), new InventoryItem(new ItemBuilder(settings.getBackMaterial())
                    .name(settings.getBackName())
                    .lore(settings.getBackLore())
                    .build()).click(() -> {
                SaberGUI parent = this.getParentGUI();
                if (parent != null) {
                    parent.openGUI(FactionsPlugin.getInstance());
                    return;
                }
                new FAuditMenu(this.player, this.faction, this.showTimestamps).openGUI(FactionsPlugin.getInstance());
            }));

            setItem(settings.getTimestampToggleSlot(), new InventoryItem(new ItemBuilder(settings.getTimestampToggleMaterial())
                    .name(settings.getTimestampToggleName(this.showTimestamps))
                    .lore(settings.getTimestampToggleLore(this.showTimestamps))
                    .build()).click(() -> new FAuditLogMenu(this.player, this.faction, this.type, currentPage, !this.showTimestamps)
                    .setParentGUI(this.getParentGUI())
                    .openGUI(FactionsPlugin.getInstance())));

            if (currentPage > 0) {
                setItem(settings.getPreviousSlot(), new InventoryItem(new ItemBuilder(settings.getPreviousMaterial())
                        .name(settings.getPreviousName())
                        .lore(settings.getPreviousLore())
                        .build()).click(() -> new FAuditLogMenu(this.player, this.faction, this.type, currentPage - 1, this.showTimestamps)
                        .setParentGUI(this.getParentGUI())
                        .openGUI(FactionsPlugin.getInstance())));
            }

            if (currentPage + 1 < totalPages) {
                setItem(settings.getNextSlot(), new InventoryItem(new ItemBuilder(settings.getNextMaterial())
                        .name(settings.getNextName())
                        .lore(settings.getNextLore())
                        .build()).click(() -> new FAuditLogMenu(this.player, this.faction, this.type, currentPage + 1, this.showTimestamps)
                        .setParentGUI(this.getParentGUI())
                        .openGUI(FactionsPlugin.getInstance())));
            }
        }
    }
}
