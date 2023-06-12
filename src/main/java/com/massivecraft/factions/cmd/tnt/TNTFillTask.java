package com.massivecraft.factions.cmd.tnt;

import com.massivecraft.factions.cmd.tnt.tntprovider.TNTProvider;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Saser
 */
public class TNTFillTask extends BukkitRunnable {

    private static final int FILLS_PER_ITERATION = 2000;

    private final CmdTntFill cmdTntFill;
    private final TNTProvider tntProvider;
    private final Queue<Block> dispensers;
    private final int count;
    private final int initialSize;

    private boolean isRunning = true;

    public TNTFillTask(CmdTntFill cmdTntFill, TNTProvider tntProvider, Collection<Block> dispensers, int count) {
        this.cmdTntFill = cmdTntFill;
        this.tntProvider = tntProvider;
        this.dispensers = new LinkedList<>(dispensers);
        this.count = count;
        this.initialSize = dispensers.size();
    }

    public boolean isCancelled() {
        return !isRunning;
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        this.isRunning = false;
    }

    @Override
    public void run() {
        if (dispensers.isEmpty() || !tntProvider.isAvailable()) {
            tntProvider.sendMessage(TL.COMMAND_TNTFILL_SUCCESS.toString().replace("{amount}", (initialSize * count) + "").replace("{dispensers}", initialSize + ""));
            cancel();
            return;
        }

        int maxIters = Math.min(FILLS_PER_ITERATION, dispensers.size());
        int availableTNT = tntProvider.getTnt();

        if (availableTNT < count) {
            tntProvider.sendMessage(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH_TNT.toString());
            cancel();
            return;
        }

        for (int i = 0; i < maxIters; i++) {
            Block block = dispensers.poll();
            BlockState blockState = block.getState();
            if (!(blockState instanceof Dispenser)) continue;
            Dispenser dispenser = (Dispenser) blockState;

            int canBeAdded = cmdTntFill.getAddable(dispenser.getInventory(), Material.TNT);
            if (canBeAdded <= 0) continue;
            int toAdd = Math.min(canBeAdded, count);

            tntProvider.takeTnt(toAdd);
            dispenser.getInventory().addItem(new ItemStack(Material.TNT, toAdd));
        }
    }
}
