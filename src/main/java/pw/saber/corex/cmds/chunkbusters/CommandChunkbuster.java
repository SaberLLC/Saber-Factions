package pw.saber.corex.cmds.chunkbusters;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.saber.corex.utils.NBTParsedItem;

public class CommandChunkbuster implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,  String[] args) {
        // /chunkbuster give <player> <amount>
        if(sender.hasPermission(Permission.CHUNKBUSTER_GIVE.node)) {
            if(args.length < 3) {
                sender.sendMessage(TextUtil.parseColor(String.valueOf(TL.COMMAND_CHUNKBUSTER_USAGE)));
                return false;
            }

            if(args[0].equalsIgnoreCase("give")) {
                Player target = Bukkit.getPlayer(args[1]);
                if(target == null || !target.isOnline()) {
                    sender.sendMessage(TextUtil.parseColor(String.valueOf(TL.INVALID_PLAYER).replace("{player}", args[1])));
                    return false;
                }
                int amount = Integer.parseInt(args[2]);
                if(amount > 0) {
                    //build chunk buster item
                    target.getInventory().addItem(NBTParsedItem.createChunkBusterItem(amount));
                    target.updateInventory();
                    target.sendMessage(TextUtil.parseColor(String.valueOf(TL.CHUNKBUSTER_RECEIEVED)));
                    return true;
                } else {
                    sender.sendMessage(TextUtil.parseColor(String.valueOf(TL.INVALID_PLAYER).replace("{player}", args[1])));
                }
            }
        }
        return false;
    }
}
