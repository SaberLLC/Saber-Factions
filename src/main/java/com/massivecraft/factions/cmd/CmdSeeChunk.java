package com.massivecraft.factions.cmd;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.FastMath;
import com.massivecraft.factions.util.VisualizeUtil;
import com.massivecraft.factions.zcore.util.TL;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CmdSeeChunk extends FCommand {

    //Used a hashmap cuz imma make a particle selection gui later, will store it where the boolean is rn.
    public static HashMap<String, Boolean> seeChunkMap = new HashMap<>();
    Long interval;
    //private boolean useParticles;
    //private final ParticleEffect effect = ParticleEffect.REDSTONE;

    private int taskID = -1;


    //I remade it cause of people getting mad that I had the same seechunk as drtshock

    private Material air;
    private Material redstoneLamp;
    private Material blackStainedGlass;

    private static final int[][] OFFSETS = new int[][] {{0, 0}, {15, 0}, {0, 15}, {15, 15}};


    public CmdSeeChunk() {
        super();
        air = XMaterial.AIR.parseMaterial();
        redstoneLamp = XMaterial.REDSTONE_LAMP.parseMaterial();
        blackStainedGlass = XMaterial.BLACK_STAINED_GLASS.parseMaterial();

        getAliases().addAll(Aliases.seeChunk);

        //this.useParticles = FactionsPlugin.getInstance().getConfig().getBoolean("see-chunk.particles", true);
        interval = FactionsPlugin.getInstance().getConfig().getLong("see-chunk.interval", 10L);

        this.setRequirements(new CommandRequirements.Builder(Permission.SEECHUNK)
                .playerOnly()
                .build());

    }

    @Override
    public void perform(CommandContext context) {
        if (seeChunkMap.remove(context.player.getName()) != null) {
            context.msg(TL.COMMAND_SEECHUNK_DISABLED);
        } else {
            seeChunkMap.put(context.player.getName(), true);
            context.msg(TL.COMMAND_SEECHUNK_ENABLED);
            manageTask();
        }
    }

    // Instead of storing an int taskID, store a ScheduledTask
    private transient ScheduledTask scheduledTask = null;

    private void manageTask() {
        // If we already have a running task, check if we need to cancel it
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            // If there's nothing to process, cancel
            if (seeChunkMap.isEmpty()) {
                scheduledTask.cancel();
                scheduledTask = null;
            }
        } else {
            // Otherwise, no task is running; start it
            startTask();
        }
    }

    private void startTask() {
        scheduledTask = Bukkit.getGlobalRegionScheduler().runAtFixedRate(
            FactionsPlugin.getInstance(),
            scheduledTask -> {
                Iterator<Map.Entry<String, Boolean>> iterator = seeChunkMap.entrySet().iterator();
    
                while (iterator.hasNext()) {
                    Map.Entry<String, Boolean> entry = iterator.next();
                    Player player = Bukkit.getPlayer(entry.getKey());
    
                    if (player == null || !player.isOnline()) {
                        iterator.remove();
                        continue;
                    }
                    showBorders(player);
                }
                manageTask(); // You’ll likely update manageTask() to check scheduledTask
            },
            0L,       // initial delay in ticks
            interval  // repeat period in ticks
        );
    }
    

    private void showBorders(Player me) {
        World world = me.getWorld();
        FLocation flocation = FLocation.wrap(me);

        int blockX = flocation.toBlockX();
        int blockZ = flocation.toBlockZ();

        for (int[] coords : OFFSETS) {
            int pillarX = blockX + coords[0];
            int pillarZ = blockZ + coords[1];

            showPillar(me, world, pillarX, pillarZ);
        }
    }

    private void showPillar(Player player, World world, int blockX, int blockZ) {
        int baseY = FastMath.floor(player.getLocation().getY());
        int maxY = baseY + 15;

        for (int y = baseY; y < maxY; y++) {
            Block block = world.getBlockAt(blockX, y, blockZ);

            if (block.getType() != Material.AIR) {
                continue;
            }
            //if (useParticles) {
            //    new ParticleBuilder(this.effect, block.getLocation().add(0.5, 0, 0.5)).setColor(Color.RED).display(player);
            //} else {
                VisualizeUtil.addLocation(player, block.getLocation(), y % 5 == 0 ? this.redstoneLamp : this.blackStainedGlass);
           // }
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.GENERIC_PLACEHOLDER;
    }

}