package com.massivecraft.factions.zcore.fperms;

import java.util.Objects;

public class DefaultPermissions {

    /**
     * @author Illyria Team
     */

    public boolean ban;
    public boolean build;
    public boolean destroy;
    public boolean frostwalk;
    public boolean painbuild;
    public boolean door;
    public boolean button;
    public boolean lever;
    public boolean container;
    public boolean invite;
    public boolean kick;
    public boolean items;
    public boolean sethome;
    public boolean territory;
    public boolean home;
    public boolean disband;
    public boolean promote;
    public boolean setwarp;
    public boolean warp;
    public boolean fly;
    public boolean vault;
    public boolean tntbank;
    public boolean tntfill;
    public boolean withdraw;
    public boolean chest;
    public boolean audit;
    public boolean check;
    public boolean drain;
    public boolean spawner;

    public DefaultPermissions() {
    }

    public DefaultPermissions(boolean def) {
        this.ban = def;
        this.build = def;
        this.destroy = def;
        this.frostwalk = def;
        this.painbuild = def;
        this.door = def;
        this.button = def;
        this.lever = def;
        this.container = def;
        this.invite = def;
        this.kick = def;
        this.items = def;
        this.sethome = def;
        this.territory = def;
        this.home = def;
        this.disband = def;
        this.promote = def;
        this.setwarp = def;
        this.warp = def;
        this.fly = def;
        this.vault = def;
        this.tntbank = def;
        this.tntfill = def;
        this.withdraw = def;
        this.chest = def;
        this.audit = def;
        this.check = def;
        this.drain = def;
        this.spawner = def;
    }

    public DefaultPermissions(boolean canBan,
                              boolean canBuild,
                              boolean canDestory,
                              boolean canFrostwalk,
                              boolean canPainbuild,
                              boolean canDoor,
                              boolean canButton,
                              boolean canLever,
                              boolean canContainer,
                              boolean canInvite,
                              boolean canKick,
                              boolean canItems,
                              boolean canSethome,
                              boolean canTerritory,
                              boolean canAudit,
                              boolean canHome,
                              boolean canDisband,
                              boolean canPromote,
                              boolean canSetwarp,
                              boolean canWarp,
                              boolean canFly,
                              boolean canVault,
                              boolean canTntbank,
                              boolean canTntfill,
                              boolean canWithdraw,
                              boolean canChest,
                              boolean canCheck,
                              boolean canDrain,
                              boolean canSpawners) {
        this.ban = canBan;
        this.build = canBuild;
        this.destroy = canDestory;
        this.frostwalk = canFrostwalk;
        this.painbuild = canPainbuild;
        this.door = canDoor;
        this.button = canButton;
        this.lever = canLever;
        this.container = canContainer;
        this.invite = canInvite;
        this.kick = canKick;
        this.items = canItems;
        this.sethome = canSethome;
        this.territory = canTerritory;
        this.home = canHome;
        this.disband = canDisband;
        this.promote = canPromote;
        this.setwarp = canSetwarp;
        this.warp = canWarp;
        this.fly = canFly;
        this.vault = canVault;
        this.tntbank = canTntbank;
        this.tntfill = canTntfill;
        this.withdraw = canWithdraw;
        this.chest = canChest;
        this.audit = canAudit;
        this.check = canCheck;
        this.drain = canDrain;
        this.spawner = canSpawners;
    }

    @Deprecated
    public boolean getbyName(String name) {
        if (Objects.equals(name, "ban")) return this.ban;
        else if (Objects.equals(name, "build")) return this.build;
        else if (Objects.equals(name, "destroy")) return this.destroy;
        else if (Objects.equals(name, "frostwalk")) return this.frostwalk;
        else if (Objects.equals(name, "painbuild")) return this.painbuild;
        else if (Objects.equals(name, "door")) return this.door;
        else if (Objects.equals(name, "button")) return this.button;
        else if (Objects.equals(name, "lever")) return this.lever;
        else if (Objects.equals(name, "home")) return this.home;
        else if (Objects.equals(name, "container")) return this.container;
        else if (Objects.equals(name, "invite")) return this.invite;
        else if (Objects.equals(name, "kick")) return this.kick;
        else if (Objects.equals(name, "items")) return this.items;
        else if (Objects.equals(name, "sethome")) return this.sethome;
        else if (Objects.equals(name, "territory")) return this.territory;
        else if (Objects.equals(name, "disband")) return this.disband;
        else if (Objects.equals(name, "promote")) return this.promote;
        else if (Objects.equals(name, "setwarp")) return this.setwarp;
        else if (Objects.equals(name, "warp")) return this.warp;
        else if (Objects.equals(name, "fly")) return this.fly;
        else if (Objects.equals(name, "vault")) return this.vault;
        else if (Objects.equals(name, "tntbank")) return this.tntbank;
        else if (Objects.equals(name, "tntfill")) return this.tntfill;
        else if (Objects.equals(name, "withdraw")) return this.withdraw;
        else if (Objects.equals(name, "chest")) return this.chest;
        else if (Objects.equals(name, "audit")) return this.audit;
        else if (Objects.equals(name, "check")) return this.check;
        else if (Objects.equals(name, "drain")) return this.drain;
        else if (Objects.equals(name, "spawner")) return this.spawner;
        else return false;
    }
}
