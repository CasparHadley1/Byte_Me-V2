package com.byteme.app;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bundle_posting")
public class BundlePosting {

    public enum Status { DRAFT, ACTIVE, CLOSED, CANCELLED }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID postingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private Instant pickupStartAt;

    @Column(nullable = false)
    private Instant pickupEndAt;

    @Column(nullable = false)
    private Integer quantityTotal;

    @Column(nullable = false)
    private Integer quantityReserved = 0;

    @Column(nullable = false)
    private Integer priceCents;

    @Column(nullable = false)
    private Integer discountPct;

    @Column(columnDefinition = "TEXT")
    private String contentsText;

    private String allergensText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.DRAFT;

    private Integer estimatedWeightGrams;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // Helper methods
    public boolean canReserve(int qty) {
        return status == Status.ACTIVE && (quantityReserved + qty) <= quantityTotal;
    }

    public int getAvailable() {
        return quantityTotal - quantityReserved;
    }

	/// Getters
    public UUID getPostingId() { return postingId; }
    public Seller getSeller() { return seller; }
    public Category getCategory() { return category; }
    public Instant getPickupStartAt() { return pickupStartAt; }
    public Instant getPickupEndAt() { return pickupEndAt; }
    public Integer getQuantityTotal() { return quantityTotal; }
    public Integer getQuantityReserved() { return quantityReserved; }
    public Integer getPriceCents() { return priceCents; }
    public Integer getDiscountPct() { return discountPct; }
    public String getContentsText() { return contentsText; }
    public String getAllergensText() { return allergensText; }
    public Status getStatus() { return status; }
    public Integer getEstimatedWeightGrams() { return estimatedWeightGrams; }
    public Instant getCreatedAt() { return createdAt; }

    // Setters
    public void setPostingId(UUID postingId) { this.postingId = postingId; }
    public void setSeller(Seller seller) { this.seller = seller; }
    public void setCategory(Category category) { this.category = category; }
    public void setPickupStartAt(Instant pickupStartAt) { this.pickupStartAt = pickupStartAt; }
    public void setPickupEndAt(Instant pickupEndAt) { this.pickupEndAt = pickupEndAt; }
    public void setQuantityTotal(Integer quantityTotal) { this.quantityTotal = quantityTotal; }
    public void setQuantityReserved(Integer quantityReserved) { this.quantityReserved = quantityReserved; }
    public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }
    public void setDiscountPct(Integer discountPct) { this.discountPct = discountPct; }
    public void setContentsText(String contentsText) { this.contentsText = contentsText; }
    public void setAllergensText(String allergensText) { this.allergensText = allergensText; }
    public void setStatus(Status status) { this.status = status; }
    public void setEstimatedWeightGrams(Integer estimatedWeightGrams) { this.estimatedWeightGrams = estimatedWeightGrams; }
}
