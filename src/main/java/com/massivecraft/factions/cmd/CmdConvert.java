package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Conf.Backend;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.persist.json.FactionsJSON;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.command.ConsoleCommandSender;

public class CmdConvert extends FCommand {

	public CmdConvert() {
		this.aliases.add("convert");

		this.permission = Permission.CONVERT.node;

		this.requiredArgs.add("[MYSQL|JSON]");
	}

	@Override
	public void perform() {
		if (!(this.sender instanceof ConsoleCommandSender)) {
			this.sender.sendMessage(TL.GENERIC_CONSOLEONLY.toString());
		}
		Backend nb = Backend.valueOf(this.argAsString(0).toUpperCase());
		if (nb == Conf.backEnd) {
			this.sender.sendMessage(TL.COMMAND_CONVERT_BACKEND_RUNNING.toString());
			return;
		}
		if (nb == Backend.JSON) {
			FactionsJSON.convertTo();
		} else {
			this.sender.sendMessage(TL.COMMAND_CONVERT_BACKEND_INVALID.toString());
			return;
		}
		Conf.backEnd = nb;
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_CONVERT_DESCRIPTION;
	}

}
