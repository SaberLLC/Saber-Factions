package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class BrigadierManager {

    private final Commodore commodore;
    private final LiteralArgumentBuilder<Object> brigadier;

    // Player-like argument names
    private static final Set<String> playerArgNames = new HashSet<>(Arrays.asList(
            "player", "target", "name", "faction", "faction tag", "player name"
    ));

    public BrigadierManager() {
        commodore = CommodoreProvider.getCommodore(FactionsPlugin.getInstance());
        brigadier = LiteralArgumentBuilder.literal("factions");
    }

    public void build() {
        commodore.register(brigadier.build());

        for (String alias : Conf.baseCommandAliases) {
            LiteralArgumentBuilder<Object> aliasLiteral = LiteralArgumentBuilder.literal(alias);
            for (CommandNode<Object> node : brigadier.getArguments()) {
                aliasLiteral.then(node);
            }
            commodore.register(aliasLiteral.build());
        }
    }

    public void addSubCommand(FCommand subCommand) {
        for (String alias : subCommand.getAliases()) {
            LiteralArgumentBuilder<Object> literal = LiteralArgumentBuilder.literal(alias);

            if (subCommand.getRequirements().getBrigadier() != null) {
                registerUsingProvider(subCommand, literal);
            } else {
                registerGeneratedBrigadier(subCommand, literal);
            }
        }
    }

    private void registerUsingProvider(FCommand subCommand, LiteralArgumentBuilder<Object> literal) {
        Class<? extends BrigadierProvider> brigadierProvider = subCommand.getRequirements().getBrigadier();
        try {
            Constructor<? extends BrigadierProvider> constructor = brigadierProvider.getDeclaredConstructor();
            brigadier.then(constructor.newInstance().get(literal));
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException exception) {
            exception.printStackTrace();
        }
    }

    private void registerGeneratedBrigadier(FCommand subCommand, LiteralArgumentBuilder<Object> literal) {
        List<RequiredArgumentBuilder<Object, ?>> argsStack = generateArgsStack(subCommand);

        RequiredArgumentBuilder<Object, ?> previous = null;
        for (int i = argsStack.size() - 1; i >= 0; i--) {
            if (previous == null) {
                previous = argsStack.get(i);
            } else {
                previous = argsStack.get(i).then(previous);
            }
        }

        if (previous == null) {
            brigadier.then(literal);
        } else {
            brigadier.then(literal.then(previous));
        }
    }

    private List<RequiredArgumentBuilder<Object, ?>> generateArgsStack(FCommand subCommand) {
        List<RequiredArgumentBuilder<Object, ?>> stack = new ArrayList<>(subCommand.getRequiredArgs().size() + subCommand.getOptionalArgs().size());

        // Handle required arguments
        for (String required : subCommand.getRequiredArgs()) {
            stack.add(createArgument(required));
        }

        // Handle optional arguments
        for (Map.Entry<String, String> optionalEntry : subCommand.getOptionalArgs().entrySet()) {
            String name = optionalEntry.getKey();
            stack.add(createArgument(name));
        }

        return stack;
    }

    private RequiredArgumentBuilder<Object, ?> createArgument(String name) {
        RequiredArgumentBuilder<Object, ?> arg = RequiredArgumentBuilder.argument(name, StringArgumentType.word());

        // If argument is player-like, suggest both player names and faction tags
        if (playerArgNames.contains(name.toLowerCase())) {
            arg.suggests((context, builder) -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    builder.suggest(player.getName());
                }
                for (Faction faction : Factions.getInstance().getAllFactions()) {
                    builder.suggest(faction.getTag());
                }
                return builder.buildFuture();
            });
        }
        return arg;
    }
}