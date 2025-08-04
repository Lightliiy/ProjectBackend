package com.example.Student.Service;

import com.example.Student.Model.Counselor;
import com.example.Student.Repository.CounselorRepo;
import com.example.Student.Repository.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CounselorService {

    @Autowired
    private CounselorRepo counselorRepo;

    @Autowired
    private StudentRepo studentRepo;


    @Autowired
    private PasswordEncoder passwordEncoder;

    public long getCounselorCount() {
        return counselorRepo.count();  // Count counselors in the repository
    }

    public List<Counselor> getAllCounselors() {
        return counselorRepo.findAll();
    }

    public Optional<Counselor> getCounselorById(Long id) {
        return counselorRepo.findById(id);
    }

    public int countAssignedStudents(Long counselorId) {
        // Assuming Student entity has a field 'counselorId'
        return studentRepo.countByCounselorId(counselorId);
    }

    public long countAllCounselors() {
        return counselorRepo.count();  // assuming you use JpaRepository
    }

    public Counselor addCounselor(Counselor counselor) {
        if (counselor.getId() == null) {
            // New counselor - hash password
            counselor.setPassword(passwordEncoder.encode(counselor.getPassword()));
        } else {
            // Existing counselor - check if password changed
            Optional<Counselor> existing = counselorRepo.findById(counselor.getId());
            if (existing.isPresent()) {
                String existingPasswordHash = existing.get().getPassword();
                String newPassword = counselor.getPassword();

                // If the new password is different from the stored hash (means raw password provided), hash it
                if (!passwordEncoder.matches(newPassword, existingPasswordHash)) {
                    counselor.setPassword(passwordEncoder.encode(newPassword));
                } else {
                    // Password unchanged (already hashed), keep existing hash
                    counselor.setPassword(existingPasswordHash);
                }
            } else {
                // No existing counselor found, treat as new
                counselor.setPassword(passwordEncoder.encode(counselor.getPassword()));
            }
        }
        return counselorRepo.save(counselor);
    }


    public Optional<Counselor> getCounselorByStudentId(String studentId) {
        return counselorRepo.findByStudents_StudentId(studentId);
    }

    public CounselorService(PasswordEncoder passwordEncoder, CounselorRepo counselorRepo) {
        this.passwordEncoder = passwordEncoder;
        this.counselorRepo = counselorRepo;
    }


    public Counselor updateCounselor(Long id, Counselor updatedCounselor) {
        return counselorRepo.findById(id)
                .map(existing -> {
                    existing.setName(updatedCounselor.getName());
                    existing.setEmail(updatedCounselor.getEmail());
                    existing.setMaxCaseload(updatedCounselor.getMaxCaseload());
                    existing.setDepartment(updatedCounselor.getDepartment());

                    // Handle password update if provided (and different)
                    if (updatedCounselor.getPassword() != null && !updatedCounselor.getPassword().isEmpty()) {
                        if (!passwordEncoder.matches(updatedCounselor.getPassword(), existing.getPassword())) {
                            existing.setPassword(passwordEncoder.encode(updatedCounselor.getPassword()));
                        }
                    }

                    return counselorRepo.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Counselor not found"));
    }

    public void deleteCounselor(Long id) {
        counselorRepo.deleteById(id);
    }


    public Counselor authenticate(String email, String rawPassword) {
        Optional<Counselor> optionalCounselor = counselorRepo.findByEmail(email);
        if (optionalCounselor.isPresent()) {
            Counselor counselor = optionalCounselor.get();
            System.out.println("Stored hash: " + counselor.getPassword());
            System.out.println("Trying to match raw password: " + rawPassword);
            boolean matches = passwordEncoder.matches(rawPassword, counselor.getPassword());
            System.out.println("Password match result: " + matches);
            if (matches) {
                return counselor;
            }
        }
        return null;
    }




}

