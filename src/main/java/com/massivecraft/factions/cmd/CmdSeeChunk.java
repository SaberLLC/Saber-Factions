package com.massivecraft.factions.cmd;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.FastMath;
import com.massivecraft.factions.util.VisualizeUtil;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class CmdSeeChunk extends FCommand {

    //Used a hashmap cuz imma make a particle selection gui later, will store it where the boolean is rn.
    public static Set<String> seeChunkSet = new HashSet<>();

    //I remade it cause of people getting mad that I had the same seechunk as drtshock
    private final Material redstoneLamp;
    private final Material blackStainedGlass;
    private static final int[][] OFFSETS = new int[][]{{0, 0}, {15, 0}, {0, 15}, {15, 15}};

    private final long updateInterval;
    private int taskID = -1;

    public CmdSeeChunk() {
        super();
        redstoneLamp = XMaterial.REDSTONE_LAMP.parseMaterial();
        blackStainedGlass = XMaterial.BLACK_STAINED_GLASS.parseMaterial();

        getAliases().addAll(Aliases.seeChunk);

        updateInterval = FactionsPlugin.getInstance().getConfig().getLong("see-chunk.interval", 15L);

        this.setRequirements(new CommandRequirements.Builder(Permission.SEECHUNK)
                .playerOnly()
                .build());

    }

    @Override
    public void perform(CommandContext context) {
        if (seeChunkSet.remove(context.player.getName())) {
            context.msg(TL.COMMAND_SEECHUNK_DISABLED);
        } else {
            seeChunkSet.add(context.player.getName());
            context.msg(TL.COMMAND_SEECHUNK_ENABLED);
            manageTask();
        }
    }

    private void manageTask() {
        if (taskID != -1) {
            if (seeChunkSet.isEmpty()) {
                Bukkit.getScheduler().cancelTask(taskID);
                taskID = -1;
            }
        } else {
            startTask();
        }
    }

    private void startTask() {
        taskID = Bukkit.getScheduler().runTaskTimer(FactionsPlugin.getInstance(), () -> {
            Iterator<String> iterator = seeChunkSet.iterator();

            while (iterator.hasNext()) {
                Player player = Bukkit.getPlayer(iterator.next());

                if (player == null || !player.isOnline()) {
                    iterator.remove();
                    continue;
                }
                showBorders(player);
            }
            manageTask();
        }, 0, updateInterval).getTaskId();
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
            VisualizeUtil.addLocation(player, block.getLocation(), y % 5 == 0 ? this.redstoneLamp : this.blackStainedGlass);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.GENERIC_PLACEHOLDER;
    }

}