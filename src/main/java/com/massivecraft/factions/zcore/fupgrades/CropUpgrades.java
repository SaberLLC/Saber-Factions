package com.massivecraft.factions.zcore.fupgrades;

import com.massivecraft.factions.*;
import org.bukkit.CropState;
import org.bukkit.Material;
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
        if (factionAtLoc != Factions.getInstance().getWilderness()) {
            int level = factionAtLoc.getUpgrade("Crop");
            if (level != 0) {
                if (level == 1) {
                    int chance = P.p.getConfig().getInt("fupgrades.MainMenu.Crops.Crop-Boost.level-1");
                    int randomNum = ThreadLocalRandom.current().nextInt(1, 100 + 1);
                    if (randomNum <= chance) {
                        growCrop(e);
                    }
                }
                if (level == 2) {
                    int chance = P.p.getConfig().getInt("fupgrades.MainMenu.Crops.Crop-Boost.level-2");
                    int randomNum = ThreadLocalRandom.current().nextInt(1, 100 + 1);
                    if (randomNum <= chance) {
                        growCrop(e);
                    }
                }
                if (level == 3) {
                    int chance = P.p.getConfig().getInt("fupgrades.MainMenu.Crops.Crop-Boost.level-3");
                    int randomNum = ThreadLocalRandom.current().nextInt(1, 100 + 1);
                    if (randomNum <= chance) {
                        growCrop(e);
                    }
                }
            }
        }
    }


    private void growCrop(BlockGrowEvent e) {

        Material CROPS;


        if (e.getBlock().getType().equals(P.p.CROPS)) {
            e.setCancelled(true);
            Crops c = new Crops(CropState.RIPE);
            org.bukkit.block.BlockState bs = e.getBlock().getState();
            bs.setData(c);
            bs.update();
        }
        org.bukkit.block.Block below = e.getBlock().getLocation().subtract(0, 1, 0).getBlock();




        if (below.getType() == P.p.SUGAR_CANE_BLOCK) {

            org.bukkit.block.Block above = e.getBlock().getLocation().add(0, 1, 0).getBlock();
            if (above.getType() == Material.AIR && above.getLocation().add(0, -2, 0).getBlock().getType() != Material.AIR) {
                above.setType(P.p.SUGAR_CANE_BLOCK);
            }

        }
        if (below.getType() == Material.CACTUS) {


            org.bukkit.block.Block above = e.getBlock().getLocation().add(0, 1, 0).getBlock();

            if (above.getType() == Material.AIR && above.getLocation().add(0, -2, 0).getBlock().getType() != Material.AIR) {
                above.setType(Material.CACTUS);
            }
        }
    }
}
