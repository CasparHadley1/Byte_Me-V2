package com.byteme.app;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "organisation")
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orgId;

    @Column(nullable = false)
    private String name;

    private String locationText;
    private String billingStub;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();


    /// Getters
    public UUID getOrgId() { return orgId; }
    public String getName() { return name; }
    public String getLocationText() { return locationText; }
    public String getBillingStub() { return billingStub; }
    public Instant getCreatedAt() { return createdAt; }

    // Setters
    public void setOrgId(UUID orgId) { this.orgId = orgId; }
    public void setName(String name) { this.name = name; }
    public void setLocationText(String locationText) { this.locationText = locationText; }
    public void setBillingStub(String billingStub) { this.billingStub = billingStub; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
