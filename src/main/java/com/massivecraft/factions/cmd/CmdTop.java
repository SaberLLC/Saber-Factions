package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.FastMath;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.entity.Player;

import java.util.*;

public class CmdTop extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     * 
     */

    private static final Map<String, Comparator<Faction>> CRITERIA = new HashMap<String, Comparator<Faction>>(){{
        put("members", (f1, f2) -> Integer.compare(f2.getFPlayers().size(), f1.getFPlayers().size()));
        put("start", (f1, f2) -> Long.compare(f2.getFoundedDate(), f1.getFoundedDate()));
        put("power", (f1, f2) -> Integer.compare(f2.getPowerRounded(), f1.getPowerRounded()));
        put("land", (f1, f2) -> Integer.compare(f2.getLandRounded(), f1.getLandRounded()));
        put("online", (f1, f2) -> Integer.compare(f2.getFPlayersWhereOnline(true).size(), f1.getFPlayersWhereOnline(true).size()));
        put("money", (f1, f2) -> {
            double f1Size = f1.getFactionBalance();
            for (FPlayer fp : f1.getFPlayers()) {
                f1Size = f1Size + Econ.getBalance(fp.getAccountId());
            }
            double f2Size = f2.getFactionBalance();
            for (FPlayer fp : f2.getFPlayers()) {
                f2Size = f2Size + Econ.getBalance(fp.getAccountId());
            }
            return Double.compare(f2Size, f1Size);
        });
    }};

    public CmdTop() {
        super();
        this.aliases.addAll(Aliases.top);
        this.requiredArgs.add("criteria");
        this.optionalArgs.put("page", "1");

        this.requirements = new CommandRequirements.Builder(Permission.TOP)
                .build();
    }


    @Override
    public void perform(CommandContext context) {
        List<Faction> factionList = Factions.getInstance().getAllNormalFactions();

        String criteria = context.argAsString(0);

        Comparator<Faction> sorter = CRITERIA.get(criteria.toLowerCase());
        if (sorter != null) {
            factionList.sort(sorter);
        } else {
            context.msg(TL.COMMAND_TOP_INVALID, criteria);
            return;
        }

        List<String> lines = new ArrayList<>();

        final int pageheight = 9;
        int pagenumber = Math.max(1, Math.min(context.argAsInt(1, 1), (factionList.size() / pageheight) + 1));
        int start = (pagenumber - 1) * pageheight;
        int end = Math.min(start + pageheight, factionList.size());

        lines.add(TL.COMMAND_TOP_TOP.format(criteria.toUpperCase(), pagenumber, (factionList.size() / pageheight) + 1));
        for (int i = start; i < end; i++) {
            Faction faction = factionList.get(i);
            String fac = context.sender instanceof Player ? faction.getRelationTo(context.fPlayer).getColor() + faction.getTag() : faction.getTag();
            lines.add(TL.COMMAND_TOP_LINE.format(i + 1, fac, getValue(faction, criteria)));
        }

        context.sendMessage(lines);
    }

    private String getValue(Faction faction, String criteria) {
        switch (criteria.toLowerCase()) {
            case "online":
                return Integer.toString(faction.getFPlayersWhereOnline(true).size());
            case "start":
                return TL.sdf.format(faction.getFoundedDate());
            case "members":
                return Integer.toString(faction.getFPlayers().size());
            case "land":
                return Integer.toString(faction.getLandRounded());
            case "power":
                return Integer.toString(faction.getPowerRounded());
            default:
                double balance = faction.getFactionBalance();
                for (FPlayer fp : faction.getFPlayers()) {
                    balance = FastMath.round(balance + Econ.getBalance(fp.getAccountId()));
                }
                return Double.toString(balance);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TOP_DESCRIPTION;
    }
}
