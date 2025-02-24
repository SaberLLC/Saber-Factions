package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.Bukkit;

public class CmdDescription extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdDescription() {
        super();
        this.getAliases().addAll(Aliases.description);

        this.getRequiredArgs().add("desc");

        this.setRequirements(new CommandRequirements.Builder(Permission.DESCRIPTION)
                .playerOnly()
                .withRole(Role.MODERATOR)
                .noErrorOnManyArgs()
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        // Replace runTaskAsynchronously with runDelayed on the AsyncScheduler:
        Bukkit.getAsyncScheduler().runDelayed(FactionsPlugin.instance, scheduledTask -> {
            // This block runs asynchronously
    
            // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
            if (!context.payForCommand(Conf.econCostDesc, TL.COMMAND_DESCRIPTION_TOCHANGE, TL.COMMAND_DESCRIPTION_FORCHANGE)) {
                return;
            }
    
            // Clean up special chars in the description
            String desc = TextUtil.implode(context.args, " ")
                .replaceAll("%", "")
                .replaceAll("(&([a-f0-9klmnor]))", "& $2");
            context.faction.setDescription(desc);
    
            // Now schedule the sync part on the main thread
            Bukkit.getGlobalRegionScheduler().runDelayed(FactionsPlugin.instance, syncTask -> {
                FactionsPlugin.instance.logFactionEvent(
                    context.faction,
                    FLogType.FDESC_EDIT,
                    context.fPlayer.getName(),
                    desc
                );
            }, 0L); // 0 ticks = immediate sync call
    
            if (!Conf.broadcastDescriptionChanges) {
                context.msg(TL.COMMAND_DESCRIPTION_CHANGED, context.faction.describeTo(context.fPlayer));
                context.sendMessage(context.faction.getDescription());
                return;
            }
    
            // Broadcast the description to everyone
            for (FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers()) {
                fplayer.msg(TL.COMMAND_DESCRIPTION_CHANGES, context.faction.describeTo(fplayer));
                fplayer.sendMessage(context.faction.getDescription());
            }
        }, 0L, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
    

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DESCRIPTION_DESCRIPTION;
    }

}