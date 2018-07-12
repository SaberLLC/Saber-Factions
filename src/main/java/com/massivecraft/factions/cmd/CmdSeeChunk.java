package com.massivecraft.factions.cmd;


import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Particles.ParticleEffect;
import com.massivecraft.factions.util.VisualizeUtil;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CmdSeeChunk extends FCommand {

    //Used a hashmap cuz imma make a particle selection gui later, will store it where the boolean is rn.
    public static HashMap<String, Boolean> seeChunkMap = new HashMap<>();
    Long interval = 10L;
    private boolean useParticles;
    private int length;
    private ParticleEffect effect;
    private int taskID = -1;


    //I remade it cause of people getting mad that I had the same seechunk as drtshock


    public CmdSeeChunk() {
        super();
        aliases.add("seechunk");
        aliases.add("sc");

        permission = Permission.SEECHUNK.node;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;

        this.useParticles = p.getConfig().getBoolean("see-chunk.particles", true);
        interval = P.p.getConfig().getLong("see-chunk.interval", 10L);
        if (effect == null) {
            effect = ParticleEffect.REDSTONE;
        }

    }

    @Override
    public void perform() {
        if (seeChunkMap.containsKey(me.getName())) {
            seeChunkMap.remove(me.getName());
            msg(TL.COMMAND_SEECHUNK_DISABLED);
        } else {
            seeChunkMap.put(me.getName(), true);
            msg(TL.COMMAND_SEECHUNK_ENABLED);
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
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(P.p, new Runnable() {
            @Override
            public void run() {
                Iterator itr = seeChunkMap.keySet().iterator();
                while (itr.hasNext()) {
                    Object nameObject = itr.next();
                    String name = nameObject + "";
                    Player player = Bukkit.getPlayer(name);
                    showBorders(player);
                }
                manageTask();
            }
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
        List<Player> onePlayer = Arrays.asList(player);
        for (int blockY = 0; blockY < player.getLocation().getBlockY() + 30; blockY++) {
            Location loc = new Location(world, blockX, blockY, blockZ).add(0.5, 0, 0.5);
            if (loc.getBlock().getType() != Material.AIR) {
                continue;
            }
            if (useParticles) {
                this.effect.display(0, 0, 0, 0, 1, loc, player);
            } else {
                int typeId = blockY % 5 == 0 ? Material.REDSTONE_LAMP_ON.getId() : Material.STAINED_GLASS.getId();
                VisualizeUtil.addLocation(player, loc, typeId);
            }
        }
    }


    @Override
    public TL getUsageTranslation() {
        return TL.GENERIC_PLACEHOLDER;
    }

}