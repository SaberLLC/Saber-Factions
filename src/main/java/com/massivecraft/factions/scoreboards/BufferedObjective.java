package com.massivecraft.factions.scoreboards;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BufferedObjective {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    private static final Method addEntryMethod;
    private static final int MAX_LINE_LENGTH;
    private static final Pattern PATTERN = Pattern.compile("(\u00A7[0-9a-fk-r])|(.)");

    static {
        // Check for long line support.
        // We require use of Spigot's `addEntry(String)` method on
        // Teams, as adding OfflinePlayers to a team is far too slow.

        Method addEntryMethodLookup = null;
        try {
            addEntryMethodLookup = Team.class.getMethod("addEntry", String.class);
        } catch (NoSuchMethodException ignored) {
        }

        addEntryMethod = addEntryMethodLookup;

        if (addEntryMethod != null) {
            MAX_LINE_LENGTH = 48;
        } else {
            MAX_LINE_LENGTH = 16;
        }
    }

    private final Scoreboard scoreboard;
    private final String baseName;
    private final Map<Integer, String> contents = new HashMap<>();
    private Objective current;
    private List<Team> currentTeams = new ArrayList<>();
    private String title;
    private DisplaySlot displaySlot;
    private int objPtr;
    private int teamPtr;
    private boolean requiresUpdate = false;

    public BufferedObjective(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        this.baseName = createBaseName();

        current = scoreboard.registerNewObjective(getNextObjectiveName(), "dummy");
    }

    private String createBaseName() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        while (builder.length() < 14) {
            builder.append(Integer.toHexString(random.nextInt()));
        }
        return builder.toString().substring(0, 14);
    }

    public void setTitle(String title) {
        if (this.title == null || !this.title.equals(title)) {
            this.title = title;
            requiresUpdate = true;
        }
    }

    public void setDisplaySlot(DisplaySlot slot) {
        this.displaySlot = slot;
        current.setDisplaySlot(slot);
    }

    public void setAllLines(List<String> lines) {
        if (lines.size() != contents.size()) {
            contents.clear();
        }
        for (int i = 0; i < lines.size(); i++) {
            setLine(lines.size() - i, lines.get(i));
        }
    }

    public void setLine(int lineNumber, String content) {
        if (content.length() > MAX_LINE_LENGTH) {
            content = content.substring(0, MAX_LINE_LENGTH);
        }
        content = ChatColor.translateAlternateColorCodes('&', content);

        if (contents.get(lineNumber) == null || !contents.get(lineNumber).equals(content)) {
            contents.put(lineNumber, content);
            requiresUpdate = true;
        }
    }

    // Hides the objective from the display slot until flip() is called
    public void hide() {
        if (displaySlot != null) {
            scoreboard.clearSlot(displaySlot);
        }
    }

    public void flip() {
        if (!requiresUpdate) {
            return;
        }
        requiresUpdate = false;

        Objective buffer = scoreboard.registerNewObjective(getNextObjectiveName(), "dummy");
        buffer.setDisplayName(title);

        List<Team> bufferTeams = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : contents.entrySet()) {
            if (entry.getValue().length() > 16) {
                Team team = scoreboard.registerNewTeam(getNextTeamName());
                bufferTeams.add(team);

                String name, prefix = null, suffix = null;

                String value = entry.getValue();
                if (value.length() > 16) {
                    String[] arrImAPirate = new String[3];
                    Matcher matcherrr = PATTERN.matcher(value);
                    StringBuilder builderrr = new StringBuilder();
                    int sCURvy = 0;
                    char currrentColorrr = 'r';
                    char currrentFormat = 'r';
                    while (sCURvy < 3 && matcherrr.find()) {
                        String tharSheBlows = matcherrr.group();
                        boolean hoist = false;
                        if (tharSheBlows.length() == 1) {
                            builderrr.append(tharSheBlows);
                            if (builderrr.length() == 16) {
                                hoist = true;
                            }
                        } else {
                            char c = tharSheBlows.charAt(1);
                            if (c >= 'k' && c <= 'r') { // format!
                                currrentFormat = c;
                                if (c == 'r') {
                                    currrentColorrr = 'r';
                                }
                            } else {
                                currrentColorrr = c;
                                currrentFormat = 'r';
                            }
                            if (builderrr.length() < 14) {
                                builderrr.append(tharSheBlows);
                            } else {
                                hoist = true;
                            }
                        }
                        if (hoist) {
                            arrImAPirate[sCURvy++] = builderrr.toString();
                            builderrr = new StringBuilder();
                            if (currrentColorrr != 'r') {
                                builderrr.append('\u00A7').append(currrentColorrr);
                            }
                            if (currrentFormat != 'r') {
                                builderrr.append('\u00A7').append(currrentFormat);
                            }
                        }
                    }
                    if (sCURvy < 3 && builderrr.length() > 0) {
                        arrImAPirate[sCURvy] = builderrr.toString();
                    }
                    if (arrImAPirate[2] == null) {
                        name = arrImAPirate[0];
                        suffix = arrImAPirate[1];
                    } else {
                        prefix = arrImAPirate[0];
                        name = arrImAPirate[1];
                        suffix = arrImAPirate[2];
                    }
                } else {
                    name = value;
                }

                if (prefix != null) {
                    team.setPrefix(prefix);
                }
                if (suffix != null) {
                    team.setSuffix(suffix);
                }


                try {
                    addEntryMethod.invoke(team, name);
                } catch (ReflectiveOperationException ignored) {
                }
                buffer.getScore(name).setScore(entry.getKey());
            } else {
                buffer.getScore(entry.getValue()).setScore(entry.getKey());
            }
        }

        if (displaySlot != null) {
            buffer.setDisplaySlot(displaySlot);
        }

        // Unregister _ALL_ the old things
        current.unregister();

        Iterator<Team> it = currentTeams.iterator();
        while (it.hasNext()) {
            it.next().unregister();
            it.remove();
        }

        current = buffer;
        currentTeams = bufferTeams;
    }

    private String getNextObjectiveName() {
        return baseName + "_" + ((objPtr++) % 2);
    }

    private String getNextTeamName() {
        return baseName.substring(0, 10) + "_" + ((teamPtr++) % 999999);
    }
}