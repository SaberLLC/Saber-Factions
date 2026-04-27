package com.massivecraft.factions.cmd.tnt.tntprovider;

import com.massivecraft.factions.cmd.CommandContext;

public class FactionTNTProvider implements TNTProvider {

    private final CommandContext context;

    public FactionTNTProvider(CommandContext context) {
        this.context = context;
    }

    @Override
    public int getTnt() {
        return (int) context.faction.getTnt();
    }

    @Override
    public void sendMessage(String message) {
        context.fPlayer.msg(message);
    }

    @Override
    public void takeTnt(int toRemove) {
        context.faction.takeTnt(toRemove);
    }

    @Override
    public boolean isAvailable() {
        return context.fPlayer.isOnline();
    }
}
