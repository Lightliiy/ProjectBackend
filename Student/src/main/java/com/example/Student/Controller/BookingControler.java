package com.example.Student.Controller;

import com.example.Student.Model.Booking;
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

        for (Booking booking : bookings) {
            if (booking.getAttachments() != null && !booking.getAttachments().isEmpty()) {
                List<String> urls = booking.getAttachments().stream()
                        .map(filename -> baseUrl + filename)
                        .toList();
                booking.setAttachmentUrls(urls);
            }
        }

        return ResponseEntity.ok(bookings);
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
        return ResponseEntity.ok(bookings);
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
