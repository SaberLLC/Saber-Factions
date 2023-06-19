package com.massivecraft.factions.listeners;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;
import java.util.UnknownFormatConversionException;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FactionsChatListener implements Listener {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    private static final Pattern CHAT_PATTERN = Pattern.compile("(?s).");

    // this is for handling slashless command usage and faction/alliance chat, set at lowest priority so Factions gets to them first
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerEarlyChat(AsyncPlayerChatEvent event) {
        Player talkingPlayer = event.getPlayer();
        String msg = event.getMessage();
        FPlayer me = FPlayers.getInstance().getByPlayer(talkingPlayer);
        ChatMode chat = me.getChatMode();
        Faction myFaction = me.getFaction();
        String nameAndTag = ChatColor.stripColor(me.getNameAndTag());

        if (me.isEnteringPassword()) {
            event.setCancelled(true);
            String censoredMessage = ChatColor.DARK_GRAY + CHAT_PATTERN.matcher(msg).replaceAll("*");
            me.sendMessage(censoredMessage);

            if (myFaction.isWarpPassword(me.getEnteringWarp(), msg)) {
                doWarmup(me.getEnteringWarp(), me);
            } else {
                me.msg(TL.COMMAND_FWARP_INVALID_PASSWORD);
            }

            me.setEnteringPassword(false, "");
            return;
        }

        if (chat == ChatMode.MOD && me.getRole().isAtLeast(Role.MODERATOR)) {
            String modMessage = String.format(Conf.modChatFormat, nameAndTag, msg);
            Collection<FPlayer> modPlayers = myFaction.getFPlayers().stream()
                    .filter(fplayer -> fplayer.getRole().isAtLeast(Role.MODERATOR))
                    .collect(Collectors.toList());

            for (FPlayer fplayer : modPlayers) {
                fplayer.sendMessage(modMessage);
            }

            Collection<FPlayer> spyingPlayers = myFaction.getFPlayers().stream()
                    .filter(fplayer -> fplayer.isSpyingChat() && me != fplayer)
                    .collect(Collectors.toList());

            for (FPlayer fplayer : spyingPlayers) {
                fplayer.sendMessage("[MCspy]: " + modMessage);
            }

            Bukkit.getLogger().log(Level.INFO, "Mod Chat: " + modMessage);
            event.setCancelled(true);
        } else if (chat == ChatMode.FACTION) {
            String factionMessage = String.format(Conf.factionChatFormat, me.describeTo(myFaction), msg);
            myFaction.sendMessage(factionMessage);

            Collection<FPlayer> spyingPlayers = FPlayers.getInstance().getOnlinePlayers().stream()
                    .filter(fplayer -> fplayer.isSpyingChat() && fplayer.getFaction() != myFaction && me != fplayer)
                    .collect(Collectors.toList());

            for (FPlayer fplayer : spyingPlayers) {
                fplayer.sendMessage("[FCspy] " + myFaction.getTag() + ": " + factionMessage);
            }

            Bukkit.getLogger().log(Level.INFO, "FactionChat " + myFaction.getTag() + ": " + factionMessage);
            event.setCancelled(true);
        } else if (chat == ChatMode.ALLIANCE) {
            String allianceMessage = String.format(Conf.allianceChatFormat, nameAndTag, msg);
            myFaction.sendMessage(allianceMessage);

            Collection<FPlayer> alliancePlayers = FPlayers.getInstance().getOnlinePlayers().stream()
                    .filter(fplayer -> myFaction.getRelationTo(fplayer) == Relation.ALLY && !fplayer.isIgnoreAllianceChat())
                    .collect(Collectors.toList());

            for (FPlayer fplayer : alliancePlayers) {
                fplayer.sendMessage(allianceMessage);
            }

            Collection<FPlayer> spyingPlayers = FPlayers.getInstance().getOnlinePlayers().stream()
                    .filter(fplayer -> fplayer.isSpyingChat() && me != fplayer)
                    .collect(Collectors.toList());

            for (FPlayer fplayer : spyingPlayers) {
                fplayer.sendMessage("[ACspy]: " + allianceMessage);
            }

            Bukkit.getLogger().log(Level.INFO, "AllianceChat: " + allianceMessage);
            event.setCancelled(true);
        } else if (chat == ChatMode.TRUCE) {
            String truceMessage = String.format(Conf.truceChatFormat, nameAndTag, msg);
            myFaction.sendMessage(truceMessage);

            Collection<FPlayer> trucePlayers = FPlayers.getInstance().getOnlinePlayers().stream()
                    .filter(fplayer -> myFaction.getRelationTo(fplayer) == Relation.TRUCE)
                    .collect(Collectors.toList());

            for (FPlayer fplayer : trucePlayers) {
                fplayer.sendMessage(truceMessage);
            }

            Collection<FPlayer> spyingPlayers = FPlayers.getInstance().getOnlinePlayers().stream()
                    .filter(fplayer -> fplayer.isSpyingChat() && fplayer != me)
                    .collect(Collectors.toList());

            for (FPlayer fplayer : spyingPlayers) {
                fplayer.sendMessage("[TCspy]: " + truceMessage);
            }

            Bukkit.getLogger().log(Level.INFO, "TruceChat: " + truceMessage);
            event.setCancelled(true);
        }
    }


    // this is for handling insertion of the player's faction tag, set at highest priority to give other plugins a chance to modify chat first
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // Are we to insert the Faction tag into the format?
        // If we are not to insert it - we are done.

        if (!Conf.chatTagEnabled || Conf.chatTagHandledByAnotherPlugin) {
            return;
        }

        Player talkingPlayer = event.getPlayer();
        String msg = event.getMessage();
        String eventFormat = event.getFormat();
        FPlayer me = FPlayers.getInstance().getByPlayer(talkingPlayer);
        int insertIndex;

        if (!Conf.chatTagReplaceString.isEmpty() && eventFormat.contains(Conf.chatTagReplaceString)) {
            // we're using the "replace" method of inserting the faction tags
            eventFormat = TextUtil.replace(eventFormat, "[FACTION_TITLE]", me.getTitle());

            insertIndex = eventFormat.indexOf(Conf.chatTagReplaceString);
            eventFormat = TextUtil.replace(eventFormat, Conf.chatTagReplaceString, "");
            Conf.chatTagPadAfter = false;
            Conf.chatTagPadBefore = false;
        } else if (!Conf.chatTagInsertAfterString.isEmpty() && eventFormat.contains(Conf.chatTagInsertAfterString)) {
            // we're using the "insert after string" method
            insertIndex = eventFormat.indexOf(Conf.chatTagInsertAfterString) + Conf.chatTagInsertAfterString.length();
        } else if (!Conf.chatTagInsertBeforeString.isEmpty() && eventFormat.contains(Conf.chatTagInsertBeforeString)) {
            // we're using the "insert before string" method
            insertIndex = eventFormat.indexOf(Conf.chatTagInsertBeforeString);
        } else {
            // we'll fall back to using the index place method
            insertIndex = Conf.chatTagInsertIndex;
            if (insertIndex > eventFormat.length()) {
                return;
            }
        }

        String formatStart = eventFormat.substring(0, insertIndex) + ((Conf.chatTagPadBefore && !me.getChatTag().isEmpty()) ? " " : "");
        String formatEnd = ((Conf.chatTagPadAfter && !me.getChatTag().isEmpty()) ? " " : "") + eventFormat.substring(insertIndex);

        String nonColoredMsgFormat = formatStart + me.getChatTag().trim() + formatEnd;

        // Relation Colored?
        if (Conf.chatTagRelationColored) {
            for (Player listeningPlayer : event.getRecipients()) {
                FPlayer you = FPlayers.getInstance().getByPlayer(listeningPlayer);
                String yourFormat = formatStart + me.getChatTag(you).trim() + formatEnd;
                try {
                    listeningPlayer.sendMessage(String.format(yourFormat, talkingPlayer.getDisplayName(), msg));
                } catch (UnknownFormatConversionException ex) {
                    Conf.chatTagInsertIndex = 0;
                    Logger.print( "Critical error in chat message formatting!", Logger.PrefixType.FAILED);
                    Logger.print( "NOTE: This has been automatically fixed right now by setting chatTagInsertIndex to 0.", Logger.PrefixType.FAILED);
                    Logger.print( "For a more proper fix, please read this regarding chat configuration: http://massivecraft.com/plugins/factions/config#Chat_configuration", Logger.PrefixType.FAILED);
                    return;
                }
            }

            // Messages are sent to players individually
            // This still leaves a chance for other plugins to pick it up
            event.getRecipients().clear();
        }
        // Message with no relation color.
        event.setFormat(nonColoredMsgFormat);
    }

    private void doWarmup(final String warp, final FPlayer fme) {
        WarmUpUtil.process(fme, WarmUpUtil.Warmup.WARP, TL.WARMUPS_NOTIFY_TELEPORT, warp, () -> {
            Player player = Bukkit.getPlayer(fme.getPlayer().getUniqueId());
            if (player != null) {
                player.teleport(fme.getFaction().getWarp(warp).getLocation());
                fme.msg(TL.COMMAND_FWARP_WARPED, warp);
            }
        }, FactionsPlugin.getInstance().getConfig().getLong("warmups.f-warp", 10));
    }

}