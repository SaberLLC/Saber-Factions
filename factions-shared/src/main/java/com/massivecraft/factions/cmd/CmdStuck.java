package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.scheduler.FactionTask;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.WorldUtil;
import com.massivecraft.factions.util.spiral.ChunkProcessingContext;
import com.massivecraft.factions.util.spiral.FoliaSpiralTask;
import com.massivecraft.factions.util.spiral.SpiralTask;
import com.massivecraft.factions.util.spiral.generator.SquareSpiralGenerator;
import com.massivecraft.factions.zcore.util.TL;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CmdStuck extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

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


        if (FactionsPlugin.getInstance().getStuckMap().containsKey(player.getUniqueId())) {
            long wait = FactionsPlugin.getInstance().getTimers().get(player.getUniqueId()) - System.currentTimeMillis();
            String time = DurationFormatUtils.formatDuration(wait, TL.COMMAND_STUCK_TIMEFORMAT.toString(), true);
            context.msg(TL.COMMAND_STUCK_EXISTS, time);
        } else {

            // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
            if (!context.payForCommand(Conf.econCostStuck, TL.COMMAND_STUCK_TOSTUCK.format(context.fPlayer.getName()), TL.COMMAND_STUCK_FORSTUCK.format(context.fPlayer.getName()))) {
                return;
            }

            FactionTask stuckTask = FactionsPlugin.getScheduler().runGlobalLater(delay * 20L, () -> {
                if (!FactionsPlugin.getInstance().getStuckMap().containsKey(player.getUniqueId())) {
                    return;
                }

                // check for world difference or radius exceeding
                final World world = chunk.getWorld();
                if (world.getUID() != player.getWorld().getUID() || sentAt.distance(player.getLocation()) > radius) {
                    context.msg(TL.COMMAND_STUCK_OUTSIDE.format(radius));
                    FactionsPlugin.getInstance().getTimers().remove(player.getUniqueId());
                    FactionsPlugin.getInstance().getStuckMap().remove(player.getUniqueId());
                    return;
                }

                final Board board = Board.getInstance();
                final int buffer = FactionsPlugin.getInstance().getConfig().getInt("world-border.buffer", 0);
                // spiral task to find nearest wilderness chunk
                if (FactionsPlugin.isFolia()) {
                    new FoliaSpiralTask(SpiralTask.buildFLocationQueue(FLocation.wrap(context.player), radius * 2, new SquareSpiralGenerator())) {
                        @Override
                        protected void work(FLocation chunk) {
                            Faction faction = board.getFactionAt(chunk);
                            if (faction.isWilderness() && !chunk.isOutsideWorldBorder(buffer)) {
                                int cx = WorldUtil.chunkToBlock(chunk.getIntX());
                                int cz = WorldUtil.chunkToBlock(chunk.getIntZ());
                                int y = world.getHighestBlockYAt(cx, cz);
                                Location tp = new Location(world, cx, y, cz);
                                context.msg(TL.COMMAND_STUCK_TELEPORT, tp.getBlockX(), tp.getBlockY(), tp.getBlockZ());
                                FactionsPlugin.getInstance().getTimers().remove(player.getUniqueId());
                                FactionsPlugin.getInstance().getStuckMap().remove(player.getUniqueId());
                                player.teleport(tp);
                                this.stop();
                            }
                        }
                    }.start();
                } else {
                    new SpiralTask(FLocation.wrap(context.player), radius * 2, new SquareSpiralGenerator()) {
                        @Override
                        public boolean work(ChunkProcessingContext ctx) {
                            FLocation chunk = ctx.getFLocation();
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
                                player.teleport(tp);
                                this.stop();
                                return false;
                            }
                            return true;
                        }
                    };
                }
            });

            FactionsPlugin.getInstance().getTimers().put(player.getUniqueId(), System.currentTimeMillis() + (delay * 1000));
            long wait = FactionsPlugin.getInstance().getTimers().get(player.getUniqueId()) - System.currentTimeMillis();
            String time = DurationFormatUtils.formatDuration(wait, TL.COMMAND_STUCK_TIMEFORMAT.toString(), true);
            context.msg(TL.COMMAND_STUCK_START, time);
            FactionsPlugin.getInstance().getStuckMap().put(player.getUniqueId(), stuckTask);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STUCK_DESCRIPTION;
    }
}

