package com.massivecraft.factions.zcore.fupgrades;

public enum UpgradeType {

	CHEST("Chest"), SPAWNER("Spawner"), EXP("Exp"), CROP("Crop"), POWER("Power"), REDSTONE("Redstone"), MEMBERS("Members");

	private String id;

	UpgradeType(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return this.id;
	}
}
