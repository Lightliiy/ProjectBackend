package com.example.Student.Controller;

import com.example.Student.Model.Chat;
import com.example.Student.Model.Message;
import com.example.Student.Service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketControler {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatWebSocketControler(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/send")
    public void receiveMessage(Message message) {
        // If chat object is missing but chatId is present, build minimal Chat object
        if (message.getChat() == null && message.getChatId() != null) {
            Chat chat = new Chat();
            chat.setId(message.getChatId());
            message.setChat(chat);
        }

        // Defensive check before saving
        if (message.getChat() == null || message.getChat().getId() == null) {
            // Log and ignore or throw exception based on your app logic
            System.err.println("Message received without valid chat or chatId. Ignoring.");
            return;
        }

        Message saved = chatService.saveMessage(message);

        if (saved.getChat() == null || saved.getChat().getId() == null) {
            System.err.println("Saved message has no chat or chatId after saving. Cannot send message.");
            return;
        }

        messagingTemplate.convertAndSend("/topic/chat/" + saved.getChat().getId(), saved);
    }


}
