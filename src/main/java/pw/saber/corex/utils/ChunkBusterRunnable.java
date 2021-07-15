package pw.saber.corex.utils;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;
import pw.saber.corex.CoreX;

import static pw.saber.corex.cmds.chunkbusters.listener.ChunkBusterListener.*;

public class ChunkBusterRunnable {

    public static void runGlassFrame(BlockPlaceEvent e) {
        Block blockPlaced = e.getBlockPlaced();
        World world = e.getBlock().getWorld();
        int bx = blockPlaced.getChunk().getX() << 4;
        int bz = blockPlaced.getChunk().getZ() << 4;
        Bukkit.getScheduler().scheduleSyncDelayedTask(FactionsPlugin.getInstance(), () -> {
            beingBusted.put(blockPlaced.getChunk(), blockPlaced.getLocation());
            waterChunks.add(blockPlaced.getChunk());
            for (int xx = bx; xx < bx + 16; xx++) {
                for (int zz = bz; zz < bz + 16; zz++) {
                    for (int yy = e.getBlockPlaced().getY(); yy >= 0; yy--) {
                        Block scannedBlock = world.getBlockAt(xx, yy, zz);
                        if(deniedBlockList.contains(XMaterial.matchXMaterial(scannedBlock.getType()).name())) continue;

                        if (!scannedBlock.getType().equals(Material.BEDROCK)) {
                            FactionsPlugin.getInstance().getNmsManager().setBlock(scannedBlock.getWorld(), scannedBlock.getX(), scannedBlock.getY(), scannedBlock.getZ(), XMaterial.GLASS.getId(), (byte) 0);
                        }
                    }
                }
            }
        }, 0);
    }

    public static void runAsyncFrame(BlockPlaceEvent e) {
        World world = e.getBlock().getWorld();
        int bx = e.getBlockPlaced().getChunk().getX() << 4;
        int bz = e.getBlockPlaced().getChunk().getZ() << 4;

        Bukkit.getScheduler().scheduleSyncDelayedTask(FactionsPlugin.getInstance(), () -> {
            int multiplier = 0;
            for (int yy = e.getBlockPlaced().getY(); yy >= 0; yy--) {
                multiplier++;
                int dy = yy;
                Bukkit.getScheduler().scheduleSyncDelayedTask(FactionsPlugin.getInstance(), () -> {
                    for (int zz = bz; zz < bz + 16; zz++) {
                        for (int xx = bx; xx < bx + 16; xx++) {
                            Block blockScanned = world.getBlockAt(xx, dy, zz);
                            if(deniedBlockList.contains(XMaterial.matchXMaterial(blockScanned.getType()).name())) continue;

                            if (!blockScanned.getType().equals(Material.BEDROCK)) {
                                FactionsPlugin.getInstance().getNmsManager().setBlock(blockScanned.getWorld(), blockScanned.getX(), blockScanned.getY(), blockScanned.getZ(), 0, (byte) 0);
                            }
                        }
                        beingBusted.remove(e.getBlock().getChunk());
                    }
                }, 20L * multiplier);

            }
        }, CoreX.getConfig().fetchInt("Chunkbuster.Countdown") * 20L);
    }


    public static void runChunkFrame(BlockPlaceEvent e) {
        World world = e.getBlock().getWorld();
        int bx = e.getBlockPlaced().getChunk().getX() << 4;
        int bz = e.getBlockPlaced().getChunk().getZ() << 4;

        Bukkit.getScheduler().scheduleSyncDelayedTask(FactionsPlugin.getInstance(), () -> {
            for (int xx = bx; xx < bx + 16; xx++) {
                for (int zz = bz; zz < bz + 16; zz++) {
                    for (int yy = 0; yy < 255; yy++) {
                        Block blockScanned = world.getBlockAt(xx, yy, zz);
                        if(deniedBlockList.contains(XMaterial.matchXMaterial(blockScanned.getType()).name())) continue;

                        if (!blockScanned.getType().equals(Material.BEDROCK)) {
                            FactionsPlugin.getInstance().getNmsManager().setBlock(blockScanned.getWorld(), blockScanned.getX(), blockScanned.getY(), blockScanned.getZ(), 0,(byte) 0);
                        }
                    }
                    beingBusted.remove(e.getBlock().getChunk());
                }
            }
        }, CoreX.getConfig().fetchInt("Chunkbuster.Countdown") * 20L);
    }
}
