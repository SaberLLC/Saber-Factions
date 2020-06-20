package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.discord.Discord;
import com.massivecraft.factions.listeners.FactionsPlayerListener;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdReload extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdReload() {
        super();
        this.aliases.addAll(Aliases.reload);

        this.requirements = new CommandRequirements.Builder(Permission.RELOAD).build();
    }

    @Override
    public void perform(CommandContext context) {
        long timeInitStart = System.currentTimeMillis();
        Conf.load();
        Conf.save();
        FactionsPlugin.getInstance().getFileManager().getShop().loadFile();
        FactionsPlugin.getInstance().reloadConfig();
        FactionsPlugin.getInstance().loadLang();


        if (!FactionsPlugin.getInstance().mc17) {
            FactionsPlayerListener.loadCorners();
        }

        Discord.setupDiscord();
        //Recheck if commands should truly be disabled and rebuild.
        FCmdRoot.instance.addVariableCommands();
        FCmdRoot.instance.rebuild();
        long timeReload = (System.currentTimeMillis() - timeInitStart);

        context.msg(TL.COMMAND_RELOAD_TIME, timeReload);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_RELOAD_DESCRIPTION;
    }
}