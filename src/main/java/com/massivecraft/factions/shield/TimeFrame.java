package com.massivecraft.factions.shield;

import com.massivecraft.factions.Faction;

/**
 * @author Saser
 */
public class TimeFrame {

    //each of these objs will be in 1 fac

    private Faction faction;
    private Enum startingTime;
    private Enum endingTime;

    private int currentMinutes; // this will be the variable for either the currentTime starting, or ending, or current in effect.

    private boolean inEffect; // if the shield is in effect
    private boolean starting; // pending starting countdown
    private boolean ending; // pending ending countdown

    private enum times {
        twelveAM, oneAM, twoAM, threeAM, fourAM, fiveAM, sixAM, sevenAM, eightAM, nineAM, tenAM, elevenAM, twelvePM,
        onePM, twoPM, threePM, fourPM, fivePM, sixPM, sevenPM, eightPM, ninePM, tenPM, elevenPM;
    }


    public TimeFrame(Faction faction, Enum startingTime, Enum endingTime, boolean starting, boolean ending, boolean inEffect, int currentMinutes){
        this.faction = faction;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.starting = starting;
        this.ending = ending;
        this.inEffect = inEffect;
        this.currentMinutes = currentMinutes;
    }




    public boolean isEnding() {
        return ending;
    }

    public boolean isInEffect() {
        return inEffect;
    }

    public Enum getEndingTime() {
        return endingTime;
    }

    public Enum getStartingTime() {
        return startingTime;
    }

    public boolean isStarting() {
        return starting;
    }

    public Faction getFaction() {
        return faction;
    }

    public void setCurrentMinutes(int currentMinutes) {
        this.currentMinutes = currentMinutes;
    }

    public int getCurrentMinutes() {
        return currentMinutes;
    }

    public void setEnding(boolean ending) {
        this.ending = ending;
    }

    public void setEndingTime(Enum endingTime) {
        this.endingTime = endingTime;
    }

    public void setStartingTime(Enum startingTime) {
        this.startingTime = startingTime;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public void setInEffect(boolean inEffect) {
        this.inEffect = inEffect;
    }

    public void setStarting(boolean starting) {
        this.starting = starting;
    }
}
