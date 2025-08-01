package com.example.Student.Controller;

import com.example.Student.Model.Counselor;
import com.example.Student.Repository.CounselorRepo;
import com.example.Student.Service.CounselorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/counselors")
public class CounselorControler {

    @Autowired
    private CounselorService counselorService;

    @Autowired
    private CounselorRepo counselorRepo;

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
    public ResponseEntity<?> resetPasswordByEmail(@RequestBody Map<String, String> updates) {
        try {
            String email = updates.get("email");
            String newPassword = updates.get("password");

            Optional<Counselor> optionalCounselor = counselorRepo.findByEmail(email);
            if (optionalCounselor.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Counselor not found"));
            }

            Counselor counselor = optionalCounselor.get();
            counselor.setPassword(newPassword); // this should get hashed in your service
            counselorService.updateCounselor(counselor.getId(), counselor);

            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }


    @DeleteMapping("/delete/{id}")
    public void deleteCounselor(@PathVariable Long id) {
        counselorService.deleteCounselor(id);
    }

}
