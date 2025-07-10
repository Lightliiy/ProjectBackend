package com.example.Student.Controller;

import com.example.Student.Model.ChatRoom;
import com.example.Student.Service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatrooms")
public class ChatRoomControler {

    @Autowired
    private ChatRoomService chatRoomService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ChatRoom createRoom(@RequestBody ChatRoom room) {
        return chatRoomService.createRoom(room);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<ChatRoom> getRooms() {
        return chatRoomService.getAllRooms();
    }
}
