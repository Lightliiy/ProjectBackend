package com.example.Student.Service;

import com.example.Student.Model.Booking;
import com.example.Student.Model.BookingStatus;
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

    public Booking createBooking(Booking booking) {
        booking.setStatus(BookingStatus.PENDING); // default status on create
        return bookingRepo.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepo.findAll();
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
        return bookingRepo.findByCounselorId(counselorId);
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

    public void deleteBooking(Long id) {
        bookingRepo.deleteById(id);
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

    public Booking cancelBooking(Long id) {
        return bookingRepo.findById(id)
                .map(b -> {
                    b.setStatus(BookingStatus.CANCELLED);
                    return bookingRepo.save(b);
                })
                .orElse(null);
    }


}
