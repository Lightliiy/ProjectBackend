package com.example.Student.Service;

import com.example.Student.Model.ChatRoom;
import com.example.Student.Repository.ChatRoomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepo chatRoomRepo;

    public ChatRoom createRoom(ChatRoom room) {
        return chatRoomRepo.save(room);
    }

    public List<ChatRoom> getAllRooms() {
        return chatRoomRepo.findAll();
    }
}
