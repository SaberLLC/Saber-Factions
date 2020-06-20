package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.integration.Essentials;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.SpiralTask;
import com.massivecraft.factions.zcore.util.TL;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CmdStuck extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdStuck() {
        super();
        this.aliases.addAll(Aliases.stuck);


        this.requirements = new CommandRequirements.Builder(Permission.STUCK)
                .playerOnly()
                .build();
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

            final int id = Bukkit.getScheduler().runTaskLater(FactionsPlugin.getInstance(), new Runnable() {

                @Override
                public void run() {
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
                    // spiral task to find nearest wilderness chunk
                    new SpiralTask(new FLocation(context.player), radius * 2) {
                        @Override
                        public boolean work() {
                            FLocation chunk = currentFLocation();
                            Faction faction = board.getFactionAt(chunk);
                            int buffer = FactionsPlugin.getInstance().getConfig().getInt("world-border.buffer", 0) - 1;
                            if (faction.isWilderness() && !chunk.isOutsideWorldBorder(buffer)) {
                                int cx = FLocation.chunkToBlock((int) chunk.getX());
                                int cz = FLocation.chunkToBlock((int) chunk.getZ());
                                int y = world.getHighestBlockYAt(cx, cz);
                                Location tp = new Location(world, cx, y, cz);
                                context.msg(TL.COMMAND_STUCK_TELEPORT, tp.getBlockX(), tp.getBlockY(), tp.getBlockZ());
                                FactionsPlugin.getInstance().getTimers().remove(player.getUniqueId());
                                FactionsPlugin.getInstance().getStuckMap().remove(player.getUniqueId());
                                if (!Essentials.handleTeleport(player, tp)) {
                                    player.teleport(tp);
                                    FactionsPlugin.getInstance().debug("/f stuck used regular teleport, not essentials!");
                                }
                                this.stop();
                                return false;
                            }
                            return true;
                        }
                    };
                }
            }, delay * 20).getTaskId();

            FactionsPlugin.getInstance().getTimers().put(player.getUniqueId(), System.currentTimeMillis() + (delay * 1000));
            long wait = FactionsPlugin.getInstance().getTimers().get(player.getUniqueId()) - System.currentTimeMillis();
            String time = DurationFormatUtils.formatDuration(wait, TL.COMMAND_STUCK_TIMEFORMAT.toString(), true);
            context.msg(TL.COMMAND_STUCK_START, time);
            FactionsPlugin.getInstance().getStuckMap().put(player.getUniqueId(), id);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STUCK_DESCRIPTION;
    }
}

