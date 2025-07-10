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
    private CounselorRepo counselorRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    public long getCounselorCount() {
        return counselorRepository.count();  // Count counselors in the repository
    }

    public List<Counselor> getAllCounselors() {
        return counselorRepository.findAll();
    }

    public Optional<Counselor> getCounselorById(Long id) {
        return counselorRepository.findById(id);
    }

    public Counselor addCounselor(Counselor counselor) {
        return counselorRepository.save(counselor);
    }

    public Optional<Counselor> getCounselorByStudentId(String studentId) {
        return counselorRepository.findByStudents_StudentId(studentId);
    }

    public Counselor updateCounselor(Long id, Counselor updatedCounselor) {
        return counselorRepository.findById(id)
                .map(c -> {
                    c.setName(updatedCounselor.getName());
                    c.setEmail(updatedCounselor.getEmail());
                    c.setMaxCaseload(updatedCounselor.getMaxCaseload());
                    c.setDepartment(updatedCounselor.getDepartment());
                    return counselorRepository.save(c);
                }).orElseThrow(() -> new RuntimeException("Counselor not found"));
    }
    public void deleteCounselor(Long id) {
        counselorRepository.deleteById(id);
    }


    public Counselor authenticate(String email, String rawPassword) {
        Optional<Counselor> optionalCounselor = counselorRepository.findByEmail(email);
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

