package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.alts.CmdAlts;
import com.massivecraft.factions.cmd.audit.CmdAudit;
import com.massivecraft.factions.cmd.banner.CmdBanner;
import com.massivecraft.factions.cmd.banner.CmdTpBanner;
import com.massivecraft.factions.cmd.check.CmdCheck;
import com.massivecraft.factions.cmd.check.CmdWeeWoo;
import com.massivecraft.factions.cmd.chest.CmdChest;
import com.massivecraft.factions.cmd.claim.*;
import com.massivecraft.factions.cmd.drain.CmdDrain;
import com.massivecraft.factions.cmd.econ.CmdMoney;
import com.massivecraft.factions.cmd.grace.CmdGrace;
import com.massivecraft.factions.cmd.points.CmdPoints;
import com.massivecraft.factions.cmd.relational.*;
import com.massivecraft.factions.cmd.reserve.CmdReserve;
import com.massivecraft.factions.cmd.roles.CmdDemote;
import com.massivecraft.factions.cmd.roles.CmdPromote;
import com.massivecraft.factions.cmd.tnt.CmdSetTnt;
import com.massivecraft.factions.cmd.tnt.CmdTnt;
import com.massivecraft.factions.cmd.tnt.CmdTntFill;
import com.massivecraft.factions.missions.CmdMissions;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import me.lucko.commodore.CommodoreProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FCmdRoot extends FCommand implements CommandExecutor {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public static FCmdRoot instance;
    public BrigadierManager brigadierManager;
    public CmdAdmin cmdAdmin = new CmdAdmin();
    public CmdAutoClaim cmdAutoClaim = new CmdAutoClaim();
    public CmdBoom cmdBoom = new CmdBoom();
    public CmdBypass cmdBypass = new CmdBypass();
    public CmdChat cmdChat = new CmdChat();
    public CmdChatSpy cmdChatSpy = new CmdChatSpy();
    public CmdClaim cmdClaim = new CmdClaim();
    public CmdConfig cmdConfig = new CmdConfig();
    public CmdCreate cmdCreate = new CmdCreate();
    public CmdDeinvite cmdDeinvite = new CmdDeinvite();
    public CmdDescription cmdDescription = new CmdDescription();
    public CmdDisband cmdDisband = new CmdDisband();
    public CmdFocus cmdFocus = new CmdFocus();
    public CmdGrace cmdGrace = new CmdGrace();
    public CmdHelp cmdHelp = new CmdHelp();
    public CmdHome cmdHome = new CmdHome();
    public CmdLeave cmdLeave = new CmdLeave();
    public CmdList cmdList = new CmdList();
    public CmdLock cmdLock = new CmdLock();
    public CmdMap cmdMap = new CmdMap();
    public CmdMod cmdMod = new CmdMod();
    public CmdMoney cmdMoney = new CmdMoney();
    public CmdOpen cmdOpen = new CmdOpen();
    public CmdOwner cmdOwner = new CmdOwner();
    public CmdOwnerList cmdOwnerList = new CmdOwnerList();
    public CmdPeaceful cmdPeaceful = new CmdPeaceful();
    public CmdPermanent cmdPermanent = new CmdPermanent();
    public CmdPermanentPower cmdPermanentPower = new CmdPermanentPower();
    public CmdPowerBoost cmdPowerBoost = new CmdPowerBoost();
    public CmdPower cmdPower = new CmdPower();
    public CmdRelationAlly cmdRelationAlly = new CmdRelationAlly();
    public CmdRelationEnemy cmdRelationEnemy = new CmdRelationEnemy();
    public CmdRelationNeutral cmdRelationNeutral = new CmdRelationNeutral();
    public CmdRelationTruce cmdRelationTruce = new CmdRelationTruce();
    public CmdReload cmdReload = new CmdReload();
    public CmdSafeunclaimall cmdSafeunclaimall = new CmdSafeunclaimall();
    public CmdSaveAll cmdSaveAll = new CmdSaveAll();
    public CmdSethome cmdSethome = new CmdSethome();
    public CmdShow cmdShow = new CmdShow();
    public CmdStatus cmdStatus = new CmdStatus();
    public CmdStealth cmdStealth = new CmdStealth();
    public CmdStuck cmdStuck = new CmdStuck();
    public CmdTag cmdTag = new CmdTag();
    public CmdTitle cmdTitle = new CmdTitle();
    public CmdPlayerTitleToggle cmdPlayerTitleToggle = new CmdPlayerTitleToggle();
    public CmdToggleAllianceChat cmdToggleAllianceChat = new CmdToggleAllianceChat();
    public CmdUnclaim cmdUnclaim = new CmdUnclaim();
    public CmdUnclaimall cmdUnclaimall = new CmdUnclaimall();
    public CmdVersion cmdVersion = new CmdVersion();
    public CmdWarunclaimall cmdWarunclaimall = new CmdWarunclaimall();
    public CmdSB cmdSB = new CmdSB();
    public CmdShowInvites cmdShowInvites = new CmdShowInvites();
    public CmdAnnounce cmdAnnounce = new CmdAnnounce();
    public CmdPaypalSet cmdPaypalSet = new CmdPaypalSet();
    public CmdPaypalSee cmdPaypalSee = new CmdPaypalSee();
    public CmdSeeChunk cmdSeeChunk = new CmdSeeChunk();
    public CmdConvert cmdConvert = new CmdConvert();
    public CmdFWarp cmdFWarp = new CmdFWarp();
    public CmdSetFWarp cmdSetFWarp = new CmdSetFWarp();
    public CmdDelFWarp cmdDelFWarp = new CmdDelFWarp();
    public CmdModifyPower cmdModifyPower = new CmdModifyPower();
    public CmdLogins cmdLogins = new CmdLogins();
    public CmdClaimLine cmdClaimLine = new CmdClaimLine();
    public CmdTop cmdTop = new CmdTop();
    public CmdAHome cmdAHome = new CmdAHome();
    public CmdPerm cmdPerm = new CmdPerm();
    public CmdPromote cmdPromote = new CmdPromote();
    public CmdDemote cmdDemote = new CmdDemote();
    public CmdSetDefaultRole cmdSetDefaultRole = new CmdSetDefaultRole();
    public CmdMapHeight cmdMapHeight = new CmdMapHeight();
    public CmdClaimAt cmdClaimAt = new CmdClaimAt();
    public CmdBan cmdban = new CmdBan();
    public CmdUnban cmdUnban = new CmdUnban();
    public CmdBanlist cmdbanlist = new CmdBanlist();
    public CmdRules cmdRules = new CmdRules();
    public CmdCheckpoint cmdCheckpoint = new CmdCheckpoint();
    public CmdTnt cmdTnt = new CmdTnt();
    public CmdNear cmdNear = new CmdNear();
    public CmdUpgrades cmdUpgrades = new CmdUpgrades();
    public CmdVault cmdVault = new CmdVault();
    public CmdGetVault cmdGetVault = new CmdGetVault();
    public CmdFly cmdFly = new CmdFly();
    public CmdColeader cmdColeader = new CmdColeader();
    //public CmdBanner cmdBanner = new CmdBanner();
    //public CmdTpBanner cmdTpBanner = new CmdTpBanner();
    public CmdUnclaimfill cmdUnclaimfill = new CmdUnclaimfill();
    public CmdKillHolograms cmdKillHolograms = new CmdKillHolograms();
    //public CmdInspect cmdInspect = new CmdInspect();
    public CmdCoords cmdCoords = new CmdCoords();
    public CmdShowClaims cmdShowClaims = new CmdShowClaims();
    public CmdLowPower cmdLowPower = new CmdLowPower();
    public CmdTntFill cmdTntFill = new CmdTntFill();
    public CmdChest cmdChest = new CmdChest();
    public CmdSetBanner cmdSetBanner = new CmdSetBanner();
    public CmdBanner cmdBanner = new CmdBanner();
    public CmdTpBanner cmdTpBanner = new CmdTpBanner();
    public CmdAlts cmdAlts = new CmdAlts();
    public CmdCorner cmdCorner = new CmdCorner();
    public CmdInventorySee cmdInventorySee = new CmdInventorySee();
    public CmdFGlobal cmdFGlobal = new CmdFGlobal();
    public CmdViewChest cmdViewChest = new CmdViewChest();
    public CmdPoints cmdPoints = new CmdPoints();
    //public CmdLogout cmdLogout = new CmdLogout();
    public CmdMissions cmdMissions = new CmdMissions();
    public CmdStrikes cmdStrikes = new CmdStrikes();
    public CmdCheck cmdCheck = new CmdCheck();
    //public CmdWeeWoo cmdWeeWoo = new CmdWeeWoo();
    public CmdSpawnerLock cmdSpawnerLock = new CmdSpawnerLock();
    public CmdSetDiscord cmdSetDiscord = new CmdSetDiscord();
    public CmdSeeDiscord cmdSeeDiscord = new CmdSeeDiscord();
    public CmdDebug cmdDebug = new CmdDebug();
    public CmdDrain cmdDrain = new CmdDrain();
    public CmdLookup cmdLookup = new CmdLookup();
    public CmdAudit cmdAudit = new CmdAudit();
    public CmdReserve cmdReserve = new CmdReserve();
    public CmdDelHome cmdDelHome = new CmdDelHome();
    public CmdClaimFill cmdClaimFill = new CmdClaimFill();
    public CmdNotifications cmdNotifications = new CmdNotifications();
    public CmdFriendlyFire cmdFriendlyFire = new CmdFriendlyFire();
    public CmdSetPower cmdSetPower = new CmdSetPower();
    public CmdSpawnerChunk cmdSpawnerChunk = new CmdSpawnerChunk();
    public CmdSetTnt cmdSetTnt = new CmdSetTnt();
    public CmdCornerList cmdCornerList = new CmdCornerList();
    public CmdAutoUnclaim cmdAutoUnclaim = new CmdAutoUnclaim();
    public CmdRally cmdRally = new CmdRally();
    public CmdSetRelation cmdSetRelation = new CmdSetRelation();
    public CmdInvite cmdInvite = new CmdInvite();
    public CmdJoin cmdJoin = new CmdJoin();
    public CmdKick cmdKick = new CmdKick();
    public CmdAllyFWarp cmdAllyFWarp = new CmdAllyFWarp();


    //Variables to know if we already set up certain sub commands
    public Boolean discordEnabled = false;
    public Boolean checkEnabled = false;
    public Boolean missionsEnabled = false;
    public Boolean fShopEnabled = false;
    public Boolean invSeeEnabled = false;
    public Boolean fPointsEnabled = false;
    public Boolean fAltsEnabled = false;
    public Boolean fGraceEnabled = false;
    public Boolean fFocusEnabled = false;
    public Boolean fFlyEnabled = false;
    public Boolean fPayPalEnabled = false;
    public Boolean coreProtectEnabled = false;
    public Boolean internalFTOPEnabled = false;
    public Boolean fWildEnabled = false;
    public Boolean fAuditEnabled = false;
    public Boolean fStrikes = false;

    public FCmdRoot() {
        super();
        instance = this;

        if (CommodoreProvider.isSupported()) brigadierManager = new BrigadierManager();


        this.aliases.addAll(Conf.baseCommandAliases);
        this.aliases.removeAll(Collections.<String>singletonList(null));

        this.setHelpShort("The faction base command");
        this.helpLong.add(TextUtil.parseTags("<i>This command contains all faction stuff."));

        this.addSubCommand(this.cmdAllyFWarp);
        this.addSubCommand(this.cmdAdmin);
        this.addSubCommand(this.cmdAutoClaim);
        this.addSubCommand(this.cmdBoom);
        this.addSubCommand(this.cmdBypass);
        this.addSubCommand(this.cmdChat);
        this.addSubCommand(this.cmdToggleAllianceChat);
        this.addSubCommand(this.cmdChatSpy);
        this.addSubCommand(this.cmdClaim);
        this.addSubCommand(this.cmdConfig);
        this.addSubCommand(this.cmdCreate);
        this.addSubCommand(this.cmdInvite);
        this.addSubCommand(this.cmdJoin);
        this.addSubCommand(this.cmdKick);
        this.addSubCommand(this.cmdDeinvite);
        this.addSubCommand(this.cmdDescription);
        this.addSubCommand(this.cmdDelHome);
        this.addSubCommand(this.cmdDisband);
        this.addSubCommand(this.cmdHelp);
        this.addSubCommand(this.cmdHome);
        this.addSubCommand(this.cmdLeave);
        this.addSubCommand(this.cmdList);
        this.addSubCommand(this.cmdLock);
        this.addSubCommand(this.cmdMap);
        this.addSubCommand(this.cmdMod);
        this.addSubCommand(this.cmdMoney);
        this.addSubCommand(this.cmdOpen);
        this.addSubCommand(this.cmdOwner);
        this.addSubCommand(this.cmdOwnerList);
        this.addSubCommand(this.cmdPeaceful);
        this.addSubCommand(this.cmdPermanent);
        this.addSubCommand(this.cmdPermanentPower);
        this.addSubCommand(this.cmdPower);
        this.addSubCommand(this.cmdPowerBoost);
        this.addSubCommand(this.cmdRelationAlly);
        this.addSubCommand(this.cmdRelationEnemy);
        this.addSubCommand(this.cmdRelationNeutral);
        this.addSubCommand(this.cmdRelationTruce);
        this.addSubCommand(this.cmdReload);
        this.addSubCommand(this.cmdSafeunclaimall);
        this.addSubCommand(this.cmdSaveAll);
        this.addSubCommand(this.cmdSethome);
        this.addSubCommand(this.cmdShow);
        this.addSubCommand(this.cmdStatus);
        this.addSubCommand(this.cmdStealth);
        this.addSubCommand(this.cmdStuck);
        //this.addSubCommand(this.cmdLogout);
        this.addSubCommand(this.cmdTag);
        this.addSubCommand(this.cmdTitle);
        this.addSubCommand(this.cmdPlayerTitleToggle);
        this.addSubCommand(this.cmdUnclaim);
        this.addSubCommand(this.cmdUnclaimall);
        this.addSubCommand(this.cmdVersion);
        this.addSubCommand(this.cmdWarunclaimall);
        this.addSubCommand(this.cmdSB);
        this.addSubCommand(this.cmdShowInvites);
        this.addSubCommand(this.cmdAnnounce);
        this.addSubCommand(this.cmdConvert);
        this.addSubCommand(this.cmdFWarp);
        this.addSubCommand(this.cmdSetFWarp);
        this.addSubCommand(this.cmdDelFWarp);
        this.addSubCommand(this.cmdModifyPower);
        this.addSubCommand(this.cmdLogins);
        this.addSubCommand(this.cmdClaimFill);
        this.addSubCommand(this.cmdClaimLine);
        this.addSubCommand(this.cmdAHome);
        this.addSubCommand(this.cmdPerm);
        this.addSubCommand(this.cmdPromote);
        this.addSubCommand(this.cmdDebug);
        this.addSubCommand(this.cmdDemote);
        this.addSubCommand(this.cmdSetDefaultRole);
        this.addSubCommand(this.cmdMapHeight);
        this.addSubCommand(this.cmdClaimAt);
        this.addSubCommand(this.cmdban);
        this.addSubCommand(this.cmdUnban);
        this.addSubCommand(this.cmdbanlist);
        this.addSubCommand(this.cmdRules);
        this.addSubCommand(this.cmdCheckpoint);
        this.addSubCommand(this.cmdTnt);
        this.addSubCommand(this.cmdNear);
        this.addSubCommand(this.cmdUpgrades);
        this.addSubCommand(this.cmdVault);
        this.addSubCommand(this.cmdGetVault);
        this.addSubCommand(this.cmdColeader);
        //this.addSubCommand(this.cmdBanner);
        //this.addSubCommand(this.cmdTpBanner);
        this.addSubCommand(this.cmdKillHolograms);
        this.addSubCommand(this.cmdCoords);
        this.addSubCommand(this.cmdShowClaims);
        this.addSubCommand(this.cmdLowPower);
        this.addSubCommand(this.cmdTntFill);
        this.addSubCommand(this.cmdChest);
        this.addSubCommand(this.cmdCorner);
        this.addSubCommand(this.cmdCornerList);
        this.addSubCommand(this.cmdFGlobal);
        this.addSubCommand(this.cmdViewChest);
        this.addSubCommand(this.cmdSpawnerLock);
        this.addSubCommand(this.cmdDrain);
        this.addSubCommand(this.cmdLookup);
        this.addSubCommand(this.cmdNotifications);
        this.addSubCommand(this.cmdFriendlyFire);
        this.addSubCommand(this.cmdSetPower);
        this.addSubCommand(this.cmdSetTnt);
        this.addSubCommand(this.cmdUnclaimfill);
        this.addSubCommand(this.cmdAutoUnclaim);
        this.addSubCommand(this.cmdRally);
        this.addSubCommand(this.cmdSetRelation);
        this.addSubCommand(this.cmdSetDiscord);
        this.addSubCommand(this.cmdSeeDiscord);
        addVariableCommands();
        if (CommodoreProvider.isSupported()) brigadierManager.build();
    }

    /**
     * Add sub commands to the root if they are enabled
     */
    public void addVariableCommands() {

        Bukkit.getScheduler().runTaskLater(FactionsPlugin.getInstance(), () -> {
            if(FactionsPlugin.getInstance().getFactionsAddonHashMap().containsKey("Roster")) {
                this.subCommands.remove(this.cmdInvite);
                this.subCommands.remove(this.cmdJoin);
                this.subCommands.remove(this.cmdKick);
            }
        }, 200);

        if (FactionsPlugin.getInstance().getConfig().getBoolean("fbanners.Enabled")) {
            this.addSubCommand(this.cmdSetBanner);
            this.addSubCommand(this.cmdTpBanner);
            this.addSubCommand(this.cmdBanner);
        }

            //Reserve
        if (Conf.useReserveSystem) {
            this.addSubCommand(this.cmdReserve);
        }

        //PayPal
        if (FactionsPlugin.getInstance().getConfig().getBoolean("fpaypal.Enabled", false) && !fPayPalEnabled) {
            this.addSubCommand(this.cmdPaypalSet);
            this.addSubCommand(this.cmdPaypalSee);
            fPayPalEnabled = true;
        }
        //Check
        if (Conf.useCheckSystem && !checkEnabled) {
            this.addSubCommand(this.cmdCheck);
           // this.addSubCommand(this.cmdWeeWoo);
            checkEnabled = true;
        }

        if(FactionsPlugin.getInstance().getConfig().getBoolean("see-chunk.Enabled")) {
            this.addSubCommand(this.cmdSeeChunk);
        }

        //CoreProtect
        //if (Bukkit.getServer().getPluginManager().getPlugin("CoreProtect") != null && !coreProtectEnabled) {
        //    FactionsPlugin.getInstance().log("Found CoreProtect, enabling Inspect");
        //    this.addSubCommand(this.cmdInspect);
        //    coreProtectEnabled = true;
        //} else {
        //    FactionsPlugin.getInstance().log("CoreProtect not found, disabling Inspect");
        //}
        //FTOP
        if ((Bukkit.getServer().getPluginManager().getPlugin("FactionsTop") != null || Bukkit.getServer().getPluginManager().getPlugin("SavageFTOP") != null || Bukkit.getServer().getPluginManager().getPlugin("SaberFTOP") != null) && !internalFTOPEnabled) {
            Logger.print( "Found FactionsTop plugin. Disabling our own /f top command.", Logger.PrefixType.DEFAULT);
        } else {
            Logger.print( "Internal Factions Top Being Used. NOTE: Very Basic", Logger.PrefixType.DEFAULT);
            this.addSubCommand(this.cmdTop);
            internalFTOPEnabled = true;
        }

        if (Conf.useAuditSystem) {
            this.addSubCommand(cmdAudit);
            fAuditEnabled = true;
        }

        if (Conf.useStrikeSystem) {
            this.addSubCommand(this.cmdStrikes);
            fStrikes = true;
        }

        if (Conf.userSpawnerChunkSystem) {
            this.addSubCommand(this.cmdSpawnerChunk);
        }

        if (FactionsPlugin.getInstance().getFileManager().getMissions().getConfig().getBoolean("Missions-Enabled", false) && !missionsEnabled) {
            this.addSubCommand(this.cmdMissions);
            missionsEnabled = true;
        }
        if (FactionsPlugin.getInstance().getConfig().getBoolean("f-inventory-see.Enabled", false) && !invSeeEnabled) {
            this.addSubCommand(this.cmdInventorySee);
            invSeeEnabled = true;
        }
        if (FactionsPlugin.getInstance().getConfig().getBoolean("f-points.Enabled", false) && !fPointsEnabled) {
            this.addSubCommand(this.cmdPoints);
            fPointsEnabled = true;
        }
        if (FactionsPlugin.getInstance().getConfig().getBoolean("f-alts.Enabled", false) && !fAltsEnabled) {
            this.addSubCommand(this.cmdAlts);
            fAltsEnabled = true;
        }
        if (FactionsPlugin.getInstance().getConfig().getBoolean("f-grace.Enabled", false) && !fGraceEnabled) {
            this.addSubCommand(this.cmdGrace);
            fGraceEnabled = true;
        }
        if (FactionsPlugin.getInstance().getConfig().getBoolean("ffocus.Enabled") && !fFocusEnabled) {
            addSubCommand(this.cmdFocus);
            fFocusEnabled = true;
        }
        if (FactionsPlugin.getInstance().getConfig().getBoolean("enable-faction-flight", true) && !fFlyEnabled) {
            this.addSubCommand(this.cmdFly);
            fFlyEnabled = true;
        }
    }

    public void rebuild() {
        if (CommodoreProvider.isSupported()) brigadierManager.build();
    }

    @Override
    public void perform(CommandContext context) {
        context.commandChain.add(this);
        this.cmdHelp.execute(context);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.execute(new CommandContext(sender, new ArrayList<>(Arrays.asList(args)), label));
        return true;
    }

    @Override
    public void addSubCommand(FCommand subCommand) {
        super.addSubCommand(subCommand);
        // People were getting NPE's as somehow CommodoreProvider#isSupported returned true on legacy versions.
        if (CommodoreProvider.isSupported()) {
            brigadierManager.addSubCommand(subCommand);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.GENERIC_PLACEHOLDER;
    }

}
