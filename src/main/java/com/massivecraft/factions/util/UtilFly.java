package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.SavageFactions;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Iterator;

public class UtilFly {

    public static ArrayList<FPlayer> playersFlying;

    static {
        playersFlying = SavageFactions.playersFlying;
    }

    public UtilFly() {
    }

    public static void run() {
        if (SavageFactions.plugin.getConfig().getBoolean("enable-faction-flight")) {
            playersFlying.clear();
            Bukkit.getScheduler().scheduleSyncRepeatingTask(SavageFactions.plugin, new Runnable() {
                public void run() {
                    Iterator var2 = UtilFly.playersFlying.iterator();

                    while (var2.hasNext()) {
                        FPlayer fp = (FPlayer) var2.next();
                        if (fp != null) {
                            fp.checkIfNearbyEnemies();
                        }
                    }

                }
            }, 0L, (long) SavageFactions.plugin.getConfig().getInt("fly-task-interval", 10));
        }
    }

    public static void setFly(FPlayer fp, boolean fly, boolean silent, boolean damage) {
        if (SavageFactions.plugin.getConfig().getBoolean("enable-faction-flight")) {
            fp.getPlayer().setAllowFlight(fly);
            fp.getPlayer().setFlying(fly);
            fp.setFlying(fly);
            if (fly) {
                playersFlying.add(fp);
            } else {
                playersFlying.remove(fp);
            }

            if (!silent) {
                if (!damage) {
                    fp.msg(TL.COMMAND_FLY_CHANGE, fly ? "enabled" : "disabled");
                } else {
                    fp.msg(TL.COMMAND_FLY_DAMAGE);
                }
            }

            setFallDamage(fp, fly, damage);
        }
    }

    public static void checkFly(FPlayer me, Faction factionTo) {
        if (SavageFactions.plugin.getConfig().getBoolean("enable-faction-flight")) {
            if (!me.isAdminBypassing() || !me.isFlying()) {
                Relation relationTo;
                if (!me.isFlying()) {
                    if (me.isAdminBypassing()) {
                        setFly(me, true, false, false);
                        return;
                    }

                    if (factionTo == me.getFaction() && me.getPlayer().hasPermission("factions.fly")) {
                        setFly(me, true, false, false);
                    } else {
                        relationTo = factionTo.getRelationTo(me);
                        if (factionTo.isWilderness() && me.canflyinWilderness() || factionTo.isWarZone() && me.canflyinWarzone() || factionTo.isSafeZone() && me.canflyinSafezone() || relationTo == Relation.ENEMY && me.canflyinEnemy() || relationTo == Relation.ALLY && me.canflyinAlly() || relationTo == Relation.TRUCE && me.canflyinTruce() || relationTo == Relation.NEUTRAL && me.canflyinNeutral()) {
                            setFly(me, true, false, false);
                        }
                    }
                } else {
                    relationTo = factionTo.getRelationTo(me);
                    if (factionTo.equals(me.getFaction()) && !me.getPlayer().hasPermission("factions.fly") || factionTo.isWilderness() && !me.canflyinWilderness() || factionTo.isWarZone() && !me.canflyinWarzone() || factionTo.isSafeZone() && !me.canflyinSafezone() || relationTo == Relation.ENEMY && !me.canflyinEnemy() || relationTo == Relation.ALLY && !me.canflyinAlly() || relationTo == Relation.TRUCE && !me.canflyinTruce() || relationTo == Relation.NEUTRAL && !me.canflyinNeutral()) {
                        setFly(me, false, false, false);
                    }
                }

            }
        }
    }

    public static void setFallDamage(final FPlayer fp, boolean fly, boolean damage) {
        if (!fly) {
            if (!damage) {
                fp.sendMessage(TL.COMMAND_FLY_COOLDOWN.toString().replace("{amount}", String.valueOf(SavageFactions.plugin.getConfig().getInt("fly-falldamage-cooldown", 3))));
            }

            int cooldown = SavageFactions.plugin.getConfig().getInt("fly-falldamage-cooldown", 3);
            if (cooldown > 0) {
                fp.setTakeFallDamage(false);
                Bukkit.getScheduler().runTaskLater(SavageFactions.plugin, new Runnable() {
                    public void run() {
                        fp.setTakeFallDamage(true);
                    }
                }, 20L * (long) cooldown);
            }
        }

    }
}

