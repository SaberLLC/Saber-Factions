package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

import java.text.DecimalFormat;
import java.util.Set;

/**
 * @author Saser
 */
public class CmdLookup extends FCommand {

    private DecimalFormat format = new DecimalFormat("#.#");

    public CmdLookup() {
        super();
        this.aliases.addAll(Aliases.lookup);
        this.requiredArgs.add("faction name");

        this.requirements = new CommandRequirements.Builder(Permission.LOOKUP)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction faction = context.argAsFaction(0);
        if (faction == null) {
            context.msg(TL.COMMAND_LOOKUP_INVALID);
            return;
        }
        if (faction.isNormal()) {
            if (faction.getHome() != null) {
                context.msg(TL.COMMAND_LOOKUP_FACTION_HOME, this.format.format(faction.getHome().getX()), this.format.format(faction.getHome().getY()), this.format.format(faction.getHome().getZ()));
            }
            Set<FLocation> locations = Board.getInstance().getAllClaims(faction);
            context.msg(TL.COMMAND_LOOKUP_CLAIM_COUNT, locations.size(), faction.getTag());
            for (FLocation flocation : locations) {
                context.msg(TL.COMMAND_LOOKUP_CLAIM_LIST, flocation.getWorldName(), flocation.getX() * 16L, flocation.getZ() * 16L);
            }
        } else {
            context.msg(TL.COMMAND_LOOKUP_ONLY_NORMAL);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_LOOKUP_DESCRIPTION;
    }
}
