package com.example.Student.Controller;

import com.example.Student.Model.Counselor;
import com.example.Student.Model.StaffUser;
import com.example.Student.Model.Student;
import com.example.Student.Repository.CounselorRepo;
import com.example.Student.Repository.StaffRepo;
import com.example.Student.Repository.StudentRepo;
import com.example.Student.Service.CounselorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/counselors")
public class CounselorControler {

    @Autowired
    private CounselorService counselorService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CounselorRepo counselorRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private StaffRepo staffRepo;

    @GetMapping
    public List<Counselor> getAllCounselors() {
        return counselorService.getAllCounselors();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Counselor> getCounselorById(@PathVariable Long id) {
        Optional<Counselor> counselor = counselorService.getCounselorById(id);
        return counselor.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCounselor(@RequestBody Counselor counselor) {
        Counselor savedCounselor = counselorService.addCounselor(counselor);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCounselor);
    }

    @GetMapping("/{id}/studentCount")
    public ResponseEntity<Map<String, Integer>> getStudentCount(@PathVariable Long id) {
        int count = counselorService.countAssignedStudents(id);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PostMapping("/{id}/slots/add")
    public ResponseEntity<?> addAvailableSlots(@PathVariable Long id, @RequestBody List<String> newSlots) {
        Optional<Counselor> optionalCounselor = counselorService.getCounselorById(id);
        if (optionalCounselor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Counselor not found");
        }

        Counselor counselor = optionalCounselor.get();

        // Get existing slots or empty list if null
        List<String> currentSlots = counselor.getAvailableSlots();
        if (currentSlots == null) {
            currentSlots = new ArrayList<>();
        }

        // Add only slots not already present to avoid duplicates
        for (String slot : newSlots) {
            if (!currentSlots.contains(slot)) {
                currentSlots.add(slot);
            }
        }

        counselor.setAvailableSlots(currentSlots);
        counselorService.addCounselor(counselor); // Save updated counselor

        return ResponseEntity.ok("Available slots added successfully");
    }

    @DeleteMapping("/{id}/slots/remove")
    public ResponseEntity<?> removeSlot(@PathVariable Long id, @RequestBody String slotToRemove) {
        Optional<Counselor> optionalCounselor = counselorService.getCounselorById(id);
        if (optionalCounselor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Counselor not found");
        }

        Counselor counselor = optionalCounselor.get();
        List<String> currentSlots = counselor.getAvailableSlots();

        if (currentSlots != null && currentSlots.remove(slotToRemove)) {
            counselor.setAvailableSlots(currentSlots);
            counselorService.addCounselor(counselor);
            return ResponseEntity.ok("Slot removed successfully");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Slot not found");
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<List<String>> getSlotsByCounselorId(@PathVariable Long id) {
        Optional<Counselor> optionalCounselor = counselorService.getCounselorById(id);
        if (optionalCounselor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        List<String> slots = optionalCounselor.get().getAvailableSlots();
        if (slots == null) slots = Collections.emptyList();
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/assigned")
    public ResponseEntity<Counselor> getCounselorByStudentId(@RequestParam String studentId) {
//    public ResponseEntity<Counselor> getCounselorByStudentId(@PathVariable String studentId) {
        Optional<Counselor> counselor = counselorService.getCounselorByStudentId(studentId);
        return counselor.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/profile")
    public ResponseEntity<Counselor> getCounselorProfile(@RequestParam String email) {
        Optional<Counselor> counselor = counselorRepo.findByEmail(email);
        return counselor.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        Map<String, String> response = new HashMap<>();

        if (email == null || currentPassword == null || newPassword == null) {
            response.put("error", "All fields are required");
            return ResponseEntity.badRequest().body(response);
        }

        // Check Counselor
        Optional<Counselor> counselorOpt = counselorRepo.findByEmail(email);
        if (counselorOpt.isPresent()) {
            Counselor counselor = counselorOpt.get();
            if (!passwordEncoder.matches(currentPassword, counselor.getPassword())) {
                response.put("error", "Current password is incorrect");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            counselor.setPassword(passwordEncoder.encode(newPassword));
            counselorRepo.save(counselor);
            response.put("message", "Counselor password reset successfully");
            return ResponseEntity.ok(response);
        }

        // Check Staff
        Optional<StaffUser> staffOpt = staffRepo.findByEmail(email);
        if (staffOpt.isPresent()) {
            StaffUser staff = staffOpt.get();
            if (!passwordEncoder.matches(currentPassword, staff.getPassword())) {
                response.put("error", "Current password is incorrect");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            staff.setPassword(passwordEncoder.encode(newPassword));
            staffRepo.save(staff);
            response.put("message", "Staff password reset successfully");
            return ResponseEntity.ok(response);
        }

        // Check Student
        Optional<Student> studentOpt = studentRepo.findByEmail(email);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            if (!passwordEncoder.matches(currentPassword, student.getPassword())) {
                response.put("error", "Current password is incorrect");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            student.setPassword(passwordEncoder.encode(newPassword));
            studentRepo.save(student);
            response.put("message", "Student password reset successfully");
            return ResponseEntity.ok(response);
        }

        response.put("error", "User not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCounselor(
            @PathVariable Long id,
            @RequestBody Counselor updatedCounselor) {

        try {
            Counselor counselor = counselorService.updateCounselor(id, updatedCounselor);
            return ResponseEntity.ok(counselor);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred"));
        }
    }
    @DeleteMapping("/delete/{id}")
    public void deleteCounselor(@PathVariable Long id) {
        counselorService.deleteCounselor(id);
    }

}
