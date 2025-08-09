package com.example.Student.Repository;

import com.example.Student.Model.Booking;
import com.example.Student.Model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {

    // Existing methods from the original BookingRepo
    List<Booking> findByStudentId(String studentId);
    List<Booking> findByCounselorId(String counselorId);
    int countByCounselorId(String counselorId);

    // New methods from the old CaseRepo, now applied to Booking
    List<Booking> findByStatus(BookingStatus status);
    long countByStatus(BookingStatus status);
}