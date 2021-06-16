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
        if(getConfig().fetchBoolean("Features.Anti-Baby-Zombies")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiBabyZombie(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Anti Baby Zombie Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Anti-Mob-Movement")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiMobMovement(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Anti Mob Movement Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Anti-Mob-Targeting")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiMobTargeting(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Anti Mob Targeting Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Iron-Golem-Health")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new IronGolemHealth(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Iron Golem Health Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Water-Proof-Blazes")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new WaterProofBlazes(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Water Proof Blazes Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Anti-Book-Quill-Crash")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiBookQuillCrash(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Anti Book & Quill Crash Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Anti-Cobble-Monster")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiCobbleMonster(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Anti Cobble Monster Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Anti-Death-Clip")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiDeathClip(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Anti Death Clip Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Anti-Dupe")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiDupe(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Anti Dupe Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Anti-Nether-Roof")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiNetherRoof(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Anti Nether Roof Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Anti-Piston-Glitch")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiPistonGlitch(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Anti Piston Glitch Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Anti-Wilderness-Spawner")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AntiWildernessSpawner(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Anti Wilderness Spawner Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Auto-Respawn")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new AutoRespawn(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Auto Respawn Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Book-Disenchant")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new BookDisenchant(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Book Disenchant Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Border-Patches")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new BorderPatches(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Border Patches Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Anti-Explosion-Damage")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new DenyExplosionDamage(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Anti Explosion Damage Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Anti-Dragon-Egg-TP")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new DragonEggAntiTP(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Anti Dragon Egg TP Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Enemy-Spawner-Mine")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new EnemySpawnerMine(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Enemy Spawner Mine Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Insta-Sponge-Break")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new InstaSpongeBreak(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Insta Sponge Break Module Injected!");
        }

        if(getConfig().fetchBoolean("Features.Anti-Natural-Mobs")) {
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new NaturalMobSpawning(), FactionsPlugin.getInstance());
            FactionsPlugin.getInstance().log(Level.INFO, "Anti Natural Mob Spawning Module Injected!");
        }
    }
}
