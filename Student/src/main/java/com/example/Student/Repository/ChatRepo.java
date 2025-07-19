package com.example.Student.Repository;

import com.example.Student.Model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChatRepo extends JpaRepository<Chat, Long> {
    Optional<Chat> findByCounselorIdAndStudentId(Long counselorId, Long studentId);
}

