package com.massivecraft.factions.cmd.check;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.OfflinePlayer;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CmdCheck extends FCommand {
    private SimpleDateFormat simpleDateFormat;

    public CmdCheck() {
        this.simpleDateFormat = new SimpleDateFormat(Conf.dateFormat);
        this.aliases.add("check");
        this.requiredArgs.add("walls/buffers/settings/leaderboard");

        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeColeader = false;
        senderMustBeAdmin = false;
    }


    public void perform() {
        if (myFaction == null || !myFaction.isNormal()) {
            return;
        }
        String subCommand = argAsString(0, null);
        Access access = myFaction.getAccess(fme, PermissableAction.CHECK);
        if (access != Access.ALLOW && fme.getRole() != Role.LEADER) {
            fme.msg(TL.GENERIC_NOPERMISSION, "check");
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (subCommand.equalsIgnoreCase("leaderboard")) {
            msg(TL.CHECK_LEADERBOARD_HEADER);
            Map<UUID, Integer> players = new HashMap<>();
            for (Map.Entry<UUID, Integer> entry : myFaction.getPlayerWallCheckCount().entrySet()) {
                players.put(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<UUID, Integer> entry : myFaction.getPlayerBufferCheckCount().entrySet()) {
                if (players.containsKey(entry.getKey())) {
                    players.replace(entry.getKey(), players.get(entry.getKey()) + entry.getValue());
                } else {
                    players.put(entry.getKey(), entry.getValue());
                }
            }
            List<Map.Entry<UUID, Integer>> entryList = players.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).collect(Collectors.toList());
            for (int max = (entryList.size() > 10) ? 10 : entryList.size(), current = 0; current < max; ++current) {
                Map.Entry<UUID, Integer> entry = entryList.get(current);
                OfflinePlayer offlinePlayer = p.getServer().getOfflinePlayer(entry.getKey());
                msg(TL.CHECK_LEADERBOARD_LINE.format(current + 1, offlinePlayer.getName(), entry.getValue(), myFaction.getPlayerBufferCheckCount().getOrDefault(entry.getKey(), 0), myFaction.getPlayerWallCheckCount().getOrDefault(entry.getKey(), 0)));
            }
            if (entryList.isEmpty()) {
                msg(TL.CHECK_LEADERBOARD_NO_DATA);
            }
        } else if (subCommand.equalsIgnoreCase("walls")) {
            if (!CheckTask.wallCheck(myFaction.getId())) {
                if (myFaction.getChecks().isEmpty()) {
                    msg(TL.CHECK_NO_CHECKS);
                    return;
                }
                msg(TL.CHECK_ALREADY_CHECKED);
            } else {
                int current = myFaction.getPlayerWallCheckCount().getOrDefault(me.getUniqueId(), 0);
                if (current == 0) {
                    myFaction.getPlayerWallCheckCount().put(me.getUniqueId(), 1);
                } else {
                    myFaction.getPlayerWallCheckCount().replace(me.getUniqueId(), current + 1);
                }
                myFaction.getChecks().put(currentTime, "U" + fme.getNameAndTag());
                msg(TL.CHECK_WALLS_MARKED_CHECKED);
            }
        } else if (subCommand.equalsIgnoreCase("buffers")) {
            if (!CheckTask.bufferCheck(myFaction.getId())) {
                if (myFaction.getChecks().isEmpty()) {
                    msg(TL.CHECK_NO_CHECKS);
                    return;
                }
                msg(TL.CHECK_ALREADY_CHECKED);
            } else {
                int current = myFaction.getPlayerBufferCheckCount().getOrDefault(me.getUniqueId(), 0);
                if (current == 0) {
                    myFaction.getPlayerBufferCheckCount().put(me.getUniqueId(), 1);
                } else {
                    myFaction.getPlayerBufferCheckCount().replace(me.getUniqueId(), current + 1);
                }
                myFaction.getChecks().put(System.currentTimeMillis(), "Y" + fme.getNameAndTag());
                msg(TL.CHECK_BUFFERS_MARKED_CHECKED);
            }
        } else if (subCommand.equalsIgnoreCase("settings")) {
            if (!fme.getRole().isAtLeast(Role.COLEADER)) {
                msg(TL.CHECK_MUST_BE_ATLEAST_COLEADER);
                return;
            }
            CheckSettingsFrame checkGUI = new CheckSettingsFrame(p, fme);
            checkGUI.build();
            fme.getPlayer().openInventory(checkGUI.getInventory());
        }
    }

    public TL getUsageTranslation() {
        return TL.COMMAND_CHECK_DESCRIPTION;
    }
}
