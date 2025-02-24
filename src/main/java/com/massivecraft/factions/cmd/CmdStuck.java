package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.integration.Essentials;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.util.SpiralTask;
import com.massivecraft.factions.util.WorldUtil;
import com.massivecraft.factions.zcore.util.TL;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask; // Folia
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CmdStuck extends FCommand {

    public CmdStuck() {
        super();
        this.getAliases().addAll(Aliases.stuck);
        this.setRequirements(new CommandRequirements.Builder(Permission.STUCK)
                .playerOnly()
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        final Player player = context.player;
        final Location sentAt = player.getLocation();
        final FLocation chunk = context.fPlayer.getLastStoodAt();
        final long delay = FactionsPlugin.getInstance().getConfig().getLong("hcf.stuck.delay", 30);
        final int radius = FactionsPlugin.getInstance().getConfig().getInt("hcf.stuck.radius", 10);

        if (!FactionsPlugin.getInstance().getConfig().getBoolean("hcf.stuck.Enabled", false)) {
            context.msg(TL.GENERIC_DISABLED, "Factions Stuck");
            return;
        }

        // Already stuck waiting?
        if (FactionsPlugin.getInstance().getStuckMap().containsKey(player.getUniqueId())) {
            long wait = FactionsPlugin.getInstance().getTimers().get(player.getUniqueId()) - System.currentTimeMillis();
            String time = DurationFormatUtils.formatDuration(wait, TL.COMMAND_STUCK_TIMEFORMAT.toString(), true);
            context.msg(TL.COMMAND_STUCK_EXISTS, time);
        } else {
            // If there's a cost, make them pay (unless bypass)
            if (!context.payForCommand(
                    Conf.econCostStuck,
                    TL.COMMAND_STUCK_TOSTUCK.format(context.fPlayer.getName()),
                    TL.COMMAND_STUCK_FORSTUCK.format(context.fPlayer.getName())
            )) {
                return;
            }

            // Schedule a delayed synchronous task using Folia’s GlobalRegionScheduler
            ScheduledTask scheduledTask = Bukkit.getGlobalRegionScheduler().runDelayed(
                FactionsPlugin.getInstance(),
                (task) -> {
                    // If they're no longer in the stuck map, do nothing
                    if (!FactionsPlugin.getInstance().getStuckMap().containsKey(player.getUniqueId())) {
                        return;
                    }

                    // Check for world difference or radius
                    World world = chunk.getWorld();
                    if (world.getUID() != player.getWorld().getUID() || sentAt.distance(player.getLocation()) > radius) {
                        context.msg(TL.COMMAND_STUCK_OUTSIDE.format(radius));
                        FactionsPlugin.getInstance().getTimers().remove(player.getUniqueId());
                        FactionsPlugin.getInstance().getStuckMap().remove(player.getUniqueId());
                        return;
                    }

                    final Board board = Board.getInstance();
                    // Use a SpiralTask to find the nearest wilderness chunk
                    new SpiralTask(FLocation.wrap(context.player), radius * 2) {
                        @Override
                        public boolean work() {
                            FLocation chunk = currentFLocation();
                            Faction faction = board.getFactionAt(chunk);
                            int buffer = FactionsPlugin.getInstance().getConfig().getInt("world-border.buffer", 0);

                            if (faction.isWilderness() && !chunk.isOutsideWorldBorder(buffer)) {
                                int cx = WorldUtil.chunkToBlock(chunk.getIntX());
                                int cz = WorldUtil.chunkToBlock(chunk.getIntZ());
                                int y = world.getHighestBlockYAt(cx, cz);
                                Location tp = new Location(world, cx, y, cz);

                                context.msg(TL.COMMAND_STUCK_TELEPORT, tp.getBlockX(), tp.getBlockY(), tp.getBlockZ());
                                FactionsPlugin.getInstance().getTimers().remove(player.getUniqueId());
                                FactionsPlugin.getInstance().getStuckMap().remove(player.getUniqueId());

                                // Attempt Essentials, otherwise normal teleport
                                if (!Essentials.handleTeleport(player, tp)) {
                                    player.teleport(tp);
                                    Logger.print("/f stuck used regular teleport, not essentials!", Logger.PrefixType.DEFAULT);
                                }
                                this.stop();
                                return false; // stops the spiral
                            }
                            return true; // keep searching
                        }
                    };
                },
                delay * 20L // convert seconds to ticks
            );

            // track the time in the timers map
            FactionsPlugin.getInstance().getTimers().put(player.getUniqueId(), System.currentTimeMillis() + (delay * 1000));
            long wait = FactionsPlugin.getInstance().getTimers().get(player.getUniqueId()) - System.currentTimeMillis();
            String time = DurationFormatUtils.formatDuration(wait, TL.COMMAND_STUCK_TIMEFORMAT.toString(), true);
            context.msg(TL.COMMAND_STUCK_START, time);

            // store the ScheduledTask in the stuck map
            FactionsPlugin.getInstance().getStuckMap().put(player.getUniqueId(), scheduledTask);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STUCK_DESCRIPTION;
    }
}
