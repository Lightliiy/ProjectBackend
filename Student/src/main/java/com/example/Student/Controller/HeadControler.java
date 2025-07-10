package com.example.Student.Controller;

import com.example.Student.Model.Case;
import com.example.Student.Model.Head;
import com.example.Student.Model.Notification;
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

    // Register new Head of Department
    @PostMapping("/register")
    public ResponseEntity<Head> registerHOD(@RequestBody Head head) {
        Head savedHod = headOfDepartmentService.saveHeadOfDepartment(head);
        return ResponseEntity.ok(savedHod);
    }

    // View escalated cases
    @GetMapping("/escalated-cases")
    public ResponseEntity<List<Case>> getEscalatedCases() {
        List<Case> escalatedCases = caseService.getEscalatedCases();
        return ResponseEntity.ok(escalatedCases);
    }

    // View pending cases
    @GetMapping("/pending-cases")
    public ResponseEntity<List<Case>> getPendingCases() {
        List<Case> pendingCases = caseService.getPendingCases();
        return ResponseEntity.ok(pendingCases);
    }

    // Forward notification to admin
//    @PostMapping("/forward-notification")
//    public ResponseEntity<Notification> forwardNotification(@RequestBody Notification notification) {
//        Notification savedNotification = notificationService.sendNotification(notification);
//        return ResponseEntity.ok(savedNotification);
//    }

    // Register new case
    @PostMapping("/add-case")
    public ResponseEntity<Case> addCase(@RequestBody Case caseItem) {
        Case savedCase = caseService.saveCase(caseItem);
        return ResponseEntity.ok(savedCase);
    }
}
