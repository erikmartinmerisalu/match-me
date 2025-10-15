package com.matchme.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameProfile> games = new ArrayList<>();

    @Column(nullable = true)
    private LocalDate birthDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Lob
    @Column(name = "profile_pic", columnDefinition = "TEXT")
    private String profilePic;

    private String displayName;
    private String timezone;
    private String aboutMe;
    private String lookingFor;
    private Integer preferredAgeMin;
    private Integer preferredAgeMax;
    private Integer maxPreferredDistance;
    private Double latitude;
    private Double longitude;
    private String location;

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

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getAboutMe() { return aboutMe; }
    public void setAboutMe(String aboutMe) { this.aboutMe = aboutMe; }

    public String getLookingFor() { return lookingFor; }
    public void setLookingFor(String lookingFor) { this.lookingFor = lookingFor; }

    public Integer getPreferredAgeMin() { return preferredAgeMin; }
    public void setPreferredAgeMin(Integer preferredAgeMin) { this.preferredAgeMin = preferredAgeMin; }

    public Integer getPreferredAgeMax() { return preferredAgeMax; }
    public void setPreferredAgeMax(Integer preferredAgeMax) { this.preferredAgeMax = preferredAgeMax; }

    public boolean isProfileCompleted() { return profileCompleted; }
    public void setProfileCompleted(boolean profileCompleted) { this.profileCompleted = profileCompleted; }

    public List<GameProfile> getGames() { return games; }
    public void setGames(List<GameProfile> games) { this.games = games; }

    public Integer getMaxPreferredDistance() {return maxPreferredDistance; }
    public void setMaxPreferredDistance( Integer maxPreferredDistanxe) {this.maxPreferredDistance = maxPreferredDistanxe ;}

    public String getProfilePic() {return profilePic; }
    public void setProfilePic( String profilePic) {this.profilePic = profilePic ;}

    public Double getLatitude() {return latitude; }
    public void setLatitude( Double latitude) {this.latitude = latitude ;}

    public Double getLongitude() {return longitude; }
    public void setLongitude( Double longitude) {this.longitude = longitude ;}

    public String getLocation() {return location; }
    public void setLocation( String location) {this.location = location ;}

    // Calculate age from birthDate
    public Integer getAge() {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

}