package com.matchme.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ElementCollection
    @CollectionTable(
        name = "recommendation_list",
        joinColumns = @JoinColumn(name = "recommendation_id")
    )
    @Column(name = "recommended_users_id")
    private List<Long> recommendedUserIds;

    public Recommendation() {}

    public Recommendation(Long userId, List<Long> recommendedUserIds) {
        this.userId = userId;
        this.recommendedUserIds = recommendedUserIds;
    }

    public Long getId() { return id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public List<Long> getRecommendedUserIds() { return recommendedUserIds; }
    public void setRecommendedUserIds(List<Long> recommendedUserIds) { 
        this.recommendedUserIds = recommendedUserIds; 
    }
}
