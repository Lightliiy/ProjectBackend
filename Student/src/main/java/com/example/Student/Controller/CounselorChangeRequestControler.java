package com.example.Student.Controller;

import com.example.Student.Model.CounselorChangeRequest;
import com.example.Student.Model.Student;
import com.example.Student.Repository.CounselorRequestRepo;
import com.example.Student.Repository.StudentRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/change-requests")
public class CounselorChangeRequestControler {

    private final CounselorRequestRepo counselorRequestRepo;
    private final StudentRepo studentRepo;

    public CounselorChangeRequestControler(CounselorRequestRepo counselorRequestRepo, StudentRepo studentRepo) {
        this.counselorRequestRepo = counselorRequestRepo;
        this.studentRepo = studentRepo;
    }

    // Student submits request
    @PostMapping
    public CounselorChangeRequest createRequest(@RequestBody CounselorChangeRequest request) {
        Student student = studentRepo.findByStudentId(request.getStudent().getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        request.setStudent(student);
        return counselorRequestRepo.save(request);
    }

    // Admin/HOD views all requests
    @GetMapping
    public List<CounselorChangeRequest> getAllRequests() {
        return counselorRequestRepo.findAll();
    }

    // Admin/HOD updates status
    @PutMapping("/{id}/status")
    public CounselorChangeRequest updateStatus(@PathVariable Long id, @RequestParam String status) {
        CounselorChangeRequest req = counselorRequestRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        req.setStatus(status);
        return counselorRequestRepo.save(req);
    }
}
