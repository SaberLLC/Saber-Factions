package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.ClaimMessageControl;
import com.massivecraft.factions.zcore.util.TL;

import java.util.HashSet;
import java.util.Set;

final class ClaimCommandUtil {

    private ClaimCommandUtil() {
    }

    static boolean shouldBatchSuccessMessages() {
        return Conf.useRadiusClaimSystem;
    }

    static boolean attemptClaim(CommandContext context, Faction forFaction, FLocation location, boolean notifyFailure, boolean batchSuccessMessages) {
        if (!batchSuccessMessages) {
            return context.fPlayer.attemptClaim(forFaction, location, notifyFailure);
        }
        return ClaimMessageControl.withoutSuccessMessages(() -> context.fPlayer.attemptClaim(forFaction, location, notifyFailure));
    }

    static void broadcastClaimSummary(CommandContext context, Faction forFaction, int claims, int chunkX, int chunkZ) {
        if (claims <= 0) {
            return;
        }

        Set<FPlayer> recipients = new HashSet<>();
        recipients.add(context.fPlayer);
        if (forFaction != null) {
            recipients.addAll(forFaction.getFPlayersWhereOnline(true));
        }

        for (FPlayer recipient : recipients) {
            recipient.msg(
                    TL.CLAIM_RADIUS_CLAIM,
                    context.fPlayer.describeTo(recipient, true),
                    Integer.toString(claims),
                    chunkX,
                    chunkZ
            );
        }
    }

    static void logClaim(Faction forFaction, FPlayer player, FLocation location) {
        FactionsPlugin.instance.logFactionEvent(
                forFaction,
                FLogType.CHUNK_CLAIMS,
                player.getName(),
                CC.GreenB + "CLAIMED",
                "1",
                location.formatXAndZ(",")
        );
    }
}
