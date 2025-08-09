package com.example.Student.Controller;

import com.example.Student.Model.Chat;
import com.example.Student.Model.Counselor;
import com.example.Student.Model.Message;
import com.example.Student.Model.Student;
import com.example.Student.Repository.ChatRepo;
import com.example.Student.Service.ChatService;
import com.example.Student.Service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chats")
public class ChatControler {

    private ChatRepo chatRepo;

    private final ChatService chatService;

    private StudentService studentService;

    private static final Logger logger = LoggerFactory.getLogger(ChatControler.class);

    public ChatControler(ChatService chatService, StudentService studentService, ChatRepo chatRepo) {
        this.chatService = chatService;
        this.chatRepo = chatRepo;
        this.studentService = studentService;
    }

    // Get all chats
    @GetMapping("/all")
    public ResponseEntity<List<Chat>> getAllChats() {
        List<Chat> chats = chatService.getAllChats();
        return ResponseEntity.ok(chats);
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<Chat>> getChatsForAssignedCounselor(@RequestParam String studentId) {
        try {
            // Correct way to handle the Optional return type
            Optional<Student> optionalStudent = studentService.findByStudentId(studentId);

            if (optionalStudent.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Student student = optionalStudent.get(); // Get the Student object from the Optional

            Counselor counselor = student.getCounselor();
            if (counselor == null) {
                return ResponseEntity.ok(List.of());
            }

            List<Chat> chats = chatService.getChatsByStudentIdAndCounselorId(studentId, String.valueOf(counselor.getId()));

            // FIX: Add the counselor's name to each Chat object
            String counselorName = counselor.getName();
            for (Chat chat : chats) {
                chat.setCounselorName(counselorName);
            }

            return ResponseEntity.ok(chats);
        } catch (Exception e) {
            // You should have a logger defined for this to work
            logger.error("Error fetching chats for assigned counselor: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    // Get chats by student ID
    @GetMapping
    public ResponseEntity<List<Chat>> getChatsByStudentId(@RequestParam String studentId) {
        List<Chat> chats = chatService.getChatsByStudentId(studentId);
        return ResponseEntity.ok(chats);
    }

    // Get or create chat between counselor and student
    @GetMapping("/between")
    public ResponseEntity<Chat> getOrCreateChat(
            @RequestParam String counselorId,
            @RequestParam String studentId,
            @RequestParam String counselorName
    ) {
        // Try to find existing chat for the counselor-student pair
        Optional<Chat> existingChat = chatRepo.findByCounselorIdAndStudentId(counselorId, studentId);

        if (existingChat.isPresent()) {
            return ResponseEntity.ok(existingChat.get());
        }

        // If not found, create a new chat
        Chat newChat = new Chat();
        newChat.setCounselorId(counselorId);
        newChat.setStudentId(studentId);
        newChat.setCounselorName(counselorName);
        Chat savedChat = chatRepo.save(newChat);

        return ResponseEntity.ok(savedChat);
    }

    // Get messages in a specific chat
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable Long chatId) {
        List<Message> messages = chatService.getMessages(chatId);
        return ResponseEntity.ok(messages);
    }

    // Send a message
    @PostMapping("/{chatId}/messages")
    public ResponseEntity<?> sendMessage(
            @PathVariable Long chatId,
            @RequestBody Map<String, Object> payload) {
        try {
            String senderId = payload.get("senderId").toString();
            String content = payload.get("content").toString();
            String attachmentUrl = payload.containsKey("attachmentUrl") && payload.get("attachmentUrl") != null
                    ? payload.get("attachmentUrl").toString()
                    : null;

            // FIX: Remove the Long.valueOf conversion. The ID should be a String.
            String counselorId = payload.containsKey("counselorId") && payload.get("counselorId") != null
                    ? payload.get("counselorId").toString()
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
        } catch (Exception e) {
            logger.error("Error sending message in chatId={}: {}", chatId, e.getMessage(), e);
            return ResponseEntity.status(500).body("Failed to send message");
        }
    }

    // Delete chat by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteChat(@PathVariable Long id) {
        try {
            chatService.deleteChat(id);
            return ResponseEntity.ok("Chat deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting chat with id={}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(404).body("Delete failed: " + e.getMessage());
        }
    }
}