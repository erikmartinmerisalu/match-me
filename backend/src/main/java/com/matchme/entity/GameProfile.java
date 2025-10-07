package com.matchme.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    private String gameName;
    private String expLvl;
    private String gamingHours;

    // @ElementCollection
    // @CollectionTable(name = "user_preferred_servers", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "server")
    private String preferredServers;

    public Set<String> getPreferredServersSet() {
        if (preferredServers == null || preferredServers.isEmpty()) return new HashSet<>();
        return new HashSet<>(Arrays.asList(preferredServers.split(",")));
    }

    public void setPreferredServersSet(Set<String> servers) {
        this.preferredServers = String.join(",", servers);
    }

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

}
