package org.saberdev.corex;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.file.CustomFile;
import org.bukkit.event.Listener;
import org.saberdev.corex.listeners.*;
import org.saberdev.corex.listeners.mob.*;

import java.util.ArrayList;
import java.util.List;


public class CoreX {

    public static CustomFile getConfig() {
        return FactionsPlugin.getInstance().getFileManager().getCoreX();
    }

    public static String pluginName() {
        return "SaberFactions";
    }

    public static void init() {
        Logger.print("CoreX Integration Starting!", Logger.PrefixType.DEFAULT);

        List<Listener> initializedFeatures = new ArrayList<>();

        if (handleFeatureRegistry("Anti-Baby-Zombies")) {
            initializedFeatures.add(new AntiBabyZombie());
        }

        if (handleFeatureRegistry("Anti-Mob-Movement")) {
            initializedFeatures.add(new AntiMobMovement());
        }

        if (handleFeatureRegistry("Anti-Mob-Targeting")) {
            initializedFeatures.add(new AntiMobTargeting());
        }

        if (handleFeatureRegistry("Iron-Golem-Health")) {
            initializedFeatures.add(new IronGolemHealth());
        }

        if (handleFeatureRegistry("Water-Proof-Blazes")) {
            initializedFeatures.add(new WaterProofBlazes());
        }

        if (handleFeatureRegistry("Anti-Book-Quill-Crash")) {
            initializedFeatures.add(new AntiBookQuillCrash());
        }

        if (handleFeatureRegistry("Anti-Death-Clip")) {
            initializedFeatures.add(new AntiDeathClip());
        }

        if (handleFeatureRegistry("Anti-Dupe")) {
            initializedFeatures.add(new AntiDupe());
        }

        if (handleFeatureRegistry("Anti-Nether-Roof")) {
            initializedFeatures.add(new AntiNetherRoof());
        }

        if (handleFeatureRegistry("Anti-Piston-Glitch")) {
            initializedFeatures.add(new AntiPistonGlitch());
        }

        if (handleFeatureRegistry("Anti-Wilderness-Spawner")) {
            initializedFeatures.add(new AntiWildernessSpawner());
        }

        if (handleFeatureRegistry("Auto-Respawn")) {
            initializedFeatures.add(new AutoRespawn());
        }

        if (handleFeatureRegistry("Book-Disenchant")) {
            initializedFeatures.add(new BookDisenchant());
        }

        if (handleFeatureRegistry("Border-Patches")) {
            initializedFeatures.add(new BorderPatches());
        }

        if (handleFeatureRegistry("Anti-Explosion-Damage")) {
            initializedFeatures.add(new DenyExplosionDamage());
        }

        if (handleFeatureRegistry("Anti-Dragon-Egg-TP")) {
            initializedFeatures.add(new DragonEggAntiTP());
        }

        if (handleFeatureRegistry("Enemy-Spawner-Mine")) {
            initializedFeatures.add(new EnemySpawnerMine());
        }

        if (handleFeatureRegistry("Insta-Sponge-Break")) {
            initializedFeatures.add(new InstaSpongeBreak());
        }

        if (handleFeatureRegistry("Anti-Natural-Mobs")) {
            initializedFeatures.add(new NaturalMobSpawning());
        }

        if (handleFeatureRegistry("Anti-Block-Placement")) {
            initializedFeatures.add(new AntiBlockPlace());
        }

        if (handleFeatureRegistry("Blocked-Enchantments")) {
            initializedFeatures.add(new BlockedEnchantments());
        }

        if (handleFeatureRegistry("Armor-Swap") && FactionsPlugin.getInstance().version == 8) {
            initializedFeatures.add(new ArmorSwap());
        }

        if (handleFeatureRegistry("No-Cursor-Drop")) {
            initializedFeatures.add(new NoCursorDrop());
        }

        if (handleFeatureRegistry("Anti-Nether-Portal")) {
            initializedFeatures.add(new AntiNetherPortal());
        }

        if (handleFeatureRegistry("Anti-End-Portal")) {
            initializedFeatures.add(new AntiEndPortal());
        }

        if (handleFeatureRegistry("EnderPearl-Cooldown")) {
            initializedFeatures.add(new EnderPearlCooldown());
        }

        if (handleFeatureRegistry("Anti-Vehicle-Teleport")) {
            initializedFeatures.add(new AntiVehicleTeleport());
        }

        if (handleFeatureRegistry("God-Apple-Cooldown")) {
            initializedFeatures.add(new GappleCooldown());
        }

        if (handleFeatureRegistry("Anti-Boat-Placement")) {
            initializedFeatures.add(new AntiBoatPlacement());
        }

        if (handleFeatureRegistry("Anti-Minecart-Placement")) {
            initializedFeatures.add(new AntiMinecartPlacement());
        }

        if (handleFeatureRegistry("Anti-Bow-Boosting")) {
            initializedFeatures.add(new AntiBowBoosting());
        }

        if (handleFeatureRegistry("Global-Gamemode")) {
            initializedFeatures.add(new GlobalGamemode());
        }

        if (handleFeatureRegistry("Enchanting-Lapis")) {
            initializedFeatures.add(new AutoLapisEnchant());
        }

        if (handleFeatureRegistry("Anti-Chicken")) {
            initializedFeatures.add(new AntiChicken());
        }

        if (handleFeatureRegistry("Anti-Natural-Spawn-Faction")) {
            initializedFeatures.add(new AntiMobFactionTerritory());
        }

        if (handleFeatureRegistry("Anti-Redstone-Trapdoor-Crash")) {
            initializedFeatures.add(new AntiRedstoneOnTrapdoorCrash());
        }

        //if(getConfig().fetchBoolean("Features.Use-Chunkbusters")) {
        //    FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new ChunkBusterListener(), FactionsPlugin.getInstance());
        //    FactionsPlugin.getInstance().getCommand("chunkbuster").setExecutor(new CommandChunkbuster());
        //}

        if (!initializedFeatures.isEmpty()) {
            Logger.print("Enabling " + initializedFeatures.size() + " CoreX Features...", Logger.PrefixType.DEFAULT);
            for (Listener eventListener : initializedFeatures) {
                FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(eventListener, FactionsPlugin.getInstance());
            }
        }
    }


    public static boolean handleFeatureRegistry(String key) {
        return getConfig().fetchBoolean("Features." + key);
    }
}
