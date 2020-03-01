package com.massivecraft.factions.duels;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author droppinganvil
 */
public class Duel {
    private Faction faction1;
    private Faction faction2;
    private HashMap<FPlayer, Boolean> eliminationMap;
    private HashMap<FPlayer, Location> oldLocMap;

    public Duel(Faction faction1, Faction faction2) {
        this.faction1 = faction1;
        this.faction2 = faction2;
        this.eliminationMap = new HashMap<>();
        this.oldLocMap = new HashMap<>();
    }

    public Faction getFaction1() {
        return faction1;
    }

    public Faction getFaction2() {
        return faction2;
    }

    public Set<FPlayer> getEliminated() {
        Set<FPlayer> list = new HashSet<>();
        for (Map.Entry<FPlayer, Boolean> entry : eliminationMap.entrySet()) {
            if (entry.getValue()) list.add(entry.getKey());
        }
        return list;
    }

    public Set<FPlayer> getEliminated(Faction faction) {
        Set<FPlayer> list = new HashSet<>();
        for (Map.Entry<FPlayer, Boolean> entry : eliminationMap.entrySet()) {
            if (entry.getValue() && entry.getKey().getFaction().equals(faction)) list.add(entry.getKey());
        }
        return list;
    }

    public Set<FPlayer> getRemaining() {
        Set<FPlayer> list = new HashSet<>();
        for (Map.Entry<FPlayer, Boolean> entry : eliminationMap.entrySet()) {
            if (!entry.getValue()) list.add(entry.getKey());
        }
        return list;
    }

    public Set<FPlayer> getRemaining(Faction faction) {
        Set<FPlayer> list = new HashSet<>();
        for (Map.Entry<FPlayer, Boolean> entry : eliminationMap.entrySet()) {
            if (!entry.getValue() && entry.getKey().getFaction().equals(faction)) list.add(entry.getKey());
        }
        return list;
    }

    public void handleExit(FPlayer fplayer, Boolean death) {
        if (death) {
            eliminationMap.replace(fplayer, false, true);
            fplayer.getPlayer().spigot().respawn();
        }
        fplayer.getPlayer().teleport(oldLocMap.get(fplayer), PlayerTeleportEvent.TeleportCause.PLUGIN);
        for (Map.Entry<Integer, ItemStack> entry : fplayer.getOldInv().entrySet()) {
            fplayer.getPlayer().getInventory().setItem(entry.getKey(), entry.getValue());
        }
        fplayer.setOldInv(null);
        fplayer.setInDuel(false);
        if (getClearWinner() != null) {
            handleEnd();
        }
    }

    public void handleEnd() {
        if (FactionsPlugin.getInstance().getConfig().getBoolean("Duels.Broadcast.BroadcastResult.Enabled")) {
            Bukkit.broadcastMessage(getEndMessage());
        }
        for (FPlayer fp : getRemaining()) {
            handleExit(fp, false);
        }
        Map.Entry<Faction, Faction> fvf = (Map.Entry<Faction, Faction>) Duels.duelQueue.entrySet().toArray()[0];
        if (fvf != null) {
            Duels.duel = new Duel(fvf.getKey(), fvf.getValue());
            Duels.preparingDuel = true;
            Duels.acceptedDuel.clear();
        }
    }

    public Faction getClearWinner() {
        Set<FPlayer> team1r = getRemaining(faction1);
        Set<FPlayer> team2r = getRemaining(faction2);
        if (team1r.isEmpty() && !team2r.isEmpty()) return faction2;
        if (team2r.isEmpty() && !team1r.isEmpty()) return faction1;
        return null;
    }

    private Faction getWinning(Set<FPlayer> s1, Set<FPlayer> s2) {
        if (s1.size() > s2.size()) return faction1;
        return faction2;
    }

    public String getEndMessage() {
        String temp;
        Set<FPlayer> team1r = getRemaining(faction1);
        Set<FPlayer> team2r = getRemaining(faction2);
        if (team1r.size() == team2r.size()) {
            //Tied
            temp = FactionsPlugin.getInstance().getConfig().getString("Duels.Broadcast.BroadcastResult.FormatTie");
        } else {
            temp = FactionsPlugin.getInstance().getConfig().getString("Duels.Broadcast.BroadcastResult.Format");
        }
        Faction winner = getWinning(team1r, team2r);
        return ChatColor.translateAlternateColorCodes('&', temp
                .replace("{team1_tag}", faction1.getTag())
                .replace("{team2_tag}", faction2.getTag())
                .replace("{remaining}", String.valueOf(getRemaining(winner).size()))
                .replace("{tag}", winner.getTag())
                .replace("{losers_tag}", winner == faction1 ? faction2.getTag() : faction1.getTag()));
    }

    public String getDeathMessage(Player player) {
        return ChatColor.translateAlternateColorCodes('&',
                FactionsPlugin.getInstance().getConfig().getString("Duels.Broadcast.BroadcastKill.Format")
                .replace("{team1_remaining}", String.valueOf(getRemaining(getFaction1()).size()))
                .replace("{team2_remaining}", String.valueOf(getRemaining(getFaction2()).size()))
                .replace("{team1_eliminated}", String.valueOf(getEliminated(getFaction1()).size()))
                .replace("{team2_eliminated}", String.valueOf(getEliminated(getFaction2()).size()))
                .replace("{team1_tag}", faction1.getTag())
                .replace("{team2_tag}", faction2.getTag())
                .replace("{eliminated_name}", player.getName()));
    }

    public HashMap<FPlayer, Location> getLocMap() {
        return oldLocMap;
    }

    public void addFPlayer(FPlayer fp) {
        oldLocMap.put(fp, fp.getPlayer().getLocation());
        eliminationMap.put(fp, false);
    }

    public void start() {
        Duels.preparingDuel = false;
        for (FPlayer fplayer : Duels.acceptedDuel) {

        }
    }
}
