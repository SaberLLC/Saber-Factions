package com.massivecraft.factions.zcore.persist;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.persist.json.JSONFPlayer;
import com.massivecraft.factions.zcore.util.FastUUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Consumer;

public abstract class MemoryFPlayers extends FPlayers {

    public Map<String, FPlayer> fPlayers = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);

    private final Map<Player, FPlayer> bukkitFPlayers = new HashMap<>(Bukkit.getMaxPlayers());

    public void clean() {
        for (FPlayer fplayer : this.fPlayers.values()) {
            if (!Factions.getInstance().isValidFactionId(fplayer.getFactionId())) {
                Logger.print("Reset faction data (invalid faction:" + fplayer.getFactionId() + ") for player " + fplayer.getName(), Logger.PrefixType.DEFAULT);
                fplayer.resetFactionData(false);
            }
        }
    }

    public Set<FPlayer> getOnlinePlayers() {
        return new HashSet<>(this.bukkitFPlayers.values());
    }

    public FPlayer removeOnlinePlayer(Player player) {
        return this.bukkitFPlayers.remove(player);
    }

    public void wipeOnlinePlayers() {
        this.bukkitFPlayers.clear();
    }

    @Override
    public FPlayer getByPlayer(Player player) {
        String id = FastUUID.toString(player.getUniqueId());
        return player.isOnline() ? this.bukkitFPlayers.computeIfAbsent(player, key -> getById(id)) : getById(id);
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
        return getById(player.getUniqueId().toString());
    }

    @Override
    public FPlayer getById(String id) {
        return fPlayers.computeIfAbsent(id, JSONFPlayer::new);
    }

    public abstract FPlayer generateFPlayer(String id);

    public abstract void convertFrom(MemoryFPlayers old);
}
