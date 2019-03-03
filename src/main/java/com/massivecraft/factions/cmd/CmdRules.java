package com.massivecraft.factions.cmd;

import com.massivecraft.factions.SavageFactions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

import java.util.HashMap;
import java.util.List;

public class CmdRules extends FCommand {
	public CmdRules() {
		super();
		aliases.add("r");
		aliases.add("rule");
		aliases.add("rules");

		this.optionalArgs.put("add/remove/set/clear", "");
		this.errorOnToManyArgs = false;

		permission = Permission.RULES.node;

		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeColeader = true;
		senderMustBeAdmin = false;

	}

	@Override
	public void perform() {
		if (!SavageFactions.plugin.getConfig().getBoolean("frules.Enabled")) {
			fme.msg(TL.COMMAND_RULES_DISABLED_MSG);
			return;
		}
		if (this.args.size() == 0) {
			HashMap<Integer, String> rules = fme.getFaction().getRulesMap();
			if (rules.size() == 0) {
				List<String> ruleList = SavageFactions.plugin.getConfig().getStringList("frules.default-rules");
				fme.sendMessage(SavageFactions.plugin.colorList(ruleList));

			} else {
				for (int i = 0; i <= rules.size() - 1; i++) {
					fme.sendMessage(SavageFactions.plugin.color(rules.get(i)));
				}
			}

		}
		if (this.args.size() == 1) {
			if (args.get(0).equalsIgnoreCase("add")) {
				fme.msg(TL.COMMAND_RULES_ADD_INVALIDARGS);
			}
			if (args.get(0).equalsIgnoreCase("set")) {
				fme.msg(TL.COMMAND_RULES_SET_INVALIDARGS);
			}
			if (args.get(0).equalsIgnoreCase("remove")) {
				fme.msg(TL.COMMAND_RULES_REMOVE_INVALIDARGS);
			}
			if (args.get(0).equalsIgnoreCase("clear")) {
				fme.getFaction().clearRules();
				fme.msg(TL.COMMAND_RULES_CLEAR_SUCCESS);
			}

		}
		if (this.args.size() >= 2) {
			if (args.get(0).equalsIgnoreCase("add")) {
				String message = "";
				StringBuilder string = new StringBuilder(message);
				for (int i = 1; i <= args.size() - 1; i++) {
					string.append(" " + args.get(i));
				}
				fme.getFaction().addRule(string.toString());
				fme.msg(TL.COMMAND_RULES_ADD_SUCCESS);
			}

			if (this.args.size() == 2) {
				if (args.get(0).equalsIgnoreCase("remove")) {
					int index = argAsInt(1);
					fme.getFaction().removeRule(index - 1);
					fme.msg(TL.COMMAND_RULES_REMOVE_SUCCESS);
				}
			}

		}
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_RULES_DESCRIPTION;
	}
}
