package com.matchme.dto;

import java.time.LocalDate;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDto {

    private Long id;

    private String displayName;

    private String aboutMe;

    private Map<String, GameProfileDto> games;

    private Integer preferredAgeMin;
    private Integer preferredAgeMax;
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

    private String competitiveness;
    private String voiceChatPreference;
    private String playSchedule;
    private String mainGoal;
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

    public String getCompetitiveness() { return competitiveness; }
    public void setCompetitiveness(String competitiveness) { this.competitiveness = competitiveness; }

    public String getVoiceChatPreference() { return voiceChatPreference; }
    public void setVoiceChatPreference(String voiceChatPreference) { this.voiceChatPreference = voiceChatPreference; }

    public String getPlaySchedule() { return playSchedule; }
    public void setPlaySchedule(String playSchedule) { this.playSchedule = playSchedule; }

    public String getMainGoal() { return mainGoal; }
    public void setMainGoal(String mainGoal) { this.mainGoal = mainGoal; }
    
}