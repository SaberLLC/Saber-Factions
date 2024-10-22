package com.massivecraft.factions.util;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.icon.ItemStackIcon;
import com.lunarclient.apollo.module.cooldown.CooldownModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import com.lunarclient.apollo.recipients.Recipients;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 4/6/2020
 */
public class Cooldown {

    private static final long MILLIS_IN_SECOND = TimeUnit.SECONDS.toMillis(1);

    private static com.lunarclient.apollo.module.cooldown.Cooldown buildCooldown(String name, int seconds, Material apolloPreview) {
        if (!Conf.enableApolloIntegration || apolloPreview == null)
            return null;

        return com.lunarclient.apollo.module.cooldown.Cooldown.builder()
                .name(name)
                .duration(Duration.ofSeconds(seconds))
                .icon(ItemStackIcon.builder().itemName(apolloPreview.name()).build())
                .build();
    }


    private static void setCooldown(Player player, String name, int seconds) {
        player.setMetadata(name, new FixedMetadataValue(FactionsPlugin.getInstance(), System.currentTimeMillis() + seconds * MILLIS_IN_SECOND));
    }

    public static void setCooldown(Player player, String name, int seconds, Material apolloPreview) {
        setCooldown(player, name, seconds);
        final com.lunarclient.apollo.module.cooldown.Cooldown cooldown = buildCooldown(name, seconds, apolloPreview);
        if (cooldown == null) return;
        final CooldownModule cooldownModule = Apollo.getModuleManager().getModule(CooldownModule.class);
        Optional<ApolloPlayer> apolloPlayerOpt = Apollo.getPlayerManager().getPlayer(player.getUniqueId());
        if (!apolloPlayerOpt.isPresent()) return;

        cooldownModule.displayCooldown(apolloPlayerOpt.get(), cooldown);
    }

    public static void setCooldown(Faction faction, String name, int seconds, Material apolloPreview) {
        Set<FPlayer> appliedToPlayers = setCooldown(faction, name, seconds);

        if (!Conf.enableApolloIntegration || apolloPreview == null)
            return;

        final CooldownModule cooldownModule = Apollo.getModuleManager().getModule(CooldownModule.class);
        if (cooldownModule == null) return;

        com.lunarclient.apollo.module.cooldown.Cooldown cooldown = com.lunarclient.apollo.module.cooldown.Cooldown.builder()
                .name(name)
                .duration(Duration.ofSeconds(seconds))
                .icon(ItemStackIcon.builder().itemName(apolloPreview.name()).build())
                .build();

        Set<UUID> receiversIds = appliedToPlayers.stream().map(FPlayer::getPlayer).map(Player::getUniqueId).collect(Collectors.toSet());
        Recipients cooldownReceivers = Recipients.of(
                Apollo.getPlayerManager().getPlayers().stream()
                        .filter(apolloPlayers -> receiversIds.contains(apolloPlayers.getUniqueId()))
                        .collect(Collectors.toList()));
        cooldownModule.displayCooldown(cooldownReceivers, cooldown);
    }

    /**
     * Set cooldown for all online players in a faction
     *
     * @param fac     Faction
     * @param name    Cooldown name
     * @param seconds Cooldown in seconds
     * @return Set of FPlayers that were set on cooldown
     */
    private static Set<FPlayer> setCooldown(Faction fac, String name, int seconds) {
        long expiration = System.currentTimeMillis() + seconds * MILLIS_IN_SECOND;
        Set<FPlayer> fPlayers = new HashSet<>();
        for (FPlayer fPlayer : fac.getFPlayersWhereOnline(true)) {
            Player player = fPlayer.getPlayer();
            if (player == null) continue;
            player.setMetadata(name, new FixedMetadataValue(FactionsPlugin.getInstance(), expiration));
            fPlayers.add(fPlayer);
        }
        return fPlayers;
    }

    public static String sendCooldownLeft(Player player, String name) {
        List<MetadataValue> values = player.getMetadata(name);
        if (values.isEmpty()) return "";

        long remaining = values.get(0).asLong() - System.currentTimeMillis();
        int remainSec = (int) (remaining / MILLIS_IN_SECOND);
        return TimeUtil.formatSeconds(remainSec);
    }

    public static boolean isOnCooldown(Player player, String name) {
        List<MetadataValue> values = player.getMetadata(name);
        if (values.isEmpty()) return false;

        long time = values.get(0).asLong();
        return time > System.currentTimeMillis();
    }
}