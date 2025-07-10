package com.example.Student.Model;

import com.example.Student.Model.Enum.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String studentId;

    private String name;
    private String department;
    @Column(name = "year_level", nullable = false)
    private Integer yearLevel;
    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String phone;
    // New fields for booking notification
     // Time of booking
    @Column(nullable = true)
    private String profileImage;      // URL to an image/avatar

    @ManyToOne(optional = true)
    @JoinColumn(name = "counselor_id", nullable = true)
    @JsonIgnoreProperties("students")
    private Counselor counselor;

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name ; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password ; }
    public void setPassword(String password) { this.password = password; }



    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getYearLevel() { return yearLevel; }
    public void setYearLevel(Integer yearLevel) { this.yearLevel = yearLevel; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Counselor getCounselor() { return counselor; }
    public void setCounselor(Counselor counselor) { this.counselor = counselor; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }


}
