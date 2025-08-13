package com.example.Student.Controller;

import com.example.Student.Model.Booking;
import com.example.Student.Model.BookingStatus;
import com.example.Student.Model.Head;
import com.example.Student.Model.StaffUser;
import com.example.Student.Repository.StaffRepo;
import com.example.Student.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.Student.Repository.BookingRepo;
import com.example.Student.Repository.CounselorRepo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hod")
public class HeadControler {

    @Autowired
    private StaffRepo staffRepo;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CounselorService counselorService;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private CounselorRepo counselorRepo;

    // View all bookings with student names populated
    @GetMapping("/all-bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/pending-bookings")
    public ResponseEntity<List<Booking>> getPendingBookings() {
        List<Booking> allBookings = bookingService.getAllBookings();
        List<Booking> pendingBookings = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pendingBookings);
    }

    @GetMapping("/escalated-bookings")
    public ResponseEntity<List<Booking>> getEscalatedBookings() {
        List<Booking> allBookings = bookingService.getAllBookings();
        List<Booking> escalatedBookings = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.ESCALATED_TO_HOD || b.getStatus() == BookingStatus.ESCALATED_TO_ADMIN)
                .collect(Collectors.toList());
        return ResponseEntity.ok(escalatedBookings);
    }

    @PostMapping("/escalate-to-hod/{bookingId}")
    public ResponseEntity<Booking> escalateToHOD(@PathVariable Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus(BookingStatus.ESCALATED_TO_HOD);
            Booking savedBooking = bookingRepo.save(booking);
            return ResponseEntity.ok(savedBooking);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/escalate-to-admin/{bookingId}")
    public ResponseEntity<Booking> escalateToAdmin(
            @PathVariable Long bookingId,
            @RequestParam String hodComment) {

        Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus(BookingStatus.ESCALATED_TO_ADMIN);
            booking.setHodComment(hodComment);
            return ResponseEntity.ok(bookingRepo.save(booking));
        }
        return ResponseEntity.notFound().build();
    }

    // New, correct endpoint for reassigning a counselor
    @PostMapping("/reassign-counselor")
    public ResponseEntity<String> reassignCounselor(
            @RequestParam Long caseId,
            @RequestParam Long counselorId
    ) {
        try {
            bookingService.reassignBooking(caseId, counselorId); // Use a new service method
            return ResponseEntity.ok("Counselor reassigned successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // New endpoint to close a case
    @PostMapping("/close-case")
    public ResponseEntity<String> closeCase(@RequestParam Long caseId) {
        try {
            bookingService.closeBooking(caseId); // Use a new service method
            return ResponseEntity.ok("Case closed successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get bookings escalated to Admin
    @GetMapping("/escalated-to-admin")
    public ResponseEntity<List<Booking>> getBookingsEscalatedToAdmin() {
        List<Booking> bookings = bookingService.getBookingsEscalatedToAdmin();
        return ResponseEntity.ok(bookings);
    }

    // Get summary counts
    @GetMapping("/summary-counts")
    public ResponseEntity<Map<String, Long>> getSummaryCounts() {
        long totalStudents = studentService.countAllStudents();
        long totalCounselors = counselorService.countAllCounselors();
        long escalatedToAdminBookings = bookingRepo.countByStatus(BookingStatus.ESCALATED_TO_ADMIN);
        long pendingBookings = bookingRepo.countByStatus(BookingStatus.PENDING);

        Map<String, Long> counts = new HashMap<>();
        counts.put("totalStudents", totalStudents);
        counts.put("totalCounselors", totalCounselors);
        counts.put("pendingBookings", pendingBookings);
        counts.put("escalatedToAdminBookings", escalatedToAdminBookings);

        return ResponseEntity.ok(counts);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName(); // The logged-in user's email (usually username)

        Optional<StaffUser> userOpt = staffRepo.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        StaffUser user = userOpt.get();

        Map<String, Object> profile = new HashMap<>();
        profile.put("userId", user.getId());
        profile.put("name", user.getName());
        profile.put("email", user.getEmail());
        profile.put("employeeId", user.getEmployeeId());
        profile.put("roles", user.getRoles());

        return ResponseEntity.ok(profile);
    }

    @PutMapping("/update-profile/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody StaffUser updatedUser) {
        Optional<StaffUser> userOpt = staffRepo.findById(id);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        StaffUser user = userOpt.get();
        user.setName(updatedUser.getName());
        user.setProfileImage(updatedUser.getProfileImage());

        StaffUser savedUser = staffRepo.save(user);

        return ResponseEntity.ok(savedUser);
    }

}
