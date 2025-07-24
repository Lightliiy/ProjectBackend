package com.example.Student.Controller;

import com.example.Student.Model.BookingStatus;
import com.example.Student.Model.Case;

import com.example.Student.Model.Head;
import com.example.Student.Service.CaseService;
import com.example.Student.Service.HeadService;
import com.example.Student.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hod")
public class HeadControler {

    @Autowired
    private HeadService headOfDepartmentService;

    @Autowired
    private CaseService caseService;

    @Autowired
    private NotificationService notificationService;

    // ✅ Register new Head of Department
    @PostMapping("/register")
    public ResponseEntity<Head> registerHOD(@RequestBody Head head) {
        Head savedHod = headOfDepartmentService.saveHeadOfDepartment(head);
        return ResponseEntity.ok(savedHod);
    }

    // ✅ View all cases
    @GetMapping("/all-cases")
    public ResponseEntity<List<Case>> getAllCases() {
        List<Case> allCases = caseService.getAllCases();  // <-- You must implement this in service
        return ResponseEntity.ok(allCases);
    }


    // ✅ View escalated cases
    @GetMapping("/escalated-cases")
    public ResponseEntity<List<Case>> getEscalatedCases() {
        List<Case> escalatedCases = caseService.getEscalatedCases();
        return ResponseEntity.ok(escalatedCases);
    }

    // ✅ View pending cases
    @GetMapping("/pending-cases")
    public ResponseEntity<List<Case>> getPendingCases() {
        List<Case> pendingCases = caseService.getPendingCases();
        return ResponseEntity.ok(pendingCases);
    }

    // ✅ Register new case (always defaults to PENDING)
    @PostMapping("/add-case")
    public ResponseEntity<Case> addCase(@RequestBody Case caseItem) {
        caseItem.setStatus(BookingStatus.PENDING); // enforce default
        Case savedCase = caseService.saveCase(caseItem);
        return ResponseEntity.ok(savedCase);
    }

    // ✅ Escalate case by ID
    @PostMapping("/escalate-case/{id}")
    public ResponseEntity<Case> escalateCase(@PathVariable Long id) {
        Case escalated = caseService.escalateCase(id);
        if (escalated == null) {
            return ResponseEntity.notFound().build();
        }

        // (Optional) send notification to admin here
        // notificationService.sendNotification(...);

        return ResponseEntity.ok(escalated);
    }
    @PostMapping("/reassign-counselor")
    public ResponseEntity<String> reassignCounselor(
            @RequestParam Long caseId,
            @RequestParam Long counselorId
    ) {
        caseService.reassignCounselor(caseId, counselorId);
        return ResponseEntity.ok("Reassigned");
    }


    @PostMapping("/escalate-to-admin/{id}") // HOD escalates to admin
    public ResponseEntity<Case> escalateToAdmin(@PathVariable Long id) {
        Case escalated = caseService.escalateToAdmin(id);
        if (escalated == null) {
            return ResponseEntity.notFound().build();
        }
        // Optional: notify admin here
        return ResponseEntity.ok(escalated);
    }

    // ✅ Get cases escalated by HOD to Admin
    @GetMapping("/escalated-to-admin")
    public ResponseEntity<List<Case>> getCasesEscalatedToAdmin() {
        List<Case> cases = caseService.getCasesByStatus(BookingStatus.ESCALATED_TO_ADMIN);
        return ResponseEntity.ok(cases);
    }


}
