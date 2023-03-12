package com.massivecraft.factions.cmd.check;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.OfflinePlayer;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CmdCheck extends FCommand {

    /**
     * @author Driftay
     */

    private SimpleDateFormat simpleDateFormat;

    public CmdCheck() {
        this.simpleDateFormat = new SimpleDateFormat(Conf.dateFormat);
        this.aliases.addAll(Aliases.check);
        this.requiredArgs.add("walls/buffers/settings/leaderboard");

        this.requirements = new CommandRequirements.Builder(Permission.CHECK)
                .playerOnly()
                .withAction(PermissableAction.CHECK)
                .memberOnly()
                .build();
    }


    public void perform(CommandContext context) {
        if (context.faction == null || !context.faction.isNormal()) {
            return;
        }
        String subCommand = context.argAsString(0, null);

        long currentTime = System.currentTimeMillis();
        if (subCommand.equalsIgnoreCase("leaderboard")) {
            context.msg(TL.CHECK_LEADERBOARD_HEADER);
            Map<UUID, Integer> players = new HashMap<>(context.faction.getPlayerWallCheckCount());
            for (Map.Entry<UUID, Integer> entry : context.faction.getPlayerBufferCheckCount().entrySet()) {
                if (players.containsKey(entry.getKey())) {
                    players.replace(entry.getKey(), players.get(entry.getKey()) + entry.getValue());
                } else {
                    players.put(entry.getKey(), entry.getValue());
                }
            }
            List<Map.Entry<UUID, Integer>> entryList = players.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).collect(Collectors.toList());
            for (int max = Math.min(entryList.size(), 10), current = 0; current < max; ++current) {
                Map.Entry<UUID, Integer> entry = entryList.get(current);
                OfflinePlayer offlinePlayer = FactionsPlugin.getInstance().getServer().getOfflinePlayer(entry.getKey());
                context.msg(TL.CHECK_LEADERBOARD_LINE.format(current + 1, offlinePlayer.getName(), entry.getValue(), context.faction.getPlayerBufferCheckCount().getOrDefault(entry.getKey(), 0), context.faction.getPlayerWallCheckCount().getOrDefault(entry.getKey(), 0)));
            }
            if (entryList.isEmpty()) {
                context.msg(TL.CHECK_LEADERBOARD_NO_DATA);
            }
        } else if (subCommand.equalsIgnoreCase("walls")) {
            if (!CheckTask.getInstance().removeWallCheck(context.faction.getId(), context.faction.getWallCheckMinutes())) {
                if (context.faction.getChecks().isEmpty()) {
                    context.msg(TL.CHECK_NO_CHECKS);
                    return;
                }
                context.msg(TL.CHECK_ALREADY_CHECKED);
            } else {
                int current = context.faction.getPlayerWallCheckCount().getOrDefault(context.player.getUniqueId(), 0);
                if (current == 0) {
                    context.faction.getPlayerWallCheckCount().put(context.player.getUniqueId(), 1);
                } else {
                    context.faction.getPlayerWallCheckCount().replace(context.player.getUniqueId(), current + 1);
                }
                context.faction.getChecks().put(currentTime, "U" + context.fPlayer.getNameAndTag());
                context.msg(TL.CHECK_WALLS_MARKED_CHECKED);
            }
        } else if (subCommand.equalsIgnoreCase("buffers")) {
            if (!CheckTask.getInstance().removeBufferCheck(context.faction.getId(), context.faction.getBufferCheckMinutes())) {
                if (context.faction.getChecks().isEmpty()) {
                    context.msg(TL.CHECK_NO_CHECKS);
                    return;
                }
                context.msg(TL.CHECK_ALREADY_CHECKED);
            } else {
                int current = context.faction.getPlayerBufferCheckCount().getOrDefault(context.player.getUniqueId(), 0);
                if (current == 0) {
                    context.faction.getPlayerBufferCheckCount().put(context.player.getUniqueId(), 1);
                } else {
                    context.faction.getPlayerBufferCheckCount().replace(context.player.getUniqueId(), current + 1);
                }
                context.faction.getChecks().put(System.currentTimeMillis(), "Y" + context.fPlayer.getNameAndTag());
                context.msg(TL.CHECK_BUFFERS_MARKED_CHECKED);

            }
        } else if (subCommand.equalsIgnoreCase("settings")) {
            if (!context.fPlayer.getRole().isAtLeast(Role.COLEADER)) {
                context.msg(TL.CHECK_MUST_BE_ATLEAST_COLEADER);
                return;
            }
            CheckSettingsFrame checkGUI = new CheckSettingsFrame(FactionsPlugin.getInstance(), context.fPlayer);
            checkGUI.build(true);
            context.fPlayer.getPlayer().openInventory(checkGUI.getInventory());
        }
    }

    public TL getUsageTranslation() {
        return TL.COMMAND_CHECK_DESCRIPTION;
    }
}
