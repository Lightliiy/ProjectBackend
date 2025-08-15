package com.example.Student.Controller;

import com.example.Student.Model.Booking;
import com.example.Student.Model.BookingStatus;
import com.example.Student.Service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingControler {

    @Autowired
    private BookingService bookingService;

    private final Path uploadDir = Paths.get("E:/Student/uploads").toAbsolutePath().normalize();

    @PostMapping("/add")
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        try {
            Booking created = bookingService.createBooking(booking);
            return ResponseEntity.status(201).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating booking: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();

        String baseUrl = "http://localhost:8080/api/bookings/download/";

        List<Booking> activeBookings = bookings.stream()
                .filter(booking -> booking.getStatus() != BookingStatus.ARCHIVED)
                .collect(Collectors.toList());

        for (Booking booking : activeBookings) {
            if (booking.getAttachments() != null && !booking.getAttachments().isEmpty()) {
                List<String> urls = booking.getAttachments().stream()
                        .map(filename -> baseUrl + filename)
                        .toList();
                booking.setAttachmentUrls(urls);
            }
        }

        return ResponseEntity.ok(activeBookings);
    }

    @GetMapping("/student")
    public ResponseEntity<List<Booking>> getBookingsByStudent(@RequestParam String studentId) {
        List<Booking> bookings = bookingService.getBookingsByStudent(studentId);

        List<Booking> activeBookings = bookings.stream()
                .filter(booking -> booking.getStatus() != BookingStatus.ARCHIVED)
                .collect(Collectors.toList());

        if (activeBookings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(activeBookings);
    }

    @GetMapping("/counsel")
    public ResponseEntity<List<Booking>> getBookingsByCounselorId(@RequestParam String counselorId) {
        List<Booking> bookings = bookingService.getBookingsByCounselorId(counselorId);

        List<Booking> activeBookings = bookings.stream()
                .filter(booking -> booking.getStatus() != BookingStatus.ARCHIVED)
                .collect(Collectors.toList());

        return ResponseEntity.ok(activeBookings);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody Booking booking) {
        Booking updated = bookingService.updateBooking(id, booking);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<?> archiveBooking(@PathVariable Long id) {
        Booking booking = bookingService.archiveBooking(id);
        if (booking != null) {
            return ResponseEntity.ok(booking);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/counselor/{id}/count")
    public ResponseEntity<Map<String, Long>> getBookingCount(@PathVariable String id) {
        Long count = bookingService.countBookingsByCounselor(id);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveBooking(@PathVariable Long id) {
        Booking booking = bookingService.approveBooking(id);
        if (booking != null) {
            return ResponseEntity.ok(booking);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        Booking booking = bookingService.cancelBooking(id);
        if (booking != null) {
            return ResponseEntity.ok(booking);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // HOD escalation endpoint
    @PutMapping("/{id}/escalate/hod")
    public ResponseEntity<?> escalateBookingToHOD(@PathVariable Long id) {
        try {
            Booking booking = bookingService.escalateBookingToHOD(id);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Admin escalation endpoint
    @PutMapping("/{id}/escalate/admin")
    public ResponseEntity<?> escalateBookingToAdmin(@PathVariable Long id, @RequestParam String adminUserId) {
        try {
            Booking booking = bookingService.escalateBookingToAdmin(id, adminUserId);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<?> downloadAttachment(@PathVariable String filename) {
        try {
            Path file = uploadDir.resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(file);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Could not read file: " + filename);
        }
    }
}
