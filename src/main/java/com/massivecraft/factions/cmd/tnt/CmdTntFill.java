package com.massivecraft.factions.cmd.tnt;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CmdTntFill extends FCommand {

    public CmdTntFill() {
        super();
        this.aliases.addAll(Aliases.tnt_tntfill);

        this.requiredArgs.add("radius");
        this.requiredArgs.add("amount");

        this.requirements = new CommandRequirements.Builder(Permission.TNTFILL)
                .playerOnly()
                .memberOnly()
                .withAction(PermissableAction.TNTFILL)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!FactionsPlugin.instance.getConfig().getBoolean("Tntfill.enabled")) {
            context.msg(TL.COMMAND_TNT_DISABLED_MSG);
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

        if (radius > FactionsPlugin.getInstance().getConfig().getInt("Tntfill.max-radius")) {
            context.msg(TL.COMMAND_TNTFILL_RADIUSMAX.toString().replace("{max}", FactionsPlugin.getInstance().getConfig().getInt("Tntfill.max-radius") + ""));
            return;
        }

        if (amount > FactionsPlugin.getInstance().getConfig().getInt("Tntfill.max-amount")) {
            context.msg(TL.COMMAND_TNTFILL_AMOUNTMAX.toString().replace("{max}", FactionsPlugin.getInstance().getConfig().getInt("Tntfill.max-amount") + ""));
            return;
        }

        List<Chunk> chunks = getChunks(context.player, radius);
        int dispensers = 0;
        int balance = context.faction.getTnt();
        Location loc = context.player.getLocation();

        for (Chunk chunk : chunks) {
            BlockState[] tileEntities;
            for (int length = (tileEntities = chunk.getTileEntities()).length, j = 0; j < length; ++j) {
                BlockState tile = tileEntities[j];
                if (tile instanceof Dispenser) {
                    if (balance >= amount) {
                        if (loc.toVector().distance(tile.getLocation().toVector()) <= radius) {
                            ((Dispenser) tile).getInventory().addItem(new ItemStack(Material.TNT, amount));
                            ++dispensers;
                            balance -= amount;
                        }
                    }
                }
            }
        }

        if (balance < amount) {
            context.fPlayer.msg(TL.COMMAND_TNTFILL_NOTENOUGH);
            return;
        }

        if (dispensers != 0) {
            context.sendMessage(TL.COMMAND_TNTFILL_SUCCESS.toString().replace("{amount}", amount + "").replace("{dispensers}", dispensers + ""));
        }

        context.faction.setTnt(balance);
    }

    private List<Chunk> getChunks(Player player, int radius) {
        List<Chunk> chunks = new ArrayList<>();
        Chunk playerChunk = player.getLocation().getChunk();
        if (radius <= 16) {
            radius += 20;
        }
        radius /= 16;
        for (int x = -radius; x <= radius; ++x) {
            for (int z = -radius; z <= radius; ++z) {
                chunks.add(playerChunk.getWorld().getChunkAt(playerChunk.getX() + x, playerChunk.getZ() + z));
            }
        }
        return chunks;
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TNTFILL_DESCRIPTION;
    }

}