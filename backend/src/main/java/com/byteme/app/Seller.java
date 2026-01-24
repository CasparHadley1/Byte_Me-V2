package com.byteme.app;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "seller")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID sellerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(nullable = false)
    private String name;

    private String locationText;
    private String openingHoursText;
    private String contactStub;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

	/// Getters
    public UUID getSellerId() { return sellerId; }
    public UserAccount getUser() { return user; }
    public String getName() { return name; }
    public String getLocationText() { return locationText; }
    public String getOpeningHoursText() { return openingHoursText; }
    public String getContactStub() { return contactStub; }
    public Instant getCreatedAt() { return createdAt; }

    // Setters
    public void setSellerId(UUID sellerId) { this.sellerId = sellerId; }
    public void setUser(UserAccount user) { this.user = user; }
    public void setName(String name) { this.name = name; }
    public void setLocationText(String locationText) { this.locationText = locationText; }
    public void setOpeningHoursText(String openingHoursText) { this.openingHoursText = openingHoursText; }
    public void setContactStub(String contactStub) { this.contactStub = contactStub; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
