package com.massivecraft.factions.cmd;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdShowClaims extends FCommand {

    public CmdShowClaims() {

        this.aliases.add("showclaims");
        this.aliases.add("showclaim");

        permission = Permission.SHOWCLAIMS.node;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
    }

    @Override
    public void perform() {
        sendMessage(TL.COMMAND_SHOWCLAIMS_HEADER.toString().replace("{faction}", fme.getFaction().describeTo(fme)));
        ListMultimap<String, String> chunkMap = ArrayListMultimap.create();
        String format = TL.COMMAND_SHOWCLAIMS_CHUNKSFORMAT.toString();
        for (FLocation fLocation : fme.getFaction().getAllClaims()) {
            chunkMap.put(fLocation.getWorldName(), format.replace("{x}", fLocation.getX() + "").replace("{z}", fLocation.getZ() + ""));
        }
        for (String world : chunkMap.keySet()) {
            String message = TL.COMMAND_SHOWCLAIMS_FORMAT.toString().replace("{world}", world);
            // made {chunks} blank as I removed the placeholder and people wont update their config :shrug:
            sendMessage(message.replace("{chunks}", ""));
            StringBuilder chunks = new StringBuilder();
            for (String chunkString : chunkMap.get(world)) {
                chunks.append(chunkString).append(", ");
                if (chunks.toString().length() >= 2000) {
                    sendMessage(chunks.toString());
                    chunks.setLength(0);
                }
            }
            if (chunks.length() != 0) sendMessage(chunks.toString());
            sendMessage("");
        }


    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SHOWCLAIMS_DESCRIPTION;
    }


}
