package com.massivecraft.factions.listeners;

import com.earth2me.essentials.User;
import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.CmdFly;
import com.massivecraft.factions.cmd.CmdSeeChunk;
import com.massivecraft.factions.event.FPlayerEnteredFactionEvent;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.scoreboards.FScoreboard;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import com.massivecraft.factions.scoreboards.sidebar.FDefaultSidebar;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.FactionGUI;
import com.massivecraft.factions.util.MultiversionMaterials;
import com.massivecraft.factions.util.VisualizeUtil;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.persist.*;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TagUtil;
import com.massivecraft.factions.zcore.util.TextUtil;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.NumberConversions;

import java.util.*;
import java.util.logging.Level;


public class FactionsPlayerListener implements Listener {


	HashMap<Player, Boolean> fallMap = new HashMap<>();

	// Holds the next time a player can have a map shown.
	private HashMap<UUID, Long> showTimes = new HashMap<>();
	// for handling people who repeatedly spam attempts to open a door (or similar) in another faction's territory
	private Map<String, InteractAttemptSpam> interactSpammers = new HashMap<>();

	public FactionsPlayerListener() {
		for (Player player : SavageFactions.plugin.getServer().getOnlinePlayers()) {
			initPlayer(player);
		}
	}

	public static Boolean isSystemFaction(Faction faction) {
		return faction.isSafeZone() ||
				  faction.isWarZone() ||
				  faction.isWilderness();
	}

	public static boolean playerCanUseItemHere(Player player, Location location, Material material, boolean justCheck) {
		String name = player.getName();
		if (Conf.playersWhoBypassAllProtection.contains(name)) {
			return true;
		}


		FPlayer me = FPlayers.getInstance().getByPlayer(player);
		if (me.isAdminBypassing()) {
			return true;
		}

		FLocation loc = new FLocation(location);
		Faction otherFaction = Board.getInstance().getFactionAt(loc);
		Faction myFaction = me.getFaction();
		Relation rel = myFaction.getRelationTo(otherFaction);

		// Also cancel if player doesn't have ownership rights for this claim
		if (Conf.ownedAreasEnabled && myFaction == otherFaction && !myFaction.playerHasOwnershipRights(me, loc)) {
			if (!justCheck) {
				me.msg("<b>You can't use that in this territory, it is owned by: " + otherFaction.getOwnerListString(loc));
			}
			return false;
		}

		if (SavageFactions.plugin.getConfig().getBoolean("hcf.raidable", false) && otherFaction.getLandRounded() > otherFaction.getPowerRounded()) {
			return true;
		}

		if (otherFaction.hasPlayersOnline()) {
			if (!Conf.territoryDenyUseageMaterials.contains(material)) {
				return true; // Item isn't one we're preventing for online factions.
			}
		} else {
			if (!Conf.territoryDenyUseageMaterialsWhenOffline.contains(material)) {
				return true; // Item isn't one we're preventing for offline factions.
			}
		}

		if (otherFaction.isWilderness()) {
			if (!Conf.wildernessDenyUseage || Conf.worldsNoWildernessProtection.contains(location.getWorld().getName())) {
				return true; // This is not faction territory. Use whatever you like here.
			}

			if (!justCheck) {
				me.msg(TL.PLAYER_USE_WILDERNESS, TextUtil.getMaterialName(material));
			}

			return false;
		} else if (otherFaction.isSafeZone()) {
			if (!Conf.safeZoneDenyUseage || Permission.MANAGE_SAFE_ZONE.has(player)) {
				return true;
			}

			if (!justCheck) {
				me.msg(TL.PLAYER_USE_SAFEZONE, TextUtil.getMaterialName(material));
			}

			return false;
		} else if (otherFaction.isWarZone()) {
			if (!Conf.warZoneDenyUseage || Permission.MANAGE_WAR_ZONE.has(player)) {
				return true;
			}

			if (!justCheck) {
				me.msg(TL.PLAYER_USE_WARZONE, TextUtil.getMaterialName(material));
			}

			return false;
		}

		// Cancel if we are not in our own territory
		if (rel.confDenyUseage()) {
			if (!justCheck) {
				me.msg(TL.PLAYER_USE_TERRITORY, TextUtil.getMaterialName(material), otherFaction.getTag(myFaction));
			}
			return false;
		}

		Access access = otherFaction.getAccess(me, PermissableAction.ITEM);
		return CheckPlayerAccess(player, me, loc, myFaction, access, PermissableAction.ITEM, false);
	}

