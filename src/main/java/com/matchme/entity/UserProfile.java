// UserProfile.java (updated)
package com.matchme.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    private String displayName;

    private String aboutMe;
    
    // Gamer-specific biographical data points
    @ElementCollection
    @CollectionTable(name = "user_preferred_servers", joinColumns = @JoinColumn(name = "profile_id"))
    private Set<String> preferredServers = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_games", joinColumns = @JoinColumn(name = "profile_id"))
    private Set<String> games = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_gaming_hours", joinColumns = @JoinColumn(name = "profile_id"))
    private Set<String> gamingHours = new HashSet<>(); // e.g., ["18:00-22:00", "20:00-02:00"]

    private String rank; // e.g., "Gold", "Diamond", "Grand Champion"
    
    @NotNull
    private LocalDate birthDate;
    
    // Calculated field (not stored in DB)
    @Transient
    private Integer age;

    // Location information
    private String timezone;
    private String region; // e.g., "NA", "EU", "Asia"

    // What they're looking for
    private String lookingFor; // "Casual", "Competitive", "Both"
    private Integer preferredAgeMin;
    private Integer preferredAgeMax;

    private boolean profileCompleted = false;

    // Constructors
    public UserProfile() {}

    public UserProfile(User user, String displayName, LocalDate birthDate) {
        this.user = user;
        this.displayName = displayName;
        this.birthDate = birthDate;
    }

    // Calculate age from birthDate
    @PostLoad
    @PostPersist
    @PostUpdate
    public void calculateAge() {
        if (birthDate != null) {
            this.age = java.time.Period.between(birthDate, LocalDate.now()).getYears();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getAboutMe() { return aboutMe; }
    public void setAboutMe(String aboutMe) { this.aboutMe = aboutMe; }
    
    public Set<String> getPreferredServers() { return preferredServers; }
    public void setPreferredServers(Set<String> preferredServers) { this.preferredServers = preferredServers; }
    
    public Set<String> getGames() { return games; }
    public void setGames(Set<String> games) { this.games = games; }
    
    public Set<String> getGamingHours() { return gamingHours; }
    public void setGamingHours(Set<String> gamingHours) { this.gamingHours = gamingHours; }
    
    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }
    
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { 
        this.birthDate = birthDate; 
        calculateAge();
    }
    
    public Integer getAge() { 
        calculateAge();
        return age; 
    }
    
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public String getLookingFor() { return lookingFor; }
    public void setLookingFor(String lookingFor) { this.lookingFor = lookingFor; }
    
    public Integer getPreferredAgeMin() { return preferredAgeMin; }
    public void setPreferredAgeMin(Integer preferredAgeMin) { this.preferredAgeMin = preferredAgeMin; }
    
    public Integer getPreferredAgeMax() { return preferredAgeMax; }
    public void setPreferredAgeMax(Integer preferredAgeMax) { this.preferredAgeMax = preferredAgeMax; }
    
    public boolean isProfileCompleted() { return profileCompleted; }
    public void setProfileCompleted(boolean profileCompleted) { this.profileCompleted = profileCompleted; }
}