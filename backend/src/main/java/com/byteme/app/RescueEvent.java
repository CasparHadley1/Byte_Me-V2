package com.byteme.app;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rescue_event")
public class RescueEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private Instant collectedAt;

    private Integer mealsEstimate;
    private Integer co2eEstimateGrams;

	/// Getters
    public UUID getEventId() { return eventId; }
    public Employee getEmployee() { return employee; }
    public Reservation getReservation() { return reservation; }
    public Instant getCollectedAt() { return collectedAt; }
    public Integer getMealsEstimate() { return mealsEstimate; }
    public Integer getCo2eEstimateGrams() { return co2eEstimateGrams; }

    // Setters
    public void setEventId(UUID eventId) { this.eventId = eventId; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
    public void setCollectedAt(Instant collectedAt) { this.collectedAt = collectedAt; }
    public void setMealsEstimate(Integer mealsEstimate) { this.mealsEstimate = mealsEstimate; }
    public void setCo2eEstimateGrams(Integer co2eEstimateGrams) { this.co2eEstimateGrams = co2eEstimateGrams; }
}
