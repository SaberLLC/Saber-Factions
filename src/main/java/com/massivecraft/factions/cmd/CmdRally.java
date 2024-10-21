package com.massivecraft.factions.cmd;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.location.ApolloBlockLocation;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.recipients.Recipients;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CmdRally extends FCommand {

    private static final WaypointModule WAYPOINT_MODULE = Apollo.getModuleManager().getModule(WaypointModule.class);

    public CmdRally() {
        super();
        this.getAliases().addAll(Aliases.rally);

        this.setRequirements(new CommandRequirements.Builder(Permission.RALLY)
                .memberOnly()
                .playerOnly()
                .build());
    }

    @Override
    public void perform(CommandContext commandContext) {
        Bukkit.getScheduler().runTaskAsynchronously(FactionsPlugin.getInstance(), () -> {
            if (commandContext == null || commandContext.player == null) return;

            final Faction faction = commandContext.faction;
            if (faction == null || faction.isSystemFaction()) {
                commandContext.msg(TL.COMMAND_RALLY_NEED_FACTION);
                return;
            }
            sendWaypoint(commandContext.fPlayer);
        });
    }

    private void sendWaypoint(FPlayer sender) {
        if (WAYPOINT_MODULE == null) return;
        final Faction faction = sender.getFaction();
        final Player player = sender.getPlayer();
        final Location location = player.getLocation();

        Set<UUID> members = faction.getFPlayers().stream()
                .map(FPlayer::getPlayer).filter(Player::isOnline)
                .map(Player::getUniqueId).collect(Collectors.toSet());

        Recipients waypointReceivers = Recipients.of(
                Apollo.getPlayerManager().getPlayers().stream()
                        .filter(apolloPlayers -> members.contains(apolloPlayers.getUniqueId()))
                        .collect(Collectors.toList())
        );

        WAYPOINT_MODULE.displayWaypoint(waypointReceivers, Waypoint.builder()
                .name("FRally")
                .location(ApolloBlockLocation.builder()
                        .world(location.getWorld().getName())
                        .x(location.getBlockX())
                        .y(location.getBlockY())
                        .z(location.getBlockZ())
                        .build())
                .color(Color.GREEN)
                .hidden(false).build());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_RALLY_DESCRIPTION;
    }
}
