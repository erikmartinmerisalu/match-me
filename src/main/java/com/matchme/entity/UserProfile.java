package com.matchme.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String displayName;
    private LocalDate birthDate;
    private Integer age;
    private String timezone;
    private String region;
    private String aboutMe;
    private String lookingFor;
    private Integer preferredAgeMin;
    private Integer preferredAgeMax;

    // Core matching fields
    private String expLvl;       // Beginner, Intermediate, Advanced
    private String gamingHours;  // Single string now
    private String rank;         // Optional, not top 5 for matching

    @ElementCollection
    @CollectionTable(name = "user_preferred_servers", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "server")
    private Set<String> preferredServers;

    @ElementCollection
    @CollectionTable(name = "user_games", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "game")
    private Set<String> games;

    private boolean profileCompleted = false;

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getAboutMe() { return aboutMe; }
    public void setAboutMe(String aboutMe) { this.aboutMe = aboutMe; }

    public String getLookingFor() { return lookingFor; }
    public void setLookingFor(String lookingFor) { this.lookingFor = lookingFor; }

    public Integer getPreferredAgeMin() { return preferredAgeMin; }
    public void setPreferredAgeMin(Integer preferredAgeMin) { this.preferredAgeMin = preferredAgeMin; }

    public Integer getPreferredAgeMax() { return preferredAgeMax; }
    public void setPreferredAgeMax(Integer preferredAgeMax) { this.preferredAgeMax = preferredAgeMax; }

    public String getExpLvl() { return expLvl; }
    public void setExpLvl(String expLvl) { this.expLvl = expLvl; }

    public String getGamingHours() { return gamingHours; }
    public void setGamingHours(String gamingHours) { this.gamingHours = gamingHours; }

    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }

    public Set<String> getPreferredServers() { return preferredServers; }
    public void setPreferredServers(Set<String> preferredServers) { this.preferredServers = preferredServers; }

    public Set<String> getGames() { return games; }
    public void setGames(Set<String> games) { this.games = games; }

    public boolean isProfileCompleted() { return profileCompleted; }
    public void setProfileCompleted(boolean profileCompleted) { this.profileCompleted = profileCompleted; }
}
