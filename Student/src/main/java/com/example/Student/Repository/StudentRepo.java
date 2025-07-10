package com.example.Student.Repository;

import com.example.Student.Model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepo extends JpaRepository<Student, Long> {
    List<Student> findByCounselorEmail(String email);

    List<Student> findByCounselorId(Long counselorId);

    Optional<Student> findByEmail(String email);

    Optional<Student> findByStudentId(String studentId);
}



