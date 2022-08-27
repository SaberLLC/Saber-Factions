package com.massivecraft.factions.cmd.relational;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TL;

/**
 * @Author: Driftay
 * @Date: 8/26/2022 6:59 PM
 */
public class CmdSetRelation extends FCommand {

    public CmdSetRelation() {
        this.aliases.addAll(Aliases.setRelation);
        this.requirements = new CommandRequirements.Builder(Permission.SET_RELATION).build();

        this.requiredArgs.add("ENEMY, NEUTRAL, TRUCE, ALLY");
        this.requiredArgs.add("fac1");
        this.requiredArgs.add("fac2");

    }

    @Override
    public void perform(CommandContext context) {
        Relation relation = Relation.fromString(context.argAsString(0));

        if(relation == Relation.MEMBER || relation == null) {
            FactionsPlugin.getInstance().cmdAutoHelp.execute(context);
            return;
        }

        Faction fac1 = context.argAsFaction(1);
        Faction fac2 = context.argAsFaction(2);

        if(fac1 == null || fac2 == null || fac1.isSystemFaction() || fac2.isSystemFaction()) {
            FactionsPlugin.getInstance().cmdAutoHelp.execute(context);
            return;
        }

        fac1.setRelationWish(fac2, relation);
        fac2.setRelationWish(fac1, relation);
        context.msg(TL.COMMAND_SET_RELATION_SUCCESS, relation, fac1, fac2);


    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SET_RELATION_DESCRIPTION;
    }
}
