package com.massivecraft.factions.cmd.relational;

import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.struct.Relation;

public class CmdRelationEnemy extends FRelationCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdRelationEnemy() {
        getAliases().addAll(Aliases.relation_enemy);
        targetRelation = Relation.ENEMY;
    }
}
