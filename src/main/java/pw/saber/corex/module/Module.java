package pw.saber.corex.module;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.event.Listener;

public abstract class Module implements Listener {

    private final FactionsPlugin plugin;

    private String name;

    public Module(FactionsPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public FactionsPlugin getPlugin() {
        return plugin;
    }

    public String getName() {
        return name;
    }

    public abstract void onEnable();

    public abstract void onDisable();
}
