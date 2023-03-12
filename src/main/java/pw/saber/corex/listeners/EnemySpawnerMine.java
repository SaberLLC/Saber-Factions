package pw.saber.corex.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pw.saber.corex.CoreX;

public class EnemySpawnerMine implements Listener {
    @EventHandler
    public void onSpawnerMine(BlockBreakEvent e) {
        if (!e.getBlock().getType().equals(XMaterial.SPAWNER.parseMaterial())) return;

        if (hasEnemiesNear(e.getPlayer(), CoreX.getConfig().fetchDouble("AntiSpawnerMine.Radius")) && !e.getPlayer().hasPermission("sabercore.spawnermine,bypass")) {
            e.setCancelled(true);
        }
        if (e.isCancelled()) {
            e.getPlayer().sendMessage(String.valueOf(TL.ANTI_SPAWNER_MINE_PLAYERS_NEAR));
        }
    }

    public boolean hasEnemiesNear(Player player, double d) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        for (Entity e : player.getNearbyEntities(d, d, d)) {
            if (e instanceof Player) {
                Player eplayer = (((Player) e).getPlayer());
                if (eplayer == null) continue;
                if (eplayer.hasMetadata("NPC")) continue;
                FPlayer efplayer = FPlayers.getInstance().getByPlayer(eplayer);
                if (efplayer == null) continue;
                if (!player.canSee(eplayer) || efplayer.isVanished()) continue;
                if (fPlayer.getRelationTo(efplayer).equals(Relation.ENEMY)) {
                    return true;
                }
            }
        }
        return false;
    }
}
