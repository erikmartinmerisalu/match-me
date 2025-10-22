package com.matchme.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_games")
public class GameProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private UserProfile userProfile;

    private String gameName;
    private String expLvl;
    private String gamingHours;
    
    @Column(name = "server")
    private String preferredServers;

    private String competitiveness;      
    private String voiceChatPreference;  
    private String playSchedule;         
    private String mainGoal;             
    private String currentRank;          

    public Set<String> getPreferredServersSet() {
        if (preferredServers == null || preferredServers.isEmpty()) return new HashSet<>();
        return new HashSet<>(Arrays.asList(preferredServers.split(",")));
    }

    public void setPreferredServersSet(Set<String> servers) {
        this.preferredServers = String.join(",", servers);
    }

    // Existing getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserProfile getUserProfile() { return userProfile; }
    public void setUserProfile(UserProfile userProfile) { this.userProfile = userProfile; }

    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }

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