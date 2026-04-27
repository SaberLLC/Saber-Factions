package com.massivecraft.factions.cmd.alts;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.massivecraft.factions.zcore.util.TagUtil.tipPlayerSingular;

public class CmdAltsList extends FCommand {

    /**
     * @author Driftay
     */

    public CmdAltsList() {
        super();
        this.getAliases().addAll(Aliases.alts_list);
        this.getOptionalArgs().put("faction", "yours");


        this.setRequirements(new CommandRequirements.Builder(Permission.LIST)
                .playerOnly()
                .memberOnly()
                .build());

    }

    @Override
    public void perform(CommandContext context) {
        Faction faction = context.faction;
        if (context.argIsSet(0)) {
            faction = context.argAsFaction(0);
        }
        if (faction == null) return;

        if (faction != context.faction && !context.fPlayer.isAdminBypassing()) {
            return;
        }

        final Set<FPlayer> alts = faction.getAltPlayers();
        if (alts == null || alts.isEmpty()) {
            context.msg(TL.COMMAND_ALTS_LIST_NOALTS, faction.getTag());
            return;
        }

        String altWord = "alt" + (alts.size() == 1 ? "" : "s");
        String verb = (alts.size() == 1 ? "is" : "are");

        Component header = Component.text("There " + verb + " " + alts.size() + " " + altWord + " in ", NamedTextColor.GOLD)
                .append(Component.text(faction.getTag(), NamedTextColor.YELLOW))
                .append(Component.text(":", NamedTextColor.GOLD));

        context.sendComponent(header);

        TextComponent.Builder message = Component.text();
        List<FPlayer> sorted = alts.stream()
                .sorted(Comparator.comparing(FPlayer::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        for (int i = 0; i < sorted.size(); i++) {
            FPlayer alt = sorted.get(i);
            String hover = tipPlayerSingular(alt);
            Component nameComp = Component.text(alt.getName(), NamedTextColor.WHITE)
                    .hoverEvent(HoverEvent.showText(Component.text(hover, NamedTextColor.GRAY)));

            message.append(nameComp);
            if (i < sorted.size() - 1) {
                message.append(Component.text(", ", NamedTextColor.GRAY));
            }
        }

        context.sendComponent(message.build());
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ALTS_LIST_DESCRIPTION;
    }
}
