package com.example.Student.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentId;
    private String counselorId;

    private String sessionType;
    private String issueType;

    @Column(length = 1000)
    private String description;

    private LocalDate scheduledDate;
    private String timeSlot;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BookingStatus status;

    @ElementCollection
    private List<String> attachments;

    @Column(name = "is_escalated", nullable = false)
    private boolean isEscalated = false;

    // Add this new field for the HOD comment
    @Column(length = 1000)
    private String hodComment;

    @Transient
    private String studentName;

    @Transient
    private List<String> attachmentUrls;

    public Booking() {}

    // ================= Getters and Setters =================

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCounselorId() {
        return counselorId;
    }
    public void setCounselorId(String counselorId) {
        this.counselorId = counselorId;
    }

    public String getSessionType() {
        return sessionType;
    }
    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public String getIssueType() {
        return issueType;
    }
    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }
    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }
    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public BookingStatus getStatus() {
        return status;
    }
    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public List<String> getAttachments() {
        return attachments;
    }
    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public boolean isEscalated() {
        return isEscalated;
    }
    public void setEscalated(boolean escalated) {
        isEscalated = escalated;
    }

    public String getStudentName() {
        return studentName;
    }
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public List<String> getAttachmentUrls() {
        return attachmentUrls;
    }
    public void setAttachmentUrls(List<String> attachmentUrls) {
        this.attachmentUrls = attachmentUrls;
    }

    // New getter and setter for hodComment
    public String getHodComment() {
        return hodComment;
    }

    public void setHodComment(String hodComment) {
        this.hodComment = hodComment;
    }
}