package com.massivecraft.factions.cmd;

import com.mojang.brigadier.builder.ArgumentBuilder;

public interface BrigadierProvider {

    /**
     * @author FactionsUUID Team
     */

    ArgumentBuilder<Object, ?> get(ArgumentBuilder<Object, ?> parent);

}
