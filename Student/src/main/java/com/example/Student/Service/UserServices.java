package com.example.Student.Service;

import com.example.Student.Model.Enum.Role; // Make sure this path is correct
import com.example.Student.Model.Student;
import com.example.Student.Repository.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServices {

    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Method to handle user registration
    public Student register(Student student) {
        // --- CRITICAL FIX: Move the password encoding and saving logic OUTSIDE the 'if' blocks ---
        // The original code would throw an exception and never save the user.

        // 1. Check if email already exists
        if (studentRepo.findByEmail(student.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered.");
        }

        // 2. Check if studentId already exists
        if (studentRepo.findByStudentId(student.getStudentId()).isPresent()) {
            throw new RuntimeException("Student ID already registered.");
        }

        // 3. Encode password before saving (This should always happen for a new registration)
        student.setPassword(passwordEncoder.encode(student.getPassword()));


        // 5. Save the student
        return studentRepo.save(student);
    }

    // Method to handle user authentication
    public Student authenticate(String email, String rawPassword) { // Renamed 'password' to 'rawPassword' for clarity
        Optional<Student> optionalStudent = studentRepo.findByEmail(email);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            // Check if the provided raw password matches the encoded password from the database
            if (passwordEncoder.matches(rawPassword, student.getPassword())) {
                return student; // Authentication successful
            }
        }
        return null; // Authentication failed (user not found or password mismatch)
    }


}