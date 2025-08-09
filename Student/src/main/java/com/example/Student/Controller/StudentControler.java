package com.example.Student.Controller;

import com.example.Student.Model.Student;
import com.example.Student.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/students")
public class StudentControler {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerStudent(@RequestBody Student student) {
        try {
            studentService.saveStudent(student);
            return ResponseEntity.status(201).body("Registration successful");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Registration failed: " + e.getMessage());
        }
    }

    // Add a new method to handle string-based student IDs
    @GetMapping("/search")
    public ResponseEntity<Student> getStudentByStudentId(@RequestParam String studentId) {
        Optional<Student> student = studentService.findByStudentId(studentId);
        if (student.isPresent()) {
            return ResponseEntity.ok(student.get());
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }


    @GetMapping("/by-counselor-id/{id}")
    public List<Student> getStudentsByCounselorId(@PathVariable Long id) {
        return studentService.getStudentsByCounselorId(id);
    }


    @GetMapping("/by-counselor")
    public List<Student> getStudentsByCounselorEmail(@RequestParam String email) {
        return studentService.getStudentsByCounselorEmail(email);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addStudent(@RequestBody Student student) {
        studentService.saveStudent(student); // Save to DB
        return ResponseEntity.ok("Student added successfully!");
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<Map<String, String>>> getNotifications() {
        List<Student> students = studentService.getAllStudents();
        List<Map<String, String>> notifications = new ArrayList<>();

        for (Student s : students) {
            Map<String, String> n = new HashMap<>();
            n.put("id", s.getStudentId());
            n.put("student", s.getName());
            n.put("image", s.getProfileImage());
            notifications.add(n);
        }

        return ResponseEntity.ok(notifications);

    }
    @PutMapping("/{id}")
    public ResponseEntity<String> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        try {
            studentService.updateStudent(id, student);
            return ResponseEntity.ok("Student updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Update failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.ok("Student deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Delete failed: " + e.getMessage());
        }
    }
}
