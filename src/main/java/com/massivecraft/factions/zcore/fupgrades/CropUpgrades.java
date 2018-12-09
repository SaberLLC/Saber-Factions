package com.massivecraft.factions.zcore.fupgrades;

import com.massivecraft.factions.*;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.material.Crops;

import java.util.concurrent.ThreadLocalRandom;

public class CropUpgrades implements Listener {
	
    @EventHandler
    public void onCropGrow(BlockGrowEvent e) {
        FLocation floc = new FLocation(e.getBlock().getLocation());
        Faction factionAtLoc = Board.getInstance().getFactionAt(floc);
        
        if (!factionAtLoc.isWilderness()) {
            int level = factionAtLoc.getUpgrade("Crop");
            if (level != 0) {
            	int chance = -1;
            	
            	switch (level)
            	{
            		case 1: chance = SavageFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Crops.Crop-Boost.level-1"); break;
            		case 2: chance = SavageFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Crops.Crop-Boost.level-2"); break;
            		case 3: chance = SavageFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Crops.Crop-Boost.level-3"); break;
            	}
            	
            	if (chance >= 0)
            	{
	            	int randomNum = ThreadLocalRandom.current().nextInt(1, 100 + 1);
	                if (randomNum <= chance)
	                    growCrop(e);
            	}
            }
        }
    }

    private void growCrop(BlockGrowEvent e) {

        if (e.getBlock().getType().equals(SavageFactions.plugin.CROPS)) {
            e.setCancelled(true);
            Crops c = new Crops(CropState.RIPE);
            BlockState bs = e.getBlock().getState();
            bs.setData(c);
            bs.update();
        }
        
        Block below = e.getBlock().getLocation().subtract(0, 1, 0).getBlock();

        if (below.getType() == SavageFactions.plugin.SUGAR_CANE_BLOCK) {
            Block above = e.getBlock().getLocation().add(0, 1, 0).getBlock();
            
            if (above.getType() == Material.AIR && above.getLocation().add(0, - 2, 0).getBlock().getType() != Material.AIR) {
                above.setType(SavageFactions.plugin.SUGAR_CANE_BLOCK);
            }

        }
        else if (below.getType() == Material.CACTUS) {
            Block above = e.getBlock().getLocation().add(0, 1, 0).getBlock();

            if (above.getType() == Material.AIR && above.getLocation().add(0, - 2, 0).getBlock().getType() != Material.AIR) {
                above.setType(Material.CACTUS);
            }
        }
    }
}
