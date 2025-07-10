package com.example.Student.Service;

import com.example.Student.Model.StaffUser;
import com.example.Student.Repository.StaffRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StaffService {

    @Autowired
    private StaffRepo staffRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register a new staff user (Admin or HOD)
    public StaffUser register(StaffUser staffUser) {
        // Hash the password before saving
        staffUser.setPassword(passwordEncoder.encode(staffUser.getPassword()));
        return staffRepo.save(staffUser);
    }

    // Authenticate user by email and password
    public StaffUser authenticate(String email, String rawPassword) {
        Optional<StaffUser> optionalUser = staffRepo.findByEmail(email);
        if (optionalUser.isPresent()) {
            StaffUser staffUser = optionalUser.get();
            if (passwordEncoder.matches(rawPassword, staffUser.getPassword())) {
                return staffUser;
            }
        }
        return null;
    }
}

