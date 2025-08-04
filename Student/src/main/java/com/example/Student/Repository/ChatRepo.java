package com.example.Student.Repository;

import com.example.Student.Model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepo extends JpaRepository<Chat, Long> {
    Optional<Chat> findByCounselorIdAndStudentId(Long counselorId, String studentId);

    List<Chat> findByStudentId(String studentId);

    List<Chat> findByStudentIdAndCounselorId(String studentId, Long counselorId);

    Optional<Chat> findById(Long id);


}

