package com.matchme.dto;

import java.util.Set;

public class GameProfileDto {
    private Set<String> preferredServers;
    private String gamingHours;
    private String expLvl;
    
    // NEW FIELDS

    private String currentRank;
    
    public GameProfileDto() {
    }

    public GameProfileDto(
        Set<String> preferredServers,
        String expLvl,
        String gamingHours,
        String currentRank
    ) {
        this.preferredServers = preferredServers;
        this.expLvl = expLvl;
        this.gamingHours = gamingHours;
        this.currentRank = currentRank;
    }

    // Existing getters/setters
    public Set<String> getPreferredServers() { return preferredServers; }
    public void setPreferredServers(Set<String> preferredServers) { this.preferredServers = preferredServers; }

    public String getExpLvl() { return expLvl; }
    public void setExpLvl(String expLvl) { this.expLvl = expLvl; }

    public String getGamingHours() { return gamingHours; }
    public void setGamingHours(String gamingHours) { this.gamingHours = gamingHours; }

    // NEW GETTERS/SETTERS


    public String getCurrentRank() { return currentRank; }
    public void setCurrentRank(String currentRank) { this.currentRank = currentRank; }
}