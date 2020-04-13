package com.massivecraft.factions.cmd;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdShowClaims extends FCommand {

    /**
     * @author Illyria Team
     */

    public CmdShowClaims() {
        this.aliases.addAll(Aliases.show_claims);

        this.requirements = new CommandRequirements.Builder(Permission.SHOWCLAIMS)
                .withAction(PermissableAction.TERRITORY)
                .playerOnly()
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        context.sendMessage(TL.COMMAND_SHOWCLAIMS_HEADER.toString().replace("{faction}", context.faction.describeTo(context.fPlayer)));
        ListMultimap<String, String> chunkMap = ArrayListMultimap.create();
        String format = TL.COMMAND_SHOWCLAIMS_CHUNKSFORMAT.toString();
        for (FLocation fLocation : context.faction.getAllClaims()) {
            chunkMap.put(fLocation.getWorldName(), format.replace("{x}", fLocation.getX() + "").replace("{z}", fLocation.getZ() + ""));
        }
        for (String world : chunkMap.keySet()) {
            String message = TL.COMMAND_SHOWCLAIMS_FORMAT.toString().replace("{world}", world);
            // made {chunks} blank as I removed the placeholder and people wont update their config :shrug:
            context.sendMessage(message.replace("{chunks}", ""));
            StringBuilder chunks = new StringBuilder();
            for (String chunkString : chunkMap.get(world)) {
                chunks.append(chunkString).append(", ");
                if (chunks.toString().length() >= 2000) {
                    context.sendMessage(chunks.toString());
                    chunks.setLength(0);
                }
            }
            if (chunks.length() != 0) context.sendMessage(chunks.toString());
            context.sendMessage("");
        }


    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SHOWCLAIMS_DESCRIPTION;
    }


}

