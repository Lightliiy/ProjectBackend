package com.example.Student.Repository;

import com.example.Student.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {
    List<Booking> findByStudentId(String studentId);
    // Additional query methods if needed
}
