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
        this.getAliases().addAll(Aliases.chat);

        //this.requiredArgs.add("");
        this.getOptionalArgs().put("mode", "next");

        this.setRequirements(new CommandRequirements.Builder(Permission.CHAT)
                .playerOnly()
                .memberOnly()
                .brigadier(ChatBrigadier.class)
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        if (!Conf.factionOnlyChat) {
            context.msg(TL.COMMAND_CHAT_DISABLED);
            return;
        }

        ChatMode modeTarget = context.fPlayer.getChatMode().getNext();
        String modeString = context.argAsString(0);
        if (modeString != null) {
            modeString = modeString.toLowerCase();
            if (modeString.startsWith("m") && !context.fPlayer.getRole().isAtLeast(Role.MODERATOR)) {
                context.msg(TL.COMMAND_CHAT_MOD_ONLY);
                return;
            }
            modeTarget = ChatMode.fromString(modeString.charAt(0));
            if (modeTarget == null) {
                context.msg(TL.COMMAND_CHAT_INVALIDMODE);
                return;
            }
        }

        context.fPlayer.setChatMode(modeTarget);
        context.msg(modeTarget.modeMessage);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CHAT_DESCRIPTION;
    }

    public static class ChatBrigadier implements BrigadierProvider {
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