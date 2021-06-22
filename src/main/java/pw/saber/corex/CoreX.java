package pw.saber.corex;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.file.CustomFile;
import pw.saber.corex.listeners.*;
import pw.saber.corex.listeners.mob.*;

import java.util.logging.Level;

public class CoreX {

    public static CustomFile getConfig() {
        return FactionsPlugin.getInstance().getFileManager().getCoreX();
    }

    public static void init() {
        FactionsPlugin.getInstance().log(Level.INFO, "CoreX Integration Starting!");
        if (getConfig().fetchBoolean("Features.Anti-Baby-Zombies")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiBabyZombie(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Anti-Mob-Movement")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiMobMovement(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Anti-Mob-Targeting")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiMobTargeting(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Iron-Golem-Health")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new IronGolemHealth(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Water-Proof-Blazes")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new WaterProofBlazes(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Anti-Book-Quill-Crash")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiBookQuillCrash(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Anti-Cobble-Monster")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiCobbleMonster(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Anti-Death-Clip")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiDeathClip(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Anti-Dupe")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiDupe(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Anti-Nether-Roof")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiNetherRoof(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Anti-Piston-Glitch")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiPistonGlitch(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Anti-Wilderness-Spawner")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiWildernessSpawner(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Auto-Respawn")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AutoRespawn(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Book-Disenchant")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new BookDisenchant(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Border-Patches")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new BorderPatches(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Anti-Explosion-Damage")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new DenyExplosionDamage(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Anti-Dragon-Egg-TP")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new DragonEggAntiTP(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Enemy-Spawner-Mine")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new EnemySpawnerMine(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Insta-Sponge-Break")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new InstaSpongeBreak(), FactionsPlugin.getInstance());
        }

        if (getConfig().fetchBoolean("Features.Anti-Natural-Mobs")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new NaturalMobSpawning(), FactionsPlugin.getInstance());
        }

        if(getConfig().fetchBoolean("Features.Anti-Block-Placement")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiBlockPlace(), FactionsPlugin.getInstance());
        }

        if(getConfig().fetchBoolean("Features.Blocked-Enchantments")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new BlockedEnchantments(), FactionsPlugin.getInstance());
        }

        if(getConfig().fetchBoolean("Features.Armor-Swap")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new ArmorSwap(), FactionsPlugin.getInstance());
        }

        if(getConfig().fetchBoolean("Features.No-Cursor-Drop")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new NoCursorDrop(), FactionsPlugin.getInstance());
        }

        if(getConfig().fetchBoolean("Features.Anti-Nether-Portal")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiNetherPortal(), FactionsPlugin.getInstance());
        }

        if(getConfig().fetchBoolean("Features.Anti-End-Portal")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiEndPortal(), FactionsPlugin.getInstance());
        }
    }
}
