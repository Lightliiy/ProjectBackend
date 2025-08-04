package com.example.Student.Service;

import com.example.Student.Model.BookingStatus;
import com.example.Student.Model.Case;
import com.example.Student.Model.Counselor;
import com.example.Student.Repository.CaseRepo;
import com.example.Student.Repository.CounselorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CaseService {

    @Autowired
    private CaseRepo caseRepo;

    @Autowired
    private CounselorRepo counselorRepo;

    // Save or update a case
    public Case saveCase(Case caseItem) {
        return caseRepo.save(caseItem);
    }


    // Get a single case by ID
    public Case getCaseById(Long id) {
        Optional<Case> optionalCase = caseRepo.findById(id);
        return optionalCase.orElse(null);
    }

    // Get all cases with status = PENDING
    public List<Case> getPendingCases() {
        return caseRepo.findByStatus(BookingStatus.PENDING);
    }

    // Get all cases with status = ESCALATED
    public List<Case> getEscalatedCases() {
        return caseRepo.findByStatus(BookingStatus.ESCALATED);
    }

    public long countAllCases() {
        return caseRepo.count();
    }

    public long countCasesByStatus(BookingStatus status) {
        return caseRepo.countByStatus(status);
    }

    public Case escalateToHODByBookingId(Long bookingId) {
        Optional<Case> caseOptional = caseRepo.findByBooking_Id(bookingId);
        if (caseOptional.isPresent()) {
            Case caseItem = caseOptional.get();
            caseItem.setStatus(BookingStatus.ESCALATED_TO_HOD);
            return caseRepo.save(caseItem);
        }
        return null;
    }


    // Escalate a case by changing its status
    public Case escalateCase(Long id) {
        Case caseItem = getCaseById(id);
        if (caseItem != null) {
            caseItem.setStatus(BookingStatus.ESCALATED);
            return saveCase(caseItem);
        }
        return null;
    }

    // Get all cases (if needed)
    public List<Case> getAllCases() {
        return caseRepo.findAll();
    }

    public Case escalateToAdmin(Long id) {
        Optional<Case> caseOpt = caseRepo.findById(id);
        if (!caseOpt.isPresent()) return null;

        Case c = caseOpt.get();
        if (c.getStatus() != BookingStatus.ESCALATED) return null; // only escalate cases already escalated by student

        c.setStatus(BookingStatus.ESCALATED_TO_ADMIN);
        return caseRepo.save(c);
    }

    public List<Case> getEscalatedToAdminCases() {
        return caseRepo.findByStatus(BookingStatus.ESCALATED_TO_ADMIN);
    }

    public List<Case> getCasesByStatus(BookingStatus status) {
        return caseRepo.findByStatus(status);
    }


    public void reassignCounselor(Long caseId, Long counselorId) {
        Optional<Case> caseOpt = caseRepo.findById(caseId);
        Optional<Counselor> counselorOpt = counselorRepo.findById(counselorId);

        if (caseOpt.isPresent() && counselorOpt.isPresent()) {
            Case caseItem = caseOpt.get();
            caseItem.setCounselorId(counselorId);
            caseRepo.save(caseItem);
        }
    }

    public Case escalateToHOD(Long caseId) {
        Optional<Case> caseOpt = caseRepo.findById(caseId);
        if (!caseOpt.isPresent()) return null;

        Case caseItem = caseOpt.get();
        // Only allow escalation if current status permits (optional)
        caseItem.setStatus(BookingStatus.ESCALATED_TO_HOD);  // You may need to add this enum value
        caseRepo.save(caseItem);

        // Send notification if needed

        return caseItem;
    }


}
