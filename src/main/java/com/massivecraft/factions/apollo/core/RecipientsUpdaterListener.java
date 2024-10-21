/*  FactionListener
 * By: jimmy "vSKAH" <vskahhh@gmail.com>
 * Created with IntelliJ IDEA
 * For the project Saber-Factions
 * 21/10/2024
 */

package com.massivecraft.factions.apollo.core;

import com.lunarclient.apollo.recipients.Recipients;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.apollo.fteam.ApolloFTeam;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class RecipientsUpdaterListener implements Listener {

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        updateRecipientsFromVanillaEvent(event.getPlayer());
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        updateRecipientsFromVanillaEvent(event.getPlayer());
    }

    private void updateRecipientsFromVanillaEvent(Player player) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (fPlayer == null) return;

        Faction faction = fPlayer.getFaction();
        if (faction == null || faction.isSystemFaction()) return;
        faction.updateFactionMembersRecipients();
    }

    @EventHandler
    private void onPlayerJoinFaction(FPlayerJoinEvent event) {
        Faction faction = event.getFaction();
        updateRecipientsFromFactionEvent(faction);
    }

    @EventHandler
    private void onPlayerLeaveFaction(FPlayerLeaveEvent event) {
        Faction faction = event.getFaction();
        Recipients recipients = faction.getFactionMembersRecipients();
        if (recipients != null) ApolloFTeam.TEAM_MODULE.resetTeamMembers(recipients);
        updateRecipientsFromFactionEvent(faction);
    }

    private void updateRecipientsFromFactionEvent(Faction faction) {
        if (faction == null || faction.isSystemFaction()) return;
        faction.updateFactionMembersRecipients();
    }

}
