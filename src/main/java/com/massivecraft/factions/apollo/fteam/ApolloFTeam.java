/*  ApolloFTeam
 * By: jimmy "vSKAH" <vskahhh@gmail.com>
 * Created with IntelliJ IDEA
 * For the project Saber-Factions
 * 21/10/2024
 */

package com.massivecraft.factions.apollo.fteam;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.BukkitApollo;
import com.lunarclient.apollo.common.location.ApolloLocation;
import com.lunarclient.apollo.module.team.TeamMember;
import com.lunarclient.apollo.module.team.TeamModule;
import com.lunarclient.apollo.recipients.Recipients;
import com.massivecraft.factions.*;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ApolloFTeam implements Listener {

    public static final TeamModule TEAM_MODULE = Apollo.getModuleManager().getModule(TeamModule.class);
    private @Getter
    static ApolloFTeam instance;

    public ApolloFTeam() {
        instance = this;
    }

    private TeamMember createTeamMember(Player player) {
        Location location = player.getLocation();
        if (location.getWorld() == null) return null;
        return TeamMember.builder()
                .playerUuid(player.getUniqueId())
                .markerColor(Color.WHITE)
                .location(BukkitApollo.toApolloLocation(player.getLocation()))
                .build();
    }

    public void applyUpdates() {
        Factions factionsInstance = Factions.getInstance();
        factionsInstance.getAllFactions().stream()
                .filter(faction -> !faction.isSystemFaction() && faction.getFPlayers().size() > 1)
                .forEach(fac -> refresh(fac, fac.getFPlayers()));
    }

    private void refresh(Faction faction, Set<FPlayer> fPlayers) {
        Recipients recipients = faction.getFactionMembersRecipients();
        if (recipients == null) return;
        List<TeamMember> teamMembers = new ArrayList<>(fPlayers.size());
        for (FPlayer fPlayer : fPlayers) {
            Player player = fPlayer.getPlayer();
            if (player == null) continue;
            TeamMember teamMember = createTeamMember(player);
            if (teamMember != null) teamMembers.add(teamMember);
        }
        TEAM_MODULE.updateTeamMembers(recipients, teamMembers);
    }
}
