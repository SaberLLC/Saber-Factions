package com.massivecraft.factions.discord;


/**
 * @author SaberTeam
 */

public class DiscordSetupAttempt {
    private Boolean success;
    private String reason;
    private Long initialTime;

    /**
     * Constructor used when an attempt fails
     *
     * @param reason String reason for the attempt failing
     * @param time   Long current system time in millis
     */
    public DiscordSetupAttempt(String reason, Long time) {
        this.success = false;
        this.reason = reason;
        this.initialTime = time;
    }

    /**
     * Constructor used for successful attempts
     *
     * @param time Long Current system time in millis
     */
    public DiscordSetupAttempt(Long time) {
        this.success = true;
        this.reason = null;
        this.initialTime = time;
    }

    /**
     * Get if this attempt to setup the Discord bot was successful
     *
     * @return Boolean success
     */
    public Boolean getSuccess() {
        return this.success;
    }

    /**
     * Get the reason for the setup failing (If it was successful it will return null)
     *
     * @return String reason
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * Get the time this setup was attempted
     *
     * @return Long initialTime
     */
    public Long getInitialTime() {
        return this.initialTime;
    }

    /**
     * Get the difference of time between when attempted and present time
     *
     * @return Long time difference in milliseconds
     */
    public Long getDifferentialTime() {
        return System.currentTimeMillis() - initialTime;
    }

    /**
     * Get the difference in time between when attempted and present time formatted MS,Seconds,Minutes,Hours,Years
     *
     * @return String with formatted time difference
     */
    public String getDifferentialFormatted() {
        int timeIndex = 0;
        //Milliseconds
        Long inProcessTime = getDifferentialTime();
        if (inProcessTime >= 1000) {
            timeIndex++;
            //Seconds
            inProcessTime = inProcessTime / Integer.toUnsignedLong(1000);
            if (inProcessTime >= 60) {
                timeIndex++;
                //Minutes
                inProcessTime = inProcessTime / Integer.toUnsignedLong(60);
                if (inProcessTime >= 60) {
                    timeIndex++;
                    //Hours
                    inProcessTime = inProcessTime / Integer.toUnsignedLong(60);
                    if (inProcessTime >= 24) {
                        timeIndex++;
                        //Days
                        inProcessTime = inProcessTime / Integer.toUnsignedLong(24);
                        //Skipping months
                        if (inProcessTime >= 365) {
                            timeIndex++;
                            //Years
                            //If someone really has 100% uptime in a year idek
                            inProcessTime = inProcessTime / Integer.toUnsignedLong(365);
                        }
                    }
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(inProcessTime);
        //Just a separator for looks
        sb.append(" ");
        String s = "";
        switch (timeIndex) {
            case 0:
                s = "MS";
                break;
            case 1:
                s = "Seconds";
                break;
            case 2:
                s = "Minutes";
                break;
            case 3:
                s = "Hours";
                break;
            case 4:
                s = "Days";
                break;
            case 5:
                s = "Years";
                break;
        }
        sb.append(s);
        sb.append(" ago");
        return sb.toString();
    }
}
