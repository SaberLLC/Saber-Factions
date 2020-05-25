package com.massivecraft.factions.cmd.wild;


import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.wait.WaitedTask;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;

/**
 * @author DroppingAnvil
 */
public class CmdWild extends FCommand implements WaitedTask {
    public static HashMap<Player, String> teleportRange;
    public static HashSet<Player> teleporting;
    public static CmdWild instance;

    public CmdWild() {
        super();
        if (instance == null) instance = this;
        this.aliases.addAll(Aliases.wild);
        this.requirements = new CommandRequirements.Builder(Permission.WILD)
                .playerOnly()
                .build();
        teleporting = new HashSet<>();
        teleportRange = new HashMap<>();
    }

    @Override
    public void perform(CommandContext context) {
        if (!teleportRange.containsKey(context.player)) {
            context.player.openInventory(new WildGUI(context.player, context.fPlayer).getInventory());
        }
    }


    public void attemptTeleport(Player p) {
        boolean success = false;
        int tries = 0;
        ConfigurationSection c = FactionsPlugin.getInstance().getConfig().getConfigurationSection("Wild.Zones." + teleportRange.get(p));
        while (tries < 5) {
            assert c != null;
            int x = new Random().nextInt((c.getInt("Range.MaxX") - c.getInt("Range.MinX")) + 1) + c.getInt("Range.MinX");
            int z = new Random().nextInt((c.getInt("Range.MaxZ") - c.getInt("Range.MinZ")) + 1) + c.getInt("Range.MinZ");
            if (Board.getInstance().getFactionAt(new FLocation(p.getWorld().getName(), x, z)).isWilderness()) {
                success = true;
                FLocation loc = new FLocation(Objects.requireNonNull(c.getString("World", "World")), x, z);
                teleportRange.remove(p);
                if (!FPlayers.getInstance().getByPlayer(p).takeMoney(c.getInt("Cost"))) {
                    p.sendMessage(TL.GENERIC_NOTENOUGHMONEY.toString());
                    return;
                }
                if (Conf.wildLoadChunkBeforeTeleport && !loc.getChunk().isLoaded()) loc.getChunk().load();
                teleportPlayer(p, loc);
                break;
            }
            tries++;
        }
        if (!success) {
            p.sendMessage(TL.COMMAND_WILD_FAILED.toString());
            teleportRange.remove(p);
        }
    }

    public void teleportPlayer(Player p, FLocation loc) {
        Location finalLoc;
        if (FactionsPlugin.getInstance().getConfig().getBoolean("Wild.Arrival.SpawnAbove")) {
            finalLoc = new Location(loc.getWorld(), loc.getX(), loc.getWorld().getHighestBlockYAt(Math.round(loc.getX()), Math.round(loc.getZ())) + FactionsPlugin.getInstance().getConfig().getInt("Wild.Arrival.SpawnAboveBlocks", 1), loc.getZ());
        } else {
            finalLoc = new Location(loc.getWorld(), loc.getX(), loc.getWorld().getHighestBlockYAt(Math.round(loc.getX()), Math.round(loc.getZ())), loc.getZ());
        }
        p.teleport(finalLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
        setTeleporting(p);
        applyEffects(p);
    }

    public void applyEffects(Player p) {
        for (String s : FactionsPlugin.getInstance().getConfig().getStringList("Wild.Arrival.Effects")) {
            p.addPotionEffect(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(s)), 40, 1));
        }
    }

    public void setTeleporting(Player p) {
        teleporting.add(p);
        Bukkit.getScheduler().scheduleSyncDelayedTask(FactionsPlugin.instance, () -> teleporting.remove(p), FactionsPlugin.getInstance().getConfig().getInt("Wild.Arrival.FallDamageWindow") * 20);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_WILD_DESCRIPTION;
    }

    @Override
    public void handleSuccess(Player player) {
        attemptTeleport(player);
    }

    @Override
    public void handleFailure(Player player) {
        player.sendMessage(TL.COMMAND_WILD_INTERUPTED.toString());
        teleportRange.remove(player);
    }
}