package com.massivecraft.factions.zcore.fperms;

import org.bukkit.ChatColor;

import com.massivecraft.factions.SaberFactions;

public enum Access {
	ALLOW("Allow"),
	DENY("Deny"),
	UNDEFINED("Undefined");

	private final String name;

	Access(String name) {
		this.name = name;
	}

	/**
	 * Case insensitive check for access.
	 *
	 * @param check
	 * @return
	 */
	public static Access fromString(String check) {
		for (Access access : values())
			if (access.name().equalsIgnoreCase(check))
				return access;
		return null;
	}

	public String getName() {
		return this.name.toLowerCase();
	}

	@Override
	public String toString() {
		return name();
	}

	public String getColor() { return SaberFactions.plugin.getConfig().getString("fperm-gui.action.Access-Colors." + this.name); }

	public static Access booleanToAccess(boolean access) {
		if (access) return Access.ALLOW;
		else return Access.DENY;
	}
}
