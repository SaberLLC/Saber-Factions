package com.massivecraft.factions.zcore.fupgrades;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.SavageFactions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.List;

public class RedstoneUpgrade implements Listener {

    @EventHandler
    public void onWaterRedstone(BlockFromToEvent e) {
        List<String> unbreakable = SavageFactions.plugin.getConfig().getStringList("no-water-destroy.Item-List");
        String block = e.getToBlock().getType().toString();
        FLocation floc = new FLocation(e.getToBlock().getLocation());
        Faction factionAtLoc = Board.getInstance().getFactionAt(floc);

        if (!factionAtLoc.isWilderness()) {
            int level = factionAtLoc.getUpgrade(UpgradeType.REDSTONE);
            if (level != 0) {
                switch (level) {
                    case 1:
                        SavageFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Redstone.Cost");
                        break;
                }
                if (unbreakable.contains(block)) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
