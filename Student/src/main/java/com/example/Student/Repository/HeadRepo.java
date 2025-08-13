package com.example.Student.Repository;

import com.example.Student.Model.Head;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeadRepo extends JpaRepository<Head, Long> {

    Head findByEmail(String email);
}

