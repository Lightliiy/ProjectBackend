package com.example.Student.Repository;

import com.example.Student.Model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepo extends JpaRepository<ChatRoom, Long> {
}
