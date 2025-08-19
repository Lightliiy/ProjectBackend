package com.example.Student.Controller;

import com.example.Student.Model.Counselor;
import com.example.Student.Model.StaffUser;
import com.example.Student.Model.Student;
import com.example.Student.Service.CounselorService;
import com.example.Student.Service.StaffService;
import com.example.Student.Service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthControler {

    @Autowired
    private UserServices userServices;

    @Autowired
    private CounselorService counselorService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StaffService staffService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Student student) {
        try {
            userServices.register(student);

            // Return only success message
            return ResponseEntity.ok(Map.of("", "Registration successful"));

        } catch (DataIntegrityViolationException e) {
            // Duplicate entry (email or studentId)
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Registration failed: Student with this email or ID already exists"));
        } catch (Exception e) {
            // Generic failure without details
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Registration failed"));
        }
    }

    @PostMapping("/register-counselor")
    public ResponseEntity<?> registerCounselor(@RequestBody Counselor counselor) {
        try {
            // Hash the password before saving
            String hashedPassword = passwordEncoder.encode(counselor.getPassword());
            counselor.setPassword(hashedPassword);

            Counselor savedCounselor = counselorService.addCounselor(counselor);

            // Return only success message and saved counselor info
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Counselor registered successfully",
                            "user", savedCounselor
                    ));

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Counselor with this email already exists"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Failed to register counselor"));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        System.out.println("Login attempt for email: " + email);

        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            return ResponseEntity.badRequest().body("Email and password are required.");
        }

        Counselor counselor = counselorService.authenticate(email, password);
        if (counselor != null) {
            System.out.println("Counselor login successful for " + email);
            return ResponseEntity.ok(Map.of(
                    "user", counselor,
                    "role", "COUNSELOR"
            ));
        } else {
            System.out.println("Counselor login failed for " + email);
        }

        StaffUser staffUser = staffService.authenticate(email, password);
        if (staffUser != null) {
            System.out.println("Staff login successful for " + email);
            String userRole = (staffUser.getRoles() != null && !staffUser.getRoles().isEmpty())
                    ? staffUser.getRoles().iterator().next()
                    : "";

            return ResponseEntity.ok(Map.of(
                    "user", staffUser,
                    "role", userRole
            ));
        } else {
            System.out.println("Staff login failed for " + email);
        }

        Student user = userServices.authenticate(email, password);
        if (user != null) {
            System.out.println("Student login successful for " + email);
            return ResponseEntity.ok(user);
        } else {
            System.out.println("Student login failed for " + email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Check your email or password"));
        }
    }


    @PostMapping("/register-staff")
    public ResponseEntity<?> registerStaff(@RequestBody StaffUser staffUser) {
        try {
            StaffUser createdUser = staffService.register(staffUser);
            return ResponseEntity.ok(Map.of("message", "Staff user registered successfully", "user", createdUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Failed to register staff user", "error", e.getMessage()));
        }
    }



}