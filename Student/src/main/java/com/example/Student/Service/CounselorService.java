package com.example.Student.Service;

import com.example.Student.Model.Counselor;
import com.example.Student.Repository.CounselorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CounselorService {

    @Autowired
    private CounselorRepo counselorRepo;


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

    public Counselor addCounselor(Counselor counselor) {
        return counselorRepo.save(counselor);
    }

    public Optional<Counselor> getCounselorByStudentId(String studentId) {
        return counselorRepo.findByStudents_StudentId(studentId);
    }

    public CounselorService(PasswordEncoder passwordEncoder, CounselorRepo counselorRepo) {
        this.passwordEncoder = passwordEncoder;
        this.counselorRepo = counselorRepo;
    }

    public void registerCounselor(Counselor counselor) {
        String rawPassword = counselor.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        counselor.setPassword(encodedPassword);
        counselorRepo.save(counselor);
    }

    public Counselor updateCounselor(Long id, Counselor updatedCounselor) {
        return counselorRepo.findById(id)
                .map(c -> {
                    c.setName(updatedCounselor.getName());
                    c.setEmail(updatedCounselor.getEmail());
                    c.setMaxCaseload(updatedCounselor.getMaxCaseload());
                    c.setDepartment(updatedCounselor.getDepartment());
                    return counselorRepo.save(c);
                }).orElseThrow(() -> new RuntimeException("Counselor not found"));
    }
    public void deleteCounselor(Long id) {
        counselorRepo.deleteById(id);
    }


    public Counselor authenticate(String email, String rawPassword) {
        Optional<Counselor> optionalCounselor = counselorRepo.findByEmail(email);
        if (optionalCounselor.isEmpty()) {
            return null; // email not found
        }

        Counselor counselor = optionalCounselor.get();

        if (passwordEncoder.matches(rawPassword, counselor.getPassword())) {
            return counselor; // valid login
        }

        return null; // invalid password
    }



}

