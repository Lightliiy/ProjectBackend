package com.example.Student.Repository;

import com.example.Student.Model.CounselorChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CounselorRequestRepo extends JpaRepository<CounselorChangeRequest, Long> {
}
