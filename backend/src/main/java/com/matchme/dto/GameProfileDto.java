package com.matchme.dto;

import java.util.Set;

public class GameProfileDto {
    private Set<String> preferredServers;
    private String gamingHours;
    private String expLvl;
    
    // NEW FIELDS
    private String competitiveness;
    private String voiceChatPreference;
    private String playSchedule;
    private String mainGoal;
    private String currentRank;

    // Existing getters/setters
    public Set<String> getPreferredServers() { return preferredServers; }
    public void setPreferredServers(Set<String> preferredServers) { this.preferredServers = preferredServers; }

    public String getExpLvl() { return expLvl; }
    public void setExpLvl(String expLvl) { this.expLvl = expLvl; }

    public String getGamingHours() { return gamingHours; }
    public void setGamingHours(String gamingHours) { this.gamingHours = gamingHours; }

    // NEW GETTERS/SETTERS
    public String getCompetitiveness() { return competitiveness; }
    public void setCompetitiveness(String competitiveness) { this.competitiveness = competitiveness; }

    public String getVoiceChatPreference() { return voiceChatPreference; }
    public void setVoiceChatPreference(String voiceChatPreference) { this.voiceChatPreference = voiceChatPreference; }

    public String getPlaySchedule() { return playSchedule; }
    public void setPlaySchedule(String playSchedule) { this.playSchedule = playSchedule; }

    public String getMainGoal() { return mainGoal; }
    public void setMainGoal(String mainGoal) { this.mainGoal = mainGoal; }

    public String getCurrentRank() { return currentRank; }
    public void setCurrentRank(String currentRank) { this.currentRank = currentRank; }
}