package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;


public class CmdHelp extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public ArrayList<ArrayList<String>> helpPages;

    //TODO: Add Help GUI
    public CmdHelp() {
        super();
        this.aliases.addAll(Aliases.help);

        //this.requiredArgs.add("");
        this.optionalArgs.put("page", "1");

        this.requirements = new CommandRequirements.Builder(Permission.HELP)
                .build();
    }

    //----------------------------------------------//
    // Build the help pages
    //----------------------------------------------//

    @Override
    public void perform(CommandContext context) {
        if (FactionsPlugin.getInstance().getConfig().getBoolean("use-old-help", true)) {
            if (helpPages == null) {
                updateHelp(context);
            }

            int page = context.argAsInt(0, 1);
            context.sendMessage(FactionsPlugin.getInstance().txt.titleize("Factions Help (" + page + "/" + helpPages.size() + ")"));

            page -= 1;

            if (page < 0 || page >= helpPages.size()) {
                context.msg(TL.COMMAND_HELP_404.format(String.valueOf(page)));
                return;
            }
            context.sendMessage(helpPages.get(page));
            return;
        }
        ConfigurationSection help = FactionsPlugin.getInstance().getConfig().getConfigurationSection("help");
        if (help == null) {
            help = FactionsPlugin.getInstance().getConfig().createSection("help"); // create new help section
            List<String> error = new ArrayList<>();
            error.add("&cUpdate help messages in config.yml!");
            error.add("&cSet use-old-help for legacy help messages");
            help.set("'1'", error); // add default error messages
        }
        String pageArg = context.argAsString(0, "1");
        List<String> page = help.getStringList(pageArg);
        if (page == null || page.isEmpty()) {
            context.msg(TL.COMMAND_HELP_404.format(pageArg));
            return;
        }
        for (String helpLine : page) {
            context.sendMessage(FactionsPlugin.getInstance().txt.parse(helpLine));
        }
    }

    public void updateHelp(CommandContext context) {
        helpPages = new ArrayList<>();
        ArrayList<String> pageLines;

        pageLines = new ArrayList<>();
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdHelp.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdList.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdShow.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdPower.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdJoin.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdLeave.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdChat.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdToggleAllianceChat.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdHome.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_NEXTCREATE.toString()));
        helpPages.add(pageLines);

        pageLines = new ArrayList<>();
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdCreate.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdDescription.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdTag.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_INVITATIONS.toString()));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdOpen.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdInvite.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdDeinvite.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_HOME.toString()));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdSethome.getUsageTemplate(context));
        helpPages.add(pageLines);

        if (Econ.isSetup() && Conf.econEnabled && Conf.bankEnabled) {
            pageLines = new ArrayList<>();
            pageLines.add("");
            pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_BANK_1.toString()));
            pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_BANK_2.toString()));
            pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_BANK_3.toString()));
            pageLines.add("");
            pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdMoney.getUsageTemplate(context));
            pageLines.add("");
            pageLines.add("");
            pageLines.add("");
            helpPages.add(pageLines);
        }

        pageLines = new ArrayList<>();
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdClaim.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdAutoClaim.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdUnclaim.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdUnclaimall.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdKick.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdMod.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdAdmin.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdTitle.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdSB.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdSeeChunk.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdStatus.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_PLAYERTITLES.toString()));
        helpPages.add(pageLines);

        pageLines = new ArrayList<>();
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdMap.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdBoom.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdOwner.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdOwnerList.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_OWNERSHIP_1.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_OWNERSHIP_2.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_OWNERSHIP_3.toString()));
        helpPages.add(pageLines);

        pageLines = new ArrayList<>();
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdDisband.getUsageTemplate(context));
        pageLines.add("");
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdRelationAlly.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdRelationNeutral.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdRelationEnemy.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_RELATIONS_1.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_RELATIONS_2.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_RELATIONS_3.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_RELATIONS_4.toString()));
        helpPages.add(pageLines);

        pageLines = new ArrayList<>();
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_RELATIONS_5.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_RELATIONS_6.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_RELATIONS_7.toString()));
        pageLines.add(TL.COMMAND_HELP_RELATIONS_8.toString());
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_RELATIONS_9.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_RELATIONS_10.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_RELATIONS_11.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_RELATIONS_12.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_RELATIONS_13.toString()));
        helpPages.add(pageLines);

        pageLines = new ArrayList<>();
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_PERMISSIONS_1.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_PERMISSIONS_2.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_PERMISSIONS_3.toString()));
        pageLines.add(TL.COMMAND_HELP_PERMISSIONS_4.toString());
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_PERMISSIONS_5.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_PERMISSIONS_6.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_PERMISSIONS_7.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_PERMISSIONS_8.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_PERMISSIONS_9.toString()));
        helpPages.add(pageLines);

        pageLines = new ArrayList<>();
        pageLines.add(TL.COMMAND_HELP_MOAR_1.toString());
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdBypass.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_ADMIN_1.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_ADMIN_2.toString()));
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_ADMIN_3.toString()));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdSafeunclaimall.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdWarunclaimall.getUsageTemplate(context));
        //TODO:TL
        pageLines.add(FactionsPlugin.getInstance().txt.parse("<i>Note: " + FactionsPlugin.getInstance().cmdBase.cmdUnclaim.getUsageTemplate(context) + FactionsPlugin.getInstance().txt.parse("<i>") + " works on safe/war zones as well."));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdPeaceful.getUsageTemplate(context));
        helpPages.add(pageLines);

        pageLines = new ArrayList<>();
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_MOAR_2.toString()));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdChatSpy.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdPermanent.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdPermanentPower.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdPowerBoost.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdConfig.getUsageTemplate(context));
        helpPages.add(pageLines);

        pageLines = new ArrayList<>();
        pageLines.add(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_HELP_MOAR_3.toString()));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdLock.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdReload.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdSaveAll.getUsageTemplate(context));
        pageLines.add(FactionsPlugin.getInstance().cmdBase.cmdVersion.getUsageTemplate(context));
        helpPages.add(pageLines);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_HELP_DESCRIPTION;
    }
}

