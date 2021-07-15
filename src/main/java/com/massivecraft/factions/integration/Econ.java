package com.massivecraft.factions.integration;

import com.massivecraft.factions.*;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.factions.zcore.util.TL;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class Econ {

    /**
     * @author FactionsUUID Team
     */

    private static final DecimalFormat format = new DecimalFormat(TL.ECON_FORMAT.toString());
    private static Economy econ = null;
    private static DecimalFormat commaFormat = new DecimalFormat("#,##0");

    public static void setup() {
        if (isSetup()) return;

        String integrationFail = "Economy integration is " + (Conf.econEnabled ? "enabled, but" : "disabled, and") + " the plugin \"Vault\" ";

        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            FactionsPlugin.getInstance().log(integrationFail + "is not installed.");
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            FactionsPlugin.getInstance().log(integrationFail + "is not hooked into an economy plugin.");
            return;
        }
        econ = rsp.getProvider();
        FactionsPlugin.getInstance().log("Economy integration through Vault plugin successful.");
        if (!Conf.econEnabled)
            FactionsPlugin.getInstance().log("NOTE: Economy is disabled. You can enable it with the command: f config econEnabled true");
        //FactionsPlugin.getInstance().cmdBase.cmdHelp.updateHelp();
        Bukkit.getScheduler().scheduleSyncDelayedTask(FactionsPlugin.getInstance(), Econ::oldMoneyDoTransfer, 20L);
    }

    public static boolean shouldBeUsed() {
        return Conf.econEnabled && econ != null && econ.isEnabled();
    }

    public static boolean isSetup() {
        return econ != null;
    }

    public static void modifyUniverseMoney(double delta) {
        if (!shouldBeUsed()) return;
        if (Conf.econUniverseAccount == null) return;
        if (Conf.econUniverseAccount.length() == 0) return;
        if (!econ.hasAccount(Conf.econUniverseAccount)) return;
        modifyBalance(Conf.econUniverseAccount, delta);
    }

    public static void oldMoneyDoTransfer() {
        if (!shouldBeUsed())
            return;
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            if (faction.getFactionBalance() <= 0.0D) {
                String accountID = faction.getAccountId();
                double foundBalance = econ.getBalance(accountID);
                if (foundBalance > 0.0D) {
                    EconomyResponse response = econ.withdrawPlayer(accountID, foundBalance);
                    if (response.transactionSuccess()) {
                        Bukkit.getLogger().info("[Factions] Converted $" + foundBalance + " for " + faction.getTag() + " id: " + faction.getId() + " accountID: " + accountID);
                        faction.setFactionBalance(foundBalance);
                        continue;
                    }
                    Bukkit.getLogger().info("[Factions] Unable to convert balance of " + faction.getAccountId() + " tag: " + faction.getTag());
                    continue;
                }
                Bukkit.getLogger().info("[Factions] Balance for " + faction.getTag() + " had money: " + faction.getFactionBalance() + " found: " + foundBalance + " acc: " + accountID);
                continue;
            }
            Bukkit.getLogger().info("[Factions] Balance for " + faction.getTag() + " had money: " + faction.getFactionBalance());
        }
    }

    public static void sendBalanceInfo(FPlayer to, EconomyParticipator about) {
        if (!shouldBeUsed()) {
            FactionsPlugin.instance.log(Level.WARNING, "Vault does not appear to be hooked into an economy plugin.");
            return;
        }
        to.msg(TL.ECON_PLAYERBALANCE, about.describeTo(to, true), Econ.moneyString(econ.getBalance(about.getAccountId())));
    }

    public static void sendBalanceInfo(FPlayer to, Faction about) {
        if (!shouldBeUsed()) {
            FactionsPlugin.instance.log(Level.WARNING, "Vault does not appear to be hooked into an economy plugin.");
            return;
        }

        String name = (about instanceof Faction) ? about.getTag() : about.describeTo(to, true);

        to.sendMessage(String.format(TL.ECON_PLAYERBALANCE.toString(), about.getTag(), insertCommas(getFactionBalance(about)), name));
    }

    public static double getFactionBalance(Faction faction) {
        return faction.getFactionBalance();
    }

    public static String insertCommas(double amount) {
        return commaFormat.format(amount);
    }

    public static boolean canIControllYou(EconomyParticipator i, EconomyParticipator you) {
        Faction fI = RelationUtil.getFaction(i);
        Faction fYou = RelationUtil.getFaction(you);

        // This is a system invoker. Accept it.
        if (fI == null) return true;
        // Bypassing players can do any kind of transaction
        if (i instanceof FPlayer && ((FPlayer) i).isAdminBypassing()) return true;
        // Players with the any withdraw can do.
        if (i instanceof FPlayer && Permission.MONEY_WITHDRAW_ANY.has(((FPlayer) i).getPlayer())) return true;
        // You can deposit to anywhere you feel like. It's your loss if you can't withdraw it again.
        if (i == you) return true;
        // A faction can always transfer away the money of it's members and its own money...
        // This will however probably never happen as a faction does not have free will.
        // Ohh by the way... Yes it could. For daily rent to the faction.
        if (i == fI && fI == fYou) return true;
        // Factions can be controlled by members that are moderators... or any member if any member can withdraw.
        if (you instanceof Faction && fI == fYou && (Conf.bankMembersCanWithdraw || (i instanceof FPlayer && ((FPlayer) i).getRole().value >= Role.MODERATOR.value)))
            return true;
        // Otherwise you may not!;,,;
        i.msg(TL.ECON_CANTCONTROLMONEY, i.describeTo(i, true), you.describeTo(i));
        return false;
    }

    public static boolean transferMoney(EconomyParticipator invoker, EconomyParticipator from, EconomyParticipator to, double amount) {
        return transferMoney(invoker, from, to, amount, true);
    }

    public static boolean transferMoney(EconomyParticipator invoker, EconomyParticipator from, EconomyParticipator to, double amount, boolean notify) {
        if (!shouldBeUsed()) {
            invoker.msg(TL.ECON_DISABLED);
            return false;
        }

        // The amount must be positive.
        // If the amount is negative we must flip and multiply amount with -1.
        if (amount < 0.0D) {
            amount *= -1.0D;
            EconomyParticipator temp = from;
            from = to;
            to = temp;
        }

        // Check the rights
        if (!canIControllYou(invoker, from)) return false;

        OfflinePlayer fromAcc = (from instanceof Faction) ? null : getPlayerFromUUIDOrName(from.getAccountId());

        OfflinePlayer toAcc = (to instanceof Faction) ? null : getPlayerFromUUIDOrName(to.getAccountId());

        // Is there enough money for the transaction to happen?
        if (!hasAtLeast(from, amount, null)) {
            // There was not enough money to pay
            if (invoker != null && notify) {
                invoker.msg(TL.COMMAND_MONEYTRANSFERFF_TRANSFERCANTAFFORD, from.describeTo(invoker, true), moneyString(amount), to.describeTo(invoker));
            }
            return false;
        }

        // Transfer money
        boolean success = (from instanceof Faction) ? withdrawFactionBalance((Faction) from, amount) : econ.withdrawPlayer(fromAcc, amount).transactionSuccess();

        if (success) {
            boolean deposited = (to instanceof Faction) ? depositFactionBalance((Faction) to, amount) : econ.depositPlayer(toAcc, amount).transactionSuccess();
            if (deposited) {
                if (notify)
                    sendTransferInfo(invoker, from, to, amount);
                return true;
            }
            if (from instanceof Faction) {
                depositFactionBalance((Faction) from, amount);
            } else {
                econ.depositPlayer(fromAcc, amount);
            }
        }

        // if we get here something with the transaction failed
        if (notify)
            invoker.msg(TL.ECON_UNABLETOTRANSFER, moneyString(amount), to.describeTo(invoker), from.describeTo(invoker, true));
        return false;
    }

    public static Set<FPlayer> getFplayers(EconomyParticipator ep) {
        Set<FPlayer> fplayers = new HashSet<>();

        if (ep != null) {
            if (ep instanceof FPlayer) {
                fplayers.add((FPlayer) ep);
            } else if (ep instanceof Faction) {
                fplayers.addAll(((Faction) ep).getFPlayers());
            }
        }

        return fplayers;
    }

    public static void sendTransferInfo(EconomyParticipator invoker, EconomyParticipator from, EconomyParticipator to, double amount) {
        Set<FPlayer> recipients = new HashSet<>();
        recipients.addAll(getFplayers(invoker));
        recipients.addAll(getFplayers(from));
        recipients.addAll(getFplayers(to));

        if (invoker == null) {
            for (FPlayer recipient : recipients)
                recipient.msg(TL.ECON_MONEYTRASFERREDFROM, moneyString(amount), from.describeTo(recipient), to.describeTo(recipient));
        } else if (invoker == from) {
            for (FPlayer recipient : recipients)
                recipient.msg(TL.ECON_PERSONGAVEMONEYTO, from.describeTo(recipient, true), moneyString(amount), to.describeTo(recipient));
        } else if (invoker == to) {
            for (FPlayer recipient : recipients)
                recipient.msg(TL.ECON_PERSONTOOKMONEYFROM, to.describeTo(recipient, true), moneyString(amount), from.describeTo(recipient));
        } else {
            for (FPlayer recipient : recipients)
                recipient.msg(TL.ECON_MONEYTRASFERREDFROMPERSONTOPERSON, invoker.describeTo(recipient, true), moneyString(amount), from.describeTo(recipient), to.describeTo(recipient));
        }
    }

    public static boolean hasAtLeast(EconomyParticipator ep, double delta, String toDoThis) {
        if (!shouldBeUsed()) return true;

        // going the hard way round as econ.has refuses to work.
        boolean affordable = false;
        double currentBalance;
        if (ep instanceof Faction) {
            currentBalance = ((Faction) ep).getFactionBalance();
        } else if (isUUID(ep.getAccountId())) {
            currentBalance = econ.getBalance(Bukkit.getOfflinePlayer(UUID.fromString(ep.getAccountId())));
        } else {
            currentBalance = econ.getBalance(Bukkit.getOfflinePlayer(ep.getAccountId()));
        }
        if (currentBalance >= delta) affordable = true;

        if (!affordable) {
            if (toDoThis != null && !toDoThis.isEmpty())
                ep.msg(TL.COMMAND_MONEY_CANTAFFORD, ep.describeTo(ep, true), moneyString(delta), toDoThis);
            return false;
        }
        return true;
    }

    private static OfflinePlayer getPlayerFromUUIDOrName(String name) {
        if (isUUID(name))
            return Bukkit.getOfflinePlayer(UUID.fromString(name));
        return Bukkit.getOfflinePlayer(name);
    }

    public static boolean withdrawFactionBalance(Faction faction, double amount) {
        double balance = faction.getFactionBalance();
        if (amount > faction.getFactionBalance()) {
            return false;
        }

        faction.setFactionBalance(balance - amount);
        return true;
    }

    public static boolean depositFactionBalance(Faction faction, double amount) {
        double balance = faction.getFactionBalance();

        if (amount < 0.0D)
            return withdrawFactionBalance(faction, Math.abs(amount));
        if (faction.getFactionBalance() + amount < 0.0D) {
            Bukkit.getLogger().info("Unable to deposit money into " + faction.getTag() + " because their balance: " + faction.getFactionBalance() + " + " + amount + " is < 0!");
            return false;
        }
        faction.setFactionBalance(balance + amount);
        return true;
    }

    public static boolean modifyMoney(EconomyParticipator ep, double delta, String toDoThis, String forDoingThis) {
        // code goes here.
        if (!shouldBeUsed()) return false;

        OfflinePlayer acc = (ep instanceof Faction) ? null : getPlayerFromUUIDOrName(ep.getAccountId());

        String You = ep.describeTo(ep, true);

        if (delta == 0.0D)
            return true;

        if (delta > 0.0D) {
            // The player should gain money
            // The account might not have enough space
            boolean deposited = (ep instanceof Faction) ? depositFactionBalance((Faction) ep, delta) : econ.depositPlayer(acc, delta).transactionSuccess();

            if (deposited) {
                modifyUniverseMoney(-delta);
                if (forDoingThis != null && !forDoingThis.isEmpty())
                    ep.msg(TL.COMMAND_MONEY_GAINED, You, moneyString(delta), forDoingThis);
                return true;
            } else {
                // transfer to account failed
                if (forDoingThis != null && !forDoingThis.isEmpty())
                    ep.msg(TL.ECON_DEPOSITFAILED, You, moneyString(delta), forDoingThis);
                return false;
            }
        } else {
            // The player should loose money
            // The player might not have enough.
            if (ep instanceof Faction) {
                if (hasAtLeast(ep, -delta, null)) {
                    withdrawFactionBalance((Faction) ep, -delta);
                    if (forDoingThis != null && !forDoingThis.isEmpty())
                        ep.msg("<h>%s<i> lost <h>%s<i> %s.", You, moneyString(-delta), forDoingThis);
                    return true;
                }
                if (toDoThis != null && !toDoThis.isEmpty())
                    ep.msg("<h>%s<i> can't afford <h>%s<i> %s.", You, moneyString(-delta), toDoThis);
                return false;
            }


            if (econ.has(acc, -delta) && econ.withdrawPlayer(acc, -delta).transactionSuccess()) {
                // There is enough money to pay
                modifyUniverseMoney(-delta);
                if (forDoingThis != null && !forDoingThis.isEmpty())
                    ep.msg(TL.ECON_MONEYLOST, You, moneyString(-delta), forDoingThis);
                return true;
            } else {
                // There was not enough money to pay
                if (toDoThis != null && !toDoThis.isEmpty())
                    ep.msg(TL.ECON_CANTAFFORD, You, moneyString(-delta), toDoThis);
                return false;
            }
        }
    }

    public static String moneyString(double amount) {
        return format.format(amount);
    }

    // calculate the cost for claiming land
    public static double calculateClaimCost(int ownedLand, boolean takingFromAnotherFaction) {
        if (!shouldBeUsed()) return 0d;
        // basic claim cost, plus land inflation cost, minus the potential bonus given for claiming from another faction
        return Conf.econCostClaimWilderness + (Conf.econCostClaimWilderness * Conf.econClaimAdditionalMultiplier * ownedLand) - (takingFromAnotherFaction ? Conf.econCostClaimFromFactionBonus : 0);
    }

    // calculate refund amount for unclaiming land
    public static double calculateClaimRefund(int ownedLand) {
        return calculateClaimCost(ownedLand - 1, false) * Conf.econClaimRefundMultiplier;
    }

    // calculate value of all owned land
    public static double calculateTotalLandValue(int ownedLand) {
        double amount = 0;
        for (int x = 0; x < ownedLand; x++) amount += calculateClaimCost(x, false);
        return amount;
    }


    // -------------------------------------------- //
    // Standard account management methods
    // -------------------------------------------- //

    // calculate refund amount for all owned land
    public static double calculateTotalLandRefund(int ownedLand) {
        return calculateTotalLandValue(ownedLand) * Conf.econClaimRefundMultiplier;
    }

    public static boolean hasAccount(String name) {
        return econ.hasAccount(name);
    }

    public static double getBalance(String account) {
        return econ.getBalance(account);
    }

    public static String getFriendlyBalance(UUID uuid) {
        OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
        if (offline.getName() == null) return "0";
        return format.format(econ.getBalance(offline));
    }

    public static String getFriendlyBalance(FPlayer player) {
        return getFriendlyBalance(UUID.fromString(player.getId()));
    }

    public static boolean setBalance(String account, double amount) {
        if (account.startsWith("faction-"))
            return true;

        double current = econ.getBalance(Bukkit.getOfflinePlayer(account));

        if (current > amount)
            return econ.withdrawPlayer(Bukkit.getOfflinePlayer(account), current - amount).transactionSuccess();
        return econ.depositPlayer(Bukkit.getOfflinePlayer(account), amount - current).transactionSuccess();
    }

    public static boolean modifyBalance(String account, double amount) {
        if (account.startsWith("faction-"))
            return true;
        if (amount < 0.0D)
            return econ.withdrawPlayer(Bukkit.getOfflinePlayer(account), -amount).transactionSuccess();
        return econ.depositPlayer(Bukkit.getOfflinePlayer(account), amount).transactionSuccess();
    }

    @Deprecated
    public static boolean deposit(String account, double amount) {
        if (account.startsWith("faction-"))
            return true;
        return econ.depositPlayer(Bukkit.getOfflinePlayer(account), amount).transactionSuccess();
    }

    @Deprecated
    public static boolean withdraw(String account, double amount) {
        if (account.startsWith("faction-"))
            return true;
        return econ.withdrawPlayer(Bukkit.getOfflinePlayer(account), amount).transactionSuccess();
    }

    // ---------------------------------------
    // Helpful Utilities
    // ---------------------------------------

    public static boolean isUUID(String uuid) {
        try {
            UUID.fromString(uuid);
        } catch (IllegalArgumentException ex) {
            return false;
        }

        return true;
    }
}