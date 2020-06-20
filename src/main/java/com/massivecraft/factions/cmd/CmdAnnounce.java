package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdAnnounce extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdAnnounce() {
        super();
        this.aliases.addAll(Aliases.announce);

        this.requiredArgs.add("message");

        this.requirements = new CommandRequirements.Builder(Permission.ANNOUNCE)
                .playerOnly()
                .memberOnly()
                .brigadier(AnnounceBrigadier.class)
                .noErrorOnManyArgs()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        String prefix = ChatColor.GREEN + context.faction.getTag() + ChatColor.YELLOW + " [" + ChatColor.GRAY + context.player.getName() + ChatColor.YELLOW + "] " + ChatColor.RESET;
        String message = StringUtils.join(context.args, " ");

        for (Player player : context.faction.getOnlinePlayers()) {
            player.sendMessage(prefix + message);
        }

        // Add for offline players.
        for (FPlayer fp : context.faction.getFPlayersWhereOnline(false)) {
            context.faction.addAnnouncement(fp, prefix + message);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ANNOUNCE_DESCRIPTION;
    }

    protected class AnnounceBrigadier implements BrigadierProvider {
        @Override
        public ArgumentBuilder<Object, ?> get(ArgumentBuilder<Object, ?> parent) {
            return parent.then(RequiredArgumentBuilder.argument("message", StringArgumentType.greedyString()));
        }
    }

}