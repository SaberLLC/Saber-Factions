package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;

public class CmdCornerList extends FCommand {
    public CmdCornerList() {
        super();
        this.aliases.addAll(Aliases.corner_list);
        this.optionalArgs.put("world", "name");

        this.requirements = new CommandRequirements.Builder(Permission.CORNER_LIST)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if(context.args.size() == 0) {
            //send player location world corners
            handleCornerList(context.fPlayer, context.player.getWorld());
        } else if(context.args.size() == 1) {
            World world = Bukkit.getWorld(context.args.get(0));
            if(world == null) {
                context.msg(TL.INVALID_WORLD.toString().replace("{world}", context.args.get(0)));
                return;
            }

            handleCornerList(context.fPlayer, world);

        }

    }

    public void handleCornerList(FPlayer fme, World world) {
        ArrayList<Component> ret = new ArrayList<>();
        ret.add(Component.text(TextUtil.titleize(TL.COMMAND_CORNERLIST_TITLE.toString().replace("{world}", world.getName()))));

        for(FLocation fLocation : FactionsPlugin.getInstance().getFactionsPlayerListener().getCorners()) {
            if(fLocation.getWorld() == world) {
                ret.add(Component.text(CC.translate("&2Faction At &e" + fLocation.getX() + ", &e" + fLocation.getZ() + ": &r" + Board.getInstance().getFactionAt(fLocation).getTag())));
            }
        }
        fme.sendComponent(ret);
    }

    @Override
    public TL getUsageTranslation() {
        return null;
    }
}
