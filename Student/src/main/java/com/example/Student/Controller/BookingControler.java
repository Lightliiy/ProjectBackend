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
            // The logic to create a separate Case has been removed.
            // All booking status management is now handled directly by the BookingService.
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

        // Filter out archived bookings before sending to the client
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
        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/counsel")
    public ResponseEntity<List<Booking>> getBookingsByCounselorId(@RequestParam String counselorId) {
        List<Booking> bookings = bookingService.getBookingsByCounselorId(counselorId);

        // Filter out archived bookings before sending to the client
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


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }



    @PutMapping("/{id}/archive")
    public ResponseEntity<?> archiveBooking(@PathVariable Long id) {
        Booking booking = bookingService.archiveBooking(id); // You must add this method to your service
        if (booking != null) {
            return ResponseEntity.ok(booking);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/counselor/{id}/count")
    public ResponseEntity<Map<String, Integer>> getBookingCount(@PathVariable String id) {
        int count = bookingService.countBookingsByCounselor(id);
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