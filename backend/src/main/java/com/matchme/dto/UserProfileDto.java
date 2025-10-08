
package com.matchme.dto;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public class UserProfileDto {

    private Long id;

    @NotBlank
    private String displayName;

    private String aboutMe;


    @NotNull
    private Map<String, GameProfileDto> games; // up to 3 games

    @NotNull
    private Integer preferredAgeMin;
    @NotNull
    private Integer preferredAgeMax;
    @NotNull
    private Integer maxPreferredDistance;

    
    private LocalDate birthDate;
    private Integer age;
    private String timezone;
    private String lookingFor;
    private boolean profileCompleted;

    private String profilePic;
    private Double latitude;
    private Double longitude;
    private String location;

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getAboutMe() { return aboutMe; }
    public void setAboutMe(String aboutMe) { this.aboutMe = aboutMe; }


    public Map<String, GameProfileDto> getGames() { return games; }
    public void setGames(Map<String, GameProfileDto> games) { this.games = games; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getLookingFor() { return lookingFor; }
    public void setLookingFor(String lookingFor) { this.lookingFor = lookingFor; }

    public Integer getPreferredAgeMin() { return preferredAgeMin; }
    public void setPreferredAgeMin(Integer preferredAgeMin) { this.preferredAgeMin = preferredAgeMin; }

    public Integer getPreferredAgeMax() { return preferredAgeMax; }
    public void setPreferredAgeMax(Integer preferredAgeMax) { this.preferredAgeMax = preferredAgeMax; }

    public Integer getMaxPreferredDistance() {return maxPreferredDistance; }
    public void setMaxPreferredDistance(Integer maxPreferredDistance) {this.maxPreferredDistance = maxPreferredDistance; }

    public boolean isProfileCompleted() { return profileCompleted; }
    public void setProfileCompleted(boolean profileCompleted) { this.profileCompleted = profileCompleted; }
    
    public String getProfilePic() { return profilePic; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
