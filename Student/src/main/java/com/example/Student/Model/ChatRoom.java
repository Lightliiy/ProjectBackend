package com.example.Student.Model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ElementCollection
    private Set<String> participants; // could be usernames or user IDs

    // Constructors, Getters, Setters
}