	@SuppressWarnings("deprecation")
	public static boolean canPlayerUseBlock(Player player, Block block, boolean justCheck) {
		if (Conf.playersWhoBypassAllProtection.contains(player.getName()))
			return true;

		FPlayer me = FPlayers.getInstance().getByPlayer(player);
		if (me.isAdminBypassing())
			return true;

		Material material = block.getType();

		// Dupe fix.
		FLocation loc = new FLocation(block);
		Faction otherFaction = Board.getInstance().getFactionAt(loc);
		Faction myFaction = me.getFaction();
		Relation rel = myFaction.getRelationTo(otherFaction);

		// no door/chest/whatever protection in wilderness, war zones, or safe zones
		if (otherFaction.isSystemFaction()) return true;
		if (myFaction.isWilderness()) {
			me.msg(TL.GENERIC_NOPERMISSION, TL.GENERIC_DOTHAT);
			return false;
		}

		if (SavageFactions.plugin.getConfig().getBoolean("hcf.raidable", false) && otherFaction.getLandRounded() > otherFaction.getPowerRounded())
			return true;

		PermissableAction action = GetPermissionFromUsableBlock(block);
		if (action == null) return false;


		// We only care about some material types.
		/// Who was the idiot?
		if (otherFaction.hasPlayersOnline()) {
			if (Conf.territoryProtectedMaterials.contains(material)) {
				return false;
			}
		} else {
			if (Conf.territoryProtectedMaterialsWhenOffline.contains(material)) {
				return false;
			}
		}

		// Move up access check to check for exceptions
		if (!otherFaction.getId().equals(myFaction.getId())) { // If the faction target is not my own
			// Get faction pain build access relation to me
			boolean pain = !justCheck && otherFaction.getAccess(me, PermissableAction.PAIN_BUILD) == Access.ALLOW;
			return CheckPlayerAccess(player, me, loc, otherFaction, otherFaction.getAccess(me, action), action, pain);
		} else if (otherFaction.getId().equals(myFaction.getId())) {
			return CheckPlayerAccess(player, me, loc, myFaction, myFaction.getAccess(me, action), action, (!justCheck && myFaction.getAccess(me, PermissableAction.PAIN_BUILD) == Access.ALLOW));
		}
		return CheckPlayerAccess(player, me, loc, myFaction, otherFaction.getAccess(me, action), action, Conf.territoryPainBuild);
	}


	public static boolean preventCommand(String fullCmd, Player player) {
		if ((Conf.territoryNeutralDenyCommands.isEmpty() && Conf.territoryEnemyDenyCommands.isEmpty() && Conf.permanentFactionMemberDenyCommands.isEmpty() && Conf.warzoneDenyCommands.isEmpty())) {
			return false;
		}

		fullCmd = fullCmd.toLowerCase();

		FPlayer me = FPlayers.getInstance().getByPlayer(player);

		String shortCmd;  // command without the slash at the beginning
		if (fullCmd.startsWith("/")) {
			shortCmd = fullCmd.substring(1);
		} else {
			shortCmd = fullCmd;
			fullCmd = "/" + fullCmd;
		}

		if (me.hasFaction() &&
				  !me.isAdminBypassing() &&
				  !Conf.permanentFactionMemberDenyCommands.isEmpty() &&
				  me.getFaction().isPermanent() &&
				  isCommandInList(fullCmd, shortCmd, Conf.permanentFactionMemberDenyCommands.iterator())) {
			me.msg(TL.PLAYER_COMMAND_PERMANENT, fullCmd);
			return true;
		}

		Faction at = Board.getInstance().getFactionAt(new FLocation(player.getLocation()));
		if (at.isWilderness() && !Conf.wildernessDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.wildernessDenyCommands.iterator())) {
			me.msg(TL.PLAYER_COMMAND_WILDERNESS, fullCmd);
			return true;
		}

