package com.massivecraft.factions.cmd.tnt;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.tnt.tntprovider.FactionTNTProvider;
import com.massivecraft.factions.cmd.tnt.tntprovider.PlayerTNTProvider;
import com.massivecraft.factions.cmd.tnt.tntprovider.TNTProvider;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.stream.Collectors;

public class CmdTntFill extends FCommand {

    private Map<Player, TNTFillTask> fillTaskMap;

    private boolean tntFillEnabled;
    private int maxTntFillRadius;
    private int maxTntFillAmount;

    public CmdTntFill() {
        super();
        this.fillTaskMap = new WeakHashMap<>();
        this.aliases.addAll(Aliases.tnt_tntfill);

        this.requiredArgs.add("radius");
        this.requiredArgs.add("amount");

        FactionsPlugin plugin = FactionsPlugin.getInstance();
        this.tntFillEnabled = plugin.getConfig().getBoolean("Tntfill.enabled");
        this.maxTntFillRadius = plugin.getConfig().getInt("Tntfill.max-radius");
        this.maxTntFillAmount = plugin.getConfig().getInt("Tntfill.max-amount");

        this.requirements = new CommandRequirements.Builder(Permission.TNTFILL).playerOnly().memberOnly().withAction(PermissableAction.TNTFILL).build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!tntFillEnabled) {
            context.msg(TL.COMMAND_TNT_DISABLED_MSG);
            return;
        }

        if (hasRunningTask(context.player)) {
            context.msg("&cCannot create another task as you currently have a fill task running.");
            return;
        }

        // Don't do I/O unless necessary
        try {
            Integer.parseInt(context.args.get(0));
            Integer.parseInt(context.args.get(1));
        } catch (NumberFormatException e) {
            context.msg(TL.COMMAND_TNT_INVALID_NUM);
            return;
        }

        context.msg(TL.COMMAND_TNTFILL_HEADER);
        int radius = context.argAsInt(0, 0); // We don't know the max yet, so let's not assume.
        int amount = context.argAsInt(1, 0); // We don't know the max yet, so let's not assume.

        if (amount <= 0 || radius <= 0) {
            context.msg(TL.COMMAND_TNT_POSITIVE);
            return;
        }

        if (radius > maxTntFillRadius) {
            context.msg(TL.COMMAND_TNTFILL_RADIUSMAX.toString().replace("{max}", String.valueOf(maxTntFillRadius)));
            return;
        }
        if (amount > maxTntFillAmount) {
            context.msg(TL.COMMAND_TNTFILL_AMOUNTMAX.toString().replace("{max}", String.valueOf(maxTntFillAmount)));
            return;
        }

        // How many dispensers are we to fill in?
        Location start = context.player.getLocation();
        // Keep it on the stack for CPU saving.
        List<Dispenser> opDispensers = new ArrayList<>();

        Block startBlock = start.getBlock();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = startBlock.getRelative(x, y, z);
                    BlockState blockState = block.getState();
                    if (blockState instanceof Dispenser) {
                        Dispenser dispenser = (Dispenser) blockState;
                        // skip if we can't add anything
                        if (!isInvFull(dispenser.getInventory())) {
                            opDispensers.add(dispenser);
                        }
                    }
                }
            }
        }

        if (opDispensers.isEmpty()) {
            context.fPlayer.msg(TL.COMMAND_TNTFILL_NODISPENSERS.toString().replace("{radius}", String.valueOf(radius)));
            return;
        }

        int requiredTnt = opDispensers.size() * amount;
        int playerTnt = getTNTInside(context.player);

        FactionTNTProvider factionTNTProvider = new FactionTNTProvider(context);
        PlayerTNTProvider playerTNTProvider = new PlayerTNTProvider(context.fPlayer);

        if (playerTnt < requiredTnt) {
            int factionTnt = context.faction.getTnt();
            if (factionTnt < (requiredTnt - playerTnt)) {
                context.fPlayer.msg(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH_TNT.toString());
                return;
            }

            context.faction.addTnt(playerTnt);

            playerTNTProvider.takeTnt(playerTnt);
            fillDispensers(context.player, factionTNTProvider, opDispensers, amount);
        } else {
            fillDispensers(context.player, playerTNTProvider, opDispensers, amount);
        }
    }

    private boolean hasRunningTask(Player player) {
        this.fillTaskMap.values().removeIf(TNTFillTask::isCancelled);
        return this.fillTaskMap.containsKey(player);
    }

    public void fillDispensers(Player player, TNTProvider tntProvider, Collection<Dispenser> dispensers, int amount) {
        TNTFillTask tntFillTask = new TNTFillTask(this, tntProvider, dispensers.stream().map(BlockState::getBlock).collect(Collectors.toList()), amount);
        tntFillTask.runTaskTimer(FactionsPlugin.getInstance(), 0, 1);
        fillTaskMap.put(player, tntFillTask);
    }

    public int getAddable(Inventory inv, Material material) {
        int notempty = (int) Arrays.stream(inv.getContents()).filter(Objects::nonNull).count();
        return Arrays.stream(inv.getContents())
                .filter(Objects::nonNull)
                .filter(item -> item.getType() == material)
                .mapToInt(item -> 64 - item.getAmount())
                .sum() + (inv.getSize() - notempty) * 64;
    }

    public boolean isInvFull(Inventory inv) {
        return inv.firstEmpty() == -1;
    }

    public int getTNTInside(Player p) {
        return Arrays.stream(p.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(item -> item.getType() == Material.TNT)
                .filter(item -> !item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().hasLore())
                .mapToInt(ItemStack::getAmount)
                .sum();
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TNTFILL_DESCRIPTION;
    }
}