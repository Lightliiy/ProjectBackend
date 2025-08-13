package com.example.Student.Service;

import com.example.Student.Model.Booking;
import com.example.Student.Model.BookingStatus;
import com.example.Student.Model.Student;
import com.example.Student.Repository.BookingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private StudentService studentService;

    public Booking createBooking(Booking booking) {
        booking.setStatus(BookingStatus.PENDING);
        return bookingRepo.save(booking);
    }

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

    public int countBookingsByCounselor(String counselorId) {
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
            if (studentOptional.isPresent()) {
                Student student = studentOptional.get();
                booking.setStudentName(student.getName());
            } else {
                booking.setStudentName("Unknown Student");
            }
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
        Optional<Booking> optionalBooking = bookingRepo.findById(id);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            // Update fields accordingly, example:
            booking.setDescription(updatedBooking.getDescription());
            booking.setStatus(updatedBooking.getStatus());
            // Update others as needed
            return bookingRepo.save(booking);
        }
        return null;
    }

    // Updated deleteBooking method: no longer references CaseRepo
    public void deleteBooking(Long bookingId) {
        bookingRepo.deleteById(bookingId);
    }

    public Booking createBookings(Booking booking) {
        Booking savedBooking = bookingRepo.save(booking);

        notificationService.createNotification(
                booking.getStudentId(),
                "Booking Confirmed",
                "Your session with counselor " + booking.getCounselorId() + " is confirmed for " + booking.getScheduledDate(),
                "booking"
        );

        return savedBooking;
    }


    public List<Booking> getBookingsEscalatedToAdmin() {
        List<Booking> bookings = bookingRepo.findByStatus(BookingStatus.ESCALATED_TO_ADMIN);
        // Ensure student and counselor data is populated
        for (Booking booking : bookings) {
            studentService.findByStudentId(booking.getStudentId())
                    .ifPresent(student -> {
                        booking.setStudentName(student.getName());
                    });
            // You should also populate the counselor name here if needed
        }
        return bookings;
    }

    // New method for reassigning a counselor
    public void reassignBooking(Long bookingId, Long counselorId) {
        Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setCounselorId(String.valueOf(counselorId));
            booking.setStatus(BookingStatus.PENDING); // Or OPEN, as per your business logic
            bookingRepo.save(booking);
        } else {
            throw new IllegalArgumentException("Booking not found with ID: " + bookingId);
        }
    }

    // New method for closing a case
    public void closeBooking(Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus(BookingStatus.CLOSED);
            bookingRepo.save(booking);
        } else {
            throw new IllegalArgumentException("Booking not found with ID: " + bookingId);
        }
    }

    public Booking approveBooking(Long id) {
        Optional<Booking> optionalBooking = bookingRepo.findById(id);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus(BookingStatus.APPROVE);
            bookingRepo.save(booking);

            // ✅ Send a notification to the student
            notificationService.createNotification(
                    booking.getStudentId(),
                    "Booking Approved",
                    "Your booking for " + booking.getScheduledDate() + " at " + booking.getTimeSlot() + " has been approved.",
                    "booking"
            );

            return booking;
        }
        return null;
    }

    public Booking escalateBookingToHOD(Long bookingId, String hodUserId) {
        Optional<Booking> optionalBooking = bookingRepo.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus(BookingStatus.ESCALATED_TO_HOD);
            bookingRepo.save(booking);

            // Create notification for HOD about the escalation
            notificationService.createNotification(
                    hodUserId,
                    "Booking Escalated",
                    "Booking " + booking.getId() + " has been escalated to you for review.",
                    "escalation"
            );

            return booking;
        } else {
            throw new IllegalArgumentException("Booking not found");
        }
    }

    public Booking escalateBookingToAdmin(Long bookingId, String adminUserId) {
        Optional<Booking> optionalBooking = bookingRepo.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus(BookingStatus.ESCALATED_TO_ADMIN);
            bookingRepo.save(booking);

            notificationService.createNotification(
                    adminUserId,
                    "Booking Escalated",
                    "Booking #" + booking.getId() + " has been escalated to Admin for review.",
                    "escalation"
            );

            return booking;
        } else {
            throw new IllegalArgumentException("Booking not found");
        }
    }



    public Booking cancelBooking(Long id) {
        return bookingRepo.findById(id)
                .map(b -> {
                    b.setStatus(BookingStatus.CANCELLED);
                    return bookingRepo.save(b);
                })
                .orElse(null);
    }
}