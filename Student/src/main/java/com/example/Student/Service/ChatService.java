package com.example.Student.Service;

import com.example.Student.Model.Chat;
import com.example.Student.Model.Counselor;
import com.example.Student.Model.Message;
import com.example.Student.Repository.ChatRepo;
import com.example.Student.Repository.CounselorRepo;
import com.example.Student.Repository.MessageRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatRepo chatRepo;
    private final CounselorRepo counselorRepo;
    private final MessageRepo messageRepo;

    public ChatService(ChatRepo chatRepo, CounselorRepo counselorRepo, MessageRepo messageRepo) {
        this.chatRepo = chatRepo;
        this.counselorRepo = counselorRepo;
        this.messageRepo = messageRepo;
    }

    public List<Chat> getAllChats() {
        List<Chat> chats = chatRepo.findAll();

        // Fetch all counselors once
        Map<Long, String> counselorNames = counselorRepo.findAll().stream()
                .collect(Collectors.toMap(Counselor::getId, Counselor::getName));

        // Set counselorName on each chat
        for (Chat chat : chats) {
            String name = counselorNames.get(chat.getCounselorId());
            chat.setCounselorName(name != null ? name : "Counselor " + chat.getCounselorId());
        }

        return chats;
    }

    // Create or get existing chat between counselor and student
    @Transactional
    public Chat getOrCreateChat(Long counselorId, String studentId, String counselorName) {
        return chatRepo.findByCounselorIdAndStudentId(counselorId, studentId)
                .orElseGet(() -> {
                    Chat chat = new Chat();
                    chat.setCounselorId(counselorId);
                    chat.setCounselorName(counselorName);
                    chat.setStudentId(studentId);
                    return chatRepo.save(chat);
                });
    }

    public List<Message> getMessages(Long chatId) {
        return messageRepo.findByChatIdOrderByTimestampAsc(chatId);
    }

    public Message sendMessage(Long chatId, String senderId, String content, String attachmentUrl, Long counselorId, String studentId, String counselorName) {
        Chat chat = chatRepo.findById(chatId).orElse(null);

        if (chat == null) {
            if (counselorId == null || studentId == null) {
                throw new RuntimeException("Chat not found and counselorId/studentId not provided to create chat.");
            }
            chat = getOrCreateChat(counselorId, studentId, counselorName);
        }

        Message message = new Message();
        message.setChat(chat);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setAttachmentUrl(attachmentUrl);
        message.setTimestamp(LocalDateTime.now());

        return messageRepo.save(message);
    }

}
