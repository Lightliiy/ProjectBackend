package com.example.Student.Repository;

import com.example.Student.Model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepo extends JpaRepository<Student, Long> {
    List<Student> findByCounselorEmail(String email);

    List<Student> findByCounselorId(Long counselorId);

    Optional<Student> findByEmail(String email);

    int countByCounselorId(Long counselorId);

    List<Student> findByDepartmentAndCounselorIsNull(String department);
    List<Student> findByDepartment(String department);


    Optional<Student> findByStudentId(String studentId);

    @Query("SELECT DISTINCT s.department FROM Student s")
    List<String> findDistinctDepartments();

    long countByDepartment(String department);

}