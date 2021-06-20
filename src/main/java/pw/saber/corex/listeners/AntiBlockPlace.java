package pw.saber.corex.listeners;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import pw.saber.corex.CoreX;

import java.util.List;

public class AntiBlockPlace implements Listener {

    private List<String> deniedMatList = CoreX.getConfig().fetchStringList("Denied-Blocks");

    @EventHandler
    public void onDeniedPlace(BlockPlaceEvent e) {
        Block block = e.getBlockPlaced();
        XMaterial xMat = XMaterial.matchXMaterial(block.getType());
        Player player = e.getPlayer();
        if(!player.isOp() && deniedMatList.contains(xMat.name())) {
            e.setCancelled(true);
        }
    }
}
