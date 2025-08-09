package com.example.Student.Service;

import com.example.Student.Model.Chat;
import com.example.Student.Model.Counselor;
import com.example.Student.Model.Message;
import com.example.Student.Repository.ChatRepo;
import com.example.Student.Repository.CounselorRepo;
import com.example.Student.Repository.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

//    @Autowired
//    private AgoraTokenService agoraTokenService;


    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public ChatService(ChatRepo chatRepo, CounselorRepo counselorRepo, MessageRepo messageRepo) {
        this.chatRepo = chatRepo;
        this.counselorRepo = counselorRepo;
        this.messageRepo = messageRepo;
    }

    public List<Chat> getChatsByStudentIdAndCounselorId(String studentId, String counselorId) {
        return chatRepo.findByStudentIdAndCounselorId(studentId, counselorId);
    }


    public List<Chat> getChatsByStudentId(String studentId) {
        return chatRepo.findByStudentId(studentId);
    }

    public Message saveMessage(Message message) {
        // Make sure chat entity is properly linked before saving
        if (message.getChat() != null && message.getChat().getId() != null) {
            // Optionally fetch Chat entity from DB if needed
        }
        message.setTimestamp(LocalDateTime.now());
        return messageRepo.save(message);
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
    public Chat getOrCreateChat(String counselorId, String studentId, String counselorName) {
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
        return messageRepo.findByChat_IdOrderByTimestampAsc(chatId);
    }

    public Message sendMessage(Long chatId, String senderId, String content, String attachmentUrl,
                               String counselorId, String studentId, String counselorName) {
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

        Message savedMessage = messageRepo.save(message);

        // 🔔 Broadcast via WebSocket to all listeners of /topic/chat.{chatId}
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, savedMessage);

        return savedMessage;
    }

    public void initiateVideoCall(Long counselorId, String studentId, String token) {
        String channelName = "chat-" + counselorId + "-" + studentId;

        Map<String, Object> callPayload = Map.of(
                "channelName", channelName,
                "token", token,
                "counselorId", counselorId
        );

        // Send to student subscribed to /topic/video/{studentId}
        messagingTemplate.convertAndSend("/topic/video/" + studentId, callPayload);
    }



    public void deleteChat(Long id) throws Exception {
        boolean exists = chatRepo.existsById(id);
        if (!exists) {
            throw new Exception("Chat not found with id: " + id);
        }
        chatRepo.deleteById(id);
    }

}
