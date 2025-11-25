package com.matchme.entity;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "recommendations", uniqueConstraints = @UniqueConstraint(columnNames = "userId"))
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    
    @Column(name = "recommended_user_ids", columnDefinition = "TEXT")
    private String recommendedUserIdsJson; // Salvestame JSON-ina

    @Transient
    private List<Long> recommendedUserIds; // Kasutamiseks Java koodis

    private static final ObjectMapper mapper = new ObjectMapper();

    public Recommendation() {}

    public Recommendation(Long userId, List<Long> recommendedUserIds) {
        this.userId = userId;
        setRecommendedUserIds(recommendedUserIds);
    }

    public Long getId() { return id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public List<Long> getRecommendedUserIds() {
        if (recommendedUserIds == null && recommendedUserIdsJson != null) {
            try {
                recommendedUserIds = mapper.readValue(recommendedUserIdsJson, new TypeReference<List<Long>>() {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return recommendedUserIds;
    }

    public void setRecommendedUserIds(List<Long> recommendedUserIds) {
        this.recommendedUserIds = recommendedUserIds;
        try {
            this.recommendedUserIdsJson = mapper.writeValueAsString(recommendedUserIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
