package com.massivecraft.factions.zcore;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.listeners.FactionsPlayerListener;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class MPluginSecretPlayerListener implements Listener {

    private MPlugin p;

    public MPluginSecretPlayerListener(MPlugin p) {
        this.p = p;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (FactionsPlayerListener.preventCommand(event.getMessage(), event.getPlayer())) {
            if (p.logPlayerCommands()) {
                Bukkit.getLogger().info("[PLAYER_COMMAND] " + event.getPlayer().getName() + ": " + event.getMessage());
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (p.handleCommand(event.getPlayer(), event.getMessage(), false, true)) {
            if (p.logPlayerCommands()) {
                Bukkit.getLogger().info("[PLAYER_COMMAND] " + event.getPlayer().getName() + ": " + event.getMessage());
            }
            event.setCancelled(true);
        }

        /* Should be handled by stuff in FactionsChatListener
        Player speaker = event.getPlayer();
        String format = event.getFormat();
        format = format.replace(Conf.chatTagReplaceString, FactionsPlugin.getInstance().getPlayerFactionTag(speaker)).replace("[FACTION_TITLE]", FactionsPlugin.getInstance().getPlayerTitle(speaker));
        event.setFormat(format);
        */
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPreLogin(PlayerLoginEvent event) {
        if (Conf.usePreStartupKickSystem && !FactionsPlugin.canPlayersJoin()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CC.translate(TL.PRE_JOIN_KICK_MESSAGE.toString()));
            return;
        }
        if (!FactionsPlugin.startupFinished) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server still starting.. try again in a moment.");
            return;
        }
        FPlayers.getInstance().getByPlayer(event.getPlayer());
    }
}
