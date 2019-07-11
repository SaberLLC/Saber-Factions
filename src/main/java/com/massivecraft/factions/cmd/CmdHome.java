package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.integration.Essentials;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.SmokeUtil;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class CmdHome extends FCommand {

	public CmdHome() {
		super();
		this.aliases.add("home");

		this.optionalArgs.put("faction", "yours");
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");

		this.permission = Permission.HOME.node;
		this.disableOnLock = false;


		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeColeader = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		// TODO: Hide this command on help also.


		if (!Conf.homesEnabled) {
			fme.msg(TL.COMMAND_HOME_DISABLED);
			return;
		}

		if (!Conf.homesTeleportCommandEnabled) {
			fme.msg(TL.COMMAND_HOME_TELEPORTDISABLED);
			return;
		}

		Faction factionArg = myFaction;

		if(argIsSet(0)){
			factionArg = argAsFaction(0);
		}

		if(factionArg == null || factionArg.isWilderness())
			return;

		if(factionArg.hasHome()){
			if(fme.isAdminBypassing()) {
				fme.getPlayer().teleport(factionArg.getHome());
				fme.msg(TL.COMMAND_HOME_TELEPORT_OTHER, factionArg.getTag());
			} else {
				fme.msg(TL.GENERIC_FPERM_NOPERMISSION, "teleport home");
				return;
			}
		} else {
			fme.msg(TL.COMMAND_HOME_OTHER_NOTSET, factionArg.getTag());
			return;
		}


		if(myFaction.isWilderness())
			return;

		if (!fme.isAdminBypassing()) {
			Access access = myFaction.getAccess(fme, PermissableAction.HOME);
			if (access != Access.ALLOW && fme.getRole() != Role.LEADER) {
				fme.msg(TL.GENERIC_FPERM_NOPERMISSION, "teleport home");
				return;
			}
		}


		if (!myFaction.hasHome()) {
			fme.msg(TL.COMMAND_HOME_NOHOME.toString() + (fme.getRole().value < Role.MODERATOR.value ? TL.GENERIC_ASKYOURLEADER.toString() : TL.GENERIC_YOUSHOULD.toString()));
			fme.sendMessage(p.cmdBase.cmdSethome.getUseageTemplate());
			return;
		}



		if (!Conf.homesTeleportAllowedFromEnemyTerritory && fme.isInEnemyTerritory()) {
			fme.msg(TL.COMMAND_HOME_INENEMY);
			return;
		}

		if (!Conf.homesTeleportAllowedFromDifferentWorld && me.getWorld().getUID() != myFaction.getHome().getWorld().getUID()) {
			fme.msg(TL.COMMAND_HOME_WRONGWORLD);
			return;
		}

		Faction faction = Board.getInstance().getFactionAt(new FLocation(me.getLocation()));
		final Location loc = me.getLocation().clone();

		// if player is not in a safe zone or their own faction territory, only allow teleport if no enemies are nearby
		if (Conf.homesTeleportAllowedEnemyDistance > 0 && !faction.isSafeZone() && (!fme.isInOwnTerritory() || !Conf.homesTeleportIgnoreEnemiesIfInOwnTerritory)) {
			World w = loc.getWorld();
			double x = loc.getX();
			double y = loc.getY();
			double z = loc.getZ();

			for (Player p : me.getServer().getOnlinePlayers()) {
				if (p == null || !p.isOnline() || p.isDead() || p == me || p.getWorld() != w) {
					continue;
				}

				FPlayer fp = FPlayers.getInstance().getByPlayer(p);
				if (fme.getRelationTo(fp) != Relation.ENEMY || fp.isVanished()) {
					continue;
				}

				Location l = p.getLocation();
				double dx = Math.abs(x - l.getX());
				double dy = Math.abs(y - l.getY());
				double dz = Math.abs(z - l.getZ());
				double max = Conf.homesTeleportAllowedEnemyDistance;

				// box-shaped distance check
				if (dx > max || dy > max || dz > max) {
					continue;
				}

				fme.msg(TL.COMMAND_HOME_ENEMYNEAR, String.valueOf(Conf.homesTeleportAllowedEnemyDistance));
				return;
			}
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostHome, TL.COMMAND_HOME_TOTELEPORT.toString(), TL.COMMAND_HOME_FORTELEPORT.toString())) {
			return;
		}

		// if Essentials teleport handling is enabled and available, pass the teleport off to it (for delay and cooldown)
		if (Essentials.handleTeleport(me, myFaction.getHome())) {
			return;
		}

		this.doWarmUp(WarmUpUtil.Warmup.HOME, TL.WARMUPS_NOTIFY_TELEPORT, "Home", () -> {
			// Create a smoke effect
			if (Conf.homesTeleportCommandSmokeEffectEnabled) {
				List<Location> smokeLocations = new ArrayList<>();
				smokeLocations.add(loc);
				smokeLocations.add(loc.add(0, 1, 0));
				smokeLocations.add(CmdHome.this.myFaction.getHome());
				smokeLocations.add(CmdHome.this.myFaction.getHome().clone().add(0, 1, 0));
				SmokeUtil.spawnCloudRandom(smokeLocations, Conf.homesTeleportCommandSmokeEffectThickness);
			}

			CmdHome.this.me.teleport(CmdHome.this.myFaction.getHome());
		}, this.p.getConfig().getLong("warmups.f-home", 0));
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_HOME_DESCRIPTION;
	}

}
