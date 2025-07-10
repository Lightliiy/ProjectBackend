package com.example.Student.Service;

import com.example.Student.Model.Case;
import com.example.Student.Repository.CaseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CaseService {

    @Autowired
    private CaseRepo caseRepository;

    public List<Case> getEscalatedCases() {
        return caseRepository.findByStatus("Escalated");
    }

    public List<Case> getPendingCases() {
        return caseRepository.findByStatus("Pending");
    }

    public Case saveCase(Case caseItem) {
        return caseRepository.save(caseItem);
    }
}

