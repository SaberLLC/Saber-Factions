package pw.saber.corex.listeners;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class AntiCobbleMonster implements Listener {

    private final BlockFace[] faces = {BlockFace.SELF, BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onFromTo(BlockFromToEvent event) {
        int id = event.getBlock().getType().getId();
        Block b = event.getToBlock();
        if (generatesCobble(id, b)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("deprecation")
    public boolean generatesCobble(int id, Block b) {
        int mirrorID1 = (id == 8) || (id == 9) ? 10 : 8;
        int mirrorID2 = (id == 8) || (id == 9) ? 11 : 9;
        for (BlockFace face : this.faces) {
            Block r = b.getRelative(face, 1);
            if ((r.getType().getId() == mirrorID1) || (r.getType().getId() == mirrorID2)) {
                return true;
            }
        }
        return false;
    }
}