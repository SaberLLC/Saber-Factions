package com.massivecraft.factions.skript.expressions.player;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class PlayerPowerExpression extends SimpleExpression<Number> {

    /**
     * @author Illyria Team
     */


    static {
        Skript.registerExpression(PlayerPowerExpression.class, Number.class, ExpressionType.SIMPLE, "[the] power of %player%", "[the] %player%['s] power");
    }

    Expression<Player> playerExpression;

    @Override
    public Class<? extends Number> getReturnType() {
        return Double.class;
    }


    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        playerExpression = (Expression<Player>) exprs[0];
        return true;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "Player Power with expression player" + playerExpression.toString(event, debug);
    }

    @Override
    protected Double[] get(Event event) {
        Player player = playerExpression.getSingle(event);

        if (player != null) {
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            return new Double[]{fPlayer.getFaction().getPower()};
        }

        return null;

    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }


    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Player player = playerExpression.getSingle(event);
        if (player == null) {
            return;
        }

        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        switch (mode) {
            case ADD:
                fPlayer.alterPower(Double.valueOf((Long) delta[0]));
                break;
            case REMOVE:
                fPlayer.alterPower(Double.valueOf((Long) delta[0]) * -1);
                break;
            case RESET:
                fPlayer.alterPower(fPlayer.getPowerMax() * -1);
                break;
            default:
        }


    }


}
