package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.timer.TimerManager;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSaveAll extends FCommand {

    public CmdSaveAll() {
        super();
        this.getAliases().addAll(Aliases.saveAll);

        this.setRequirements(
            new CommandRequirements.Builder(Permission.SAVE).build()
        );
    }

    @Override
    public void perform(CommandContext context) {
        // Force-save all data
        FPlayers.getInstance().forceSave(false);
        Factions.getInstance().forceSave(false);
        Board.getInstance().forceSave(false);

        // Save config
        Conf.save();

        // Safely handle TimerManager, in case it is null
        TimerManager timerManager = FactionsPlugin.getInstance().getTimerManager();
        if (timerManager != null) {
            timerManager.saveTimerData();
        }

        // Save logs
        try {
            FactionsPlugin.getInstance().getFlogManager().saveLogs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Notify player
        context.msg(TL.COMMAND_SAVEALL_SUCCESS);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SAVEALL_DESCRIPTION;
    }
}
