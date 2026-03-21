package com.massivecraft.factions.zcore.fperms;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.Logger;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public final class FPerms {

    private static final Map<String, String> LEGACY_ALIASES;
    private static final Map<String, FPermAction> ACTIONS = new LinkedHashMap<>();
    private static final Map<String, Integer> CONFIG_SLOTS = new HashMap<>();
    private static final Map<String, Integer> ASSIGNED_SLOTS = new HashMap<>();
    private static final Set<Integer> USED_SLOTS = new HashSet<>();

    static {
        Map<String, String> legacy = new HashMap<>();
        legacy.put("frost_walk", "frostwalk");
        legacy.put("pain_build", "painbuild");
        legacy.put("item", "items");
        legacy.put("items", "items");
        LEGACY_ALIASES = Collections.unmodifiableMap(legacy);
    }

    private FPerms() {
    }

    public static void reloadFromConfig() {
        ACTIONS.clear();
        CONFIG_SLOTS.clear();
        ASSIGNED_SLOTS.clear();
        USED_SLOTS.clear();

        ConfigurationSection actionSection = FactionsPlugin.getInstance().getFileManager().getFperms()
                .getConfig()
                .getConfigurationSection("fperm-gui.action");
        if (actionSection == null) {
            Logger.print("Missing fperm-gui.action section in fperms.yml; no actions loaded.", Logger.PrefixType.WARNING);
            return;
        }

        Set<String> ids = new LinkedHashSet<>();
        ConfigurationSection materials = actionSection.getConfigurationSection("Materials");
        ConfigurationSection descriptions = actionSection.getConfigurationSection("Descriptions");
        ConfigurationSection slots = actionSection.getConfigurationSection("slots");
        if (materials != null) ids.addAll(materials.getKeys(false));
        if (descriptions != null) ids.addAll(descriptions.getKeys(false));
        if (slots != null) {
            for (String key : slots.getKeys(false)) {
                int slot = slots.getInt(key, -1);
                if (slot >= 0) {
                    USED_SLOTS.add(slot);
                }
                if (!key.equalsIgnoreCase("relation") && !key.equalsIgnoreCase("back")) {
                    String id = normalizeId(key);
                    if (id != null) {
                        CONFIG_SLOTS.put(id, slot);
                    }
                }
            }
        }

        for (String rawId : ids) {
            String id = normalizeId(rawId);
            if (id == null || id.isEmpty()) continue;
            register(new FPermAction(id));
        }

        if (ACTIONS.isEmpty()) {
            Logger.print("No fperms actions were detected in fperms.yml.", Logger.PrefixType.WARNING);
        }
    }

    public static Collection<FPermAction> getAll() {
        return Collections.unmodifiableCollection(ACTIONS.values());
    }

    public static List<FPermAction> getSortedBySlot() {
        List<FPermAction> list = new ArrayList<>(ACTIONS.values());
        list.sort(Comparator.comparingInt(a -> a.getSlot() < 0 ? Integer.MAX_VALUE : a.getSlot()));
        return list;
    }

    public static FPermAction getById(String id) {
        String key = normalizeId(id);
        if (key == null) return null;
        return ACTIONS.get(key);
    }

    public static FPermAction getById(FPermKey key) {
        return key == null ? null : getById(key.getId());
    }

    public static void register(FPermAction action) {
        String id = normalizeId(action.getId());
        if (id == null || id.isEmpty()) return;
        ACTIONS.put(id, action);
        if (!CONFIG_SLOTS.containsKey(id)) {
            assignSlot(id);
        }
    }

    public static String normalizeId(String id) {
        if (id == null) return null;
        String normalized = id.trim().toLowerCase(Locale.ROOT);
        normalized = normalized.replace(" ", "");
        String alias = LEGACY_ALIASES.get(normalized);
        return alias != null ? alias : normalized;
    }

    public static int getAssignedSlot(String id) {
        Integer slot = ASSIGNED_SLOTS.get(normalizeId(id));
        return slot != null ? slot : -1;
    }

    private static void assignSlot(String id) {
        int totalSlots = getTotalActionSlots();
        if (totalSlots <= 0) return;

        for (int slot = 0; slot < totalSlots; slot++) {
            if (USED_SLOTS.contains(slot)) continue;
            USED_SLOTS.add(slot);
            ASSIGNED_SLOTS.put(id, slot);
            return;
        }
    }

    private static int getTotalActionSlots() {
        ConfigurationSection actionSection = FactionsPlugin.getInstance().getFileManager().getFperms()
                .getConfig()
                .getConfigurationSection("fperm-gui.action");
        if (actionSection == null) return 0;
        int rows = actionSection.getInt("rows", 0);
        return Math.max(0, rows) * 9;
    }
}
