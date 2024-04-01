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

        registerFeature(initializedFeatures, "Anti-Baby-Zombies", new AntiBabyZombie());
        registerFeature(initializedFeatures, "Anti-Mob-Movement", new AntiMobMovement());
        registerFeature(initializedFeatures, "Anti-Mob-Targeting", new AntiMobTargeting());
        registerFeature(initializedFeatures, "Iron-Golem-Health", new IronGolemHealth());
        registerFeature(initializedFeatures, "Water-Proof-Blazes", new WaterProofBlazes());
        registerFeature(initializedFeatures, "Anti-Book-Quill-Crash", new AntiBookQuillCrash());
        registerFeature(initializedFeatures, "Anti-Death-Clip", new AntiDeathClip());
        registerFeature(initializedFeatures, "Anti-Dupe", new AntiDupe());
        registerFeature(initializedFeatures, "Anti-Nether-Roof", new AntiNetherRoof());
        registerFeature(initializedFeatures, "Anti-Piston-Glitch", new AntiPistonGlitch());
        registerFeature(initializedFeatures, "Anti-Wilderness-Spawner", new AntiWildernessSpawner());
        registerFeature(initializedFeatures, "Auto-Respawn", new AutoRespawn());
        registerFeature(initializedFeatures, "Book-Disenchant", new BookDisenchant());
        registerFeature(initializedFeatures, "Border-Patches", new BorderPatches());
        registerFeature(initializedFeatures, "Anti-Explosion-Damage", new DenyExplosionDamage());
        registerFeature(initializedFeatures, "Anti-Dragon-Egg-TP", new DragonEggAntiTP());
        registerFeature(initializedFeatures, "Enemy-Spawner-Mine", new EnemySpawnerMine());
        registerFeature(initializedFeatures, "Insta-Sponge-Break", new InstaSpongeBreak());
        registerFeature(initializedFeatures, "Anti-Natural-Mobs", new NaturalMobSpawning());
        registerFeature(initializedFeatures, "Anti-Block-Placement", new AntiBlockPlace());
        registerFeature(initializedFeatures, "Blocked-Enchantments", new BlockedEnchantments());
        if (FactionsPlugin.getInstance().version == 8)
            registerFeature(initializedFeatures, "Armor-Swap", new ArmorSwap());
        registerFeature(initializedFeatures, "No-Cursor-Drop", new NoCursorDrop());
        registerFeature(initializedFeatures, "Anti-Nether-Portal", new AntiNetherPortal());
        registerFeature(initializedFeatures, "Anti-End-Portal", new AntiEndPortal());
        registerFeature(initializedFeatures, "EnderPearl-Cooldown", new EnderPearlCooldown());
        registerFeature(initializedFeatures, "Anti-Vehicle-Teleport", new AntiVehicleTeleport());
        registerFeature(initializedFeatures, "God-Apple-Cooldown", new GappleCooldown());
        registerFeature(initializedFeatures, "Anti-Boat-Placement", new AntiBoatPlacement());
        registerFeature(initializedFeatures, "Anti-Minecart-Placement", new AntiMinecartPlacement());
        registerFeature(initializedFeatures, "Anti-Bow-Boosting", new AntiBowBoosting());
        registerFeature(initializedFeatures, "Global-Gamemode", new GlobalGamemode());
        registerFeature(initializedFeatures, "Enchanting-Lapis", new AutoLapisEnchant());
        registerFeature(initializedFeatures, "Anti-Chicken", new AntiChicken());
        registerFeature(initializedFeatures, "Anti-Natural-Spawn-Faction", new AntiMobFactionTerritory());
        registerFeature(initializedFeatures, "Anti-Redstone-Trapdoor-Crash", new AntiRedstoneOnTrapdoorCrash());

        if (!initializedFeatures.isEmpty()) {
            Logger.print("Enabling " + initializedFeatures.size() + " CoreX Features...", Logger.PrefixType.DEFAULT);
            for (Listener eventListener : initializedFeatures) {
                FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(eventListener, FactionsPlugin.getInstance());
            }
        }
    }

    private static void registerFeature(List<Listener> initializedFeatures, String featureName, Listener listener) {
        if (handleFeatureRegistry(featureName)) {
            initializedFeatures.add(listener);
        }
    }

    public static boolean handleFeatureRegistry(String key) {
        return getConfig().fetchBoolean("Features." + key);
    }
}
