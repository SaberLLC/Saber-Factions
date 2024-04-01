package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FactionDisbandEvent.PlayerDisbandReason;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.ChunkReference;
import com.massivecraft.factions.util.Cooldown;
import com.massivecraft.factions.util.FastChunk;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.frame.fdisband.FDisbandFrame;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class CmdDisband extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    private final HashMap<String, String> disbandMap;

    public CmdDisband() {
        super();
        this.aliases.addAll(Aliases.disband);
        this.optionalArgs.put("faction tag", "yours");
        this.requirements = new CommandRequirements.Builder(Permission.DISBAND)
                .build();
        this.disbandMap = new HashMap<>();
    }

    @Override
    public void perform(CommandContext context) {
        Faction faction = context.argAsFaction(0, context.fPlayer == null ? null : context.faction);
        if (faction == null) return;

        boolean isMyFaction = context.fPlayer != null && faction == context.faction;

        if (!isMyFaction && !Permission.DISBAND_ANY.has(context.sender, true)) return;

        if (context.fPlayer != null && !context.fPlayer.isAdminBypassing() && !hasDisbandPermission(context, faction)) {
            context.msg(TL.GENERIC_FPERM_NOPERMISSION, "disband " + faction.getTag());
            return;
        }

        if (!faction.isNormal()) {
            context.msg(TL.COMMAND_DISBAND_IMMUTABLE.toString());
            return;
        }
        if (faction.isPermanent()) {
            context.msg(TL.COMMAND_DISBAND_MARKEDPERMANENT.toString());
            return;
        }

        if(Conf.userSpawnerChunkSystem && !Conf.allowUnclaimSpawnerChunksWithSpawnersInChunk) {
            for(FastChunk fastChunk : faction.getSpawnerChunks()) {
                if(ChunkReference.getSpawnerCount(fastChunk.getChunk()) > 0) {
                    context.msg(TL.COMMAND_DISBAND_SPAWNERS_SPAWNER_CHUNKS_FOUND.toString().replace("{faction}", faction.getTag()));
                    return;
                }
            }
        }

        if (context.player == null) {
            faction.disband(null, PlayerDisbandReason.PLUGIN);
            return;
        }

        if (Cooldown.isOnCooldown(context.fPlayer.getPlayer(), "disbandCooldown") && !context.fPlayer.isAdminBypassing()) {
            context.msg(TL.COMMAND_COOLDOWN);
            return;
        }

        if (!isConfirmingDisband(context)) {
            promptDisbandConfirmation(context, faction);
            return;
        }

        broadcastDisband(context, faction);
        faction.disband(context.player, PlayerDisbandReason.COMMAND);
        Cooldown.setCooldown(context.fPlayer.getPlayer(), "disbandCooldown", FactionsPlugin.getInstance().getConfig().getInt("fcooldowns.f-disband"));
    }

    private boolean hasDisbandPermission(CommandContext context, Faction faction) {
        Access access = faction.getAccess(context.fPlayer, PermissableAction.DISBAND);
        return context.fPlayer.getRole() == Role.LEADER || faction.getFPlayerLeader() == context.fPlayer || access == Access.ALLOW;
    }

    private boolean isConfirmingDisband(CommandContext context) {
        long time;
        boolean access = context.fPlayer.getPlayer().hasMetadata("disband_confirm")
                && (time = context.fPlayer.getPlayer().getMetadata("disband_confirm").get(0).asLong()) != 0L
                && System.currentTimeMillis() - time <= TimeUnit.SECONDS.toMillis(3L);
        return access || Conf.useDisbandGUI && (!context.fPlayer.isAdminBypassing() || !context.player.isOp()) && !disbandMap.containsKey(context.player.getUniqueId().toString());
    }

    private void promptDisbandConfirmation(CommandContext context, Faction faction) {
        if (!disbandMap.containsKey(context.player.getUniqueId().toString()) && faction.getTnt() > 0) {
            context.msg(TL.COMMAND_DISBAND_CONFIRM.toString().replace("{tnt}", String.valueOf(faction.getTnt())));
            disbandMap.put(context.player.getUniqueId().toString(), faction.getId());
            Bukkit.getScheduler().scheduleSyncDelayedTask(FactionsPlugin.getInstance(), () -> disbandMap.remove(context.player.getUniqueId().toString()), 200L);
        } else if (!disbandMap.containsKey(context.player.getUniqueId().toString())) {
            new FDisbandFrame(context.player).openGUI(FactionsPlugin.getInstance());
        }
    }

    private void broadcastDisband(CommandContext context, Faction faction) {
        if (FactionsPlugin.getInstance().getConfig().getBoolean("faction-disband-broadcast", true)) {
            String yours_message = TL.COMMAND_DISBAND_BROADCAST_YOURS.toString().replace("{claims}", String.valueOf(faction.getAllClaims().size()));
            String notyours_message = TL.COMMAND_DISBAND_BROADCAST_NOTYOURS.toString().replace("{claims}", String.valueOf(faction.getAllClaims().size()));
            String amountString = context.sender instanceof ConsoleCommandSender ? TL.GENERIC_SERVERADMIN.toString() : context.fPlayer.describeTo(null);
            if (yours_message.contains("{player}") || notyours_message.contains("{player}")) {
                amountString = context.sender instanceof ConsoleCommandSender ? TL.GENERIC_SERVERADMIN.toString() : context.fPlayer.getName();
            }
            for (FPlayer follower : FPlayers.getInstance().getOnlinePlayers()) {
                if (follower.getFaction() == faction) {
                    follower.msg(yours_message, amountString);
                } else {
                    follower.msg(notyours_message, amountString, faction.getTag(follower));
                }
            }
        } else {
            context.player.sendMessage(String.valueOf(TL.COMMAND_DISBAND_PLAYER));
        }
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DISBAND_DESCRIPTION;
    }
}
