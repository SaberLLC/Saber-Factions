package com.massivecraft.factions.cmd;


import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Particles.ParticleEffect;
import com.massivecraft.factions.util.VisualizeUtil;
import com.massivecraft.factions.util.XMaterial;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CmdSeeChunk extends FCommand {

    /**
     * @author FactionsUUID Team
     */

    //Used a hashmap cuz imma make a particle selection gui later, will store it where the boolean is rn.
    public static HashMap<String, Boolean> seeChunkMap = new HashMap<>();
    private long interval;
    private boolean useParticles;
    private ParticleEffect effect;
    private int taskID = -1;


    //I remade it cause of people getting mad that I had the same seechunk as drtshock


    public CmdSeeChunk() {
        super();
        aliases.addAll(Aliases.seeChunk);

        this.useParticles = FactionsPlugin.getInstance().getConfig().getBoolean("see-chunk.particles", true);
        interval = FactionsPlugin.getInstance().getConfig().getLong("see-chunk.interval", 10L);
        if (effect == null) {
            effect = ParticleEffect.REDSTONE;
        }

        this.requirements = new CommandRequirements.Builder(Permission.SEECHUNK)
                .playerOnly()
                .build();

    }

    @Override
    public void perform(CommandContext context) {
        if (seeChunkMap.containsKey(context.player.getName())) {
            seeChunkMap.remove(context.player.getName());
            context.msg(TL.COMMAND_SEECHUNK_DISABLED);
        } else {
            seeChunkMap.put(context.player.getName(), true);
            context.msg(TL.COMMAND_SEECHUNK_ENABLED);
            manageTask();
        }
    }

    private void manageTask() {
        if (taskID != -1) {
            if (seeChunkMap.keySet().size() == 0) {
                Bukkit.getScheduler().cancelTask(taskID);
                taskID = -1;
            }
        } else {
            startTask();
        }
    }

    private void startTask() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(FactionsPlugin.getInstance(), () -> {
            for (Object nameObject : seeChunkMap.keySet()) {
                String name = nameObject + "";
                Player player = Bukkit.getPlayer(name);
                if (player != null) {
                    showBorders(player);
                }
            }
            manageTask();
        }, 0, interval);
    }

    private void showBorders(Player me) {
        World world = me.getWorld();
        FLocation flocation = new FLocation(me);
        int chunkX = (int) flocation.getX();
        int chunkZ = (int) flocation.getZ();

        int blockX;
        int blockZ;

        blockX = chunkX * 16;
        blockZ = chunkZ * 16;
        showPillar(me, world, blockX, blockZ);


        blockX = chunkX * 16 + 15;
        blockZ = chunkZ * 16;
        showPillar(me, world, blockX, blockZ);

        blockX = chunkX * 16;
        blockZ = chunkZ * 16 + 15;
        showPillar(me, world, blockX, blockZ);

        blockX = chunkX * 16 + 15;
        blockZ = chunkZ * 16 + 15;
        showPillar(me, world, blockX, blockZ);
    }

    private void showPillar(Player player, World world, int blockX, int blockZ) {
        for (int blockY = 0; blockY < player.getLocation().getBlockY() + 30; blockY++) {
            Location loc = new Location(world, blockX, blockY, blockZ).add(0.5, 0, 0.5);
            if (loc.getBlock().getType() != Material.AIR) continue;
            if (useParticles) {
                if (FactionsPlugin.getInstance().useNonPacketParticles) {
                    // Dust options only exists in the 1.13 API, so we use an
                    // alternative method to achieve this in lower versions.
                    if (FactionsPlugin.getInstance().mc113 || FactionsPlugin.getInstance().mc114 || FactionsPlugin.getInstance().mc115) {
                        player.spawnParticle(Particle.REDSTONE, loc, 0, new Particle.DustOptions(Color.RED, 1));
                    } else {
                        player.getWorld().spawnParticle(Particle.REDSTONE, loc, 0, 255, 0, 0, 1);
                    }
                } else {
                    this.effect.display(0, 0, 0, 0, 1, loc, player);
                }
            } else {
                Material type = blockY % 5 == 0 ? XMaterial.REDSTONE_LAMP.parseMaterial() : XMaterial.BLACK_STAINED_GLASS.parseMaterial();
                VisualizeUtil.addLocation(player, loc, type);
            }
        }
    }


    @Override
    public TL getUsageTranslation() {
        return TL.GENERIC_PLACEHOLDER;
    }

}