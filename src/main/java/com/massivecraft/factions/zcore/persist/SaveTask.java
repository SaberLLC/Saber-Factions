package com.massivecraft.factions.zcore.persist;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.zcore.MPlugin;

import java.util.concurrent.atomic.AtomicBoolean;

public class SaveTask implements Runnable {

    private static final AtomicBoolean RUNNING = new AtomicBoolean(false);

    private final MPlugin p;

    public SaveTask(MPlugin p) {
        this.p = p;
    }

    public void run() {
        if (!p.getAutoSave() || RUNNING.get()) {
            return;
        }
        if (RUNNING.compareAndSet(false, true)) {
            p.preAutoSave();
            Factions.getInstance().forceSave(true);
            FPlayers.getInstance().forceSave(true);
            Board.getInstance().forceSave(true);
            p.postAutoSave();
            if (!RUNNING.compareAndSet(true, false)) {
                throw new IllegalStateException("Overlapping saves, please report this to the SaberFactions Development Team!");
            }
        }
    }
}