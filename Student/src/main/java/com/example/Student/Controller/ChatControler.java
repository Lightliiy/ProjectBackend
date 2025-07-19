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

    // Create or get chat between counselor and student

    @GetMapping("/all")
    public ResponseEntity<List<Chat>> getAllChats() {
        List<Chat> chats = chatService.getAllChats();
        return ResponseEntity.ok(chats);
    }

    @PostMapping("/between")
    public ResponseEntity<Chat> getOrCreateChat(
            @RequestParam Long counselorId,
            @RequestParam Long studentId) {
        Chat chat = chatService.getOrCreateChat(counselorId, studentId);
        return ResponseEntity.ok(chat);
    }

    // Get all messages for a chat
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable Long chatId) {
        List<Message> messages = chatService.getMessages(chatId);
        return ResponseEntity.ok(messages);
    }

    // Send a message in chat without DTO
    @PostMapping("/{chatId}/messages")
    public ResponseEntity<Message> sendMessage(
            @PathVariable Long chatId,
            @RequestBody Map<String, Object> payload) {

        Long senderId = Long.valueOf(payload.get("senderId").toString());
        String content = payload.get("content").toString();
        String attachmentUrl = null;
        if (payload.containsKey("attachmentUrl")) {
            attachmentUrl = payload.get("attachmentUrl") != null ? payload.get("attachmentUrl").toString() : null;
        }

        Message message = chatService.sendMessage(chatId, senderId, content, attachmentUrl);
        return ResponseEntity.status(201).body(message);
    }
}
