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
    public List<Counselor> findAll() {
        return counselorRepo.findAll();
    }

    public long countAllCounselors() {
        return counselorRepo.count();
    }

    public Optional<Counselor> getCounselorByStudentId(String studentId) {
        return counselorRepo.findByStudents_StudentId(studentId);
    }

    public CounselorService(PasswordEncoder passwordEncoder, CounselorRepo counselorRepo) {
        this.passwordEncoder = passwordEncoder;
        this.counselorRepo = counselorRepo;
    }


    public Counselor addCounselor(Counselor counselor) {
        if (counselor.getId() == null) {
            // New counselor → hash password if not already hashed
            if (!counselor.getPassword().startsWith("$2a$")) {
                counselor.setPassword(passwordEncoder.encode(counselor.getPassword()));
            }
        } else {
            // Existing counselor → load current data
            Counselor existing = counselorRepo.findById(counselor.getId())
                    .orElseThrow(() -> new RuntimeException("Counselor not found"));

            if (counselor.getPassword() == null || counselor.getPassword().isBlank()) {
                counselor.setPassword(existing.getPassword());
            } else if (!counselor.getPassword().startsWith("$2a$")) {
                counselor.setPassword(passwordEncoder.encode(counselor.getPassword()));
            } else {
                // Already hashed, keep as is
                counselor.setPassword(counselor.getPassword());
            }
        }
        return counselorRepo.save(counselor);
    }


    public Counselor updateCounselor(Long id, Counselor updatedCounselor) {
        return counselorRepo.findById(id)
                .map(existing -> {
                    existing.setName(updatedCounselor.getName());
                    existing.setEmail(updatedCounselor.getEmail());
                    existing.setMaxCaseload(updatedCounselor.getMaxCaseload());
                    existing.setDepartment(updatedCounselor.getDepartment());

                    if (updatedCounselor.getPassword() != null && !updatedCounselor.getPassword().isBlank()) {
                        existing.setPassword(passwordEncoder.encode(updatedCounselor.getPassword()));
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

