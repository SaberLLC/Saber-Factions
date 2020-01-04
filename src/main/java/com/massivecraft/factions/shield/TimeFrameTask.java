package com.massivecraft.factions.shield;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

/**
 * @author Saser
 */
public class TimeFrameTask implements Runnable {

    @Override
    public void run() {
        //remove time from the timeFrame


        for (Faction faction : Factions.getInstance().getAllFactions()) {
            if (faction.getTimeFrame() != null) {
                TimeFrame timeFrame = faction.getTimeFrame();
                if (timeFrame.isStarting() || timeFrame.isEnding() || timeFrame.isInEffect()) {
                    //either starting, ending, or in effect, so we have to remove 1 minute interval from the currentTime
                    int newTime = Math.subtractExact(timeFrame.getCurrentMinutes(), 1);
                    if (newTime == 0) {
                        //time is done, do functions...
                        if (timeFrame.isStarting() || timeFrame.isInEffect()) {
                            if (timeFrame.isStarting()) {
                                //it was starting, now set to inEffect
                                timeFrame.setStarting(false);
                                timeFrame.setInEffect(true);
                            }
                            //we don't need to check for inEffect because if it is, it'll just set the time back anyways...
                            timeFrame.setCurrentMinutes(720);
                            continue; // continue to the next faction
                        } else if (timeFrame.isEnding()) {
                            //it was ending, now set inEffect to false, basically remove from the faction obj
                            timeFrame.setEnding(false);
                            timeFrame.setInEffect(false);
                            //remove from faction object
                            faction.setTimeFrame(null);
                            continue; // continue to the next faction
                        }
                    }
                    timeFrame.setCurrentMinutes(newTime);
                }
            }
        }
    }
}
