package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class CmdChat extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdChat() {
        super();
        this.aliases.addAll(Aliases.chat);

        //this.requiredArgs.add("");
        this.optionalArgs.put("mode", "next");

        this.requirements = new CommandRequirements.Builder(Permission.CHAT)
                .playerOnly()
                .memberOnly()
                .brigadier(ChatBrigadier.class)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!Conf.factionOnlyChat) {
            context.msg(TL.COMMAND_CHAT_DISABLED.toString());
            return;
        }

        String modeString = context.argAsString(0);
        ChatMode modeTarget = context.fPlayer.getChatMode().getNext();

        if (modeString != null) {
            modeString = modeString.toLowerCase();
            // Only allow Mods and higher rank to switch to this channel.
            if (modeString.startsWith("m")) {
                if (!context.fPlayer.getRole().isAtLeast(Role.MODERATOR)) {
                    context.msg(TL.COMMAND_CHAT_MOD_ONLY);
                    return;
                } else modeTarget = ChatMode.MOD;
            } else if (modeString.startsWith("p")) {
                modeTarget = ChatMode.PUBLIC;
            } else if (modeString.startsWith("a")) {
                modeTarget = ChatMode.ALLIANCE;
            } else if (modeString.startsWith("f")) {
                modeTarget = ChatMode.FACTION;
            } else if (modeString.startsWith("t")) {
                modeTarget = ChatMode.TRUCE;
            } else {
                context.msg(TL.COMMAND_CHAT_INVALIDMODE);
                return;
            }
        }

        context.fPlayer.setChatMode(modeTarget);

        switch (context.fPlayer.getChatMode()) {
            case MOD:
                context.msg(TL.COMMAND_CHAT_MODE_MOD);
                break;
            case PUBLIC:
                context.msg(TL.COMMAND_CHAT_MODE_PUBLIC);
                break;
            case ALLIANCE:
                context.msg(TL.COMMAND_CHAT_MODE_ALLIANCE);
                break;
            case TRUCE:
                context.msg(TL.COMMAND_CHAT_MODE_TRUCE);
                break;
            default:
                context.msg(TL.COMMAND_CHAT_MODE_FACTION);
                break;
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CHAT_DESCRIPTION;
    }

    protected class ChatBrigadier implements BrigadierProvider {
        @Override
        public ArgumentBuilder<Object, ?> get(ArgumentBuilder<Object, ?> parent) {
            return parent.then(LiteralArgumentBuilder.literal("public"))
                    .then(LiteralArgumentBuilder.literal("mod"))
                    .then(LiteralArgumentBuilder.literal("alliance"))
                    .then(LiteralArgumentBuilder.literal("faction"))
                    .then(LiteralArgumentBuilder.literal("truce"));
        }
    }

}