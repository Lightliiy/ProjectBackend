package com.example.Student.Repository;

import com.example.Student.Model.BookingStatus;
import com.example.Student.Model.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CaseRepo extends JpaRepository<Case, Long> {

    List<Case> findByStatus(BookingStatus status);

    long countByStatus(BookingStatus status);

    Optional<Case> findByBooking_Id(Long bookingId);



}
