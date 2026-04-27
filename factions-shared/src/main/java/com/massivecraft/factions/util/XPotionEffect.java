package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum XPotionEffect {
    SPEED(0, 1, "SPEED"),
    SLOWNESS(0, 2, "SLOWNESS", "SLOW"),
    HASTE(5, 3, "HASTE", "FAST_DIGGING"),
    MINING_FATIGUE(0, 4, "MINING_FATIGUE", "SLOW_DIGGING"),
    STRENGTH(0, 5, "STRENGTH", "INCREASE_DAMAGE"),
    INSTANT_HEALTH(0, 6, "INSTANT_HEALTH", "HEAL"),
    INSTANT_DAMAGE(0, 7, "INSTANT_DAMAGE", "HARM"),
    JUMP_BOOST(0, 8, "JUMP_BOOST", "JUMP"),
    NAUSEA(0, 9, "NAUSEA", "CONFUSION"),
    REGENERATION(0, 10, "REGENERATION"),
    RESISTANCE(0, 11, "RESISTANCE", "DAMAGE_RESISTANCE"),
    FIRE_RESISTANCE(0, 12, "FIRE_RESISTANCE"),
    WATER_BREATHING(0, 13, "WATER_BREATHING"),
    INVISIBILITY(0, 14, "INVISIBILITY"),
    BLINDNESS(0, 15, "BLINDNESS"),
    NIGHT_VISION(0, 16, "NIGHT_VISION"),
    HUNGER(0, 17, "HUNGER"),
    WEAKNESS(0, 18, "WEAKNESS"),
    POISON(0, 19, "POISON"),
    WITHER(4, 20, "WITHER"),
    HEALTH_BOOST(6, 21, "HEALTH_BOOST"),
    ABSORPTION(6, 22, "ABSORPTION"),
    SATURATION(6, 23, "SATURATION"),
    GLOWING(9, 24, "GLOWING"),
    LEVITATION(9, 25, "LEVITATION"),
    LUCK(9, 26, "LUCK"),
    UNLUCK(9, 27, "UNLUCK"),
    SLOW_FALLING(13, 28, "SLOW_FALLING"),
    CONDUIT_POWER(13, 29, "CONDUIT_POWER"),
    DOLPHINS_GRACE(13, 30, "DOLPHINS_GRACE"),
    BAD_OMEN(14, 31, "BAD_OMEN"),
    HERO_OF_THE_VILLAGE(14, 32, "HERO_OF_THE_VILLAGE"),
    DARKNESS(19, 33, "DARKNESS"),
    TRIAL_OMEN(20, 34, "TRIAL_OMEN"),
    RAID_OMEN(20, 35, "RAID_OMEN"),
    WIND_CHARGED(20, 36, "WIND_CHARGED"),
    WEAVING(20, 37, "WEAVING"),
    OOZING(20, 38, "OOZING"),
    INFESTED(20, 39, "INFESTED");

    private final int version;
    private final int potion_id;
    private final List<String> aliases;

    XPotionEffect(int version, int potion_id, String... aliases) {
        this.version = version;
        this.potion_id = potion_id;
        this.aliases = Arrays.asList(aliases);
    }

    public int getVersion() {
        return version;
    }

    public int getPotionIdentifier() {
        return potion_id;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public boolean isSupported() {
        return FactionsPlugin.getInstance().version >= version;
    }

    public Optional<PotionEffectType> toPotionEffectType() {
        for (String name : aliases) {
            PotionEffectType type = PotionEffectType.getByName(name);
            if (type != null) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}