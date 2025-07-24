package com.example.Student.Repository;

import com.example.Student.Model.BookingStatus;
import com.example.Student.Model.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CaseRepo extends JpaRepository<Case, Long> {

    List<Case> findByStatus(BookingStatus status);

}
