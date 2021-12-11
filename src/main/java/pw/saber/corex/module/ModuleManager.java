package pw.saber.corex.module;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private FactionsPlugin plugin;

    private final List<Module> modules = new ArrayList<>();

    public void loadModules(FactionsPlugin plugin) {
        this.plugin = plugin;
        if(!this.modules.isEmpty()) {
            //unload
            unloadModules();
        }



        for (Module module : this.modules) {
            try {
                module.onEnable();
            } catch (Exception e) {
                e.printStackTrace();
                //plugin.getLogger().severe("There was an error loading the " + module.getModuleType() + " patch module");
                //plugin.getLogger().severe("The plugin will now disable..");
                //plugin.getServer().getPluginManager().disablePlugin((Plugin)plugin);
                break;
            }
        }
        plugin.getLogger().info("Loaded " + this.modules.size() + " enchants.");
    }

    public void unloadModules() {
        for (Module module : this.modules) {
            try {
                HandlerList.unregisterAll(module);
                module.onDisable();
            } catch (Exception e) {
                e.printStackTrace();
                this.plugin.getLogger().severe("There was an error unloading the " + module.getName() + " Module.");
            }
        }
        this.modules.clear();
    }

    public Module getModule(String name) {
        for(Module module : modules) {
            if(module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public void registerModule(Module module) {
        FactionsPlugin plugin = module.getPlugin();
        plugin.getServer().getPluginManager().registerEvents(module, plugin);
        this.modules.add(module);
    }

    public boolean isEnabled(String name) {
        for(Module module : this.modules) {
            return module.getName().equalsIgnoreCase(name);
        }
        return false;
    }
}