		Relation rel = at.getRelationTo(me);
		if (at.isNormal() && rel.isAlly() && !Conf.territoryAllyDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.territoryAllyDenyCommands.iterator())) {
			me.msg(TL.PLAYER_COMMAND_ALLY, fullCmd);
			return false;
		}

		if (at.isNormal() && rel.isNeutral() && !Conf.territoryNeutralDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.territoryNeutralDenyCommands.iterator())) {
			me.msg(TL.PLAYER_COMMAND_NEUTRAL, fullCmd);
			return true;
		}

		if (at.isNormal() && rel.isEnemy() && !Conf.territoryEnemyDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.territoryEnemyDenyCommands.iterator())) {
			me.msg(TL.PLAYER_COMMAND_ENEMY, fullCmd);
			return true;
		}

		if (at.isWarZone() && !Conf.warzoneDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.warzoneDenyCommands.iterator())) {
			me.msg(TL.PLAYER_COMMAND_WARZONE, fullCmd);
			return true;
		}

		return false;
	}

	private static boolean isCommandInList(String fullCmd, String shortCmd, Iterator<String> iter) {
		String cmdCheck;
		while (iter.hasNext()) {
			cmdCheck = iter.next();
			if (cmdCheck == null) {
				iter.remove();
				continue;
			}

			cmdCheck = cmdCheck.toLowerCase();
			if (fullCmd.startsWith(cmdCheck) || shortCmd.startsWith(cmdCheck)) {
				return true;
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		initPlayer(event.getPlayer());
	}

	private void initPlayer(Player player) {
		// Make sure that all online players do have a fplayer.
		final FPlayer me = FPlayers.getInstance().getByPlayer(player);
		((MemoryFPlayer) me).setName(player.getName());

		// Update the lastLoginTime for this fplayer
		me.setLastLoginTime(System.currentTimeMillis());

		// Store player's current FLocation and notify them where they are
		me.setLastStoodAt(new FLocation(player.getLocation()));

		me.login(); // set kills / deaths

		// Check for Faction announcements. Let's delay this so they actually see it.
		Bukkit.getScheduler().runTaskLater(SavageFactions.plugin, new Runnable() {
			@Override
			public void run() {
				if (me.isOnline()) {
					me.getFaction().sendUnreadAnnouncements(me);
				}
			}
		}, 33L); // Don't ask me why.

		if (SavageFactions.plugin.getConfig().getBoolean("scoreboard.default-enabled", false)) {
			FScoreboard.init(me);
			FScoreboard.get(me).setDefaultSidebar(new FDefaultSidebar(), SavageFactions.plugin.getConfig().getInt("scoreboard.default-update-interval", 20));
			FScoreboard.get(me).setSidebarVisibility(me.showScoreboard());
		}

		Faction myFaction = me.getFaction();
		if (!myFaction.isWilderness()) {
			for (FPlayer other : myFaction.getFPlayersWhereOnline(true)) {
				if (other != me && other.isMonitoringJoins()) {
					other.msg(TL.FACTION_LOGIN, me.getName());
				}
			}
		}


		fallMap.put(me.getPlayer(), false);
		Bukkit.getScheduler().scheduleSyncDelayedTask(SavageFactions.plugin, new Runnable() {
			@Override
			public void run() {
				fallMap.remove(me.getPlayer());

			}
		}, 180L);


		if (me.isSpyingChat() && !player.hasPermission(Permission.CHATSPY.node)) {
			me.setSpyingChat(false);
			SavageFactions.plugin.log(Level.INFO, "Found %s spying chat without permission on login. Disabled their chat spying.", player.getName());
		}

		if (me.isAdminBypassing() && !player.hasPermission(Permission.BYPASS.node)) {
			me.setIsAdminBypassing(false);
			SavageFactions.plugin.log(Level.INFO, "Found %s on admin Bypass without permission on login. Disabled it for them.", player.getName());
		}


		// If they have the permission, don't let them autoleave. Bad inverted setter :\
		me.setAutoLeave(!player.hasPermission(Permission.AUTO_LEAVE_BYPASS.node));
		me.setTakeFallDamage(true);
	}

	@EventHandler
	public void onPlayerFall(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
				Player player = (Player) e.getEntity();
				if (fallMap.containsKey(player)) {
					e.setCancelled(true);
					fallMap.remove(player);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		FPlayer me = FPlayers.getInstance().getByPlayer(event.getPlayer());

		// Make sure player's power is up to date when they log off.
		me.getPower();
		// and update their last login time to point to when the logged off, for auto-remove routine
		me.setLastLoginTime(System.currentTimeMillis());

		me.logout(); // cache kills / deaths

		// if player is waiting for fstuck teleport but leaves, remove
		if (SavageFactions.plugin.getStuckMap().containsKey(me.getPlayer().getUniqueId())) {
			FPlayers.getInstance().getByPlayer(me.getPlayer()).msg(TL.COMMAND_STUCK_CANCELLED);
			SavageFactions.plugin.getStuckMap().remove(me.getPlayer().getUniqueId());
			SavageFactions.plugin.getTimers().remove(me.getPlayer().getUniqueId());
		}

		Faction myFaction = me.getFaction();
		if (!myFaction.isWilderness()) {
			myFaction.memberLoggedOff();
		}

		if (!myFaction.isWilderness()) {
			for (FPlayer player : myFaction.getFPlayersWhereOnline(true)) {
				if (player != me && player.isMonitoringJoins()) {
					player.msg(TL.FACTION_LOGOUT, me.getName());
				}
			}
		}

		CmdSeeChunk.seeChunkMap.remove(event.getPlayer().getName());

		FScoreboard.remove(me);
	}

	public String parseAllPlaceholders(String string, Faction faction, Player player) {
		string = TagUtil.parsePlaceholders(player, string);

		string = string.replace("{Faction}", faction.getTag())
				  .replace("{online}", faction.getOnlinePlayers().size() + "")
				  .replace("{offline}", faction.getFPlayers().size() - faction.getOnlinePlayers().size() + "")
				  .replace("{chunks}", faction.getAllClaims().size() + "")
				  .replace("{power}", faction.getPower() + "")
				  .replace("{leader}", faction.getFPlayerAdmin() + "");

		return string;
	}

	public void enableFly(FPlayer me) {
		if (SavageFactions.plugin.getConfig().getBoolean("ffly.AutoEnable")) {

			me.setFlying(true);
			CmdFly.flyMap.put(me.getName(), true);
			if (CmdFly.id == -1) {
				if (SavageFactions.plugin.getConfig().getBoolean("ffly.Particles.Enabled")) {
					CmdFly.startParticles();
				}
			}
			if (CmdFly.flyid == -1) {
				CmdFly.startFlyCheck();
			}
		}
	}

	//inspect
	@EventHandler
	public void onInspect(PlayerInteractEvent e) {
		if (e.getAction().name().contains("BLOCK")) {
			FPlayer fplayer = FPlayers.getInstance().getByPlayer(e.getPlayer());
			if (!fplayer.isInspectMode()) {
				return;
			}
			e.setCancelled(true);
			if (!fplayer.isAdminBypassing()) {
				if (!fplayer.hasFaction()) {
					fplayer.setInspectMode(false);
					fplayer.msg(TL.COMMAND_INSPECT_DISABLED_NOFAC);
					return;
				}
				if (fplayer.getFaction() != Board.getInstance().getFactionAt(new FLocation(e.getPlayer().getLocation()))) {
					fplayer.msg(TL.COMMAND_INSPECT_NOTINCLAIM);
					return;
				}
			} else {
				fplayer.msg(TL.COMMAND_INSPECT_BYPASS);
			}
			List<String[]> info = CoreProtect.getInstance().getAPI().blockLookup(e.getClickedBlock(), 0);
			if (info.size() == 0) {
				e.getPlayer().sendMessage(TL.COMMAND_INSPECT_NODATA.toString());
				return;
			}
			Player player = e.getPlayer();
			CoreProtectAPI coAPI = CoreProtect.getInstance().getAPI();
			player.sendMessage(TL.COMMAND_INSPECT_HEADER.toString().replace("{x}", e.getClickedBlock().getX() + "")
					  .replace("{y}", e.getClickedBlock().getY() + "")
					  .replace("{z}", e.getClickedBlock().getZ() + ""));
			String rowFormat = TL.COMMAND_INSPECT_ROW.toString();
			for (int i = 0; i < info.size(); i++) {
				CoreProtectAPI.ParseResult row = coAPI.parseResult(info.get(0));
				player.sendMessage(rowFormat
						  .replace("{time}", convertTime(row.getTime()))
						  .replace("{action}", row.getActionString())
						  .replace("{player}", row.getPlayer())
						  .replace("{block-type}", row.getType().toString().toLowerCase()));
			}
		}
	}

	//For disabling enderpearl throws
	@EventHandler
	public void onPearl(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (player.getItemInHand().getType() == Material.ENDER_PEARL) {
			FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
			if (fPlayer.isFlying()) {
				if (Conf.noEnderpearlsInFly) {
					fPlayer.msg(TL.COMMAND_FLY_NO_EPEARL);
					e.setCancelled(true);
				}
			}
		}
	}

	//For Blocking Homes and Blocking Teleportation To Homes
	@EventHandler
	public void onPlayerHomeCheck(PlayerTeleportEvent event) throws Exception {
		if (event.getPlayer().hasMetadata("NPC")) {
			return;
		}
		if (event.getPlayer().hasPermission("factions.homes.bypass")) {
			return;
		}
		if (Bukkit.getPluginManager().getPlugin("Essentials") == null) {
			return;
		}
		boolean isHome = false;
		for (String str : SavageFactions.plugin.ess.getUser(event.getPlayer()).getHomes()) {
			Location home = SavageFactions.plugin.ess.getUser(event.getPlayer()).getHome(str);
			if (home.getBlockX() == event.getTo().getBlockX() && home.getBlockY() == event.getTo().getBlockY() && home.getBlockZ() == event.getTo().getBlockZ()) {
				isHome = true;
			}
		}
		if (!isHome) {
			return;
		}
		Location loc = event.getTo();
		FLocation floc = new FLocation(event.getTo());
		Faction fac = Board.getInstance().getFactionAt(floc);
		Player player = event.getPlayer();
		FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
		User user = SavageFactions.plugin.ess.getUser(event.getPlayer());
		List<String> homes = user.getHomes();
		if (fac.isWilderness() || fplayer.getFactionId().equals(fac.getId())) {
			return;
		}
		//Warzone and SafeZone Home Initializers
		if (fac.isWarZone() || fac.isSafeZone() && SavageFactions.plugin.getConfig().getBoolean("deny-homes-in-system-factions")) {
			event.setCancelled(true);
			fplayer.msg(TL.COMMAND_HOME_BLOCKED, fac.getTag());
			if (SavageFactions.plugin.getConfig().getBoolean("remove-homes-in-system-factions"))
				for (String s : homes) {
					if (user.getHome(s).getBlock().getLocation().getChunk().equals(loc.getChunk())) {
						user.delHome(s);
					}
				}
		}

		if (fplayer.getFaction().getRelationTo(fac) == Relation.ENEMY && SavageFactions.plugin.getConfig().getBoolean("deny-homes-in-enemy-factions")) {
			event.setCancelled(true);
			fplayer.msg(TL.COMMAND_HOME_BLOCKED, fac.getTag());
			if (SavageFactions.plugin.getConfig().getBoolean("remove-homes-in-enemy-factions"))
				for (String s : homes) {
					if (user.getHome(s).getBlock().getLocation().getChunk().equals(loc.getChunk())) {
						user.delHome(s);
					}
				}
		}
		if (fplayer.getFaction().getRelationTo(fac) == Relation.NEUTRAL && SavageFactions.plugin.getConfig().getBoolean("deny-homes-in-neutral-factions")) {
			event.setCancelled(true);
			fplayer.msg(TL.COMMAND_HOME_BLOCKED, fac.getTag());
			if (SavageFactions.plugin.getConfig().getBoolean("remove-homes-in-neutral-factions"))
				for (String s : homes) {
					if (user.getHome(s).getBlock().getLocation().getChunk().equals(loc.getChunk())) {
						user.delHome(s);
					}
				}
		}
		if (fplayer.getFaction().getRelationTo(fac) == Relation.ALLY && SavageFactions.plugin.getConfig().getBoolean("deny-homes-in-ally-factions")) {
			event.setCancelled(true);
			fplayer.msg(TL.COMMAND_HOME_BLOCKED, fac.getTag());
			if (SavageFactions.plugin.getConfig().getBoolean("remove-homes-in-ally-factions"))
				for (String s : homes) {
					if (user.getHome(s).getBlock().getLocation().getChunk().equals(loc.getChunk())) {
						user.delHome(s);
					}
				}
		}
		if (fplayer.getFaction().getRelationTo(fac) == Relation.TRUCE && SavageFactions.plugin.getConfig().getBoolean("deny-homes-in-truce-factions")) {
			event.setCancelled(true);
			fplayer.msg(TL.COMMAND_HOME_BLOCKED, fac.getTag());
			if (SavageFactions.plugin.getConfig().getBoolean("remove-homes-in-truce-factions"))
				for (String s : homes) {
					if (user.getHome(s).getBlock().getLocation().getChunk().equals(loc.getChunk())) {
						user.delHome(s);
					}
				}
		}
	}


	//Colors a String
	public String color(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}


	private String convertTime(int time) {
		String result = String.valueOf(Math.round((System.currentTimeMillis() / 1000L - time) / 36.0D) / 100.0D);
		return (result.length() == 3 ? result + "0" : result) + "/hrs ago";
	}


	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		FPlayer me = FPlayers.getInstance().getByPlayer(player);

		// clear visualization
		if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
			VisualizeUtil.clear(event.getPlayer());
			if (me.isWarmingUp()) {
				me.clearWarmup();
				me.msg(TL.WARMUPS_CANCELLED);
			}
		}

		// quick check to make sure player is moving between chunks; good performance boost
		if (event.getFrom().getBlockX() >> 4 == event.getTo().getBlockX() >> 4 && event.getFrom().getBlockZ() >> 4 == event.getTo().getBlockZ() >> 4 && event.getFrom().getWorld() == event.getTo().getWorld()) {
			return;
		}


		// Did we change coord?
		FLocation from = me.getLastStoodAt();
		FLocation to = new FLocation(event.getTo());

		if (from.equals(to)) {
			return;
		}

		// Yes we did change coord (:

		me.setLastStoodAt(to);

		// Did we change "host"(faction)?
		Faction factionFrom = Board.getInstance().getFactionAt(from);
		Faction factionTo = Board.getInstance().getFactionAt(to);
		boolean changedFaction = (factionFrom != factionTo);


		if (changedFaction) {
			Bukkit.getServer().getPluginManager().callEvent(new FPlayerEnteredFactionEvent(factionTo, factionFrom, me));
			if (SavageFactions.plugin.getConfig().getBoolean("Title.Show-Title")) {
				String title = SavageFactions.plugin.getConfig().getString("Title.Format.Title");
				title = title.replace("{Faction}", factionTo.getColorTo(me) + factionTo.getTag());
				title = parseAllPlaceholders(title, factionTo, player);
				String subTitle = SavageFactions.plugin.getConfig().getString("Title.Format.Subtitle").replace("{Description}", factionTo.getDescription()).replace("{Faction}", factionTo.getColorTo(me) + factionTo.getTag());
				subTitle = parseAllPlaceholders(subTitle, factionTo, player);
				if (!SavageFactions.plugin.mc17) {
					if (!SavageFactions.plugin.mc18) {
						me.getPlayer().sendTitle(SavageFactions.plugin.color(title), SavageFactions.plugin.color(subTitle), SavageFactions.plugin.getConfig().getInt("Title.Options.FadeInTime"),
								  SavageFactions.plugin.getConfig().getInt("Title.Options.ShowTime"),
								  SavageFactions.plugin.getConfig().getInt("Title.Options.FadeOutTime"));
					} else {
						me.getPlayer().sendTitle(SavageFactions.plugin.color(title), SavageFactions.plugin.color(subTitle));
					}


				}

			}

			if (!SavageFactions.plugin.factionsFlight) {
				return;
			}


			// enable fly :)
			if (me.hasFaction() && !me.isFlying()) {
				if (factionTo == me.getFaction()) {
					enableFly(me);
				}
				// bypass checks
				Relation relationTo = factionTo.getRelationTo(me);
				if ((factionTo.isWilderness() && me.canflyinWilderness()) ||
						  (factionTo.isWarZone() && me.canflyinWarzone()) ||
						  (factionTo.isSafeZone() && me.canflyinSafezone()) ||
						  (relationTo == Relation.ENEMY && me.canflyinEnemy()) ||
						  (relationTo == Relation.ALLY && me.canflyinAlly()) ||
						  (relationTo == Relation.TRUCE && me.canflyinTruce()) ||
						  (relationTo == Relation.NEUTRAL && me.canflyinNeutral() && !isSystemFaction(factionTo))) {
					enableFly(me);
				}

			}
		}


		if (me.isMapAutoUpdating()) {
			if (showTimes.containsKey(player.getUniqueId()) && (showTimes.get(player.getUniqueId()) > System.currentTimeMillis())) {
				if (SavageFactions.plugin.getConfig().getBoolean("findfactionsexploit.log", false)) {
					SavageFactions.plugin.log(Level.WARNING, "%s tried to show a faction map too soon and triggered exploit blocker.", player.getName());
				}
			} else {
				me.sendFancyMessage(Board.getInstance().getMap(me, to, player.getLocation().getYaw()));
				showTimes.put(player.getUniqueId(), System.currentTimeMillis() + SavageFactions.plugin.getConfig().getLong("findfactionsexploit.cooldown", 2000));
			}
		} else {
			Faction myFaction = me.getFaction();
			String ownersTo = myFaction.getOwnerListString(to);
			if (changedFaction) {
				me.sendFactionHereMessage(factionFrom);
				if (Conf.ownedAreasEnabled && Conf.ownedMessageOnBorder && myFaction == factionTo && !ownersTo.isEmpty()) {
					me.sendMessage(TL.GENERIC_OWNERS.format(ownersTo));
				}
			} else if (Conf.ownedAreasEnabled && Conf.ownedMessageInsideTerritory && myFaction == factionTo && !myFaction.isWilderness()) {
				String ownersFrom = myFaction.getOwnerListString(from);
				if (Conf.ownedMessageByChunk || !ownersFrom.equals(ownersTo)) {
					if (!ownersTo.isEmpty()) {
						me.sendMessage(TL.GENERIC_OWNERS.format(ownersTo));
					} else if (!TL.GENERIC_PUBLICLAND.toString().isEmpty()) {
						me.sendMessage(TL.GENERIC_PUBLICLAND.toString());
					}
				}
			}
		}

		if (me.getAutoClaimFor() != null) {
			me.attemptClaim(me.getAutoClaimFor(), event.getTo(), true);
		} else if (me.isAutoSafeClaimEnabled()) {
			if (!Permission.MANAGE_SAFE_ZONE.has(player)) {
				me.setIsAutoSafeClaimEnabled(false);
			} else {
				if (!Board.getInstance().getFactionAt(to).isSafeZone()) {
					Board.getInstance().setFactionAt(Factions.getInstance().getSafeZone(), to);
					me.msg(TL.PLAYER_SAFEAUTO);
				}
			}
		} else if (me.isAutoWarClaimEnabled()) {
			if (!Permission.MANAGE_WAR_ZONE.has(player)) {
				me.setIsAutoWarClaimEnabled(false);
			} else {
				if (!Board.getInstance().getFactionAt(to).isWarZone()) {
					Board.getInstance().setFactionAt(Factions.getInstance().getWarZone(), to);
					me.msg(TL.PLAYER_WARAUTO);
				}
			}
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		FPlayer fme = FPlayers.getInstance().getById(e.getPlayer().getUniqueId().toString());
		if (fme.isInVault())
			fme.setInVault(false);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		// only need to check right-clicks and physical as of MC 1.4+; good performance boost
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_AIR)) return;
        if (event.getPlayer().getItemInHand() != null) {
            Material handItem = event.getPlayer().getItemInHand().getType();
            if (handItem.isEdible()
                || handItem.equals(Material.POTION)
                || handItem.equals(Material.LINGERING_POTION)
                || handItem.equals(Material.SPLASH_POTION)) {
                return;
            }
        }

		Block block = event.getClickedBlock();
		Player player = event.getPlayer();

        // Check if the material is bypassing protection
        if (Conf.territoryBypasssProtectedMaterials.contains(block.getType())) return;

		if (block == null) return;  // clicked in air, apparently

		if (GetPermissionFromUsableBlock(event.getMaterial()) == null) return;
		player.sendMessage("Checking if you can use that block");
		if (!canPlayerUseBlock(player, block, false)) {
			event.setCancelled(true);
			event.setUseInteractedBlock(Event.Result.DENY);
			return;
		}
		player.sendMessage("Checking if you can use that item");
		if (!playerCanUseItemHere(player, block.getLocation(), event.getMaterial(), false)) {
			event.setCancelled(true);
			event.setUseInteractedBlock(Event.Result.DENY);
			return;
		}
	}

	@EventHandler
	public void onPlayerBoneMeal(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == MultiversionMaterials.GRASS_BLOCK.parseMaterial()
				  && event.hasItem() && event.getItem().getType() == MultiversionMaterials.BONE_MEAL.parseMaterial()) {
			if (!FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), block.getLocation(), PermissableAction.BUILD.name(), true)) {
				FPlayer me = FPlayers.getInstance().getById(event.getPlayer().getUniqueId().toString());
				Faction otherFaction = Board.getInstance().getFactionAt(new FLocation(block.getLocation()));
				Faction myFaction = me.getFaction();

				me.msg("<b>You can't use bone meal in the territory of " + otherFaction.getTag(myFaction));
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		FPlayer me = FPlayers.getInstance().getByPlayer(event.getPlayer());

		me.getPower();  // update power, so they won't have gained any while dead

		Location home = me.getFaction().getHome();
		if (Conf.homesEnabled &&
				  Conf.homesTeleportToOnDeath &&
				  home != null &&
				  (Conf.homesRespawnFromNoPowerLossWorlds || !Conf.worldsNoPowerLoss.contains(event.getPlayer().getWorld().getName()))) {
			event.setRespawnLocation(home);
		}
	}

	// For some reason onPlayerInteract() sometimes misses bucket events depending on distance (something like 2-3 blocks away isn't detected),
	// but these separate bucket events below always fire without fail
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if (!playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if (!playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteractGUI(InventoryClickEvent event) {
		if (event.getClickedInventory() == null) {
			return;
		}
		if (event.getClickedInventory().getHolder() instanceof FactionGUI) {
			event.setCancelled(true);
			((FactionGUI) event.getClickedInventory().getHolder()).onClick(event.getRawSlot(), event.getClick());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMoveGUI(InventoryDragEvent event) {
		if (event.getInventory().getHolder() instanceof FactionGUI) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event) {
		FPlayer badGuy = FPlayers.getInstance().getByPlayer(event.getPlayer());
		if (badGuy == null) {
			return;
		}

		// if player was banned (not just kicked), get rid of their stored info
		if (Conf.removePlayerDataWhenBanned && event.getReason().equals("Banned by admin.")) {
			if (badGuy.getRole() == Role.LEADER) {
				badGuy.getFaction().promoteNewLeader();
			}

			badGuy.leave(false);
			badGuy.remove();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	final public void onFactionJoin(FPlayerJoinEvent event) {
		FTeamWrapper.applyUpdatesLater(event.getFaction());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFactionLeave(FPlayerLeaveEvent event) {
		FTeamWrapper.applyUpdatesLater(event.getFaction());
	}

	private static class InteractAttemptSpam {
		private int attempts = 0;
		private long lastAttempt = System.currentTimeMillis();

		// returns the current attempt count
		public int increment() {
			long now = System.currentTimeMillis();
			if (now > lastAttempt + 2000) {
				attempts = 1;
			} else {
				attempts++;
			}
			lastAttempt = now;
			return attempts;
		}
	}
	/// <summary>
	///	This checks if the current player can execute an action based on it's factions access and surroundings
	/// It will grant access in the following priorities:
	/// - If Faction Land is Owned and the Owner is the current player, or player is faction leader.
	/// - If Faction Land is not Owned and my access value is not set to DENY
	/// - If none of the filters above matches, then we consider access is set to ALLOW|UNDEFINED
	/// This check does not performs any kind of bypass check (i.e.: me.isAdminBypassing())
	/// </summary>
	/// <param name="player">The player entity which the check will be made upon</param>
	/// <param name="me">The Faction player object related to the player</param>
	/// <param name="loc">The World location where the action is being executed</param>
	/// <param name="myFaction">The faction of the player being checked</param>
	/// <param name="access">The current's faction access permission for the action</param>
	private static boolean CheckPlayerAccess(Player player, FPlayer me, FLocation loc, Faction myFaction, Access access, PermissableAction action, boolean pain) {
		boolean doPain = pain && Conf.handleExploitInteractionSpam;
		if (access != null && access != Access.UNDEFINED) {
			// TODO: Update this once new access values are added other than just allow / deny.
			boolean landOwned = (myFaction.doesLocationHaveOwnersSet(loc) && !myFaction.getOwnerList(loc).isEmpty());
			if ((landOwned && myFaction.getOwnerListString(loc).contains(player.getName())) || (me.getRole() == Role.LEADER && me.getFactionId().equals(myFaction.getId()))) return true;
			else if (landOwned && !myFaction.getOwnerListString(loc).contains(player.getName())) {
				me.msg("<b>You can't do that in this territory, it is owned by: " + myFaction.getOwnerListString(loc));
				if (doPain) {
					player.damage(Conf.actionDeniedPainAmount);
				}
				return false;
			} else if (!landOwned && access == Access.ALLOW) return true;
			else {
				me.msg(TL.GENERIC_NOPERMISSION, action);
				return false;
			}
		}
		me.msg(TL.GENERIC_NOPERMISSION, action);
		return false;
	}
	/// <summary>
	/// This will try to resolve a permission action based on the item material, if it's not usable, will return null
	/// </summary>
	private static PermissableAction GetPermissionFromUsableBlock(Block block) {
		return GetPermissionFromUsableBlock(block.getType());
	}
	private static PermissableAction GetPermissionFromUsableBlock(Material material) {
		if (SavageFactions.plugin.mc113) {
			switch (material) {
				case LEVER:
					return PermissableAction.LEVER;

				case ACACIA_BUTTON:
				case BIRCH_BUTTON:
				case DARK_OAK_BUTTON:
				case JUNGLE_BUTTON:
				case OAK_BUTTON:
				case SPRUCE_BUTTON:
				case STONE_BUTTON:
					return PermissableAction.BUTTON;

				case ACACIA_DOOR:
				case BIRCH_DOOR:
				case IRON_DOOR:
				case JUNGLE_DOOR:
				case OAK_DOOR:
				case SPRUCE_DOOR:
				case DARK_OAK_DOOR:

				case ACACIA_TRAPDOOR:
				case BIRCH_TRAPDOOR:
				case DARK_OAK_TRAPDOOR:
				case IRON_TRAPDOOR:
				case JUNGLE_TRAPDOOR:
				case OAK_TRAPDOOR:
				case SPRUCE_TRAPDOOR:

				case ACACIA_FENCE_GATE:
				case BIRCH_FENCE_GATE:
				case DARK_OAK_FENCE_GATE:
				case JUNGLE_FENCE_GATE:
				case OAK_FENCE_GATE:
				case SPRUCE_FENCE_GATE:
					return PermissableAction.DOOR;

				case CHEST:
				case TRAPPED_CHEST:
				case CHEST_MINECART:

				case SHULKER_BOX:
				case BLACK_SHULKER_BOX:
				case BLUE_SHULKER_BOX:
				case BROWN_SHULKER_BOX:
				case CYAN_SHULKER_BOX:
				case GRAY_SHULKER_BOX:
				case GREEN_SHULKER_BOX:
				case LIGHT_BLUE_SHULKER_BOX:
				case LIGHT_GRAY_SHULKER_BOX:
				case LIME_SHULKER_BOX:
				case MAGENTA_SHULKER_BOX:
				case ORANGE_SHULKER_BOX:
				case PINK_SHULKER_BOX:
				case PURPLE_SHULKER_BOX:
				case RED_SHULKER_BOX:
				case WHITE_SHULKER_BOX:
				case YELLOW_SHULKER_BOX:

				case FURNACE:
				case DROPPER:
				case DISPENSER:
				case ENCHANTING_TABLE:
				case BREWING_STAND:
				case CAULDRON:
				case HOPPER:
				case BEACON:
				case JUKEBOX:

				case ANVIL:
				case CHIPPED_ANVIL:
				case DAMAGED_ANVIL:
					return PermissableAction.CONTAINER;
				default:
					// Check for doors that might have diff material name in old version.
					if (material.name().contains("DOOR"))
						return PermissableAction.DOOR;
					return null;
			}
		} else {
			switch (material) {
				case LEVER:
					return PermissableAction.LEVER;
				case DARK_OAK_DOOR:
				case ACACIA_DOOR:
				case BIRCH_DOOR:
				case IRON_DOOR:
				case JUNGLE_DOOR:
				case SPRUCE_DOOR:
				case ACACIA_FENCE_GATE:
				case BIRCH_FENCE_GATE:
				case DARK_OAK_FENCE_GATE:
				case JUNGLE_FENCE_GATE:
				case SPRUCE_FENCE_GATE:
					return PermissableAction.DOOR;
				case CHEST:
				case ENDER_CHEST:
				case TRAPPED_CHEST:
				case DISPENSER:
				case ENCHANTING_TABLE:
				case DROPPER:
				case FURNACE:
				case HOPPER:
				case ANVIL:
				case CHIPPED_ANVIL:
				case DAMAGED_ANVIL:
				case BREWING_STAND:
					return PermissableAction.CONTAINER;
				default:
					// Check for doors that might have diff material name in old version.
					if (material.name().contains("DOOR"))
						return PermissableAction.DOOR;
					if (material.toString().toUpperCase().contains("BUTTON"))
						return PermissableAction.BUTTON;
					return null;
			}
		}
	}
}
