package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/*
    Object that contains information about a command being executed,
    args, player, faction
 */
public class CommandContext {

    public CommandSender sender;
    public Player player;
    public FPlayer fPlayer;
    public Faction faction;
    public List<String> args;
    public String alias;
    public List<FCommand> commandChain = new ArrayList<>();

    public CommandContext(CommandSender sender, List<String> args, String alias) {
        this.sender = sender;
        this.args = args;
        this.alias = alias;

        if (sender instanceof Player) {
            this.player = (Player) sender;
            this.fPlayer = FPlayers.getInstance().getByPlayer(player);
            this.faction = fPlayer.getFaction();
        }
    }

    public void msg(String str, Object... args) {
        sender.sendMessage(TextUtil.parse(str, args));
    }

    public void msg(TL translation, Object... args) {
        sender.sendMessage(TextUtil.parse(translation.toString(), args));
    }



    public void sendMessage(String msg) {
        sender.sendMessage(msg);
    }

    public void sendMessage(List<String> msgs) {
        msgs.forEach(this::sendMessage);
    }

    public void sendComponent(Component message) {
        if (this.player != null && this.player.isOnline()) {
            TextUtil.AUDIENCES.player(this.player).sendMessage(message);
        } else {
            TextUtil.AUDIENCES.sender(this.sender).sendMessage(message);
        }
    }

    public void sendComponent(List<Component> messages) {
        messages.forEach(this::sendComponent);
    }

    public boolean argIsSet(int idx) {
        return args.size() >= idx + 1;
    }

    public String argAsString(int idx, String def) {
        return args.size() > idx ? args.get(idx) : def;
    }

    public String argAsString(int idx) {
        return argAsString(idx, null);
    }

    public Integer argAsInt(int idx, Integer def) {
        String str = argAsString(idx);
        return str != null ? strAsInt(str, def) : def;
    }

    public Integer argAsInt(int idx) {
        return argAsInt(idx, null);
    }

    public Double argAsDouble(int idx, Double def) {
        String str = argAsString(idx);
        return str != null ? strAsDouble(str, def) : def;
    }

    public Double argAsDouble(int idx) {
        return argAsDouble(idx, null);
    }

    public Boolean argAsBool(int idx, boolean def) {
        String str = argAsString(idx);
        return str != null ? strAsBool(str) : def;
    }

    public Boolean argAsBool(int idx) {
        return argAsBool(idx, false);
    }

    public Player argAsPlayer(int idx, Player def, boolean msg) {
        return strAsPlayer(argAsString(idx), def, msg);
    }

    public Player argAsPlayer(int idx, Player def) {
        return argAsPlayer(idx, def, true);
    }

    public Player argAsPlayer(int idx) {
        return argAsPlayer(idx, null);
    }

    public Player argAsBestPlayerMatch(int idx, Player def, boolean msg) {
        return strAsBestPlayerMatch(argAsString(idx), def, msg);
    }

    public Player argAsBestPlayerMatch(int idx, Player def) {
        return argAsBestPlayerMatch(idx, def, true);
    }

    public Player argAsBestPlayerMatch(int idx) {
        return argAsBestPlayerMatch(idx, null);
    }

    public FPlayer argAsFPlayer(int idx, FPlayer def, boolean msg) {
        return strAsFPlayer(argAsString(idx), def, msg);
    }

    public FPlayer argAsFPlayer(int idx, FPlayer def) {
        return argAsFPlayer(idx, def, true);
    }

    public FPlayer argAsFPlayer(int idx) {
        return argAsFPlayer(idx, null);
    }

    public FPlayer argAsBestFPlayerMatch(int idx, FPlayer def, boolean msg) {
        return strAsBestFPlayerMatch(argAsString(idx), def, msg);
    }

    public FPlayer argAsBestFPlayerMatch(int idx, FPlayer def) {
        return argAsBestFPlayerMatch(idx, def, true);
    }

    public FPlayer argAsBestFPlayerMatch(int idx) {
        return argAsBestFPlayerMatch(idx, null);
    }

    public Faction argAsFaction(int idx, Faction def, boolean msg) {
        return strAsFaction(argAsString(idx), def, msg);
    }

    public Faction argAsFaction(int idx, Faction def) {
        return argAsFaction(idx, def, true);
    }

    public Faction argAsFaction(int idx) {
        return argAsFaction(idx, null);
    }

    public boolean assertHasFaction() {
        if (player == null || fPlayer.hasFaction()) {
            return true;
        }
        msg(TL.GENERIC_MEMBERONLY);
        return false;
    }

    public boolean assertMinRole(Role role) {
        if (player == null || fPlayer.getRole().value >= role.value) {
            return true;
        }
        msg("<b>You must be " + role);
        return false;
    }

