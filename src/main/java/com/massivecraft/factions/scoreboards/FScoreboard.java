package com.massivecraft.factions.scoreboards;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

public class FScoreboard {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    private static final Map<FPlayer, FScoreboard> fscoreboards = new HashMap<>();

    private final Scoreboard scoreboard;
    private final FPlayer fplayer;
    private final BufferedObjective bufferedObjective;
    private FSidebarProvider defaultProvider;
    private FSidebarProvider temporaryProvider;
    private boolean removed = false;

    private FScoreboard(FPlayer fplayer) {
        this.fplayer = fplayer;

        if (isSupportedByServer()) {
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            this.bufferedObjective = new BufferedObjective(scoreboard);

            fplayer.getPlayer().setScoreboard(scoreboard);
        } else {
            this.scoreboard = null;
            this.bufferedObjective = null;
        }
    }

    // Glowstone doesn't support scoreboards.
    // All references to this and related workarounds can be safely
    // removed when scoreboards are supported.
    public static boolean isSupportedByServer() {
        return Bukkit.getScoreboardManager() != null;
    }

    public static void init(FPlayer fplayer) {
        FScoreboard fboard = new FScoreboard(fplayer);
        fscoreboards.put(fplayer, fboard);

        if (fplayer.hasFaction()) {
            FTeamWrapper.applyUpdates(fplayer.getFaction());
        }
        FTeamWrapper.track(fboard);
    }

    public static void remove(FPlayer fplayer, Player player) {
        FScoreboard fboard = fscoreboards.remove(fplayer);

        if (fboard != null) {
            if(Bukkit.getScoreboardManager() != null) {
                if (fboard.scoreboard == player.getScoreboard()) { // No equals method implemented, so may as well skip a nullcheck
                    player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                }
            }
            fboard.removed = true;
            FTeamWrapper.untrack(fboard);
        }
    }

    public static FScoreboard get(FPlayer fplayer) {
        return fscoreboards.get(fplayer);
    }

    public static FScoreboard get(Player player) {
        return fscoreboards.get(FPlayers.getInstance().getByPlayer(player));
    }

    protected FPlayer getFPlayer() {
        return fplayer;
    }

    protected Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void setSidebarVisibility(boolean visible) {
        if (!isSupportedByServer()) {
            return;
        }

        bufferedObjective.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);
    }

    public void setDefaultSidebar(final FSidebarProvider provider) {
        if (!isSupportedByServer()) {
            return;
        }

        defaultProvider = provider;
        if (temporaryProvider == null) {
            // We have no temporary provider; update the BufferedObjective!
            updateObjective();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (removed || provider != defaultProvider) {
                    cancel();
                    return;
                }

                if (temporaryProvider == null) {
                    updateObjective();
                }
            }
        }.runTaskTimer(FactionsPlugin.getInstance(), 20, 20);
    }

    public void setTemporarySidebar(final FSidebarProvider provider) {
        if (!isSupportedByServer()) {
            return;
        }

        temporaryProvider = provider;
        updateObjective();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (removed) {
                    return;
                }

                if (temporaryProvider == provider) {
                    temporaryProvider = null;
                    updateObjective();
                }
            }
        }.runTaskLater(FactionsPlugin.getInstance(), FactionsPlugin.getInstance().getConfig().getInt("scoreboard.expiration", 7) * 20L);
    }

    private void updateObjective() {
        FSidebarProvider provider = temporaryProvider != null ? temporaryProvider : defaultProvider;

        if (provider == null) {
            bufferedObjective.hide();
        } else {
            bufferedObjective.setTitle(provider.getTitle(fplayer));
            bufferedObjective.setAllLines(provider.getLines(fplayer));
            bufferedObjective.flip();
        }
    }
}