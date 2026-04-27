package org.saberdev.corex.addons;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.Lazy;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.saberdev.corex.CoreAddon;
import org.saberdev.corex.CoreX;

@CoreAddon(configVariable = "Enemy-Spawner-Mine")
public class EnemySpawnerMine implements Listener {

    private final Lazy<Material> spawner = Lazy.of(XMaterial.SPAWNER::parseMaterial);

    @EventHandler
    public void onSpawnerMine(BlockBreakEvent e) {
        if (e.getBlock().getType() == this.spawner.get()) {
            if (!e.getPlayer().hasPermission("sabercore.spawnermine.bypass") && hasEnemiesNear(e.getPlayer(), CoreX.getConfig().fetchDouble("AntiSpawnerMine.Radius"))) {
                e.setCancelled(true);
            }
            if (e.isCancelled()) {
                e.getPlayer().sendMessage(String.valueOf(TL.ANTI_SPAWNER_MINE_PLAYERS_NEAR));
            }
        }
    }

    public boolean hasEnemiesNear(Player player, double d) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        for (Entity e : player.getNearbyEntities(d, d, d)) {
            if (e.getType() == EntityType.PLAYER) {
                Player eplayer = (Player) e;
                if (eplayer.hasMetadata("NPC")) continue;
                FPlayer efplayer = FPlayers.getInstance().getByPlayer(eplayer);
                if (efplayer == null) continue;
                if (!player.canSee(eplayer)) continue;
                if (fPlayer.getRelationTo(efplayer).equals(Relation.ENEMY)) {
                    return true;
                }
            }
        }
        return false;
    }
}