package pw.saber.corex.listeners.mob;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pw.saber.corex.CoreX;

import java.util.List;

public class AntiMobMovement implements Listener {

    public List<String> entList = CoreX.getConfig().fetchStringList("Anti-Mob-Movement.Mob-List");

    @EventHandler(priority = EventPriority.LOW)
    public void entitySpawnEvent(EntitySpawnEvent event) {
        if (entList.isEmpty()) return;
        if (event.getEntity().getType() == EntityType.DROPPED_ITEM || event.getEntity().getType() == EntityType.PRIMED_TNT)
            return;

        if (event.getEntity() instanceof Player) return;

        if (!entList.contains(event.getEntity().getType().toString())) return;
        final LivingEntity entity = (LivingEntity) event.getEntity();
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999999, 25));
    }
}
