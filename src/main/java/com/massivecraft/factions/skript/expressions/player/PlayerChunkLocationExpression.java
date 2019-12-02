package com.massivecraft.factions.skript.expressions.player;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.massivecraft.factions.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class PlayerChunkLocationExpression extends SimpleExpression<String> {

    /**
     * @author Driftay
     */

    static {
        Skript.registerExpression(PlayerChunkLocationExpression.class, String.class, ExpressionType.SIMPLE, "[the] faction chunk at %player%", "[the] %player%['s] chunk");
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
        return "Player Location Expression with expression faction" + playerExpression.toString(event, debug);
    }

    @Override
    protected String[] get(Event event) {
        Player player = playerExpression.getSingle(event);

        if (player != null) {
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            FLocation floc = new FLocation(fPlayer.getPlayer().getLocation());
            Faction fac = Board.getInstance().getFactionAt(floc);
            return new String[]{String.valueOf(fac.getTag())};
        }
        return null;
    }
}
