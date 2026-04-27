package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdModifyPower extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdModifyPower() {
        super();

        this.getAliases().addAll(Aliases.modifyPower);

        this.getRequiredArgs().add("name");
        this.getRequiredArgs().add("power");

        this.setRequirements(new CommandRequirements.Builder(Permission.MODIFY_POWER)
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        // /f modify <name> #
        FPlayer player = context.argAsBestFPlayerMatch(0);
        Double number = context.argAsDouble(1); // returns null if not a Double.

        if (player == null || number == null) {
            context.sender.sendMessage(getHelpShort());
            return;
        }

        player.alterPower(number);
        int newPower = player.getPowerRounded(); // int so we don't have super long doubles.
        context.msg(TL.COMMAND_MODIFYPOWER_ADDED, number, player.getName(), newPower);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MODIFYPOWER_DESCRIPTION;
    }
}
