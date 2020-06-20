package com.massivecraft.factions.cmd.relational;

import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.struct.Relation;

public class CmdRelationNeutral extends FRelationCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdRelationNeutral() {
        aliases.addAll(Aliases.relation_neutral);
        targetRelation = Relation.NEUTRAL;
    }
}
