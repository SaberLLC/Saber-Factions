package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;


public class UtilFly {
    /**
     * UtilFly is being removed very soon as all of its functionality has been updated and moved to CmdFly
     */
    @Deprecated
    public static void run() {

        Bukkit.getScheduler().scheduleSyncRepeatingTask(FactionsPlugin.getInstance(), () -> {
            for (FPlayer fp : FPlayers.getInstance().getOnlinePlayers()) {
                if (fp.isFlying())
                    fp.checkIfNearbyEnemies();
            }
        }, 0, FactionsPlugin.getInstance().getConfig().getInt("fly-task-interval", 10));
    }

    @Deprecated
    public static void setFly(FPlayer fp, boolean fly, boolean silent, boolean damage) {

        fp.getPlayer().setAllowFlight(fly);
        fp.getPlayer().setFlying(fly);
        fp.setFlying(fly);


        if (!silent) {
            if (!damage) {
                fp.msg(TL.COMMAND_FLY_CHANGE, fly ? "enabled" : "disabled");
            } else {
                fp.msg(TL.COMMAND_FLY_DAMAGE);
            }
        }

        setFallDamage(fp, fly, damage);
    }

    @Deprecated
    public static void checkFly(FPlayer me, Faction factionTo) {
        if (!FactionsPlugin.getInstance().getConfig().getBoolean("enable-faction-flight"))
            return;

        if (me.isAdminBypassing() && me.isFlying() && me.getPlayer().hasPermission("factions.fly.enemy-bypass"))
            return;

        if (!me.isFlying()) {
            if (me.isAdminBypassing() || me.getPlayer().hasPermission("factions.fly.enemy-bypass")) {
                UtilFly.setFly(me, true, true, false);
                return;
            }

            if (factionTo == me.getFaction() && me.getPlayer().hasPermission("factions.fly")) {
                UtilFly.setFly(me, true, false, false);
            } else {
                Relation relationTo = factionTo.getRelationTo(me);
                if ((factionTo.isWilderness() && me.canflyinWilderness()) || (factionTo.isWarZone() && me.canflyinWarzone())
                        || (factionTo.isSafeZone() && me.canflyinSafezone()) || (relationTo == Relation.ENEMY && me.canflyinEnemy())
                        || (relationTo == Relation.ALLY && me.canflyinAlly()) || (relationTo == Relation.TRUCE && me.canflyinTruce())
                        || (relationTo == Relation.NEUTRAL && me.canflyinNeutral())) {
                    UtilFly.setFly(me, true, false, false);
                }
            }
        } else {
            Relation relationTo = factionTo.getRelationTo(me);
            if ((factionTo.equals(me.getFaction()) && !me.getPlayer().hasPermission("factions.fly"))
                    || (factionTo.isWilderness() && !me.canflyinWilderness()) || (factionTo.isWarZone() && !me.canflyinWarzone())
                    || (factionTo.isSafeZone() && !me.canflyinSafezone()) || (relationTo == Relation.ENEMY && !me.canflyinEnemy())
                    || (relationTo == Relation.ALLY && !me.canflyinAlly()) || (relationTo == Relation.TRUCE && !me.canflyinTruce())
                    || (relationTo == Relation.NEUTRAL && !me.canflyinNeutral()) || !me.isVanished()) {
                UtilFly.setFly(me, false, false, false);
            }
        }
    }

    public static void setFallDamage(FPlayer fp, boolean fly, boolean damage) {
        if (!fly) {
            if (!damage) {
                fp.sendMessage(TL.COMMAND_FLY_COOLDOWN.toString().replace("{amount}", FactionsPlugin.getInstance().getConfig().getInt("fly-falldamage-cooldown", 3) + ""));
            }

            int cooldown = FactionsPlugin.getInstance().getConfig().getInt("fly-falldamage-cooldown", 3);
            if (cooldown > 0) {
                fp.setTakeFallDamage(false);
                Bukkit.getScheduler().runTaskLater(FactionsPlugin.getInstance(), () -> fp.setTakeFallDamage(true), 20L * cooldown);
            }
        }
    }
}



