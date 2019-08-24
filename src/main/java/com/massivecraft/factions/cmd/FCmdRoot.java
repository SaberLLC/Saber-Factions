package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.cmd.alts.CmdAlts;
import com.massivecraft.factions.cmd.chest.CmdChest;
import com.massivecraft.factions.cmd.claim.*;
import com.massivecraft.factions.cmd.econ.CmdMoney;
import com.massivecraft.factions.cmd.grace.CmdGrace;
import com.massivecraft.factions.cmd.logout.CmdLogout;
import com.massivecraft.factions.cmd.points.CmdPoints;
import com.massivecraft.factions.cmd.relational.CmdRelationAlly;
import com.massivecraft.factions.cmd.relational.CmdRelationEnemy;
import com.massivecraft.factions.cmd.relational.CmdRelationNeutral;
import com.massivecraft.factions.cmd.relational.CmdRelationTruce;
import com.massivecraft.factions.cmd.roles.CmdDemote;
import com.massivecraft.factions.cmd.roles.CmdPromote;
import com.massivecraft.factions.cmd.tnt.CmdTnt;
import com.massivecraft.factions.cmd.tnt.CmdTntFill;
import com.massivecraft.factions.missions.CmdMissions;
import com.massivecraft.factions.shop.CmdShop;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.logging.Level;

public class FCmdRoot extends FCommand {

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
    public CmdInvite cmdInvite = new CmdInvite();
    public CmdJoin cmdJoin = new CmdJoin();
    public CmdKick cmdKick = new CmdKick();
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
    public CmdBanner cmdBanner = new CmdBanner();
    public CmdTpBanner cmdTpBanner = new CmdTpBanner();
    public CmdKillHolograms cmdKillHolograms = new CmdKillHolograms();
    public CmdInspect cmdInspect = new CmdInspect();
    public CmdCoords cmdCoords = new CmdCoords();
    public CmdShowClaims cmdShowClaims = new CmdShowClaims();
    public CmdLowPower cmdLowPower = new CmdLowPower();
    public CmdTntFill cmdTntFill = new CmdTntFill();
    public CmdChest cmdChest = new CmdChest();
    public CmdSetBanner cmdSetBanner = new CmdSetBanner();
    public CmdStrike cmdStrike = new CmdStrike();
    public CmdStrikeSet cmdStrikeSet = new CmdStrikeSet();
    public CmdAlts cmdAlts = new CmdAlts();
    public CmdSpam cmdSpam = new CmdSpam();
    public CmdCorner cmdCorner = new CmdCorner();
    public CmdInventorySee cmdInventorySee = new CmdInventorySee();
    public CmdFGlobal cmdFGlobal = new CmdFGlobal();
    public CmdViewChest cmdViewChest = new CmdViewChest();
    public CmdPoints cmdPoints = new CmdPoints();
    public CmdLogout cmdLogout = new CmdLogout();
    public CmdNotifications cmdNotifications = new CmdNotifications();
    public CmdShop cmdShop = new CmdShop();
    public CmdMissions cmdMissions = new CmdMissions();
    public CmdCheck cmdCheck = new CmdCheck();


