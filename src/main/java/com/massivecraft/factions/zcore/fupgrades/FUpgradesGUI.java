package com.massivecraft.factions.zcore.fupgrades;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;


public class FUpgradesGUI implements Listener {
    public void openMainMenu(FPlayer fme) {
        Inventory inventory = Bukkit.createInventory(null, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.DummyItem.rows") * 9, FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.Title").replace("{faction}", fme.getFaction().getTag())));
        ItemStack dummyItem = XMaterial.matchXMaterial(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.DummyItem.Type")).parseItem();
        ItemMeta meta = dummyItem.getItemMeta();
        meta.setLore(FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().getConfig().getStringList("fupgrades.MainMenu.DummyItem.Lore")));
        meta.setDisplayName(FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.DummyItem.Name")));
        dummyItem.setItemMeta(meta);

        for (int fill = 0; fill < FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.DummyItem.rows") * 9; ++fill) {
            inventory.setItem(fill, dummyItem);
        }

        ItemStack[] items = buildItems(fme);
        List<Integer> cropSlots = FactionsPlugin.getInstance().getConfig().getIntegerList("fupgrades.MainMenu.Crops.CropItem.slots");
        List<Integer> spawnerSlots = FactionsPlugin.getInstance().getConfig().getIntegerList("fupgrades.MainMenu.Spawners.SpawnerItem.slots");
        List<Integer> expSlots = FactionsPlugin.getInstance().getConfig().getIntegerList("fupgrades.MainMenu.EXP.EXPItem.slots");
        List<Integer> chestSlots = FactionsPlugin.getInstance().getConfig().getIntegerList("fupgrades.MainMenu.Chest.ChestItem.slots");
        List<Integer> powerSlots = FactionsPlugin.getInstance().getConfig().getIntegerList("fupgrades.MainMenu.Power.PowerItem.slots");
        List<Integer> redSlots = FactionsPlugin.getInstance().getConfig().getIntegerList("fupgrades.MainMenu.Redstone.RedstoneItem.slots");
        List<Integer> memberSlots = FactionsPlugin.getInstance().getConfig().getIntegerList("fupgrades.MainMenu.Members.MembersItem.slots");
        List<Integer> reductSlots = FactionsPlugin.getInstance().getConfig().getIntegerList("fupgrades.MainMenu.DamageReduct.ReduceItem.slots");
        List<Integer> increaseSlots = FactionsPlugin.getInstance().getConfig().getIntegerList("fupgrades.MainMenu.DamageIncrease.IncreaseItem.slots");
        List<Integer> tntSlots = FactionsPlugin.getInstance().getConfig().getIntegerList("fupgrades.MainMenu.TNT.TntItem.slots");
        List<Integer> warpSlots = FactionsPlugin.getInstance().getConfig().getIntegerList("fupgrades.MainMenu.Warps.WarpItem.slots");
        List<Integer> armorSlots = FactionsPlugin.getInstance().getConfig().getIntegerList("fupgrades.MainMenu.Armor.ArmorItem.slots");

        for (Integer cropSlot : cropSlots) if (cropSlot != -1) inventory.setItem(cropSlot, items[2]);

        for (Integer spawnerSlot : spawnerSlots) if (spawnerSlot != -1) inventory.setItem(spawnerSlot, items[1]);

        for (Integer expSlot : expSlots) if (expSlot != -1) inventory.setItem(expSlot, items[0]);

        for (Integer chestSlot : chestSlots) if (chestSlot != -1) inventory.setItem(chestSlot, items[3]);

        for (Integer powerSlot : powerSlots) if (powerSlot != -1) inventory.setItem(powerSlot, items[4]);

        for (Integer redSlot : redSlots) if (redSlot != -1) inventory.setItem(redSlot, items[5]);

        for (Integer memberSlot : memberSlots) if (memberSlot != -1) inventory.setItem(memberSlot, items[6]);

        for (Integer reduceSlot : reductSlots) if (reduceSlot != -1) inventory.setItem(reduceSlot, items[7]);

        for (Integer increaseSlot : increaseSlots) if (increaseSlot != -1) inventory.setItem(increaseSlot, items[8]);

        for(Integer tntSlot : tntSlots) if(tntSlot != -1) inventory.setItem(tntSlot, items[9]);

        for(Integer warpSlot : warpSlots) if(warpSlot != -1) inventory.setItem(warpSlot, items[10]);

        for(Integer armorSlot : armorSlots) if(armorSlot != -1) inventory.setItem(armorSlot, items[11]);

        fme.getPlayer().openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null || e.getCursor() == null)
            return;

        FPlayer fme = FPlayers.getInstance().getByPlayer((Player) e.getWhoClicked());
        if (e.getView().getTitle().equalsIgnoreCase(FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.Title").replace("{faction}", fme.getFaction().getTag())))) {
            e.setCancelled(true);
            ItemStack[] items = buildItems(fme);
            ItemStack cropItem = items[2];
            ItemStack expItem = items[0];
            ItemStack chestitem = items[3];
            ItemStack spawnerItem = items[1];
            ItemStack powerItem = items[4];
            ItemStack redItem = items[5];
            ItemStack memberItem = items[6];
            ItemStack reduceItem = items[7];
            ItemStack increaseItem = items[8];
            ItemStack tntItem = items[9];
            ItemStack warpItem = items[10];
            ItemStack armorItem = items[11];

            if (e.getCurrentItem().equals(cropItem)) {
                int cropLevel = fme.getFaction().getUpgrade(UpgradeType.CROP);
                switch (cropLevel) {
                    case 3:
                        return;
                    case 2:
                        upgradeItem(fme, UpgradeType.CROP, 3, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Crops.Cost.level-3"));
                        break;
                    case 1:
                        upgradeItem(fme, UpgradeType.CROP, 2, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Crops.Cost.level-2"));
                        break;
                    case 0:
                        upgradeItem(fme, UpgradeType.CROP, 1, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Crops.Cost.level-1"));
                        break;
                }
            } else if (e.getCurrentItem().equals(powerItem)) {
                int powerLevel = fme.getFaction().getUpgrade(UpgradeType.POWER);
                switch (powerLevel) {
                    case 3:
                        return;
                    case 2:
                        upgradeItem(fme, UpgradeType.POWER, 3, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Power.Cost.level-3"));
                        updatePower(fme.getFaction());
                        break;
                    case 1:
                        upgradeItem(fme, UpgradeType.POWER, 2, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Power.Cost.level-2"));
                        updatePower(fme.getFaction());
                        break;
                    case 0:
                        upgradeItem(fme, UpgradeType.POWER, 1, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Power.Cost.level-1"));
                        updatePower(fme.getFaction());
                        break;
                }
            } else if (e.getCurrentItem().equals(spawnerItem)) {
                int spawnerLevel = fme.getFaction().getUpgrade(UpgradeType.SPAWNER);
                switch (spawnerLevel) {
                    case 3:
                        return;
                    case 2:
                        upgradeItem(fme, UpgradeType.SPAWNER, 3, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Spawners.Cost.level-3"));
                        break;
                    case 1:
                        upgradeItem(fme, UpgradeType.SPAWNER, 2, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Spawners.Cost.level-2"));
                        break;
                    case 0:
                        upgradeItem(fme, UpgradeType.SPAWNER, 1, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Spawners.Cost.level-1"));
                        break;
                }
            } else if (e.getCurrentItem().equals(expItem)) {
                int expLevel = fme.getFaction().getUpgrade(UpgradeType.EXP);

                switch (expLevel) {
                    case 3:
                        return;
                    case 2:
                        upgradeItem(fme, UpgradeType.EXP, 3, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.EXP.Cost.level-3"));
                        break;
                    case 1:
                        upgradeItem(fme, UpgradeType.EXP, 2, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.EXP.Cost.level-2"));
                        break;
                    case 0:
                        upgradeItem(fme, UpgradeType.EXP, 1, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.EXP.Cost.level-1"));
                        break;
                }
            } else if (e.getCurrentItem().equals(redItem)) {
                int redLevel = fme.getFaction().getUpgrade(UpgradeType.REDSTONE);
                switch (redLevel) {
                    case 1:
                        return;
                    case 0:
                        upgradeItem(fme, UpgradeType.REDSTONE, 1, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Redstone.Cost"));
                        break;
                }
            } else if (e.getCurrentItem().equals(chestitem)) {
                int chestLevel = fme.getFaction().getUpgrade(UpgradeType.CHEST);
                switch (chestLevel) {
                    case 3:
                        return;
                    case 2: {
                        if (upgradeItem(fme, UpgradeType.CHEST, 3, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Chest.Cost.level-3")))
                            updateChests(fme.getFaction());
                        break;
                    }
                    case 1: {
                        if (upgradeItem(fme, UpgradeType.CHEST, 2, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Chest.Cost.level-2")))
                            updateChests(fme.getFaction());
                        break;
                    }
                    case 0: {
                        if (upgradeItem(fme, UpgradeType.CHEST, 1, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Chest.Cost.level-1")))
                            updateChests(fme.getFaction());
                        break;
                    }
                }
            } else if (e.getCurrentItem().equals(armorItem)) {
                int armorLevel = fme.getFaction().getUpgrade(UpgradeType.REINFORCEDARMOR);
                switch (armorLevel) {
                    case 3:
                        return;
                    case 2: {
                        if (upgradeItem(fme, UpgradeType.REINFORCEDARMOR, 3, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Armor.Cost.level-3")))
                        break;
                    }
                    case 1: {
                        if (upgradeItem(fme, UpgradeType.REINFORCEDARMOR, 2, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Armor.Cost.level-2")))
                        break;
                    }
                    case 0: {
                        if (upgradeItem(fme, UpgradeType.REINFORCEDARMOR, 1, FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Armor.Cost.level-1")))
                        break;
                    }
                }
            } else if (e.getCurrentItem().equals(memberItem)) {
                int memberLevel = fme.getFaction().getUpgrade(UpgradeType.MEMBERS) + 1;
                if (!FactionsPlugin.getInstance().getConfig().isSet("fupgrades.MainMenu.Members.Cost.level-" + memberLevel)) {
                    return;
                }
                int cost = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Members.Cost.level-" + memberLevel);
                if (hasMoney(fme, cost)) {
                    fme.getFaction().setUpgrade(UpgradeType.MEMBERS, memberLevel);
                    fme.getPlayer().closeInventory();
                    takeMoney(fme, cost);
                }
            } else if (e.getCurrentItem().equals(reduceItem)) {
                int reduceLevel = fme.getFaction().getUpgrade(UpgradeType.DAMAGEDECREASE) + 1;
                if (!FactionsPlugin.getInstance().getConfig().isSet("fupgrades.MainMenu.DamageReduct.Cost.level-" + reduceLevel)) {
                    return;
                }
                int cost = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.DamageReduct.Cost.level-" + reduceLevel);
                if (hasMoney(fme, cost)) {
                    fme.getFaction().setUpgrade(UpgradeType.DAMAGEDECREASE, reduceLevel);
                    fme.getPlayer().closeInventory();
                    takeMoney(fme, cost);
                }
            } else if (e.getCurrentItem().equals(increaseItem)) {
                int increaseLevel = fme.getFaction().getUpgrade(UpgradeType.DAMAGEINCREASE) + 1;
                if (!FactionsPlugin.getInstance().getConfig().isSet("fupgrades.MainMenu.DamageIncrease.Cost.level-" + increaseLevel)) {
                    return;
                }
                int cost = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.DamageIncrease.Cost.level-" + increaseLevel);
                if (hasMoney(fme, cost)) {
                    fme.getFaction().setUpgrade(UpgradeType.DAMAGEINCREASE, increaseLevel);
                    fme.getPlayer().closeInventory();
                    takeMoney(fme, cost);
                }
            } else if(e.getCurrentItem().equals(tntItem)){
                int tntLevel = fme.getFaction().getUpgrade(UpgradeType.TNT) + 1;
                if (!FactionsPlugin.getInstance().getConfig().isSet("fupgrades.MainMenu.TNT.Cost.level-" + tntLevel)) {
                    return;
                }
                int cost = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.TNT.Cost.level-" + tntLevel);
                if (hasMoney(fme, cost)) {
                    fme.getFaction().setUpgrade(UpgradeType.TNT, tntLevel);
                    fme.getPlayer().closeInventory();
                    takeMoney(fme, cost);
                    updateTntBanks(fme.getFaction());
                }
            } else if(e.getCurrentItem().equals(warpItem)){
                int warpLevel = fme.getFaction().getUpgrade(UpgradeType.WARP) + 1;
                if (!FactionsPlugin.getInstance().getConfig().isSet("fupgrades.MainMenu.Warps.Cost.level-" + warpLevel)) {
                    return;
                }
                int cost = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Warps.Cost.level-" + warpLevel);
                if (hasMoney(fme, cost)) {
                    fme.getFaction().setUpgrade(UpgradeType.WARP, warpLevel);
                    fme.getPlayer().closeInventory();
                    takeMoney(fme, cost);
                    setWarpLimit(fme.getFaction());
                }
            }
        }
    }

    private void updateChests(Faction faction) {
        String invName = FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fchest.Inventory-Title"));

        for (HumanEntity player : faction.getChestInventory().getViewers()) {
            if (player.getInventory().getTitle() != null && player.getInventory().getTitle().equalsIgnoreCase(invName))
                player.closeInventory();
        }

        int level = faction.getUpgrade(UpgradeType.CHEST);
        int size = 1;

        switch (level) {
            case 1:
                size = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Chest.Chest-Size.level-1");
                break;
            case 2:
                size = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Chest.Chest-Size.level-2");
                break;
            case 3:
                size = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Chest.Chest-Size.level-3");
                break;
        }
        faction.setChestSize(size * 9);
    }

    private void updatePower(Faction faction) {
        int level = faction.getUpgrade(UpgradeType.POWER);
        double power = 0.0;

        switch (level) {
            case 1:
                power = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Power.Power-Boost.level-1");
                break;
            case 2:
                power = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Power.Power-Boost.level-2");
                break;
            case 3:
                power = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Power.Power-Boost.level-3");
                break;
        }
        faction.setPowerBoost(power);
    }

    @SuppressWarnings("Duplicates")
    private ItemStack[] buildItems(FPlayer fme) {
        byte expData = (byte) (FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.EXP.EXPItem.Damage"));
        Material expMaterial = XMaterial.parseMaterial(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.EXP.EXPItem.Type"), expData);
        int expAmt = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.EXP.EXPItem.Amount");
        String expName = FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.EXP.EXPItem.Name"));
        List<String> expLore = FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().getConfig().getStringList("fupgrades.MainMenu.EXP.EXPItem.Lore"));
        int expLevel = fme.getFaction().getUpgrade(UpgradeType.EXP);

        for (int i = 0; i <= expLore.size() - 1; i++)
            expLore.set(i, expLore.get(i).replace("{level}", expLevel + ""));

        ItemStack expItem = FactionsPlugin.getInstance().createItem(expMaterial, expAmt, expData, expName, expLore);

        if (expLevel >= 1) {
            ItemMeta itemMeta = expItem.getItemMeta();
            if (!FactionsPlugin.getInstance().mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);

            expItem.setItemMeta(itemMeta);
            expItem.setAmount(expLevel);
        }

        byte spawnerData = (byte) (FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Spawners.SpawnerItem.Damage"));
        Material spawnerMaterial = XMaterial.parseMaterial(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.Spawners.SpawnerItem.Type"), spawnerData);
        int spawnerAmt = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Spawners.SpawnerItem.Amount");
        String spawnerName = FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.Spawners.SpawnerItem.Name"));
        List<String> spawnerLore = FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().getConfig().getStringList("fupgrades.MainMenu.Spawners.SpawnerItem.Lore"));
        int spawnerLevel = fme.getFaction().getUpgrade(UpgradeType.SPAWNER);

        for (int i = 0; i <= spawnerLore.size() - 1; i++) {
            spawnerLore.set(i, spawnerLore.get(i).replace("{level}", spawnerLevel + ""));
        }


        byte cropData = (byte) (FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Crops.CropItem.Damage"));
        Material cropMaterial = XMaterial.parseMaterial(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.Crops.CropItem.Type"), cropData);
        int cropAmt = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Crops.CropItem.Amount");
        String cropName = FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.Crops.CropItem.Name"));
        List<String> cropLore = FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().getConfig().getStringList("fupgrades.MainMenu.Crops.CropItem.Lore"));
        int cropLevel = fme.getFaction().getUpgrade(UpgradeType.CROP);

        for (int i = 0; i <= cropLore.size() - 1; i++) {
            String line = cropLore.get(i);
            line = line.replace("{level}", cropLevel + "");
            cropLore.set(i, line);
        }

        ItemStack cropItem = FactionsPlugin.getInstance().createItem(cropMaterial, cropAmt, cropData, cropName, cropLore);
        cropItem.getItemMeta().setLore(cropLore);

        if (cropLevel >= 1) {
            ItemMeta itemMeta = cropItem.getItemMeta();
            if (!FactionsPlugin.getInstance().mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);
            cropItem.setItemMeta(itemMeta);

            cropItem.setAmount(cropLevel);
        }

        ItemStack spawnerItem = FactionsPlugin.getInstance().createItem(spawnerMaterial, spawnerAmt, spawnerData, spawnerName, spawnerLore);
        spawnerItem.getItemMeta().setLore(spawnerLore);
        if (spawnerLevel >= 1) {
            ItemMeta itemMeta = spawnerItem.getItemMeta();
            if (!FactionsPlugin.getInstance().mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);

            spawnerItem.setItemMeta(itemMeta);
            spawnerItem.setAmount(spawnerLevel);
        }
        byte chestData = (byte) (FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Chest.ChestItem.Damage"));
        Material chestMaterial = XMaterial.parseMaterial(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.Chest.ChestItem.Type"), chestData);
        int chestAmt = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Chest.ChestItem.Amount");
        String chestName = FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.Chest.ChestItem.Name", "&e&lUpgrade Chest Size"));
        List<String> chestLore = FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().getConfig().getStringList("fupgrades.MainMenu.Chest.ChestItem.Lore"));
        int chestlevel = fme.getFaction().getUpgrade(UpgradeType.CHEST);

        for (int i = 0; i <= chestLore.size() - 1; i++) {
            String line = chestLore.get(i);
            line = line.replace("{level}", chestlevel + "");
            chestLore.set(i, line);
        }

        ItemStack chestItem = FactionsPlugin.getInstance().createItem(chestMaterial, chestAmt, chestData, chestName, chestLore);

        if (chestlevel >= 1) {
            ItemMeta itemMeta = chestItem.getItemMeta();
            if (!FactionsPlugin.getInstance().mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);

            chestItem.setItemMeta(itemMeta);
            chestItem.setAmount(chestlevel);
        }

        byte memberData = (byte) (FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Members.MembersItem.Damage"));
        Material memberMaterial = XMaterial.parseMaterial(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.Members.MembersItem.Type"), memberData);
        int memberAmt = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Members.MembersItem.Amount");
        String memberName = FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.Members.MembersItem.Name", "&e&lUpgrade Member Size"));
        List<String> memberLore = FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().getConfig().getStringList("fupgrades.MainMenu.Members.MembersItem.Lore"));
        int memberlevel = fme.getFaction().getUpgrade(UpgradeType.MEMBERS);

        for (int i = 0; i <= memberLore.size() - 1; i++) {
            String line = memberLore.get(i);
            line = line.replace("{level}", memberlevel + "");
            memberLore.set(i, line);
        }

        ItemStack memberItem = FactionsPlugin.getInstance().createItem(memberMaterial, memberAmt, memberData, memberName, memberLore);

        if (memberlevel >= 1) {
            ItemMeta itemMeta = memberItem.getItemMeta();
            if (!FactionsPlugin.getInstance().mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);

            memberItem.setItemMeta(itemMeta);
            memberItem.setAmount(memberlevel);
        }

        byte powerData = (byte) (FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Power.PowerItem.Damage"));
        Material powerMaterial = XMaterial.parseMaterial(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.Power.PowerItem.Type"), powerData);
        int powerAmt = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Power.PowerItem.Amount");
        String powerName = FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.Power.PowerItem.Name"));
        List<String> powerLore = FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().getConfig().getStringList("fupgrades.MainMenu.Power.PowerItem.Lore"));
        int powerLevel = fme.getFaction().getUpgrade(UpgradeType.POWER);

        for (int i = 0; i <= powerLore.size() - 1; i++) {
            String line = powerLore.get(i);
            line = line.replace("{level}", powerLevel + "");
            powerLore.set(i, line);
        }

        ItemStack powerItem = FactionsPlugin.getInstance().createItem(powerMaterial, powerAmt, powerData, powerName, powerLore);
        powerItem.getItemMeta().setLore(powerLore);

        if (powerLevel >= 1) {
            ItemMeta itemMeta = powerItem.getItemMeta();
            if (!FactionsPlugin.getInstance().mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);
            powerItem.setItemMeta(itemMeta);

            powerItem.setAmount(powerLevel);
        }

        byte redData = (byte) (FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Redstone.RedstoneItem.Damage"));
        Material redMaterial = XMaterial.parseMaterial(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.Redstone.RedstoneItem.Type"), redData);
        int redAmt = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Redstone.RedstoneItem.Amount");
        String redName = FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.Redstone.RedstoneItem.Name"));
        List<String> redLore = FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().getConfig().getStringList("fupgrades.MainMenu.Redstone.RedstoneItem.Lore"));
        int redLevel = fme.getFaction().getUpgrade(UpgradeType.REDSTONE);

        for (int i = 0; i <= redLore.size() - 1; i++) {
            String line = redLore.get(i);
            line = line.replace("{level}", redLevel + "");
            redLore.set(i, line);
        }

        ItemStack redItem = FactionsPlugin.getInstance().createItem(redMaterial, redAmt, redData, redName, redLore);
        redItem.getItemMeta().setLore(redLore);

        if (redLevel >= 1) {
            ItemMeta itemMeta = redItem.getItemMeta();
            if (!FactionsPlugin.getInstance().mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);
            redItem.setItemMeta(itemMeta);
            redItem.setAmount(redLevel);
        }

        byte reduceData = (byte) (FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.DamageReduct.ReduceItem.Damage"));
        Material reduceMaterial = XMaterial.parseMaterial(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.DamageReduct.ReduceItem.Type"), reduceData);
        int reduceAmt = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.DamageReduct.ReduceItem.Amount");
        String reduceName = FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.DamageReduct.ReduceItem.Name"));
        List<String> reduceLore = FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().getConfig().getStringList("fupgrades.MainMenu.DamageReduct.ReduceItem.Lore"));
        int reduceLevel = fme.getFaction().getUpgrade(UpgradeType.DAMAGEDECREASE);

        for (int i = 0; i <= reduceLore.size() - 1; i++) {
            String line = reduceLore.get(i);
            line = line.replace("{level}", reduceLevel + "");
            reduceLore.set(i, line);
        }

        ItemStack reduceItem = FactionsPlugin.getInstance().createItem(reduceMaterial, reduceAmt, reduceData, reduceName, reduceLore);
        reduceItem.getItemMeta().setLore(reduceLore);

        if (reduceLevel >= 1) {
            ItemMeta itemMeta = reduceItem.getItemMeta();
            if (!FactionsPlugin.getInstance().mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);
            reduceItem.setItemMeta(itemMeta);

            reduceItem.setAmount(reduceLevel);
        }

        byte increaseData = (byte) (FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.DamageIncrease.IncreaseItem.Damage"));
        Material increaseMaterial = XMaterial.parseMaterial(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.DamageIncrease.IncreaseItem.Type"), increaseData);
        int increaseAmt = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.DamageIncrease.IncreaseItem.Amount");
        String increaseName = FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.DamageIncrease.IncreaseItem.Name"));
        List<String> increaseLore = FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().getConfig().getStringList("fupgrades.MainMenu.DamageIncrease.IncreaseItem.Lore"));
        int increaseLevel = fme.getFaction().getUpgrade(UpgradeType.DAMAGEINCREASE);

        for (int i = 0; i <= increaseLore.size() - 1; i++) {
            String line = increaseLore.get(i);
            line = line.replace("{level}", increaseLevel + "");
            increaseLore.set(i, line);
        }

        ItemStack increaseItem = FactionsPlugin.getInstance().createItem(increaseMaterial, increaseAmt, increaseData, increaseName, increaseLore);
        increaseItem.getItemMeta().setLore(increaseLore);

        if (increaseLevel >= 1) {
            ItemMeta itemMeta = increaseItem.getItemMeta();
            if (!FactionsPlugin.getInstance().mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);
            increaseItem.setItemMeta(itemMeta);

            increaseItem.setAmount(increaseLevel);
        }
        byte tntData = (byte) (FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.TNT.TntItem.Damage"));
        Material tntMaterial = XMaterial.parseMaterial(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.TNT.TntItem.Type"), tntData);
        int tntAmt = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.TNT.TntItem.Amount");
        String tntName = FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.TNT.TntItem.Name"));
        List<String> tntLore = FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().getConfig().getStringList("fupgrades.MainMenu.TNT.TntItem.Lore"));
        int tntLevel = fme.getFaction().getUpgrade(UpgradeType.TNT);

        for (int i = 0; i <= tntLore.size() - 1; i++) {
            String line = tntLore.get(i);
            line = line.replace("{level}", tntLevel + "");
            tntLore.set(i, line);
        }

        ItemStack tntItem = FactionsPlugin.getInstance().createItem(tntMaterial, tntAmt, tntData, tntName, tntLore);
        tntItem.getItemMeta().setLore(tntLore);

        if (tntLevel >= 1) {
            ItemMeta itemMeta = tntItem.getItemMeta();
            if (!FactionsPlugin.getInstance().mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);
            tntItem.setItemMeta(itemMeta);

            tntItem.setAmount(tntLevel);
        }
        byte warpData = (byte) (FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Warps.WarpItem.Damage"));
        Material warpMaterial = XMaterial.parseMaterial(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.Warps.WarpItem.Type"), warpData);
        int warpAmt = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Warps.WarpItem.Amount");
        String warpName = FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.Warps.WarpItem.Name"));
        List<String> warpLore = FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().getConfig().getStringList("fupgrades.MainMenu.Warps.WarpItem.Lore"));
        int warpLevel = fme.getFaction().getUpgrade(UpgradeType.WARP);

        for (int i = 0; i <= warpLore.size() - 1; i++) {
            String line = warpLore.get(i);
            line = line.replace("{level}", warpLevel + "");
            warpLore.set(i, line);
        }

        ItemStack warpItem = FactionsPlugin.getInstance().createItem(warpMaterial, warpAmt, warpData, warpName, warpLore);
        warpItem.getItemMeta().setLore(warpLore);

        if (warpLevel >= 1) {
            ItemMeta itemMeta = warpItem.getItemMeta();
            if (!FactionsPlugin.getInstance().mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);
            warpItem.setItemMeta(itemMeta);

            warpItem.setAmount(warpLevel);
        }

        byte armorData = (byte) (FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Armor.ArmorItem.Damage"));
        Material armorMaterial = XMaterial.parseMaterial(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.Armor.ArmorItem.Type"), warpData);
        int armorAmt = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Armor.ArmorItem.Amount");
        String armorName = FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fupgrades.MainMenu.Armor.ArmorItem.Name"));
        List<String> armorLore = FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().getConfig().getStringList("fupgrades.MainMenu.Armor.ArmorItem.Lore"));
        int armorLevel = fme.getFaction().getUpgrade(UpgradeType.REINFORCEDARMOR);

        for (int i = 0; i <= armorLore.size() - 1; i++) {
            String line = armorLore.get(i);
            line = line.replace("{level}", armorLevel + "");
            armorLore.set(i, line);
        }

        ItemStack armorItem = FactionsPlugin.getInstance().createItem(armorMaterial, armorAmt, armorData, armorName, armorLore);
        armorItem.getItemMeta().setLore(armorLore);

        if (armorLevel >= 1) {
            ItemMeta itemMeta = armorItem.getItemMeta();
            if (!FactionsPlugin.getInstance().mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);
            armorItem.setItemMeta(itemMeta);

            armorItem.setAmount(armorLevel);
        }

        return new ItemStack[]{expItem, spawnerItem, cropItem, chestItem, powerItem, redItem, memberItem, reduceItem, increaseItem, tntItem, warpItem, armorItem};
    }

    private void updateTntBanks(Faction faction) {
        int level = faction.getUpgrade(UpgradeType.TNT);
        int size = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.TNT.tnt-limit.level-" + (level));
        faction.setTntBankLimit(size);
    }

    private void setWarpLimit(Faction faction) {
        int level = faction.getUpgrade(UpgradeType.WARP);
        int size = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Warps.warp-limit.level-" + (level));
        faction.setWarpsLimit(size);
    }

    private boolean hasMoney(FPlayer fme, int amt) {
        return fme.hasMoney(amt);
    }

    private void takeMoney(FPlayer fme, int amt) {
        fme.takeMoney(amt);
    }

    private boolean upgradeItem(FPlayer fme, UpgradeType upgrade, int level, int cost) {
        if (hasMoney(fme, cost)) {
            takeMoney(fme, cost);
            fme.getFaction().setUpgrade(upgrade, level);
            fme.getPlayer().closeInventory();
            return true;
        }
        return false;
    }
}
