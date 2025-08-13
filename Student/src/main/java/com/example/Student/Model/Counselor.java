package com.example.Student.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Counselor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private int maxCaseload;
    private String department;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // Store available slots as a collection of strings
    @ElementCollection
    @CollectionTable(name = "counselor_slots", joinColumns = @JoinColumn(name = "counselor_id"))
    @Column(name = "slot")
    private List<String> availableSlots;

    @OneToMany(mappedBy = "counselor")
    @JsonIgnoreProperties("counselor")
    private List<Student> students;

    public Counselor() {}

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getMaxCaseload() { return maxCaseload; }
    public void setMaxCaseload(int maxCaseload) { this.maxCaseload = maxCaseload; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public List<String> getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(List<String> availableSlots) { this.availableSlots = availableSlots; }

    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
