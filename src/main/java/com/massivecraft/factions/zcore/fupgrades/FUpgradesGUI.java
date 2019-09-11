package com.massivecraft.factions.zcore.fupgrades;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.util.XMaterial;
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
        Inventory inventory = Bukkit.createInventory(null, P.p.getConfig().getInt("fupgrades.MainMenu.DummyItem.rows") * 9, P.p.color(P.p.getConfig().getString("fupgrades.MainMenu.Title").replace("{faction}", fme.getFaction().getTag())));
        ItemStack dummyItem = XMaterial.matchXMaterial(P.p.getConfig().getString("fupgrades.MainMenu.DummyItem.Type")).parseItem();
        ItemMeta meta = dummyItem.getItemMeta();
        meta.setLore(P.p.colorList(P.p.getConfig().getStringList("fupgrades.MainMenu.DummyItem.Lore")));
        meta.setDisplayName(P.p.color(P.p.getConfig().getString("fupgrades.MainMenu.DummyItem.Name")));
        dummyItem.setItemMeta(meta);

        for (int fill = 0; fill < P.p.getConfig().getInt("fupgrades.MainMenu.DummyItem.rows") * 9; ++fill) {
            inventory.setItem(fill, dummyItem);
        }

        ItemStack[] items = buildItems(fme);
        List<Integer> cropSlots = P.p.getConfig().getIntegerList("fupgrades.MainMenu.Crops.CropItem.slots");
        List<Integer> spawnerSlots = P.p.getConfig().getIntegerList("fupgrades.MainMenu.Spawners.SpawnerItem.slots");
        List<Integer> expSlots = P.p.getConfig().getIntegerList("fupgrades.MainMenu.EXP.EXPItem.slots");
        List<Integer> chestSlots = P.p.getConfig().getIntegerList("fupgrades.MainMenu.Chest.ChestItem.slots");
        List<Integer> powerSlots = P.p.getConfig().getIntegerList("fupgrades.MainMenu.Power.PowerItem.slots");
        List<Integer> redSlots = P.p.getConfig().getIntegerList("fupgrades.MainMenu.Redstone.RedstoneItem.slots");
        List<Integer> memberSlots = P.p.getConfig().getIntegerList("fupgrades.MainMenu.Members.MembersItem.slots");

        for (Integer cropSlot : cropSlots) if (cropSlot != -1) inventory.setItem(cropSlot, items[2]);

        for (Integer spawnerSlot : spawnerSlots) if (spawnerSlot != -1) inventory.setItem(spawnerSlot, items[1]);

        for (Integer expSlot : expSlots) if (expSlot != -1) inventory.setItem(expSlot, items[0]);

        for (Integer chestSlot : chestSlots) if (chestSlot != -1) inventory.setItem(chestSlot, items[3]);

        for (Integer powerSlot : powerSlots) if (powerSlot != -1) inventory.setItem(powerSlot, items[4]);

        for (Integer redSlot : redSlots) if (redSlot != -1) inventory.setItem(redSlot, items[5]);

        for (Integer memberSlot : memberSlots) if (memberSlot != -1) inventory.setItem(memberSlot, items[6]);

        fme.getPlayer().openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null || e.getCursor() == null)
            return;

        FPlayer fme = FPlayers.getInstance().getByPlayer((Player) e.getWhoClicked());
        if (e.getView().getTitle().equalsIgnoreCase(P.p.color(P.p.getConfig().getString("fupgrades.MainMenu.Title").replace("{faction}", fme.getFaction().getTag())))) {
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
                        upgradeItem(fme, UpgradeType.CROP, 3, P.p.getConfig().getInt("fupgrades.MainMenu.Crops.Cost.level-3"));
                        break;
                    case 1:
                        upgradeItem(fme, UpgradeType.CROP, 2, P.p.getConfig().getInt("fupgrades.MainMenu.Crops.Cost.level-2"));
                        break;
                    case 0:
                        upgradeItem(fme, UpgradeType.CROP, 1, P.p.getConfig().getInt("fupgrades.MainMenu.Crops.Cost.level-1"));
                        break;
                }
            } else if (e.getCurrentItem().equals(powerItem)) {
                int powerLevel = fme.getFaction().getUpgrade(UpgradeType.POWER);
                switch (powerLevel) {
                    case 3:
                        return;
                    case 2:
                        upgradeItem(fme, UpgradeType.POWER, 3, P.p.getConfig().getInt("fupgrades.MainMenu.Power.Cost.level-3"));
                        updatePower(fme.getFaction());
                        break;
                    case 1:
                        upgradeItem(fme, UpgradeType.POWER, 2, P.p.getConfig().getInt("fupgrades.MainMenu.Power.Cost.level-2"));
                        updatePower(fme.getFaction());
                        break;
                    case 0:
                        upgradeItem(fme, UpgradeType.POWER, 1, P.p.getConfig().getInt("fupgrades.MainMenu.Power.Cost.level-1"));
                        updatePower(fme.getFaction());
                        break;
                }
            } else if (e.getCurrentItem().equals(spawnerItem)) {
                int spawnerLevel = fme.getFaction().getUpgrade(UpgradeType.SPAWNER);
                switch (spawnerLevel) {
                    case 3:
                        return;
                    case 2:
                        upgradeItem(fme, UpgradeType.SPAWNER, 3, P.p.getConfig().getInt("fupgrades.MainMenu.Spawners.Cost.level-3"));
                        break;
                    case 1:
                        upgradeItem(fme, UpgradeType.SPAWNER, 2, P.p.getConfig().getInt("fupgrades.MainMenu.Spawners.Cost.level-2"));
                        break;
                    case 0:
                        upgradeItem(fme, UpgradeType.SPAWNER, 1, P.p.getConfig().getInt("fupgrades.MainMenu.Spawners.Cost.level-1"));
                        break;
                }
            } else if (e.getCurrentItem().equals(expItem)) {
                int expLevel = fme.getFaction().getUpgrade(UpgradeType.EXP);

                switch (expLevel) {
                    case 3:
                        return;
                    case 2:
                        upgradeItem(fme, UpgradeType.EXP, 3, P.p.getConfig().getInt("fupgrades.MainMenu.EXP.Cost.level-3"));
                        break;
                    case 1:
                        upgradeItem(fme, UpgradeType.EXP, 2, P.p.getConfig().getInt("fupgrades.MainMenu.EXP.Cost.level-2"));
                        break;
                    case 0:
                        upgradeItem(fme, UpgradeType.EXP, 1, P.p.getConfig().getInt("fupgrades.MainMenu.EXP.Cost.level-1"));
                        break;
                }
            } else if (e.getCurrentItem().equals(redItem)) {
                int redLevel = fme.getFaction().getUpgrade(UpgradeType.REDSTONE);
                switch (redLevel) {
                    case 1:
                        return;
                    case 0:
                        upgradeItem(fme, UpgradeType.REDSTONE, 1, P.p.getConfig().getInt("fupgrades.MainMenu.Redstone.Cost"));
                        break;
                }
            } else if (e.getCurrentItem().equals(chestitem)) {
                int chestLevel = fme.getFaction().getUpgrade(UpgradeType.CHEST);
                switch (chestLevel) {
                    case 3:
                        return;
                    case 2: {
                        if (upgradeItem(fme, UpgradeType.CHEST, 3, P.p.getConfig().getInt("fupgrades.MainMenu.Chest.Cost.level-3")))
                            updateChests(fme.getFaction());
                        break;
                    }
                    case 1: {
                        if (upgradeItem(fme, UpgradeType.CHEST, 2, P.p.getConfig().getInt("fupgrades.MainMenu.Chest.Cost.level-2")))
                            updateChests(fme.getFaction());
                        break;
                    }
                    case 0: {
                        if (upgradeItem(fme, UpgradeType.CHEST, 1, P.p.getConfig().getInt("fupgrades.MainMenu.Chest.Cost.level-1")))
                            updateChests(fme.getFaction());
                        break;
                    }
                }
            } else if (e.getCurrentItem().equals(memberItem)) {
                int memberLevel = fme.getFaction().getUpgrade(UpgradeType.MEMBERS) + 1;
                if (!P.p.getConfig().isSet("fupgrades.MainMenu.Members.Cost.level-" + memberLevel)) {
                    return;
                }
                int cost = P.p.getConfig().getInt("fupgrades.MainMenu.Members.Cost.level-" + memberLevel);
                if (hasMoney(fme, cost)) {
                    fme.getFaction().setUpgrade(UpgradeType.MEMBERS, memberLevel);
                    fme.getPlayer().closeInventory();
                    takeMoney(fme, cost);
                }
            }
        }
    }

    private void updateChests(Faction faction) {
        String invName = P.p.color(P.p.getConfig().getString("fchest.Inventory-Title"));

        for (Player player : faction.getOnlinePlayers()) {
            if (player.getInventory().getTitle() != null && player.getInventory().getTitle().equalsIgnoreCase(invName))
                player.closeInventory();
        }

        int level = faction.getUpgrade(UpgradeType.CHEST);
        int size = 1;

        switch (level) {
            case 1:
                size = P.p.getConfig().getInt("fupgrades.MainMenu.Chest.Chest-Size.level-1");
                break;
            case 2:
                size = P.p.getConfig().getInt("fupgrades.MainMenu.Chest.Chest-Size.level-2");
                break;
            case 3:
                size = P.p.getConfig().getInt("fupgrades.MainMenu.Chest.Chest-Size.level-3");
                break;
        }
        faction.setChestSize(size * 9);
    }

    private void updatePower(Faction faction) {
        int level = faction.getUpgrade(UpgradeType.POWER);
        double power = 0.0;

        switch (level) {
            case 1:
                power = P.p.getConfig().getInt("fupgrades.MainMenu.Power.Power-Boost.level-1");
                break;
            case 2:
                power = P.p.getConfig().getInt("fupgrades.MainMenu.Power.Power-Boost.level-2");
                break;
            case 3:
                power = P.p.getConfig().getInt("fupgrades.MainMenu.Power.Power-Boost.level-3");
                break;
        }
        faction.setPowerBoost(power);
    }

    @SuppressWarnings("Duplicates")
    private ItemStack[] buildItems(FPlayer fme) {
        Material expMaterial = Material.getMaterial(P.p.getConfig().getString("fupgrades.MainMenu.EXP.EXPItem.Type"));
        int expAmt = P.p.getConfig().getInt("fupgrades.MainMenu.EXP.EXPItem.Amount");
        short expData = Short.parseShort(P.p.getConfig().getInt("fupgrades.MainMenu.EXP.EXPItem.Damage") + "");
        String expName = P.p.color(P.p.getConfig().getString("fupgrades.MainMenu.EXP.EXPItem.Name"));
        List<String> expLore = P.p.colorList(P.p.getConfig().getStringList("fupgrades.MainMenu.EXP.EXPItem.Lore"));
        int expLevel = fme.getFaction().getUpgrade(UpgradeType.EXP);

        for (int i = 0; i <= expLore.size() - 1; i++)
            expLore.set(i, expLore.get(i).replace("{level}", expLevel + ""));

        ItemStack expItem = P.p.createItem(expMaterial, expAmt, expData, expName, expLore);

        if (expLevel >= 1) {
            ItemMeta itemMeta = expItem.getItemMeta();
            if (!P.p.mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);

            expItem.setItemMeta(itemMeta);
            expItem.setAmount(expLevel);
        }

        Material spawnerMaterial = Material.getMaterial(P.p.getConfig().getString("fupgrades.MainMenu.Spawners.SpawnerItem.Type"));
        int spawnerAmt = P.p.getConfig().getInt("fupgrades.MainMenu.Spawners.SpawnerItem.Amount");
        short spawnerData = Short.parseShort(P.p.getConfig().getInt("fupgrades.MainMenu.Spawners.SpawnerItem.Damage") + "");
        String spawnerName = P.p.color(P.p.getConfig().getString("fupgrades.MainMenu.Spawners.SpawnerItem.Name"));
        List<String> spawnerLore = P.p.colorList(P.p.getConfig().getStringList("fupgrades.MainMenu.Spawners.SpawnerItem.Lore"));
        int spawnerLevel = fme.getFaction().getUpgrade(UpgradeType.SPAWNER);

        for (int i = 0; i <= spawnerLore.size() - 1; i++) {
            spawnerLore.set(i, spawnerLore.get(i).replace("{level}", spawnerLevel + ""));
        }


        Material cropMaterial = Material.getMaterial(P.p.getConfig().getString("fupgrades.MainMenu.Crops.CropItem.Type"));
        int cropAmt = P.p.getConfig().getInt("fupgrades.MainMenu.Crops.CropItem.Amount");
        short cropData = Short.parseShort(P.p.getConfig().getInt("fupgrades.MainMenu.Crops.CropItem.Damage") + "");
        String cropName = P.p.color(P.p.getConfig().getString("fupgrades.MainMenu.Crops.CropItem.Name"));
        List<String> cropLore = P.p.colorList(P.p.getConfig().getStringList("fupgrades.MainMenu.Crops.CropItem.Lore"));
        int cropLevel = fme.getFaction().getUpgrade(UpgradeType.CROP);

        for (int i = 0; i <= cropLore.size() - 1; i++) {
            String line = cropLore.get(i);
            line = line.replace("{level}", cropLevel + "");
            cropLore.set(i, line);
        }

        ItemStack cropItem = P.p.createItem(cropMaterial, cropAmt, cropData, cropName, cropLore);
        cropItem.getItemMeta().setLore(cropLore);

        if (cropLevel >= 1) {
            ItemMeta itemMeta = cropItem.getItemMeta();
            if (!P.p.mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);
            cropItem.setItemMeta(itemMeta);

            cropItem.setAmount(cropLevel);
        }

        ItemStack spawnerItem = P.p.createItem(spawnerMaterial, spawnerAmt, spawnerData, spawnerName, spawnerLore);
        spawnerItem.getItemMeta().setLore(spawnerLore);
        if (spawnerLevel >= 1) {
            ItemMeta itemMeta = spawnerItem.getItemMeta();
            if (!P.p.mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);

            spawnerItem.setItemMeta(itemMeta);
            spawnerItem.setAmount(spawnerLevel);
        }

        Material chestMaterial = Material.getMaterial(P.p.getConfig().getString("fupgrades.MainMenu.Chest.ChestItem.Type"));
        int chesttAmt = P.p.getConfig().getInt("fupgrades.MainMenu.Chest.ChestItem.Amount");
        short chestData = Short.parseShort(P.p.getConfig().getInt("fupgrades.MainMenu.Chest.ChestItem.Damage") + "");
        String chestName = P.p.color(P.p.getConfig().getString("fupgrades.MainMenu.Chest.ChestItem.Name", "&e&lUpgrade Chest Size"));
        List<String> chestLore = P.p.colorList(P.p.getConfig().getStringList("fupgrades.MainMenu.Chest.ChestItem.Lore"));
        int chestlevel = fme.getFaction().getUpgrade(UpgradeType.CHEST);

        for (int i = 0; i <= chestLore.size() - 1; i++) {
            String line = chestLore.get(i);
            line = line.replace("{level}", chestlevel + "");
            chestLore.set(i, line);
        }

        ItemStack chestItem = P.p.createItem(chestMaterial, chesttAmt, chestData, chestName, chestLore);

        if (chestlevel >= 1) {
            ItemMeta itemMeta = chestItem.getItemMeta();
            if (!P.p.mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);

            chestItem.setItemMeta(itemMeta);
            chestItem.setAmount(chestlevel);
        }

        Material memberMaterial = Material.getMaterial(P.p.getConfig().getString("fupgrades.MainMenu.Members.MembersItem.Type"));
        int memberAmt = P.p.getConfig().getInt("fupgrades.MainMenu.Members.MembersItem.Amount");
        short memberData = Short.parseShort(P.p.getConfig().getInt("fupgrades.MainMenu.Members.MembersItem.Damage") + "");
        String memberName = P.p.color(P.p.getConfig().getString("fupgrades.MainMenu.Members.MembersItem.Name", "&e&lUpgrade Member Size"));
        List<String> memberLore = P.p.colorList(P.p.getConfig().getStringList("fupgrades.MainMenu.Members.MembersItem.Lore"));
        int memberlevel = fme.getFaction().getUpgrade(UpgradeType.MEMBERS);

        for (int i = 0; i <= memberLore.size() - 1; i++) {
            String line = memberLore.get(i);
            line = line.replace("{level}", memberlevel + "");
            memberLore.set(i, line);
        }

        ItemStack memberItem = P.p.createItem(memberMaterial, memberAmt, memberData, memberName, memberLore);

        if (memberlevel >= 1) {
            ItemMeta itemMeta = memberItem.getItemMeta();
            if (!P.p.mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);

            memberItem.setItemMeta(itemMeta);
            memberItem.setAmount(memberlevel);
        }

        Material powerMaterial = Material.getMaterial(P.p.getConfig().getString("fupgrades.MainMenu.Power.PowerItem.Type"));
        int powerAmt = P.p.getConfig().getInt("fupgrades.MainMenu.Power.PowerItem.Amount");
        short powerData = Short.parseShort(P.p.getConfig().getInt("fupgrades.MainMenu.Power.PowerItem.Damage") + "");
        String powerName = P.p.color(P.p.getConfig().getString("fupgrades.MainMenu.Power.PowerItem.Name"));
        List<String> powerLore = P.p.colorList(P.p.getConfig().getStringList("fupgrades.MainMenu.Power.PowerItem.Lore"));
        int powerLevel = fme.getFaction().getUpgrade(UpgradeType.POWER);

        for (int i = 0; i <= powerLore.size() - 1; i++) {
            String line = powerLore.get(i);
            line = line.replace("{level}", powerLevel + "");
            powerLore.set(i, line);
        }

        ItemStack powerItem = P.p.createItem(powerMaterial, powerAmt, powerData, powerName, powerLore);
        powerItem.getItemMeta().setLore(powerLore);

        if (powerLevel >= 1) {
            ItemMeta itemMeta = powerItem.getItemMeta();
            if (!P.p.mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);
            powerItem.setItemMeta(itemMeta);

            powerItem.setAmount(powerLevel);
        }

        Material redMaterial = Material.getMaterial(P.p.getConfig().getString("fupgrades.MainMenu.Redstone.RedstoneItem.Type"));
        int redAmt = P.p.getConfig().getInt("fupgrades.MainMenu.Redstone.RedstoneItem.Amount");
        short redData = Short.parseShort(P.p.getConfig().getInt("fupgrades.MainMenu.Redstone.RedstoneItem.Damage") + "");
        String redName = P.p.color(P.p.getConfig().getString("fupgrades.MainMenu.Redstone.RedstoneItem.Name"));
        List<String> redLore = P.p.colorList(P.p.getConfig().getStringList("fupgrades.MainMenu.Redstone.RedstoneItem.Lore"));
        int redLevel = fme.getFaction().getUpgrade(UpgradeType.REDSTONE);

        for (int i = 0; i <= redLore.size() - 1; i++) {
            String line = redLore.get(i);
            line = line.replace("{level}", redLevel + "");
            redLore.set(i, line);
        }

        ItemStack redItem = P.p.createItem(redMaterial, redAmt, redData, redName, redLore);
        redItem.getItemMeta().setLore(redLore);

        if (redLevel >= 1) {
            ItemMeta itemMeta = redItem.getItemMeta();
            if (!P.p.mc17) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);
            redItem.setItemMeta(itemMeta);

            redItem.setAmount(redLevel);
        }

        return new ItemStack[]{expItem, spawnerItem, cropItem, chestItem, powerItem, redItem, memberItem};
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
