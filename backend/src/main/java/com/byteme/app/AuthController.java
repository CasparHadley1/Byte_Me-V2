package com.byteme.app;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAccountRepository userRepo;
    private final SellerRepository sellerRepo;
    private final EmployeeRepository employeeRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepo.existsByEmail(req.email)) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        var user = UserAccount.builder()
                .email(req.email)
                .passwordHash(passwordEncoder.encode(req.password))
                .role(req.role)
                .build();
        userRepo.save(user);

        // Create seller profile if SELLER role
        if (req.role == UserAccount.Role.SELLER && req.businessName != null) {
            var seller = Seller.builder()
                    .user(user)
                    .name(req.businessName)
                    .locationText(req.location)
                    .build();
            sellerRepo.save(seller);
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getEmail(), user.getRole());
        return ResponseEntity.ok(new AuthResponse(token, user.getUserId(), user.getEmail(), user.getRole()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        var user = userRepo.findByEmail(req.email)
                .orElse(null);
        
        if (user == null || !passwordEncoder.matches(req.password, user.getPasswordHash())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getEmail(), user.getRole());
        return ResponseEntity.ok(new AuthResponse(token, user.getUserId(), user.getEmail(), user.getRole()));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepo.findById(userId).orElseThrow();
        return ResponseEntity.ok(new AuthResponse(null, user.getUserId(), user.getEmail(), user.getRole()));
    }

    // DTOs
    public static class RegisterRequest {
        String email;
        String password;
        UserAccount.Role role;
        String businessName; // for sellers
        String location;     // for sellers
		
		/// Getters
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public UserAccount.Role getRole() { return role; }
        public String getBusinessName() { return businessName; }
        public String getLocation() { return location; }

        // Setters
        public void setEmail(String email) { this.email = email; }
        public void setPassword(String password) { this.password = password; }
        public void setRole(UserAccount.Role role) { this.role = role; }
        public void setBusinessName(String businessName) { this.businessName = businessName; }
        public void setLocation(String location) { this.location = location; }
    }

    public static class LoginRequest {
        String email;
        String password;
		
		/// Getters
        public String getEmail() { return email; }
        public String getPassword() { return password; }

        // Setters
        public void setEmail(String email) { this.email = email; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        String token;
        UUID userId;
        String email;
        UserAccount.Role role;

		/// Getters
        public String getToken() { return token; }
        public UUID getUserId() { return userId; }
        public String getEmail() { return email; }
        public UserAccount.Role getRole() { return role; }

        // Setters
        public void setToken(String token) { this.token = token; }
        public void setUserId(UUID userId) { this.userId = userId; }
        public void setEmail(String email) { this.email = email; }
        public void setRole(UserAccount.Role role) { this.role = role; }
    }
}
