package com.massivecraft.factions.zcore.persist;

import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.AsciiCompass;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.util.WorldUtil;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;


public abstract class MemoryBoard extends Board {

    public MemoryBoardMap flocationIds = new MemoryBoardMap();

    //----------------------------------------------//
    // Get and Set
    //----------------------------------------------//
    public String getIdAt(FLocation flocation) {
        return flocationIds.getOrDefault(flocation, "0");
    }

    public Faction getFactionAt(FLocation flocation) {
        return Factions.getInstance().getFactionById(getIdAt(flocation));
    }

    public void setIdAt(String id, FLocation flocation) {
        String idAt = getIdAt(flocation);
        if (idAt.equals(id)) {
            return;
        }

        clearOwnershipAt(flocation);

        if (id.equals("0")) {
            removeAt(flocation);
        }

        flocationIds.put(flocation, id);
    }

    public void setFactionAt(Faction faction, FLocation flocation) {
        setIdAt(faction.getId(), flocation);
    }

    public void removeAt(FLocation flocation) {
        Faction faction = getFactionAt(flocation);
        faction.getWarps().values().removeIf(lazyLocation -> flocation.isInChunk(lazyLocation.getLocation()));

        for (FPlayer onlinePlayer : FPlayers.getInstance().getOnlinePlayers()) {
            Player player = onlinePlayer.getPlayer();
            if (player == null) {
                continue;
            }
            FLocation standing = FLocation.wrap(player);
            if (!standing.equals(flocation)) {
                continue;
            }
            if (!onlinePlayer.isAdminBypassing() && onlinePlayer.isFlying()) {
                onlinePlayer.setFlying(false);
            }
            if (onlinePlayer.isWarmingUp()) {
                onlinePlayer.clearWarmup();
                onlinePlayer.msg(TL.WARMUPS_CANCELLED);
            }
        }

        clearOwnershipAt(flocation);
        flocationIds.remove(flocation);
    }

    public Set<FLocation> getAllClaims(String factionId) {
        return this.flocationIds.getFactionClaims(factionId);
    }

    public Set<FLocation> getAllClaims(Faction faction) {
        return getAllClaims(faction.getId());
    }

    // not to be confused with claims, ownership referring to further member-specific ownership of a claim
    public void clearOwnershipAt(FLocation flocation) {
        Faction faction = getFactionAt(flocation);
        if (faction != null && faction.isNormal()) {
            faction.clearClaimOwnership(flocation);
        }
    }

    public void unclaimAll(String factionId) {
        Faction faction = Factions.getInstance().getFactionById(factionId);
        if (faction != null && faction.isNormal()) {
            faction.clearAllClaimOwnership();
            faction.clearWarps();
            faction.clearSpawnerChunks();
        }
        clean(factionId);
    }

    public void unclaimAllInWorld(String factionId, World world) {
        for (FLocation loc : getAllClaims(factionId)) {
            if (loc.getWorldName().equals(world.getName())) {
                removeAt(loc);
            }
        }
    }

    public void clean(String factionId) {
        flocationIds.removeFaction(factionId);
    }

    // Is this coord NOT completely surrounded by coords claimed by the same faction?
    // Simpler: Is there any nearby coord with a faction other than the faction here?
    public boolean isBorderLocation(FLocation flocation) {
        Faction faction = getFactionAt(flocation);
        FLocation a = flocation.getRelative(1, 0);
        FLocation b = flocation.getRelative(-1, 0);
        FLocation c = flocation.getRelative(0, 1);
        FLocation d = flocation.getRelative(0, -1);
        return faction != getFactionAt(a) || faction != getFactionAt(b) || faction != getFactionAt(c) || faction != getFactionAt(d);
    }

    // Is this coord connected to any coord claimed by the specified faction?
    public boolean isConnectedLocation(FLocation flocation, Faction faction) {
        FLocation a = flocation.getRelative(1, 0);
        FLocation b = flocation.getRelative(-1, 0);
        FLocation c = flocation.getRelative(0, 1);
        FLocation d = flocation.getRelative(0, -1);
        return faction == getFactionAt(a) || faction == getFactionAt(b) || faction == getFactionAt(c) || faction == getFactionAt(d);
    }

