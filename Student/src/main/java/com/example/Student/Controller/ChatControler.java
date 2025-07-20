package com.example.Student.Controller;

import com.example.Student.Model.Chat;
import com.example.Student.Model.Message;
import com.example.Student.Service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chats")
public class ChatControler {

    private final ChatService chatService;

    public ChatControler(ChatService chatService) {
        this.chatService = chatService;
    }

    // Get all chats
    @GetMapping("/all")
    public ResponseEntity<List<Chat>> getAllChats() {
        List<Chat> chats = chatService.getAllChats();
        return ResponseEntity.ok(chats);
    }

    // Get or create chat between counselor and student
    @GetMapping("/between")
    public ResponseEntity<Chat> getOrCreateChat(
            @RequestParam Long counselorId,
            @RequestParam String studentId,
            @RequestParam String counselorName) {
        Chat chat = chatService.getOrCreateChat(counselorId, studentId, counselorName);
        return ResponseEntity.ok(chat);
    }

    // Get messages in a specific chat
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable Long chatId) {
        List<Message> messages = chatService.getMessages(chatId);
        return ResponseEntity.ok(messages);
    }

    // Send a message
    @PostMapping("/{chatId}/messages")
    public ResponseEntity<Message> sendMessage(
            @PathVariable Long chatId,
            @RequestBody Map<String, Object> payload) {

        String senderId = payload.get("senderId").toString();
        String content = payload.get("content").toString();
        String attachmentUrl = payload.containsKey("attachmentUrl") && payload.get("attachmentUrl") != null
                ? payload.get("attachmentUrl").toString()
                : null;

        Long counselorId = payload.containsKey("counselorId") && payload.get("counselorId") != null
                ? Long.valueOf(payload.get("counselorId").toString())
                : null;

        String studentId = payload.containsKey("studentId") && payload.get("studentId") != null
                ? payload.get("studentId").toString()
                : null;

        String counselorName = payload.containsKey("counselorName") ? payload.get("counselorName").toString() : null;

        Message message = chatService.sendMessage(
                chatId,
                senderId,
                content,
                attachmentUrl,
                counselorId,
                studentId,
                counselorName
        );

        return ResponseEntity.status(201).body(message);
    }

}
