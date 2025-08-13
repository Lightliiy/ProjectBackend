package com.example.Student.Service;

import com.example.Student.Model.Head;
import com.example.Student.Repository.HeadRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HeadService {

    @Autowired
    private HeadRepo headOfDepartmentRepository;

    public Head saveHeadOfDepartment(Head headOfDepartment) {
        return headOfDepartmentRepository.save(headOfDepartment);
    }

    public Head findByEmail(String email) {
        return headOfDepartmentRepository.findByEmail(email);
    }
}