    public FCmdRoot() {
        super();
        this.aliases.addAll(Conf.baseCommandAliases);
        this.aliases.removeAll(Collections.<String>singletonList(null));  // remove any nulls from extra commas
        this.allowNoSlashAccess = Conf.allowNoSlashCommand;

        //this.requiredArgs.add("");
        //this.optionalArgs.put("","")

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;

        this.disableOnLock = false;

        this.setHelpShort("The faction base command");
        this.helpLong.add(p.txt.parseTags("<i>This command contains all faction stuff."));

        //this.subCommands.add(plugin.cmdHelp);

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
        this.addSubCommand(this.cmdDeinvite);
        this.addSubCommand(this.cmdDescription);
        this.addSubCommand(this.cmdDisband);
        this.addSubCommand(this.cmdStrike);
        this.addSubCommand(this.cmdHelp);
        this.addSubCommand(this.cmdHome);
        this.addSubCommand(this.cmdInvite);
        this.addSubCommand(this.cmdJoin);
        this.addSubCommand(this.cmdKick);
        this.addSubCommand(this.cmdLeave);
        this.addSubCommand(this.cmdList);
        this.addSubCommand(this.cmdLock);
        this.addSubCommand(this.cmdMap);
        this.addSubCommand(this.cmdMod);
        this.addSubCommand(this.cmdMoney);
        this.addSubCommand(this.cmdNotifications);
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
        this.addSubCommand(this.cmdLogout);
        this.addSubCommand(this.cmdTag);
        this.addSubCommand(this.cmdTitle);
        this.addSubCommand(this.cmdUnclaim);
        this.addSubCommand(this.cmdUnclaimall);
        this.addSubCommand(this.cmdVersion);
        this.addSubCommand(this.cmdWarunclaimall);
        this.addSubCommand(this.cmdSB);
        this.addSubCommand(this.cmdShowInvites);
        this.addSubCommand(this.cmdAnnounce);
        this.addSubCommand(this.cmdSeeChunk);
        this.addSubCommand(this.cmdConvert);
        this.addSubCommand(this.cmdFWarp);
        this.addSubCommand(this.cmdSetFWarp);
        this.addSubCommand(this.cmdDelFWarp);
        this.addSubCommand(this.cmdModifyPower);
        this.addSubCommand(this.cmdLogins);
        this.addSubCommand(this.cmdClaimLine);
        this.addSubCommand(this.cmdAHome);
        this.addSubCommand(this.cmdPerm);
        this.addSubCommand(this.cmdPromote);
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
        this.addSubCommand(this.cmdBanner);
        this.addSubCommand(this.cmdTpBanner);
        this.addSubCommand(this.cmdKillHolograms);
        this.addSubCommand(this.cmdCoords);
        this.addSubCommand(this.cmdShowClaims);
        this.addSubCommand(this.cmdLowPower);
        this.addSubCommand(this.cmdTntFill);
        this.addSubCommand(this.cmdChest);
        this.addSubCommand(this.cmdSetBanner);
        this.addSubCommand(this.cmdStrikeSet);
        this.addSubCommand(this.cmdSpam);
        this.addSubCommand(this.cmdCorner);
        this.addSubCommand(this.cmdFGlobal);
        this.addSubCommand(this.cmdViewChest);

        if (Conf.useCheckSystem) {
            this.addSubCommand(this.cmdCheck);
        }

        if (P.p.getConfig().getBoolean("Missions-Enabled")) {
            this.addSubCommand(this.cmdMissions);
        }

        if (P.p.getConfig().getBoolean("F-Shop.Enabled")) {
            this.addSubCommand(this.cmdShop);
        }

        if (P.p.getConfig().getBoolean("f-inventory-see.Enabled")) {
            this.addSubCommand(this.cmdInventorySee);
        }

        if (P.p.getConfig().getBoolean("f-points.Enabled")) {
            this.addSubCommand(this.cmdPoints);
        }

        if (P.p.getConfig().getBoolean("f-alts.Enabled")) {
            this.addSubCommand(this.cmdAlts);
        }

        if (P.p.getConfig().getBoolean("f-grace.Enabled")) {
            this.addSubCommand(this.cmdGrace);
        }


        if (Bukkit.getServer().getPluginManager().getPlugin("CoreProtect") != null) {
            P.p.log("Found CoreProtect, enabling Inspect");
            this.addSubCommand(this.cmdInspect);
        } else {
            P.p.log("CoreProtect not found, disabling Inspect");
        }
        if (P.p.getConfig().getBoolean("ffocus.Enabled")) {
            addSubCommand(this.cmdFocus);
        }

        if (P.p.getConfig().getBoolean("enable-faction-flight", false)) {
            this.addSubCommand(this.cmdFly);
        }
        if (Bukkit.getServer().getPluginManager().getPlugin("FactionsTop") != null || Bukkit.getServer().getPluginManager().getPlugin("SavageFTOP") != null) {
            P.p.log(Level.INFO, "Found FactionsTop plugin. Disabling our own /f top command.");
        } else {
            P.p.log(Level.INFO, "Enabling FactionsTop command, this is a very basic /f top please get a dedicated /f top resource if you want land calculation etc.");
            this.addSubCommand(this.cmdTop);
        }
        if (P.p.getConfig().getBoolean("fpaypal.Enabled")) {
            this.addSubCommand(this.cmdPaypalSet);
            this.addSubCommand(this.cmdPaypalSee);
        }

    }

    @Override
    public void perform() {
        this.commandChain.add(this);
        this.cmdHelp.execute(this.sender, this.args, this.commandChain);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.GENERIC_PLACEHOLDER;
    }

}
