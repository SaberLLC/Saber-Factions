/*  ApolloFTeamTask
 * By: jimmy "vSKAH" <vskahhh@gmail.com>
 * Created with IntelliJ IDEA
 * For the project Saber-Factions
 * 21/10/2024
 */

package com.massivecraft.factions.apollo.fteam;

import java.util.TimerTask;

public class ApolloFTeamTask extends TimerTask {

    @Override
    public void run() {
        ApolloFTeam.getInstance().applyUpdates();
    }
}
