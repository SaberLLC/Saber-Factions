package pw.saber.corex.cmds.chunkbusters.listener;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import pw.saber.corex.CoreX;
import pw.saber.corex.utils.ChunkBusterRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ChunkBusterListener implements Listener {

    public static HashMap<Chunk, Location> beingBusted = new HashMap<>();
    public static HashSet<Chunk> waterChunks = new HashSet<>();
    public static List<String> deniedBlockList = CoreX.getConfig().fetchStringList("Chunkbuster.denyBustedItems");


    @EventHandler
    public void onChunkBusterPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Block blockPlaced = e.getBlockPlaced();
        Block block = e.getBlock();


        NBTItem nbtItem = new NBTItem(e.getItemInHand());

        if (!nbtItem.hasKey("Chunkbuster")) return;

        if(FactionsBlockListener.playerCanBuildDestroyBlock(player, blockPlaced.getLocation(), "destroy", false)) {
            if (beingBusted.containsKey(block.getLocation().getChunk())) {
                e.setCancelled(true);
                player.sendMessage(TextUtil.parseColor(TL.CHUNKBUSTER_ALREADY_BEING_BUSTED.toString()));
                return;
            }

            Entity[] entities = e.getBlock().getChunk().getEntities();
            for (int i = 0; i <= entities.length - 1; i++) {
                if (entities[i] instanceof HumanEntity) {
                    entities[i].sendMessage(TextUtil.parseColor(TL.CHUNKBUSTER_USE_MESSAGE.toString()));
                }
            }

            ChunkBusterRunnable.runGlassFrame(e);

            if(CoreX.getConfig().fetchBoolean("Chunkbuster.Run-Async")) {
                ChunkBusterRunnable.runAsyncFrame(e);
            } else {
                ChunkBusterRunnable.runChunkFrame(e);
            }
        }
    }

    public static HashSet<Chunk> getWaterChunks() {
        return waterChunks;
    }

    @EventHandler
    public void onLiquidFlow(BlockFromToEvent e){
        Block block = e.getBlock();

        if(block.getType() == XMaterial.WATER.parseMaterial() || block.getType() == XMaterial.LAVA.parseMaterial()) {
            if (!getWaterChunks().contains(block.getChunk()) && getWaterChunks().contains(e.getToBlock().getChunk())) {
                e.setCancelled(true);
            }
        }
    }
}
