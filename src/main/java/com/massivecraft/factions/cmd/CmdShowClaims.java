package com.massivecraft.factions.cmd;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.World;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CmdShowClaims extends FCommand{

    public CmdShowClaims(){

        this.aliases.add("showclaims");
        this.aliases.add("showclaim");

        permission = Permission.SHOWCLAIMS.node;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
        this.senderMustBePlayer = true;


    }

    @Override
    public void perform(){
        sendMessage(TL.COMMAND_SHOWCLAIMS_HEADER.toString().replace("{faction}",fme.getFaction().describeTo(fme)));
        ListMultimap<String,String> chunkMap = ArrayListMultimap.create();
        String format = TL.COMMAND_SHOWCLAIMS_CHUNKSFORMAT.toString();
        for (FLocation fLocation : fme.getFaction().getAllClaims()){
            chunkMap.put(fLocation.getWorldName(),format.replace("{x}",fLocation.getX() + "").replace("{z}",fLocation.getZ() + ""));
        }
        for (String world : chunkMap.keySet()){
            String message = TL.COMMAND_SHOWCLAIMS_FORMAT.toString().replace("{world}",world);
            StringBuilder chunks = new StringBuilder("");
            for (String chunkString : chunkMap.get(world)){
                chunks.append(chunkString + ", ");
            }
            sendMessage(message.replace("{chunks}",chunks));
            sendMessage("");
        }






    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SHOWCLAIMS_DESCRIPTION;
    }


}
