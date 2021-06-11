package pw.saber.corex.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pw.saber.corex.CoreX;

import java.util.List;

public class EnemySpawnerMine implements Listener {
    @EventHandler
    public void onSpawnerMine(BlockBreakEvent e) {
        if (!e.getBlock().getType().equals(XMaterial.SPAWNER.parseMaterial())) return;

        if (nearbyEnemies(e.getPlayer(), CoreX.getConfig().fetchDouble("AntiSpawnerMine.Radius"), null) && !e.getPlayer().hasPermission("sabercore.spawnermine,bypass")) {
            e.setCancelled(true);
        }
        if (e.isCancelled()) {
            e.getPlayer().sendMessage(String.valueOf(TL.ANTI_SPAWNER_MINE_PLAYERS_NEAR));
        }
    }

    public boolean nearbyEnemies(Player player, double d, String str) {
        List<Entity> nearbyEntities = player.getNearbyEntities(d, d, d);
        FPlayer byPlayer = FPlayers.getInstance().getByPlayer(player);
        for (Entity entity : nearbyEntities) {
            if ((entity instanceof Player) && (str == null || entity.hasPermission(str))) {
                FPlayer byPlayer2 = FPlayers.getInstance().getByPlayer((Player) entity);
                if (!(byPlayer2.getFactionId().equals(byPlayer.getFactionId()) || byPlayer2.getFaction().getRelationWish(byPlayer.getFaction()).isAlly())) {
                    return true;
                }
            }
        }
        return false;
    }
}
