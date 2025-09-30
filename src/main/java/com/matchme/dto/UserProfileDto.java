// UserProfileDto.java (updated)
package com.matchme.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;


public class UserProfileDto {
    private Long id;
    private String displayName;
    private String aboutMe;
    private String profilePicture;
    
    // Gamer-specific fields below
    private Set<String> preferredServers;
    private Set<String> games;
    private Set<String> gamingHours;
    private String rank;
    
    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;
    private Integer age;

    private String timezone;
    private String region;
    private String lookingFor;
    private Integer preferredAgeMin;
    private Integer preferredAgeMax;
    private boolean profileCompleted;


    // Constructors
    public UserProfileDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    
    public String getProfilePicture() {return profilePicture;}
    public void setProfilePicture(String profilePicture) {this.profilePicture = profilePicture;}

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    
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