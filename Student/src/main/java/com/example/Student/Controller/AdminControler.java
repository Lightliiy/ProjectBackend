package com.example.Student.Controller;

import com.example.Student.Model.Booking;
import com.example.Student.Model.Counselor;
import com.example.Student.Repository.BookingRepo;
import com.example.Student.Repository.CounselorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminControler {

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private CounselorRepo counselorRepo;

    @PostMapping("/reassign-counselor")
    public ResponseEntity<?> reassignCounselor(@RequestBody Map<String, Object> payload) {
        Long caseId = Long.parseLong(payload.get("caseId").toString());
        Long counselorId = Long.parseLong(payload.get("counselorId").toString());

        Optional<Booking> bookingOpt = bookingRepo.findById(caseId);
        Optional<Counselor> counselorOpt = counselorRepo.findById(counselorId);

        if (bookingOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Booking not found");
        }
        if (counselorOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Counselor not found");
        }

        Booking booking = bookingOpt.get();
        booking.setCounselorId(counselorId.toString()); // you use String for counselorId
        bookingRepo.save(booking);

        return ResponseEntity.ok("Counselor reassigned successfully");
    }
}
