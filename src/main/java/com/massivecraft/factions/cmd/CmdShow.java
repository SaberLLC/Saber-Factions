package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TagReplacer;
import com.massivecraft.factions.zcore.util.TagUtil;
import mkremins.fanciful.FancyMessage;

import java.util.ArrayList;
import java.util.List;

public class CmdShow extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    List<String> defaults = new ArrayList<>();

    public CmdShow() {
        this.aliases.addAll(Aliases.show_show);

        // add defaults to /f show in case config doesnt have it
        defaults.add("&8&m--------------&7 &8<&e{faction}&8> &8&m--------------");
        defaults.add("&4* &cOwner: &f{leader}");
        defaults.add("&4* &cDescription: &f{description}");
        defaults.add("&4* &cLand / Power / Max Power: &f{chunks} &8/ &f{power} &8/ &f{maxPower}");
        defaults.add("&4* &cFaction Strikes: &f{strikes}");
        defaults.add("&4* &cFaction Points: &f{faction-points}");
        defaults.add("&4* &cFounded: &f{create-date}");
        defaults.add("&4* &cBalance: &f{faction-balance}");
        defaults.add("&4* &cAllies: &a{allies-list}");
        defaults.add("&4* &cEnemies: &4{enemies-list}");
        defaults.add("&4* &cOnline Members: &8[&f{online}/{members}&8] &a{online-list}");
        defaults.add("&4* &cOffline Members: &8[&f{offline}/{members}&8] &a{offline-list}");
        defaults.add("&4* &cAlts: &f{alts}");
        defaults.add("&4* &cBans: &f{faction-bancount}");
        defaults.add("&8&m----------------------------------------");

        // this.requiredArgs.add("");
        this.optionalArgs.put("faction tag", "yours");

        this.requirements = new CommandRequirements.Builder(Permission.SHOW).build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction faction = context.faction;
        FactionsPlugin instance = FactionsPlugin.getInstance();
        if (context.argIsSet(0))
            faction = context.argAsFaction(0);

        if (faction == null)
            return;

        if (context.fPlayer != null && !context.player.getPlayer().hasPermission("factions.show.bypassexempt")
                && instance.getConfig().getStringList("show-exempt").contains(faction.getTag())) {
            context.msg(TL.COMMAND_SHOW_EXEMPT);
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!context.payForCommand(Conf.econCostShow, TL.COMMAND_SHOW_TOSHOW, TL.COMMAND_SHOW_FORSHOW)) {
            return;
        }

        List<String> show = instance.getConfig().getStringList("show");
        if (show == null || show.isEmpty())
            show = defaults;

        if (!faction.isNormal()) {
            String tag = faction.getTag(context.fPlayer);
            // send header and that's all
            String header = show.get(0);
            if (TagReplacer.HEADER.contains(header)) {
                context.msg(instance.txt.titleize(tag));
            } else {
                context.msg(instance.txt.parse(TagReplacer.FACTION.replace(header, tag)));
            }
            return; // we only show header for non-normal factions
        }

        List<FancyMessage> fancy = new ArrayList<>();
        List<String> finalShow = show;
        Faction finalFaction = faction;
        instance.getServer().getScheduler().runTaskAsynchronously(instance, () -> {
            for (String raw : finalShow) {
                String parsed = instance.getConfig().getBoolean("relational-show", true) ? TagUtil.parsePlain(finalFaction, context.fPlayer, raw) : TagUtil.parsePlain(finalFaction, raw); // use relations
                if (parsed == null) {
                    continue; // Due to minimal f show.
                }

                if (context.fPlayer != null) {
                    parsed = TagUtil.parsePlaceholders(context.fPlayer.getPlayer(), parsed);
                }

                if (TagUtil.hasFancy(parsed)) {
                    List<FancyMessage> localFancy = TagUtil.parseFancy(finalFaction, context.fPlayer, parsed);
                    if (localFancy != null)
                        fancy.addAll(localFancy);

                    continue;
                }
                if (!parsed.contains("{notFrozen}") && !parsed.contains("{notPermanent}")) {
                    if (parsed.contains("{ig}")) {
                        // replaces all variables with no home TL
                        parsed = parsed.substring(0, parsed.indexOf("{ig}")) + TL.COMMAND_SHOW_NOHOME.toString();
                    }
                    if (parsed.contains("%")) {
                        parsed = parsed.replaceAll("%", ""); // Just in case it got in there before we disallowed it.
                    }
                    parsed = FactionsPlugin.getInstance().txt.parse(parsed);
                    FancyMessage localFancy = instance.txt.parseFancy(parsed);
                    fancy.add(localFancy);
                }
            }
            instance.getServer().getScheduler().runTask(instance, () -> context.sendFancyMessage(fancy));
        });
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SHOW_COMMANDDESCRIPTION;
    }

}