package com.matchme.dto;

import java.util.List;

public class RecommendationDto {
    private Long userId;
    private String displayName;
    private List<String> compatibleGames;

    public RecommendationDto() {}

    public RecommendationDto(Long userId, String displayName, List<String> compatibleGames) {
        this.userId = userId;
        this.displayName = displayName;
        this.compatibleGames = compatibleGames;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public List<String> getCompatibleGames() { return compatibleGames; }
    public void setCompatibleGames(List<String> compatibleGames) { this.compatibleGames = compatibleGames; }
}