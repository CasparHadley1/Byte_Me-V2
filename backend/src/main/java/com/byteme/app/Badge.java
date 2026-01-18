package com.byteme.app;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "badge")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID badgeId;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    public Badge(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public UUID getBadgeId() {return badgeId;}
    public void setBadgeId(UUID badgeId) {this.badgeId = badgeId;}
    public String getCode() {return code;}
    public void setCode(String code) {this.code = code;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
}