    public boolean canIAdministerYou(FPlayer i, FPlayer you) {
        if (!i.getFaction().equals(you.getFaction())) {
            i.msg(TL.COMMAND_CONTEXT_ADMINISTER_DIF_FACTION, you.describeTo(i, true));
            return false;
        }
        if (i.getRole().value >= you.getRole().value || i.getRole() == Role.LEADER) {
            return true;
        }
        i.sendMessage(TextUtil.parse("%s <b>has a higher rank than you.", you.describeTo(i, true)));
        return false;
    }

    public boolean payForCommand(double cost, String toDoThis, String forDoingThis) {
        if (!Econ.shouldBeUsed() || fPlayer == null || cost == 0.0 || fPlayer.isAdminBypassing()) {
            return true;
        }
        if (Conf.bankEnabled && Conf.bankFactionPaysCosts && fPlayer.hasFaction()) {
            return Econ.modifyMoney(faction, -cost, toDoThis, forDoingThis);
        } else {
            return Econ.modifyMoney(fPlayer, -cost, toDoThis, forDoingThis);
        }
    }

    public boolean payForCommand(double cost, TL toDoThis, TL forDoingThis) {
        return payForCommand(cost, toDoThis.toString(), forDoingThis.toString());
    }

    public boolean canAffordCommand(double cost, String toDoThis) {
        if (!Econ.shouldBeUsed() || fPlayer == null || cost == 0.0 || fPlayer.isAdminBypassing()) {
            return true;
        }
        if (Conf.bankEnabled && Conf.bankFactionPaysCosts && fPlayer.hasFaction()) {
            return Econ.hasAtLeast(faction, cost, toDoThis);
        } else {
            return Econ.hasAtLeast(fPlayer, cost, toDoThis);
        }
    }

    public void doWarmUp(WarmUpUtil.Warmup warmup, TL translationKey, String action, Runnable runnable, long delay) {
        this.doWarmUp(fPlayer, warmup, translationKey, action, runnable, delay);
    }

    public void doWarmUp(FPlayer player, WarmUpUtil.Warmup warmup, TL translationKey, String action, Runnable runnable, long delay) {
        WarmUpUtil.process(player, warmup, translationKey, action, runnable, delay);
    }

    // Helper methods
    private Integer strAsInt(String str, Integer def) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ignored) {
            return def;
        }
    }

    private Double strAsDouble(String str, Double def) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException ignored) {
            return def;
        }
    }

    public Boolean strAsBool(String str) {
        str = str.toLowerCase();
        return str.startsWith("y") || str.startsWith("t") || str.startsWith("on") || str.startsWith("+") || str.startsWith("1");
    }

    private Player strAsPlayer(String name, Player def, boolean msg) {
        Player ret = def;
        if (name != null) {
            Player player = Bukkit.getPlayer(name);
            if (player != null) {
                ret = player;
            }
        }
        if (msg && ret == null) {
            sender.sendMessage(TL.GENERIC_NOPLAYERFOUND.format(name));
        }
        return ret;
    }

    private Player strAsBestPlayerMatch(String name, Player def, boolean msg) {
        Player ret = def;
        if (name != null) {
            List<Player> players = Bukkit.matchPlayer(name);
            if (!players.isEmpty()) {
                ret = players.get(0);
            }
        }
        if (msg && ret == null) {
            sender.sendMessage(TL.GENERIC_NOPLAYERMATCH.format(name));
        }
        return ret;
    }

    private FPlayer strAsFPlayer(String name, FPlayer def, boolean msg) {
        FPlayer ret = def;
        if (name != null) {
            for (FPlayer fplayer : FPlayers.getInstance().getAllFPlayers()) {
                if (fplayer.getName().equalsIgnoreCase(name)) {
                    ret = fplayer;
                    break;
                }
            }
        }
        if (msg && ret == null) {
            msg(TL.GENERIC_NOPLAYERFOUND, name);
        }
        return ret;
    }

    private FPlayer strAsBestFPlayerMatch(String name, FPlayer def, boolean msg) {
        return strAsFPlayer(name, def, msg);
    }

    private Faction strAsFaction(String name, Faction def, boolean msg) {
        Faction ret = def;
        if (name != null) {
            Faction faction = Factions.getInstance().getByTag(name);
            if (faction == null && name.equalsIgnoreCase("warzone")) {
                faction = Factions.getInstance().getWarZone();
            } else if (faction == null && name.equalsIgnoreCase("safezone")) {
                faction = Factions.getInstance().getSafeZone();
            } else if (faction == null) {
                faction = Factions.getInstance().getBestTagMatch(name);
            }
            if (faction == null) {
                FPlayer fplayer = strAsFPlayer(name, null, false);
                if (fplayer != null) {
                    faction = fplayer.getFaction();
                }
            }
            if (faction != null) {
                ret = faction;
            }
        }
        if (msg && ret == null) {
            sender.sendMessage(TL.GENERIC_NOFACTION_FOUND.format(name));
        }
        return ret;
    }
}
