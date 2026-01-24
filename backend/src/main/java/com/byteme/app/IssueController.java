package com.byteme.app;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueReportRepository issueRepo;
    private final BundlePostingRepository bundleRepo;
    private final ReservationRepository reservationRepo;
    private final EmployeeRepository employeeRepo;

	/// Getters
    public IssueReportRepository getIssueRepo() { 
        return issueRepo; 
    }

    public BundlePostingRepository getBundleRepo() { 
        return bundleRepo; 
    }

    public ReservationRepository getReservationRepo() { 
        return reservationRepo; 
    }

    public EmployeeRepository getEmployeeRepo() { 
        return employeeRepo; 
    }

    @GetMapping("/seller/{sellerId}")
    public List<IssueReport> getBySeller(@PathVariable UUID sellerId) {
        return issueRepo.findBySeller(sellerId);
    }

    @GetMapping("/seller/{sellerId}/open")
    public List<IssueReport> getOpenBySeller(@PathVariable UUID sellerId) {
        return issueRepo.findOpenBySeller(sellerId);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateIssueRequest req) {
        var issue = IssueReport.builder()
                .posting(req.postingId != null ? bundleRepo.findById(req.postingId).orElse(null) : null)
                .reservation(req.reservationId != null ? reservationRepo.findById(req.reservationId).orElse(null) : null)
                .employee(req.employeeId != null ? employeeRepo.findById(req.employeeId).orElse(null) : null)
                .type(req.type)
                .description(req.description)
                .build();

        return ResponseEntity.ok(issueRepo.save(issue));
    }

    @PostMapping("/{id}/respond")
    public ResponseEntity<?> respond(@PathVariable UUID id, @RequestBody RespondRequest req) {
        var issue = issueRepo.findById(id).orElse(null);
        if (issue == null) return ResponseEntity.notFound().build();

        issue.setSellerResponse(req.response);
        issue.setStatus(IssueReport.Status.RESPONDED);
        
        if (req.resolve) {
            issue.setStatus(IssueReport.Status.RESOLVED);
            issue.setResolvedAt(Instant.now());
        }

        return ResponseEntity.ok(issueRepo.save(issue));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<?> resolve(@PathVariable UUID id) {
        var issue = issueRepo.findById(id).orElse(null);
        if (issue == null) return ResponseEntity.notFound().build();

        issue.setStatus(IssueReport.Status.RESOLVED);
        issue.setResolvedAt(Instant.now());

        return ResponseEntity.ok(issueRepo.save(issue));
    }

    // DTOs
    
    public static class CreateIssueRequest {
        UUID postingId;
        UUID reservationId;
        UUID employeeId;
        IssueReport.Type type;
        String description;

		/// Getters
        public UUID getPostingId() { return postingId; }
        public UUID getReservationId() { return reservationId; }
        public UUID getEmployeeId() { return employeeId; }
        public IssueReport.Type getType() { return type; }
        public String getDescription() { return description; }

        // Setters
        public void setPostingId(UUID postingId) { this.postingId = postingId; }
        public void setReservationId(UUID reservationId) { this.reservationId = reservationId; }
        public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }
        public void setType(IssueReport.Type type) { this.type = type; }
        public void setDescription(String description) { this.description = description; }
    }

    
    public static class RespondRequest {
        String response;
        boolean resolve;

		// Getters
        public String getResponse() { return response; }
        public boolean isResolve() { return resolve; }

        // Setters
        public void setResponse(String response) { this.response = response; }
        public void setResolve(boolean resolve) { this.resolve = resolve; }
    }
}
