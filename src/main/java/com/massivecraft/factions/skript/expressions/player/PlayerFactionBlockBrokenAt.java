package com.massivecraft.factions.skript.expressions.player;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * SaberFactionsX - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 12/5/2020
 */
public class PlayerFactionBlockBrokenAt extends SimpleExpression<String> {

    /**
     * @author Driftay
     */

    static {
        Skript.registerExpression(PlayerFactionBlockBrokenAt.class, String.class, ExpressionType.SIMPLE, "[the] faction block broken at %player%");
    }

    Expression<Player> playerExpression;

    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        playerExpression = (Expression<Player>) exprs[0];
        return true;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "Player Block Broken Within Faction Expression" + playerExpression.toString(event, debug);
    }

    @Override
    protected String[] get(Event event) {
        Player player = playerExpression.getSingle(event);
        if (event instanceof BlockBreakEvent) {
            BlockBreakEvent blockBreakEvent = (BlockBreakEvent) event;
            if (player != null) {
                Faction fac = Board.getInstance().getFactionAt(new FLocation(blockBreakEvent.getBlock().getLocation()));
                return new String[]{String.valueOf(fac.getTag())};
            }
        }
        return null;
    }
}
