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

    @PutMapping("/update/{id}")
    public Counselor updateCounselor(@PathVariable Long id, @RequestBody Counselor counselor) {
        return counselorService.updateCounselor(id, counselor);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCounselor(@PathVariable Long id) {
        counselorService.deleteCounselor(id);
    }

}
