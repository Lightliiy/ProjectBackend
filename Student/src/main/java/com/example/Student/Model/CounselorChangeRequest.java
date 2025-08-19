package com.example.Student.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class CounselorChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    private String currentCounselorName;

    @Column(nullable = false, length = 1000)
    private String reason;

    // pending, approved, rejected
    @Column(nullable = false)
    private String status = "PENDING";

    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getCurrentCounselorName() { return currentCounselorName; }
    public void setCurrentCounselorName(String currentCounselorName) { this.currentCounselorName = currentCounselorName; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
