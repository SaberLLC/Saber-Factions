package com.massivecraft.factions.zcore.persist;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.util.FastUUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Consumer;

public abstract class MemoryFPlayers extends FPlayers {

    public Map<String, FPlayer> fPlayers = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);

    private final Map<Player, FPlayer> bukkitFPlayers = new ConcurrentHashMap<>(Math.max(16, Bukkit.getMaxPlayers()));
    private final Set<FPlayer> onlineFPlayers = ConcurrentHashMap.newKeySet(Math.max(16, Bukkit.getMaxPlayers()));
    private final Set<FPlayer> onlineFPlayersView = Collections.unmodifiableSet(this.onlineFPlayers);

    public void clean() {
        for (FPlayer fplayer : this.fPlayers.values()) {
            if (!Factions.getInstance().isValidFactionId(fplayer.getFactionId())) {
                Logger.print("Reset faction data (invalid faction:" + fplayer.getFactionId() + ") for player " + fplayer.getName(), Logger.PrefixType.DEFAULT);
                fplayer.resetFactionData(false);
            }
        }
    }

    public Set<FPlayer> getOnlinePlayers() {
        return this.onlineFPlayersView;
    }

    public FPlayer removeOnlinePlayer(Player player) {
        FPlayer removed = this.bukkitFPlayers.remove(player);
        if (removed != null) {
            this.onlineFPlayers.remove(removed);
            if (removed instanceof MemoryFPlayer) {
                ((MemoryFPlayer) removed).unbindPlayer(player);
            }
        }
        return removed;
    }

    public void wipeOnlinePlayers() {
        for (FPlayer onlineFPlayer : this.bukkitFPlayers.values()) {
            if (onlineFPlayer instanceof MemoryFPlayer) {
                ((MemoryFPlayer) onlineFPlayer).unbindPlayer(null);
            }
        }
        this.bukkitFPlayers.clear();
        this.onlineFPlayers.clear();
    }

    @Override
    public FPlayer getByPlayer(Player player) {
        String id = FastUUID.toString(player.getUniqueId());
        FPlayer fPlayer = player.isOnline() ? this.bukkitFPlayers.computeIfAbsent(player, key -> getById(id)) : getById(id);
        if (fPlayer instanceof MemoryFPlayer) {
            ((MemoryFPlayer) fPlayer).bindPlayer(player);
        }
        if (player.isOnline()) {
            this.onlineFPlayers.add(fPlayer);
        }
        return fPlayer;
    }

    @Override
    public List<FPlayer> getAllFPlayers() {
        return new ArrayList<>(fPlayers.values());
    }

    @Override
    public abstract void forceSave();

    public abstract void load(Consumer<Boolean> finish);

    @Override
    public FPlayer getByOfflinePlayer(OfflinePlayer player) {
        return getById(FastUUID.toString(player.getUniqueId()));
    }

    @Override
    public FPlayer getById(String id) {
        return fPlayers.computeIfAbsent(id, this::generateFPlayer);
    }

    public abstract FPlayer generateFPlayer(String id);

    public abstract void convertFrom(MemoryFPlayers old);
}
