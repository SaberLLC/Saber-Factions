package org.saberdev.corex.addons;

import com.cryptomorin.xseries.XEntityType;
import com.cryptomorin.xseries.XPotion;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.saberdev.corex.CoreAddon;
import org.saberdev.corex.CoreX;

import java.util.List;

@CoreAddon(configVariable = "Anti-Mob-Movement")
public class AntiMobMovement implements Listener {

    public List<String> entList = CoreX.getConfig().fetchStringList("Anti-Mob-Movement.Mob-List");

    @EventHandler(priority = EventPriority.LOW)
    public void entitySpawnEvent(EntitySpawnEvent event) {
        if (entList.isEmpty()) return;
        EntityType type = event.getEntityType();
        if (type == EntityType.PLAYER || type == XEntityType.ITEM.get() || type == XEntityType.TNT.get()) {
            return;
        }
        if (!entList.contains(type.name())) {
            return;
        }
        LivingEntity entity = (LivingEntity) event.getEntity();
        PotionEffect potionEffect = XPotion.SLOWNESS.buildPotionEffect(Integer.MAX_VALUE, 25);
        if (potionEffect != null) {
            entity.addPotionEffect(potionEffect);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.getEntity() instanceof Creeper) {
            Creeper creeper = (Creeper) e.getEntity();
            removeAllPotionEffects(creeper);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (entList.contains(e.getEntity().getType().name())) {
            removeAllPotionEffects(e.getEntity());
        }
    }

    private void removeAllPotionEffects(LivingEntity entity) {
        for (PotionEffect activePotionEffect : entity.getActivePotionEffects()) {
            entity.removePotionEffect(activePotionEffect.getType());
        }
    }
}