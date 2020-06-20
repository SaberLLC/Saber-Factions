package com.massivecraft.factions.cmd.relational;

import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.struct.Relation;

public class CmdRelationAlly extends FRelationCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdRelationAlly() {
        aliases.addAll(Aliases.relation_ally);
        targetRelation = Relation.ALLY;
    }
}
