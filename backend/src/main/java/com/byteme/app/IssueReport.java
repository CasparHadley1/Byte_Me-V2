package com.byteme.app;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "issue_report")
public class IssueReport {

    public enum Type { UNAVAILABLE, QUALITY, OTHER }
    public enum Status { OPEN, RESPONDED, RESOLVED }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID issueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posting_id")
    private BundlePosting posting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN;

    @Column(columnDefinition = "TEXT")
    private String sellerResponse;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant resolvedAt;

	/// Getters
    public UUID getIssueId() { return issueId; }
    public BundlePosting getPosting() { return posting; }
    public Reservation getReservation() { return reservation; }
    public Employee getEmployee() { return employee; }
    public Type getType() { return type; }
    public String getDescription() { return description; }
    public Status getStatus() { return status; }
    public String getSellerResponse() { return sellerResponse; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getResolvedAt() { return resolvedAt; }

    // Setters
    public void setIssueId(UUID issueId) { this.issueId = issueId; }
    public void setPosting(BundlePosting posting) { this.posting = posting; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public void setType(Type type) { this.type = type; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(Status status) { this.status = status; }
    public void setSellerResponse(String sellerResponse) { this.sellerResponse = sellerResponse; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
}
