package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;

public class CmdTitle extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdTitle() {
        this.aliases.addAll(Aliases.title);
        this.requiredArgs.add("player name");
        this.optionalArgs.put("title", "");

        this.requirements = new CommandRequirements.Builder(Permission.TITLE)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        FactionsPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(FactionsPlugin.instance, () -> {
            FPlayer you = context.argAsBestFPlayerMatch(0);
            if (you == null) return;
            context.args.remove(0);
            String title = TextUtil.implode(context.args, " ");
            if (!context.canIAdministerYou(context.fPlayer, you)) return;
            // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
            if (!context.payForCommand(Conf.econCostTitle, TL.COMMAND_TITLE_TOCHANGE, TL.COMMAND_TITLE_FORCHANGE))
                return;
            you.setTitle(context.sender, title);
            // Inform
            context.faction.msg(TL.COMMAND_TITLE_CHANGED, context.fPlayer.describeTo(context.faction, true), you.describeTo(context.faction, true));
        });
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TITLE_DESCRIPTION;
    }

}