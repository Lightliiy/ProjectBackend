package com.example.Student.Service;

import com.example.Student.Model.Chat;
import com.example.Student.Model.Message;
import com.example.Student.Repository.ChatRepo;
import com.example.Student.Repository.MessageRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatService {

    private final ChatRepo chatRepo;
    private final MessageRepo messageRepo;

    public List<Chat> getAllChats() {
        return chatRepo.findAll();
    }


    public ChatService(ChatRepo chatRepo, MessageRepo messageRepo) {
        this.chatRepo = chatRepo;
        this.messageRepo = messageRepo;
    }

    // Create or get existing chat between counselor and student
    @Transactional
    public Chat getOrCreateChat(Long counselorId, Long studentId) {
        return chatRepo.findByCounselorIdAndStudentId(counselorId, studentId)
                .orElseGet(() -> {
                    Chat chat = new Chat();
                    chat.setCounselorId(counselorId);
                    chat.setStudentId(studentId);
                    return chatRepo.save(chat);
                });
    }

    public List<Message> getMessages(Long chatId) {
        return messageRepo.findByChatIdOrderByTimestampAsc(chatId);
    }

    public Message sendMessage(Long chatId, Long senderId, String content, String attachmentUrl) {
        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        Message message = new Message();
        message.setChat(chat);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setAttachmentUrl(attachmentUrl);

        return messageRepo.save(message);
    }
}
