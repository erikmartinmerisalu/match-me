package com.matchme.dto;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import com.matchme.entity.UserProfile;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class GameProfileDto {
    private Set<String> preferredServers;
    private String gamingHours;
    private String rank;

    

    // getterid ja setterid
    public Set<String> getPreferredServers() { return preferredServers; }
    public void setPreferredServers(Set<String> preferredServers) { this.preferredServers = preferredServers; }

    public String getGamingHours() { return gamingHours; }
    public void setGamingHours(String gamingHours) { this.gamingHours = gamingHours; }

    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }
}
