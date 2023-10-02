package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BrigadierManager {

    private final Commodore commodore;
    private final LiteralArgumentBuilder<Object> brigadier;

    public BrigadierManager() {
        commodore = CommodoreProvider.getCommodore(FactionsPlugin.getInstance());
        brigadier = LiteralArgumentBuilder.literal("factions");
    }

    public void build() {
        commodore.register(brigadier.build());

        // Register 'f' alias with all children of 'factions'
        LiteralArgumentBuilder<Object> fLiteral = LiteralArgumentBuilder.literal("f");
        for (CommandNode<Object> node : brigadier.getArguments()) {

            fLiteral.then(node);
        }
        commodore.register(fLiteral.build());
    }

    public void addSubCommand(FCommand subCommand) {
        for (String alias : subCommand.aliases) {
            LiteralArgumentBuilder<Object> literal = LiteralArgumentBuilder.literal(alias);

            if (subCommand.requirements.brigadier != null) {
                registerUsingProvider(subCommand, literal);
            } else {
                registerGeneratedBrigadier(subCommand, literal);
            }
        }
    }

    private void registerUsingProvider(FCommand subCommand, LiteralArgumentBuilder<Object> literal) {
        Class<? extends BrigadierProvider> brigadierProvider = subCommand.requirements.brigadier;
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
        List<RequiredArgumentBuilder<Object, ?>> stack = new ArrayList<>(subCommand.requiredArgs.size() + subCommand.optionalArgs.size());

        for (String required : subCommand.requiredArgs) {
            stack.add(RequiredArgumentBuilder.argument(required, StringArgumentType.word()));
        }

        for (Map.Entry<String, String> optionalEntry : subCommand.optionalArgs.entrySet()) {
            RequiredArgumentBuilder<Object, ?> optional;
            if (optionalEntry.getKey().equalsIgnoreCase(optionalEntry.getValue())) {
                optional = RequiredArgumentBuilder.argument(":" + optionalEntry.getKey(), StringArgumentType.word());
            } else {
                optional = RequiredArgumentBuilder.argument(optionalEntry.getKey() + "|" + optionalEntry.getValue(), StringArgumentType.word());
            }
            stack.add(optional);
        }

        return stack;
    }
}