    /**
     * Checks if there is another faction within a given radius other than Wilderness. Used for HCF feature that
     * requires a 'buffer' between factions.
     *
     * @param flocation - center location.
     * @param faction   - faction checking for.
     * @param radius    - chunk radius to check.
     * @return true if another Faction is within the radius, otherwise false.
     */
    public boolean hasFactionWithin(FLocation flocation, Faction faction, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x == 0 && z == 0) {
                    continue;
                }

                FLocation relative = flocation.getRelative(x, z);
                Faction other = getFactionAt(relative);

                if (other.isNormal() && other != faction) {
                    return true;
                }
            }
        }
        return false;
    }

    public void clean() {
        List<FLocation> invalidClaims = new ArrayList<>();
        for (Entry<FLocation, String> entry : this.flocationIds.entrySet()) {

            FLocation location = entry.getKey();
            String id = entry.getValue();

            String worldName = location.getWorldName();
            if (Bukkit.getWorld(worldName) != null) {
                if (Factions.getInstance().isValidFactionId(id)) {
                    continue;
                }
            } else {
                if (!Conf.removeInvalidClaims) {
                    Logger.print("No world '" + worldName + "' found. Removed or not loaded?. Enable removeInvalidClaims in Conf.json to remove these claims.");
                    continue;
                }
            }
            Logger.print("Board cleaner removed " + id + " from " + location);
            invalidClaims.add(location);
        }

        for (FLocation invalidClaim : invalidClaims) {
            this.flocationIds.remove(invalidClaim);
        }
    }


    //----------------------------------------------//
    // Cleaner. Remove orphaned foreign keys
    //----------------------------------------------//

    public int getFactionCoordCount(String factionId) {
        return flocationIds.getOwnedLandCount(factionId);
    }

    //----------------------------------------------//
    // Coord count
    //----------------------------------------------//

    public int getFactionCoordCount(Faction faction) {
        return getFactionCoordCount(faction.getId());
    }

    public int getFactionCoordCountInWorld(Faction faction, String worldName) {
        return flocationIds.getOwnedLandCount(faction.getId(), worldName);
    }

    //----------------------------------------------//
    // Map generation
    //----------------------------------------------//
    @Override
    public List<Component> getMap(FPlayer fPlayer, FLocation flocation, float inDegrees) {
        List<Component> lines = new ArrayList<>(18);
        Faction currentFaction = getFactionAt(flocation);
        lines.add(Component.text(TextUtil.titleize(ChatColor.DARK_GRAY + TextUtil.titleize("(" + flocation.getCoordString() + ") " + currentFaction.getTag(fPlayer)))));

        Player player = fPlayer.getPlayer();
        Faction playerFaction = fPlayer.getFaction();
        String playerFactionId = fPlayer.getFactionId();
        String worldName = flocation.getWorldName();
        World world = flocation.getWorld();
        int worldBorderBuffer = FactionsPlugin.getInstance().getConfig().getInt("world-border.buffer", 0);
        boolean canClickToClaim = Conf.enableClickToClaim && player != null && player.hasPermission(Permission.CLAIMAT.node);

        int requestedHeight = Math.max(3, Math.min(fPlayer.getMapHeight(), Conf.mapHeight));
        int length = Math.max(3, Conf.mapWidth);
        int height = Conf.showMapFactionKey ? requestedHeight - 1 : requestedHeight;
        int centerX = flocation.getIntX();
        int centerZ = flocation.getIntZ();
        int startX = centerX - (length / 2);
        int startZ = centerZ - (requestedHeight / 2);

        Map<Faction, Character> territories = new LinkedHashMap<>(length * height, 1.02f);
        List<Component> compass = AsciiCompass.getAsciiCompass(inDegrees);
        Map<Faction, Relation> relationCache = new HashMap<>();
        Map<Faction, HoverEvent<Component>> factionHoverCache = new HashMap<>();
        Map<Faction, HoverEvent<Component>> spawnerFactionHoverCache = new HashMap<>();
        Map<Faction, ClickEvent> showFactionClickCache = new HashMap<>();
        Map<Faction, TextColor> factionColorCache = new HashMap<>();
        HoverEvent<Component> hereHover = HoverEvent.showText(TL.CLAIM_YOUAREHERE.toComponent());
        HoverEvent<Component> outsideBorderHover = HoverEvent.showText(TL.CLAIM_MAP_OUTSIDEBORDER.toComponent());
        HoverEvent<Component> wildernessHover = HoverEvent.showText(TL.WILDERNESS.toComponent());
        HoverEvent<Component> safeZoneHover = HoverEvent.showText(TL.SAFEZONE.toComponent());
        HoverEvent<Component> warZoneHover = HoverEvent.showText(TL.WARZONE.toComponent());
        TextColor aqua = TextUtil.kyoriColor(ChatColor.AQUA);
        TextColor black = TextUtil.kyoriColor(ChatColor.BLACK);
        TextColor gray = TextUtil.kyoriColor(ChatColor.GRAY);
        TextColor wildernessColor = TextUtil.kyoriColor(Conf.colorWilderness);
        TextColor safeZoneColor = TextUtil.kyoriColor(Conf.colorSafezone);
        TextColor warZoneColor = TextUtil.kyoriColor(Conf.colorWar);
        TextColor spawnerChunkColor = TextUtil.kyoriColor(Conf.spawnerChunkColor);

        for (int y = 0; y < height; y++) {
            TextComponent.Builder row = Component.text();
            for (int x = y < 3 ? 2 : 0; x < length; x++) {
                if (y < 3 && x == 2) {
                    row.append(compass.get(y));
                    continue;
                }

                int chunkX = startX + x;
                int chunkZ = startZ + y;

                if (chunkX == centerX && chunkZ == centerZ) {
                    row.append(
                            Component.text("+")
                                    .color(aqua)
                                    .hoverEvent(hereHover)
                    );
                    continue;
                }
                if (world != null && FLocation.isOutsideWorldBorder(world, chunkX, chunkZ, worldBorderBuffer)) {
                    row.append(
                            Component.text("-")
                                    .color(black)
                                    .hoverEvent(outsideBorderHover)
                    );
                    continue;
                }

                FLocation found = FLocation.wrap(worldName, chunkX, chunkZ);
                Faction factionFound = this.getFactionAt(found);
                if (factionFound.isWilderness()) {
                    TextComponent.Builder land = Component.text()
                            .content("-")
                            .color(wildernessColor);

                    if (canClickToClaim) {
                        land.hoverEvent(TL.CLAIM_CLICK_TO_CLAIM.toFormattedComponent(chunkX, chunkZ))
                                .clickEvent(ClickEvent.runCommand("/f claimat " + worldName + " " + chunkX + " " + chunkZ));
                    } else {
                        land.hoverEvent(wildernessHover);
                    }

                    row.append(land.build());
                    continue;
                }
                if (factionFound.isSafeZone()) {
                    row.append(
                            Component.text("+")
                                    .color(safeZoneColor)
                                    .hoverEvent(safeZoneHover)
                    );
                    continue;
                }
                if (factionFound.isWarZone()) {
                    row.append(
                            Component.text("+")
                                    .color(warZoneColor)
                                    .hoverEvent(warZoneHover)
                    );
                    continue;
                }
                Relation relation = relationCache.computeIfAbsent(factionFound, fPlayer::getRelationTo);
                if (this.shouldShowFactionOnMap(playerFactionId, factionFound, relation)) {
                    char assigned = territories.computeIfAbsent(factionFound, faction -> Conf.mapKeyChrs[(territories.size() + 1) % Conf.mapKeyChrs.length]);
                    ClickEvent showFactionClick = showFactionClickCache.computeIfAbsent(factionFound, faction -> ClickEvent.runCommand("/f show " + faction.getTag()));

                    if (Conf.userSpawnerChunkSystem && factionFound.getSpawnerChunks().contains(found.toFastChunk())) {
                        row.append(
                                Component.text(assigned)
                                        .color(spawnerChunkColor)
                                        .hoverEvent(spawnerFactionHoverCache.computeIfAbsent(
                                                factionFound,
                                                faction -> HoverEvent.showText(Component.text(toolTip(faction, fPlayer) + CC.Reset + CC.Blue + " " + Conf.spawnerChunkString))
                                        ))
                                        .clickEvent(showFactionClick)

                        );
                    } else {
                        row.append(
                                Component.text(assigned)
                                        .color(factionColorCache.computeIfAbsent(factionFound, faction -> TextUtil.kyoriColor(faction.getColorTo(playerFaction))))
                                        .hoverEvent(factionHoverCache.computeIfAbsent(
                                                factionFound,
                                                faction -> HoverEvent.showText(Component.text(toolTip(faction, fPlayer)))
                                        ))
                                        .clickEvent(showFactionClick)
                        );
                    }
                    continue;
                }
                String factionTag = factionFound.getTag();
                row.append(
                        Component.text("-")
                                .color(gray)
                                .hoverEvent(
                                        HoverEvent.showText(Component.text(factionTag).color(gray)))
                                .clickEvent(showFactionClickCache.computeIfAbsent(factionFound, faction -> ClickEvent.runCommand("/f show " + faction.getTag())))
                );
            }
            lines.add(row.build());
        }
        if (Conf.showMapFactionKey && !territories.isEmpty()) {
            TextComponent.Builder territory = Component.text();
            for (Entry<Faction, Character> entry : territories.entrySet()) {
                Faction faction = entry.getKey();
                Character character = entry.getValue();
                territory.append(
                        Component.text(character + ": " + faction.getTag() + " ")
                                .color(factionColorCache.computeIfAbsent(faction, fac -> TextUtil.kyoriColor(fac.getColorTo(playerFaction))))
                );
            }
            lines.add(territory.build());
        }
        return lines;
    }

    private boolean shouldShowFactionOnMap(String playerFactionId, Faction factionFound, Relation relation) {
        return playerFactionId.equals(factionFound.getId())
                || relation.isAtLeast(Relation.ALLY)
                || Conf.showNeutralFactionsOnMap && relation == Relation.NEUTRAL
                || Conf.showEnemyFactionsOnMap && relation == Relation.ENEMY
                || Conf.showTrucesFactionsOnMap && relation == Relation.TRUCE;
    }

    private String toolTip(Faction faction, FPlayer to) {
        return faction.describeTo(to);
    }
    public abstract void convertFrom(MemoryBoard old);

    public static class MemoryBoardMap extends AbstractMap<FLocation, String> implements Serializable {
        private static final long serialVersionUID = -6689617828610585368L;

        private final Map<String, Map<Long, String>> worldToFactionMap = new HashMap<>();
        private final Map<String, Set<ChunkRef>> factionToLandMap = new HashMap<>();
        private final Map<String, Map<String, Integer>> factionToWorldCountMap = new HashMap<>();
        private transient Set<Entry<FLocation, String>> entrySet;
        private int size;

        @Override
        public String put(FLocation floc, String factionId) {
            return put(floc.getWorldName(), floc.toKey(), factionId);
        }

        @Override
        public String get(Object key) {
            if (!(key instanceof FLocation)) {
                return null;
            }
            FLocation floc = (FLocation) key;
            return get(floc.getWorldName(), floc.toKey());
        }

        @Override
        public String remove(Object key) {
            if (!(key instanceof FLocation)) {
                return null;
            }
            FLocation floc = (FLocation) key;
            String worldName = floc.getWorldName();
            long chunkKey = floc.toKey();
            Map<Long, String> worldClaims = this.worldToFactionMap.get(worldName);
            if (worldClaims == null) {
                return null;
            }

            String result = worldClaims.remove(chunkKey);
            if (result != null) {
                ChunkRef ref = new ChunkRef(worldName, chunkKey);
                this.removeFactionClaim(result, ref);
                this.decrementWorldCount(result, worldName);
                this.size--;
                if (worldClaims.isEmpty()) {
                    this.worldToFactionMap.remove(worldName);
                }
            }
            return result;
        }

        @Override
        public void clear() {
            this.worldToFactionMap.clear();
            this.factionToLandMap.clear();
            this.factionToWorldCountMap.clear();
            this.size = 0;
        }

        @Override
        public int size() {
            return this.size;
        }

        @Override
        public Set<Entry<FLocation, String>> entrySet() {
            if (this.entrySet == null) {
                this.entrySet = new AbstractSet<Entry<FLocation, String>>() {
                    @Override
                    public Iterator<Entry<FLocation, String>> iterator() {
                        return new MemoryBoardMapEntryIterator();
                    }

                    @Override
                    public int size() {
                        return MemoryBoardMap.this.size;
                    }

                    @Override
                    public void clear() {
                        MemoryBoardMap.this.clear();
                    }
                };
            }
            return this.entrySet;
        }

        public int getOwnedLandCount(String factionId) {
            Set<ChunkRef> claims = this.factionToLandMap.get(factionId);
            return claims == null ? 0 : claims.size();
        }

        public int getOwnedLandCount(String factionId, String worldName) {
            Map<String, Integer> worldCounts = this.factionToWorldCountMap.get(factionId);
            if (worldCounts == null) {
                return 0;
            }
            return worldCounts.getOrDefault(worldName, 0);
        }

        public Set<FLocation> getFactionClaims(String factionId) {
            Set<ChunkRef> claims = this.factionToLandMap.get(factionId);
            if (claims == null || claims.isEmpty()) {
                return new HashSet<>();
            }

            Set<FLocation> result = new HashSet<>(claims.size());
            for (ChunkRef claim : claims) {
                result.add(claim.toFLocation());
            }
            return result;
        }

        public void removeFaction(String factionId) {
            Set<ChunkRef> claims = this.factionToLandMap.remove(factionId);
            if (claims == null || claims.isEmpty()) {
                this.factionToWorldCountMap.remove(factionId);
                return;
            }

            for (FPlayer fPlayer : FPlayers.getInstance().getOnlinePlayers()) {
                if (this.containsClaim(claims, fPlayer.getLastStoodAt())) {
                    if (FCmdRoot.instance.fFlyEnabled && !fPlayer.isAdminBypassing() && fPlayer.isFlying()) {
                        fPlayer.setFlying(false);
                    }
                    if (fPlayer.isWarmingUp()) {
                        fPlayer.clearWarmup();
                        fPlayer.msg(TL.WARMUPS_CANCELLED);
                    }
                }
            }

            for (ChunkRef claim : claims) {
                Map<Long, String> worldClaims = this.worldToFactionMap.get(claim.worldName);
                if (worldClaims != null && worldClaims.remove(claim.chunkKey) != null) {
                    this.size--;
                    if (worldClaims.isEmpty()) {
                        this.worldToFactionMap.remove(claim.worldName);
                    }
                }
            }

            this.factionToWorldCountMap.remove(factionId);
        }

        public Set<Entry<String, Map<Long, String>>> worldEntrySet() {
            return this.worldToFactionMap.entrySet();
        }

        public String put(String worldName, int x, int z, String factionId) {
            return put(worldName, WorldUtil.encodeChunk(x, z), factionId);
        }

        private String put(String worldName, long chunkKey, String factionId) {
            Map<Long, String> worldClaims = this.worldToFactionMap.computeIfAbsent(worldName, key -> new HashMap<>());
            String previousValue = worldClaims.get(chunkKey);
            if (Objects.equals(previousValue, factionId)) {
                return previousValue;
            }

            worldClaims.put(chunkKey, factionId);
            ChunkRef ref = new ChunkRef(worldName, chunkKey);
            if (previousValue != null) {
                this.removeFactionClaim(previousValue, ref);
                this.decrementWorldCount(previousValue, worldName);
            } else {
                this.size++;
            }

            this.addFactionClaim(factionId, ref);
            this.incrementWorldCount(factionId, worldName);
            return previousValue;
        }

        private String get(String worldName, long chunkKey) {
            Map<Long, String> worldClaims = this.worldToFactionMap.get(worldName);
            if (worldClaims == null) {
                return null;
            }
            return worldClaims.get(chunkKey);
        }

        private void addFactionClaim(String factionId, ChunkRef claim) {
            this.factionToLandMap.computeIfAbsent(factionId, key -> new HashSet<>()).add(claim);
        }

        private void removeFactionClaim(String factionId, ChunkRef claim) {
            Set<ChunkRef> claims = this.factionToLandMap.get(factionId);
            if (claims == null) {
                return;
            }

            claims.remove(claim);
            if (claims.isEmpty()) {
                this.factionToLandMap.remove(factionId);
            }
        }

        private void incrementWorldCount(String factionId, String worldName) {
            this.factionToWorldCountMap
                    .computeIfAbsent(factionId, key -> new HashMap<>())
                    .merge(worldName, 1, Integer::sum);
        }

        private void decrementWorldCount(String factionId, String worldName) {
            Map<String, Integer> worldCounts = this.factionToWorldCountMap.get(factionId);
            if (worldCounts == null) {
                return;
            }

            Integer count = worldCounts.get(worldName);
            if (count == null) {
                return;
            }

            if (count <= 1) {
                worldCounts.remove(worldName);
                if (worldCounts.isEmpty()) {
                    this.factionToWorldCountMap.remove(factionId);
                }
            } else {
                worldCounts.put(worldName, count - 1);
            }
        }

        private boolean containsClaim(Set<ChunkRef> claims, FLocation location) {
            if (location == null || claims == null || claims.isEmpty()) {
                return false;
            }
            return claims.contains(ChunkRef.from(location));
        }

        private static final class ChunkRef {
            private final String worldName;
            private final long chunkKey;

            private ChunkRef(String worldName, long chunkKey) {
                this.worldName = worldName;
                this.chunkKey = chunkKey;
            }

            private static ChunkRef from(FLocation location) {
                return new ChunkRef(location.getWorldName(), location.toKey());
            }

            private FLocation toFLocation() {
                return FLocation.wrap(this.worldName, WorldUtil.getChunkX(this.chunkKey), WorldUtil.getChunkZ(this.chunkKey));
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof ChunkRef)) return false;
                ChunkRef chunkRef = (ChunkRef) o;
                return chunkKey == chunkRef.chunkKey && Objects.equals(worldName, chunkRef.worldName);
            }

            @Override
            public int hashCode() {
                return Objects.hash(worldName, chunkKey);
            }
        }

        private final class MemoryBoardMapEntryIterator implements Iterator<Entry<FLocation, String>> {
            private final Iterator<Entry<String, Map<Long, String>>> worldIterator = worldToFactionMap.entrySet().iterator();
            private Iterator<Entry<Long, String>> chunkIterator = Collections.emptyIterator();
            private String currentWorldName;

            @Override
            public boolean hasNext() {
                while (!this.chunkIterator.hasNext() && this.worldIterator.hasNext()) {
                    Entry<String, Map<Long, String>> worldEntry = this.worldIterator.next();
                    this.currentWorldName = worldEntry.getKey();
                    this.chunkIterator = worldEntry.getValue().entrySet().iterator();
                }
                return this.chunkIterator.hasNext();
            }

            @Override
            public Entry<FLocation, String> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                Entry<Long, String> claimEntry = this.chunkIterator.next();
                FLocation location = FLocation.wrap(this.currentWorldName, WorldUtil.getChunkX(claimEntry.getKey()), WorldUtil.getChunkZ(claimEntry.getKey()));
                return new AbstractMap.SimpleImmutableEntry<>(location, claimEntry.getValue());
            }
        }
    }
}
