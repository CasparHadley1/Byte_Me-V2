package com.byteme.app;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
public class GamificationController {

    private final EmployeeRepository employeeRepo;
    private final RescueEventRepository rescueEventRepo;
    private final BadgeRepository badgeRepo;
    private final EmployeeBadgeRepository employeeBadgeRepo;

	/// Getters
    public EmployeeRepository getEmployeeRepo() { return employeeRepo; }
    public RescueEventRepository getRescueEventRepo() { return rescueEventRepo; }
    public BadgeRepository getBadgeRepo() { return badgeRepo; }
    public EmployeeBadgeRepository getEmployeeBadgeRepo() { return employeeBadgeRepo; }

    @GetMapping("/streak/{employeeId}")
    public ResponseEntity<?> getStreak(@PathVariable UUID employeeId) {
        var employee = employeeRepo.findById(employeeId).orElse(null);
        if (employee == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(new StreakResponse(
                employee.getCurrentStreakWeeks(),
                employee.getBestStreakWeeks(),
                employee.getLastRescueWeekStart()
        ));
    }

    @GetMapping("/impact/{employeeId}")
    public ResponseEntity<?> getImpact(@PathVariable UUID employeeId) {
        var employee = employeeRepo.findById(employeeId).orElse(null);
        if (employee == null) return ResponseEntity.notFound().build();

        long totalRescues = rescueEventRepo.countByEmployee_EmployeeId(employeeId);
        long totalMeals = rescueEventRepo.sumMealsByEmployee(employeeId);
        long totalCo2eGrams = rescueEventRepo.sumCo2eByEmployee(employeeId);
        int badgeCount = employeeBadgeRepo.findByEmployeeId(employeeId).size();

        return ResponseEntity.ok(new ImpactResponse(
                (int) totalRescues,
                (int) totalMeals,
                totalCo2eGrams / 1000.0, // Convert to kg
                employee.getCurrentStreakWeeks(),
                badgeCount
        ));
    }

    @GetMapping("/badges/{employeeId}")
    public List<EmployeeBadge> getEmployeeBadges(@PathVariable UUID employeeId) {
        return employeeBadgeRepo.findByEmployeeId(employeeId);
    }

    @GetMapping("/badges")
    public List<Badge> getAllBadges() {
        return badgeRepo.findAll();
    }

    // DTOs
    public static class StreakResponse {
        int currentStreakWeeks;
        int bestStreakWeeks;
        java.time.LocalDate lastRescueWeekStart;

		/// Getters
        public int getCurrentStreakWeeks() { return currentStreakWeeks; }
        public int getBestStreakWeeks() { return bestStreakWeeks; }
        public java.time.LocalDate getLastRescueWeekStart() { return lastRescueWeekStart; }

        // Setters
        public void setCurrentStreakWeeks(int currentStreakWeeks) { this.currentStreakWeeks = currentStreakWeeks; }
        public void setBestStreakWeeks(int bestStreakWeeks) { this.bestStreakWeeks = bestStreakWeeks; }
        public void setLastRescueWeekStart(java.time.LocalDate lastRescueWeekStart) { this.lastRescueWeekStart = lastRescueWeekStart; }
    }

    public static class ImpactResponse {
        int totalRescues;
        int totalMealsSaved;
        double totalCo2eSavedKg;
        int currentStreakWeeks;
        int badgesEarned;

		/// Getters
        public int getTotalRescues() { return totalRescues; }
        public int getTotalMealsSaved() { return totalMealsSaved; }
        public double getTotalCo2eSavedKg() { return totalCo2eSavedKg; }
        public int getCurrentStreakWeeks() { return currentStreakWeeks; }
        public int getBadgesEarned() { return badgesEarned; }

        // Setters
        public void setTotalRescues(int totalRescues) { this.totalRescues = totalRescues; }
        public void setTotalMealsSaved(int totalMealsSaved) { this.totalMealsSaved = totalMealsSaved; }
        public void setTotalCo2eSavedKg(double totalCo2eSavedKg) { this.totalCo2eSavedKg = totalCo2eSavedKg; }
        public void setCurrentStreakWeeks(int currentStreakWeeks) { this.currentStreakWeeks = currentStreakWeeks; }
        public void setBadgesEarned(int badgesEarned) { this.badgesEarned = badgesEarned; }
    }
}
