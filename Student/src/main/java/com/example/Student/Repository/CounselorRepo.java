package com.example.Student.Repository;

import com.example.Student.Model.Counselor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CounselorRepo extends JpaRepository<Counselor, Long> {

    // Find counselor by student's studentId (String)
    Optional<Counselor> findByStudents_StudentId(String studentId);

    Optional<Counselor> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT c FROM Counselor c JOIN c.students s WHERE s.studentId = :studentId")
    Optional<Counselor> findCounselorByStudentId(@Param("studentId") String studentId);


}