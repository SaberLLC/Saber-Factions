package com.massivecraft.factions.cmd.relational;

import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.struct.Relation;

public class CmdRelationTruce extends FRelationCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdRelationTruce() {
        aliases.addAll(Aliases.relation_truce);
        targetRelation = Relation.TRUCE;
    }
}
