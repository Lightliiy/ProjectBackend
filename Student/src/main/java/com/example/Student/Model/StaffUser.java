package com.example.Student.Model;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class StaffUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

    @Column(unique = true)
    private String employeeId;

    // Store roles as comma-separated values in one column
    @Column(name = "roles")
    private String rolesString;

    // Transient field to use Set in code
    @Transient
    private Set<String> roles = new HashSet<>();

    @PostLoad
    private void loadRoles() {
        if (rolesString != null && !rolesString.isEmpty()) {
            roles = Arrays.stream(rolesString.split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());
        }
    }

    @PrePersist
    @PreUpdate
    private void saveRoles() {
        if (roles != null && !roles.isEmpty()) {
            rolesString = String.join(",", roles);
        } else {
            rolesString = "";
        }
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getRolesString() {
        return rolesString;
    }

    public void setRolesString(String rolesString) {
        this.rolesString = rolesString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}
