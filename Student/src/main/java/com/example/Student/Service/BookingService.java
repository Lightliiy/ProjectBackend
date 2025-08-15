package com.example.Student.Service;

import com.example.Student.Model.Booking;
import com.example.Student.Model.BookingStatus;
import com.example.Student.Model.Head;
import com.example.Student.Model.Student;
import com.example.Student.Repository.BookingRepo;
import com.example.Student.Repository.HeadRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private HeadRepo headRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private StudentService studentService;

    // Create a new booking (PENDING by default)
    public Booking createBooking(Booking booking) {
        booking.setStatus(BookingStatus.PENDING);
        return bookingRepo.save(booking);
    }

    // Create booking and notify student
    public Booking createBookings(Booking booking) {
        Booking savedBooking = bookingRepo.save(booking);

        notificationService.createNotification(
                booking.getStudentId(),
                "Booking Confirmed",
                "Your session with counselor " + booking.getCounselorId() +
                        " is confirmed for " + booking.getScheduledDate(),
                "booking"
        );

        return savedBooking;
    }

    // Get all bookings with student names
    public List<Booking> getAllBookings() {
        List<Booking> bookings = bookingRepo.findAll();

        for (Booking booking : bookings) {
            studentService.findByStudentId(booking.getStudentId())
                    .ifPresentOrElse(
                            student -> booking.setStudentName(student.getName()),
                            () -> booking.setStudentName("N/A")
                    );
        }
        return bookings;
    }

    public Long countBookingsByCounselor(String counselorId) {
        return bookingRepo.countByCounselorId(counselorId);
    }

    public List<Booking> getBookingsByStudent(String studentId) {
        return bookingRepo.findByStudentId(studentId);
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepo.findById(id);
    }

    public List<Booking> getBookingsByCounselorId(String counselorId) {
        List<Booking> bookings = bookingRepo.findByCounselorId(counselorId);

        for (Booking booking : bookings) {
            Optional<Student> studentOptional = studentService.findByStudentId(booking.getStudentId());
            booking.setStudentName(studentOptional.map(Student::getName).orElse("Unknown Student"));
        }
        return bookings;
    }

    public Booking archiveBooking(Long id) {
        Booking booking = bookingRepo.findById(id).orElse(null);
        if (booking != null) {
            booking.setStatus(BookingStatus.ARCHIVED);
            return bookingRepo.save(booking);
        }
        return null;
    }

    public Booking updateBooking(Long id, Booking updatedBooking) {
        return bookingRepo.findById(id)
                .map(booking -> {
                    booking.setDescription(updatedBooking.getDescription());
                    booking.setStatus(updatedBooking.getStatus());
                    return bookingRepo.save(booking);
                })
                .orElse(null);
    }

    public void deleteBooking(Long bookingId) {
        bookingRepo.deleteById(bookingId);
    }

    public List<Booking> getBookingsEscalatedToAdmin() {
        List<Booking> bookings = bookingRepo.findByStatus(BookingStatus.ESCALATED_TO_ADMIN);
        for (Booking booking : bookings) {
            studentService.findByStudentId(booking.getStudentId())
                    .ifPresent(student -> booking.setStudentName(student.getName()));
        }
        return bookings;
    }

    public void reassignBooking(Long bookingId, Long counselorId) {
        bookingRepo.findById(bookingId)
                .map(booking -> {
                    booking.setCounselorId(String.valueOf(counselorId));
                    booking.setStatus(BookingStatus.PENDING);
                    return bookingRepo.save(booking);
                })
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));
    }

    public void closeBooking(Long bookingId) {
        bookingRepo.findById(bookingId)
                .map(booking -> {
                    booking.setStatus(BookingStatus.CLOSED);
                    return bookingRepo.save(booking);
                })
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));
    }

    public Booking approveBooking(Long id) {
        return bookingRepo.findById(id)
                .map(booking -> {
                    booking.setStatus(BookingStatus.APPROVE);
                    bookingRepo.save(booking);

                    notificationService.createNotification(
                            booking.getStudentId(),
                            "Booking Approved",
                            "Your booking for " + booking.getScheduledDate() + " at " + booking.getTimeSlot() + " has been approved.",
                            "booking"
                    );

                    return booking;
                })
                .orElse(null);
    }

    // Escalate booking to HOD
    public Booking escalateBookingToHOD(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(BookingStatus.ESCALATED_TO_HOD);
        bookingRepo.save(booking);

        Head hod = headRepo.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No HOD found"));

        notificationService.createNotification(
                String.valueOf(hod.getId()),
                "Booking Escalated",
                "A booking from student " + booking.getStudentId() + " has been escalated to you.",
                "ESCALATION"
        );

        return booking;
    }

    // Escalate booking to Admin
    public Booking escalateBookingToAdmin(Long bookingId, String adminUserId) {
        return bookingRepo.findById(bookingId)
                .map(booking -> {
                    booking.setStatus(BookingStatus.ESCALATED_TO_ADMIN);
                    bookingRepo.save(booking);

                    notificationService.createNotification(
                            adminUserId,
                            "Booking Escalated",
                            "Booking #" + booking.getId() + " has been escalated to Admin for review.",
                            "escalation"
                    );

                    return booking;
                })
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    // Cancel booking and notify student
    public Booking cancelBooking(Long id) {
        return bookingRepo.findById(id)
                .map(booking -> {
                    booking.setStatus(BookingStatus.CANCELLED);
                    Booking saved = bookingRepo.save(booking);

                    notificationService.createNotification(
                            booking.getStudentId(),
                            "Booking Cancelled",
                            "Your booking for " + booking.getScheduledDate() + " at " + booking.getTimeSlot() + " has been cancelled.",
                            "booking"
                    );

                    return saved;
                })
                .orElse(null);
    }
}
