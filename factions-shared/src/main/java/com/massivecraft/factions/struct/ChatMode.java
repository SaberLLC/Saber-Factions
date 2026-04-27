package com.massivecraft.factions.struct;

import com.massivecraft.factions.zcore.util.TL;

public enum ChatMode {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    MOD(4, TL.CHAT_MOD, TL.COMMAND_CHAT_MODE_MOD),
    FACTION(3, TL.CHAT_FACTION, TL.COMMAND_CHAT_MODE_FACTION),
    ALLIANCE(2, TL.CHAT_ALLIANCE, TL.COMMAND_CHAT_MODE_ALLIANCE),
    TRUCE(1, TL.CHAT_TRUCE, TL.COMMAND_CHAT_MODE_TRUCE),
    PUBLIC(0, TL.CHAT_PUBLIC, TL.COMMAND_CHAT_MODE_PUBLIC);

    public final int value;
    public final TL nicename;
    public final TL modeMessage;

    ChatMode(final int value, final TL nicename, final TL modeMessage) {
        this.value = value;
        this.nicename = nicename;
        this.modeMessage = modeMessage;
    }

    public boolean isAtLeast(ChatMode role) {
        return this.value >= role.value;
    }

    public boolean isAtMost(ChatMode role) {
        return this.value <= role.value;
    }

    public static ChatMode fromString(char c) {
        switch (c) {
            case 'p':
                return ChatMode.PUBLIC;
            case 'a':
                return ChatMode.ALLIANCE;
            case 'f':
                return ChatMode.FACTION;
            case 't':
                return ChatMode.TRUCE;
            case 'm':
                return ChatMode.MOD;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return this.nicename.toString();
    }

    public ChatMode getNext() {
        if (this == PUBLIC) {
            return ALLIANCE;
        }
        if (this == ALLIANCE) {
            return FACTION;
        }
        return PUBLIC;
    }
}
