package com.example.Student.Service;

import com.example.Student.Model.Student;
import com.example.Student.Repository.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Student registerStudent(Student student) {
        // Encode password before save
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        return studentRepo.save(student);
    }

    public long getStudentCount() {
        return studentRepo.count();
    }

    public List<Student> getAllStudents() {
        return studentRepo.findAll();
    }


    public Student authenticate(String email, String rawPassword) {
        Optional<Student> optionalStudent = studentRepo.findByEmail(email);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            if (passwordEncoder.matches(rawPassword, student.getPassword())) {
                return student;
            }
        }
        return null;
    }

    public Student saveStudent(Student student) {
        // Hash password before saving
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        return studentRepo.save(student);
    }

    public List<Student> getStudentsByCounselorId(Long id) {
        return studentRepo.findByCounselorId(id);
    }

    public long countAllStudents() {
        return studentRepo.count();  // assuming you use JpaRepository
    }

    public List<Student> getStudentsByCounselorEmail(String email) {
        return studentRepo.findByCounselorEmail(email);
    }

    public String addStudent(Student student) {
        System.out.println("Student received: " + student);
        studentRepo.save(student);  // actually save student
        return "Student added successfully!";
    }

    // --- NEW methods ---

    // Update student by ID
    public Student updateStudent(Long id, Student updatedStudent) {
        Optional<Student> optionalStudent = studentRepo.findById(id);
        if (optionalStudent.isPresent()) {
            Student existingStudent = optionalStudent.get();

            // Update fields (adjust according to your Student model)
            existingStudent.setStudentId(updatedStudent.getStudentId());
            existingStudent.setName(updatedStudent.getName());
            existingStudent.setEmail(updatedStudent.getEmail());
            existingStudent.setPhone(updatedStudent.getPhone());
            existingStudent.setDepartment(updatedStudent.getDepartment());
            existingStudent.setYearLevel(updatedStudent.getYearLevel());
            existingStudent.setCounselor(updatedStudent.getCounselor());
            existingStudent.setProfileImage(updatedStudent.getProfileImage());

            return studentRepo.save(existingStudent);
        } else {
            throw new RuntimeException("Student with id " + id + " not found");
        }
    }

    // Delete student by ID
    public void deleteStudent(Long id) {
        if (studentRepo.existsById(id)) {
            studentRepo.deleteById(id);
        } else {
            throw new RuntimeException("Student with id " + id + " not found");
        }
    }
}
