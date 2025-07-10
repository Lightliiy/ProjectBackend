package com.example.Student.Repository;

import com.example.Student.Model.StaffUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepo extends JpaRepository<StaffUser, Long> {
    Optional<StaffUser> findByEmail(String email);
}
