package com.example.Student.Controller;

import com.example.Student.Model.Booking;
import com.example.Student.Model.BookingStatus;
import com.example.Student.Model.Head;
import com.example.Student.Repository.BookingRepo;
import com.example.Student.Service.CounselorService;
import com.example.Student.Service.HeadService;
import com.example.Student.Service.NotificationService;
import com.example.Student.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/hod")
public class HeadControler {

    @Autowired
    private HeadService headOfDepartmentService;

    @Autowired
    private BookingRepo bookingRepo;

    // The CaseService is no longer needed
    // @Autowired
    // private CaseService caseService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CounselorService counselorService;

    // ✅ Register new Head of Department
    @PostMapping("/register")
    public ResponseEntity<Head> registerHOD(@RequestBody Head head) {
        Head savedHod = headOfDepartmentService.saveHeadOfDepartment(head);
        return ResponseEntity.ok(savedHod);
    }

    // ✅ View all bookings (no longer cases)
    @GetMapping("/all-bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> allBookings = bookingRepo.findAll();
        return ResponseEntity.ok(allBookings);
    }

    // ✅ View escalated bookings (no longer cases)
    @GetMapping("/escalated-bookings")
    public ResponseEntity<List<Booking>> getEscalatedBookings() {
        List<Booking> escalatedBookings = bookingRepo.findByStatus(BookingStatus.ESCALATED_TO_HOD);
        return ResponseEntity.ok(escalatedBookings);
    }

    // ✅ View pending bookings (no longer cases)
    @GetMapping("/pending-bookings")
    public ResponseEntity<List<Booking>> getPendingBookings() {
        List<Booking> pendingBookings = bookingRepo.findByStatus(BookingStatus.PENDING);
        return ResponseEntity.ok(pendingBookings);
    }

    // ✅ Escalate booking to HOD
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

    // ✅ Escalate booking to Admin
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

    // ✅ Reassign a counselor to a booking
    @PostMapping("/reassign-counselor")
    public ResponseEntity<String> reassignCounselor(
            @RequestParam Long bookingId, // Change to bookingId
            @RequestParam Long counselorId
    ) {
        Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            // Assuming the booking object has a way to set counselorId
            booking.setCounselorId(String.valueOf(counselorId));
            bookingRepo.save(booking);
            return ResponseEntity.ok("Counselor reassigned successfully.");
        }
        return ResponseEntity.notFound().build();
    }

    // ✅ Get bookings escalated to Admin
    @GetMapping("/escalated-to-admin")
    public ResponseEntity<List<Booking>> getBookingsEscalatedToAdmin() {
        List<Booking> bookings = bookingRepo.findByStatus(BookingStatus.ESCALATED_TO_ADMIN);
        return ResponseEntity.ok(bookings);
    }

    // ✅ Get summary counts
    @GetMapping("/summary-counts")
    public ResponseEntity<Map<String, Long>> getSummaryCounts() {
        long totalStudents = studentService.countAllStudents();
        long totalCounselors = counselorService.countAllCounselors();
        long escalatedToAdminBookings = bookingRepo.countByStatus(BookingStatus.ESCALATED_TO_ADMIN);

        Map<String, Long> counts = new HashMap<>();
        counts.put("totalStudents", totalStudents);
        counts.put("totalCounselors", totalCounselors);
        counts.put("escalatedToAdminBookings", escalatedToAdminBookings);

        return ResponseEntity.ok(counts);
    }
}