package com.example.Student.Repository;

import com.example.Student.Model.Booking;
import com.example.Student.Model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {

    // Get bookings by student ID
    List<Booking> findByStudentId(String studentId);

    // Get bookings by counselor ID
    List<Booking> findByCounselorId(String counselorId);

    // Count bookings by counselor ID
    long countByCounselorId(String counselorId);

    // Get bookings by status
    List<Booking> findByStatus(BookingStatus status);

    Long countByCounselorIdAndStatus(String counselorId, BookingStatus status);

    // Count bookings by status
    long countByStatus(BookingStatus status);

    @Query("SELECT b.issueType, COUNT(b) FROM Booking b WHERE b.counselorId = :counselorId GROUP BY b.issueType")
    List<Object[]> countBookingsByIssueType(String counselorId);
}
