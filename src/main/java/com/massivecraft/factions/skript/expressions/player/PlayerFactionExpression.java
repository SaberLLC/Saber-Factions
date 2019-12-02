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
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class PlayerFactionExpression extends SimpleExpression<String> {

    /**
     * @author Illyria Team
     */


    static {
        Skript.registerExpression(PlayerFactionExpression.class, String.class, ExpressionType.SIMPLE, "[the] faction of %player%", "[the] %player%['s] faction");
    }

    Expression<Player> playerExpression;

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
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
        return "Player Faction Name Expression with expression player" + playerExpression.toString(event, debug);
    }

    @Override
    protected String[] get(Event event) {
        Player player = playerExpression.getSingle(event);

        if (player != null) {
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            return new String[]{fPlayer.getFaction().getTag()};
        }

        return null;

    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(String.class);
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
            case DELETE:
            case RESET:
                fPlayer.setFaction(Factions.getInstance().getWilderness(), false);
                break;
            case SET:
                Faction faction = Factions.getInstance().getByTag((String) delta[0]);
                if (faction == null) {
                    faction = Factions.getInstance().getWilderness();
                }
                fPlayer.setFaction(faction, false);
                break;
            default:
        }
    }
}
