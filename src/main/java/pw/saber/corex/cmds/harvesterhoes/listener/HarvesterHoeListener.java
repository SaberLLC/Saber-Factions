package pw.saber.corex.cmds.harvesterhoes.listener;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import pw.saber.corex.utils.NBTParsedItem;

public class HarvesterHoeListener implements Listener {

    @EventHandler
    public void onCaneBreak(BlockBreakEvent e) {
        if (!e.isCancelled() && e.getBlock().getType() == XMaterial.SUGAR_CANE.parseMaterial() && NBTParsedItem.isHarvesterHoe(e.getPlayer().getItemInHand())) {
            e.setCancelled(true);
            e.setExpToDrop(0);
            handleYCoordBreakage(e, e.getPlayer().getItemInHand());
        }
    }

    public void handleYCoordBreakage(BlockBreakEvent e, ItemStack itemStack) {
        if (NBTParsedItem.isHarvesterHoe(itemStack)) {
            int found = 1;
            int y;
            for (y = 1; y < 255 - e.getBlock().getY(); y++) {
                if(NBTParsedItem.getSectionForHavesterHoesKeys("levels").contains(String.valueOf(NBTParsedItem.getData(itemStack, "Level" + 1)))) {
                    int levelRequirement = NBTParsedItem.getSectionForHavesterHoes("levels").getInt(NBTParsedItem.getData(itemStack, "Level" + 1) + ".requirement");
                    if(NBTParsedItem.getData(itemStack, "Mined" + y) == levelRequirement) {
                        //send level up message
                        //run command dispatch for leveling up
                        //add newly leveled item into inventory
                        //update inventory
                    }

                    if(NBTParsedItem.getSectionForHavesterHoesKeys("levels").contains(String.valueOf(NBTParsedItem.getData(itemStack, "Level")))) {
                        //run rewards for ranking up

                    }
                }
            }
        }
    }
}
