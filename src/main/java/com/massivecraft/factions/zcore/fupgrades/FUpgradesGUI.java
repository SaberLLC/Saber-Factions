package com.massivecraft.factions.zcore.fupgrades;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.SaberFactions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
        Inventory inventory = Bukkit.createInventory(null, 45, SaberFactions.plugin.color(SaberFactions.plugin.getConfig().getString("fupgrades.MainMenu.Title").replace("{faction}", fme.getFaction().getTag())));
        List<Integer> dummySlots = SaberFactions.plugin.getConfig().getIntegerList("fupgrades.MainMenu.DummyItem.slots");
        Material dummyMaterial = Material.getMaterial(SaberFactions.plugin.getConfig().getString("fupgrades.MainMenu.DummyItem.Type"));
        int dummyAmount = SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.DummyItem.Amount");
        short dummyData = Short.parseShort(SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.DummyItem.Damage") + "");
        ItemStack dummyItem = SaberFactions.plugin.createItem(dummyMaterial,
                dummyAmount,
                dummyData,
                SaberFactions.plugin.color(SaberFactions.plugin.getConfig().getString("fupgrades.MainMenu.DummyItem.Name")),
                SaberFactions.plugin.colorList(SaberFactions.plugin.getConfig().getStringList("fupgrades.MainMenu.DummyItem.Lore")));

        for (int i = 0; i <= dummySlots.size() - 1; i++) {
            inventory.setItem(dummySlots.get(i), dummyItem);
        }

        ItemStack[] items = buildItems(fme);
        List<Integer> cropSlots = SaberFactions.plugin.getConfig().getIntegerList("fupgrades.MainMenu.Crops.CropItem.slots");
        List<Integer> spawnerSlots = SaberFactions.plugin.getConfig().getIntegerList("fupgrades.MainMenu.Spawners.SpawnerItem.slots");
        List<Integer> expSlots = SaberFactions.plugin.getConfig().getIntegerList("fupgrades.MainMenu.EXP.EXPItem.slots");
        List<Integer> chestSlots = SaberFactions.plugin.getConfig().getIntegerList("fupgrades.MainMenu.Chest.ChestItem.slots");
        List<Integer> powerSlots = SaberFactions.plugin.getConfig().getIntegerList("fupgrades.MainMenu.Power.PowerItem.slots");
        List<Integer> redSlots = SaberFactions.plugin.getConfig().getIntegerList("fupgrades.MainMenu.Redstone.RedstoneItem.slots");
        List<Integer> memberSlots = SaberFactions.plugin.getConfig().getIntegerList("fupgrades.MainMenu.Members.MembersItem.slots");

        for (int i = 0; i < cropSlots.size(); i++)
            if (cropSlots.get(i) != -1) inventory.setItem(cropSlots.get(i), items[2]);

        for (int i = 0; i < spawnerSlots.size(); i++)
            if (spawnerSlots.get(i) != -1) inventory.setItem(spawnerSlots.get(i), items[1]);

        for (int i = 0; i < expSlots.size(); i++)
            if (expSlots.get(i) != -1) inventory.setItem(expSlots.get(i), items[0]);

        for (int i = 0; i < chestSlots.size(); i++)
            if (chestSlots.get(i) != -1) inventory.setItem(chestSlots.get(i), items[3]);

        for (int i = 0; i < powerSlots.size(); i++)
            if (powerSlots.get(i) != -1) inventory.setItem(powerSlots.get(i), items[4]);

        for (int i = 0; i < redSlots.size(); i++)
            if (redSlots.get(i) != -1) inventory.setItem(redSlots.get(i), items[5]);

        for (int i = 0; i < memberSlots.size(); i++)
            if (memberSlots.get(i) != -1) inventory.setItem(memberSlots.get(i), items[6]);

        fme.getPlayer().openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null || e.getCursor() == null)
            return;

        FPlayer fme = FPlayers.getInstance().getByPlayer((Player) e.getWhoClicked());
        if (e.getClickedInventory().getTitle().equalsIgnoreCase(SaberFactions.plugin.color(SaberFactions.plugin.getConfig().getString("fupgrades.MainMenu.Title").replace("{faction}", fme.getFaction().getTag())))) {
            e.setCancelled(true);
            ItemStack[] items = buildItems(fme);
            ItemStack cropItem = items[2];
            ItemStack expItem = items[0];
            ItemStack chestitem = items[3];
            ItemStack spawnerItem = items[1];
            ItemStack powerItem = items[4];
            ItemStack redItem = items[5];
            ItemStack memberItem = items[6];

            if (e.getCurrentItem().equals(cropItem)) {
                int cropLevel = fme.getFaction().getUpgrade(UpgradeType.CROP);
                switch (cropLevel) {
                    case 3:
                        return;
                    case 2:
                        upgradeItem(fme, UpgradeType.CROP, 3, SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Crops.Cost.level-3"));
                        break;
                    case 1:
                        upgradeItem(fme, UpgradeType.CROP, 2, SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Crops.Cost.level-2"));
                        break;
                    case 0:
                        upgradeItem(fme, UpgradeType.CROP, 1, SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Crops.Cost.level-1"));
                        break;
                }
            } else if (e.getCurrentItem().equals(powerItem)) {
                int powerLevel = fme.getFaction().getUpgrade(UpgradeType.POWER);
                switch (powerLevel) {
                    case 3:
                        return;
                    case 2:
                        upgradeItem(fme, UpgradeType.POWER, 3, SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Power.Cost.level-3"));
                        updatePower(fme.getFaction());
                        break;
                    case 1:
                        upgradeItem(fme, UpgradeType.POWER, 2, SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Power.Cost.level-2"));
                        updatePower(fme.getFaction());
                        break;
                    case 0:
                        upgradeItem(fme, UpgradeType.POWER, 1, SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Power.Cost.level-1"));
                        updatePower(fme.getFaction());
                        break;
                }
            } else if (e.getCurrentItem().equals(spawnerItem)) {
                int spawnerLevel = fme.getFaction().getUpgrade(UpgradeType.SPAWNER);
                switch (spawnerLevel) {
                    case 3:
                        return;
                    case 2:
                        upgradeItem(fme, UpgradeType.SPAWNER, 3, SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Spawners.Cost.level-3"));
                        break;
                    case 1:
                        upgradeItem(fme, UpgradeType.SPAWNER, 2, SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Spawners.Cost.level-2"));
                        break;
                    case 0:
                        upgradeItem(fme, UpgradeType.SPAWNER, 1, SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Spawners.Cost.level-1"));
                        break;
                }
            } else if (e.getCurrentItem().equals(expItem)) {
                int expLevel = fme.getFaction().getUpgrade(UpgradeType.EXP);

                switch (expLevel) {
                    case 3:
                        return;
                    case 2:
                        upgradeItem(fme, UpgradeType.EXP, 3, SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.EXP.Cost.level-3"));
                        break;
                    case 1:
                        upgradeItem(fme, UpgradeType.EXP, 2, SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.EXP.Cost.level-2"));
                        break;
                    case 0:
                        upgradeItem(fme, UpgradeType.EXP, 1, SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.EXP.Cost.level-1"));
                        break;
                }
            } else if (e.getCurrentItem().equals(redItem)) {
                int redLevel = fme.getFaction().getUpgrade(UpgradeType.REDSTONE);
                switch (redLevel) {
                    case 1:
                        return;
                    case 0:
                        upgradeItem(fme, UpgradeType.REDSTONE, 1, SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Redstone.Cost"));
                        break;
                }
            } else if (e.getCurrentItem().equals(chestitem)) {
                int chestLevel = fme.getFaction().getUpgrade(UpgradeType.CHEST);
                switch (chestLevel) {
                    case 3:
                        return;
                    case 2: {
                        if (upgradeItem(fme, UpgradeType.CHEST, 3, SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Chest.Cost.level-3")))
                            updateChests(fme.getFaction());
                        break;
                    }
                    case 1: {
                        if (upgradeItem(fme, UpgradeType.CHEST, 2, SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Chest.Cost.level-2")))
                            updateChests(fme.getFaction());
                        break;
                    }
                    case 0: {
                        if (upgradeItem(fme, UpgradeType.CHEST, 1, SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Chest.Cost.level-1")))
                            updateChests(fme.getFaction());
                        break;
                    }
                }
            } else if(e.getCurrentItem().equals(memberItem)){
                int memberLevel = fme.getFaction().getUpgrade(UpgradeType.MEMBERS) + 1;
                if(!SaberFactions.plugin.getConfig().isSet("fupgrades.MainMenu.Members.Cost.level-" + memberLevel)){
                    return;
                }
                int cost = SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Members.Cost.level-" + memberLevel);
                if(hasMoney(fme, cost)){
                    fme.getFaction().setUpgrade(UpgradeType.MEMBERS, memberLevel);
                    fme.getPlayer().closeInventory();
                    takeMoney(fme, cost);
                }
            }
        }
    }

    private void updateChests(Faction faction) {
        String invName = SaberFactions.plugin.color(SaberFactions.plugin.getConfig().getString("fchest.Inventory-Title"));

        for (Player player : faction.getOnlinePlayers()) {
            if (player.getInventory().getTitle() != null && player.getInventory().getTitle().equalsIgnoreCase(invName))
                player.closeInventory();
        }

        int level = faction.getUpgrade(UpgradeType.CHEST);
        int size = 1;

        switch (level) {
            case 1:
                size = SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Chest.Chest-Size.level-1");
                break;
            case 2:
                size = SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Chest.Chest-Size.level-2");
                break;
            case 3:
                size = SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Chest.Chest-Size.level-3");
                break;
        }
        faction.setChestSize(size * 9);
    }

    private void updatePower(Faction faction) {
        int level = faction.getUpgrade(UpgradeType.POWER);
        double power = 0.0;

        switch (level) {
            case 1:
                power = SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Power.Power-Boost.level-1");
                break;
            case 2:
                power = SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Power.Power-Boost.level-2");
                break;
            case 3:
                power = SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Power.Power-Boost.level-3");
                break;
        }
        faction.setPowerBoost(power);
    }

    @SuppressWarnings("Duplicates")
    private ItemStack[] buildItems(FPlayer fme) {
        Material expMaterial = Material.getMaterial(SaberFactions.plugin.getConfig().getString("fupgrades.MainMenu.EXP.EXPItem.Type"));
        int expAmt = SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.EXP.EXPItem.Amount");
        short expData = Short.parseShort(SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.EXP.EXPItem.Damage") + "");
        String expName = SaberFactions.plugin.color(SaberFactions.plugin.getConfig().getString("fupgrades.MainMenu.EXP.EXPItem.Name"));
        List<String> expLore = SaberFactions.plugin.colorList(SaberFactions.plugin.getConfig().getStringList("fupgrades.MainMenu.EXP.EXPItem.Lore"));
        int expLevel = fme.getFaction().getUpgrade(UpgradeType.EXP);

        for (int i = 0; i <= expLore.size() - 1; i++)
            expLore.set(i, expLore.get(i).replace("{level}", expLevel + ""));

        ItemStack expItem = SaberFactions.plugin.createItem(expMaterial, expAmt, expData, expName, expLore);

        if (expLevel >= 1) {
            ItemMeta itemMeta = expItem.getItemMeta();
            if (!SaberFactions.plugin.mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);

            expItem.setItemMeta(itemMeta);
            expItem.setAmount(expLevel);
        }

        Material spawnerMaterial = Material.getMaterial(SaberFactions.plugin.getConfig().getString("fupgrades.MainMenu.Spawners.SpawnerItem.Type"));
        int spawnerAmt = SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Spawners.SpawnerItem.Amount");
        short spawnerData = Short.parseShort(SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Spawners.SpawnerItem.Damage") + "");
        String spawnerName = SaberFactions.plugin.color(SaberFactions.plugin.getConfig().getString("fupgrades.MainMenu.Spawners.SpawnerItem.Name"));
        List<String> spawnerLore = SaberFactions.plugin.colorList(SaberFactions.plugin.getConfig().getStringList("fupgrades.MainMenu.Spawners.SpawnerItem.Lore"));
        int spawnerLevel = fme.getFaction().getUpgrade(UpgradeType.SPAWNER);

        for (int i = 0; i <= spawnerLore.size() - 1; i++) {
            spawnerLore.set(i, spawnerLore.get(i).replace("{level}", spawnerLevel + ""));
        }



        Material cropMaterial = Material.getMaterial(SaberFactions.plugin.getConfig().getString("fupgrades.MainMenu.Crops.CropItem.Type"));
        int cropAmt = SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Crops.CropItem.Amount");
        short cropData = Short.parseShort(SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Crops.CropItem.Damage") + "");
        String cropName = SaberFactions.plugin.color(SaberFactions.plugin.getConfig().getString("fupgrades.MainMenu.Crops.CropItem.Name"));
        List<String> cropLore = SaberFactions.plugin.colorList(SaberFactions.plugin.getConfig().getStringList("fupgrades.MainMenu.Crops.CropItem.Lore"));
        int cropLevel = fme.getFaction().getUpgrade(UpgradeType.CROP);

        for (int i = 0; i <= cropLore.size() - 1; i++) {
            String line = cropLore.get(i);
            line = line.replace("{level}", cropLevel + "");
            cropLore.set(i, line);
        }

        ItemStack cropItem = SaberFactions.plugin.createItem(cropMaterial, cropAmt, cropData, cropName, cropLore);
        cropItem.getItemMeta().setLore(cropLore);

        if (cropLevel >= 1) {
            ItemMeta itemMeta = cropItem.getItemMeta();
            if (!SaberFactions.plugin.mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);
            cropItem.setItemMeta(itemMeta);

            cropItem.setAmount(cropLevel);
        }

        ItemStack spawnerItem = SaberFactions.plugin.createItem(spawnerMaterial, spawnerAmt, spawnerData, spawnerName, spawnerLore);
        spawnerItem.getItemMeta().setLore(spawnerLore);
        if (spawnerLevel >= 1) {
            ItemMeta itemMeta = spawnerItem.getItemMeta();
            if (!SaberFactions.plugin.mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);

            spawnerItem.setItemMeta(itemMeta);
            spawnerItem.setAmount(spawnerLevel);
        }

        Material chestMaterial = Material.getMaterial(SaberFactions.plugin.getConfig().getString("fupgrades.MainMenu.Chest.ChestItem.Type"));
        int chesttAmt = SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Chest.ChestItem.Amount");
        short chestData = Short.parseShort(SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Chest.ChestItem.Damage") + "");
        String chestName = SaberFactions.plugin.color(SaberFactions.plugin.getConfig().getString("fupgrades.MainMenu.Chest.ChestItem.Name", "&e&lUpgrade Chest Size"));
        List<String> chestLore = SaberFactions.plugin.colorList(SaberFactions.plugin.getConfig().getStringList("fupgrades.MainMenu.Chest.ChestItem.Lore"));
        int chestlevel = fme.getFaction().getUpgrade(UpgradeType.CHEST);

        for (int i = 0; i <= chestLore.size() - 1; i++) {
            String line = chestLore.get(i);
            line = line.replace("{level}", chestlevel + "");
            chestLore.set(i, line);
        }

        ItemStack chestItem = SaberFactions.plugin.createItem(chestMaterial, chesttAmt, chestData, chestName, chestLore);

        if (chestlevel >= 1) {
            ItemMeta itemMeta = chestItem.getItemMeta();
            if (!SaberFactions.plugin.mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);

            chestItem.setItemMeta(itemMeta);
            chestItem.setAmount(chestlevel);
        }

        Material memberMaterial = Material.getMaterial(SaberFactions.plugin.getConfig().getString("fupgrades.MainMenu.Members.MembersItem.Type"));
        int memberAmt = SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Members.MembersItem.Amount");
        short memberData = Short.parseShort(SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Members.MembersItem.Damage") + "");
        String memberName = SaberFactions.plugin.color(SaberFactions.plugin.getConfig().getString("fupgrades.MainMenu.Members.MembersItem.Name", "&e&lUpgrade Member Size"));
        List<String> memberLore = SaberFactions.plugin.colorList(SaberFactions.plugin.getConfig().getStringList("fupgrades.MainMenu.Members.MembersItem.Lore"));
        int memberlevel = fme.getFaction().getUpgrade(UpgradeType.MEMBERS);

        for (int i = 0; i <= memberLore.size() - 1; i++) {
            String line = memberLore.get(i);
            line = line.replace("{level}", memberlevel + "");
            memberLore.set(i, line);
        }

        ItemStack memberItem = SaberFactions.plugin.createItem(memberMaterial, memberAmt, memberData, memberName, memberLore);

        if (memberlevel >= 1) {
            ItemMeta itemMeta = memberItem.getItemMeta();
            if (!SaberFactions.plugin.mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);

            memberItem.setItemMeta(itemMeta);
            memberItem.setAmount(memberlevel);
        }

        Material powerMaterial = Material.getMaterial(SaberFactions.plugin.getConfig().getString("fupgrades.MainMenu.Power.PowerItem.Type"));
        int powerAmt = SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Power.PowerItem.Amount");
        short powerData = Short.parseShort(SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Power.PowerItem.Damage") + "");
        String powerName = SaberFactions.plugin.color(SaberFactions.plugin.getConfig().getString("fupgrades.MainMenu.Power.PowerItem.Name"));
        List<String> powerLore = SaberFactions.plugin.colorList(SaberFactions.plugin.getConfig().getStringList("fupgrades.MainMenu.Power.PowerItem.Lore"));
        int powerLevel = fme.getFaction().getUpgrade(UpgradeType.POWER);

        for (int i = 0; i <= powerLore.size() - 1; i++) {
            String line = powerLore.get(i);
            line = line.replace("{level}", powerLevel + "");
            powerLore.set(i, line);
        }

        ItemStack powerItem = SaberFactions.plugin.createItem(powerMaterial, powerAmt, powerData, powerName, powerLore);
        powerItem.getItemMeta().setLore(powerLore);

        if (powerLevel >= 1) {
            ItemMeta itemMeta = powerItem.getItemMeta();
            if (!SaberFactions.plugin.mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);
            powerItem.setItemMeta(itemMeta);

            powerItem.setAmount(powerLevel);
        }

        Material redMaterial = Material.getMaterial(SaberFactions.plugin.getConfig().getString("fupgrades.MainMenu.Redstone.RedstoneItem.Type"));
        int redAmt = SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Redstone.RedstoneItem.Amount");
        short redData = Short.parseShort(SaberFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Redstone.RedstoneItem.Damage") + "");
        String redName = SaberFactions.plugin.color(SaberFactions.plugin.getConfig().getString("fupgrades.MainMenu.Redstone.RedstoneItem.Name"));
        List<String> redLore = SaberFactions.plugin.colorList(SaberFactions.plugin.getConfig().getStringList("fupgrades.MainMenu.Redstone.RedstoneItem.Lore"));
        int redLevel = fme.getFaction().getUpgrade(UpgradeType.REDSTONE);

        for (int i = 0; i <= redLore.size() - 1; i++) {
            String line = redLore.get(i);
            line = line.replace("{level}", redLevel + "");
            redLore.set(i, line);
        }

        ItemStack redItem = SaberFactions.plugin.createItem(redMaterial, redAmt, redData, redName, redLore);
        redItem.getItemMeta().setLore(redLore);

        if (redLevel >= 1) {
            ItemMeta itemMeta = redItem.getItemMeta();
            if (!SaberFactions.plugin.mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);
            redItem.setItemMeta(itemMeta);

            redItem.setAmount(redLevel);
        }


        ItemStack[] items = {expItem, spawnerItem, cropItem, chestItem, powerItem, redItem, memberItem};
        return items;
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
