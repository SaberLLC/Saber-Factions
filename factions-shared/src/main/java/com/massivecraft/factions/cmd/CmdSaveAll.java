package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSaveAll extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdSaveAll() {
        super();
        this.getAliases().addAll(Aliases.saveAll);

        this.setRequirements(new CommandRequirements.Builder(Permission.SAVE)
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        FPlayers.getInstance().forceSave(false);
        Factions.getInstance().forceSave(false);
        Board.getInstance().forceSave(false);
        Conf.save();
        FactionsPlugin.getInstance().getTimerManager().saveTimerData();
        try {
            FactionsPlugin.instance.getFlogManager().saveLogs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        context.msg(TL.COMMAND_SAVEALL_SUCCESS);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SAVEALL_DESCRIPTION;
    }

}