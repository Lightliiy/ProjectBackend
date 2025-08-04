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
        if (message.getChat() == null && message.getChatId() != null) {
            Chat chat = new Chat();
            chat.setId(message.getChatId());
            message.setChat(chat);
        }
        Message saved = chatService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/chat/" + saved.getChat().getId(), saved);
    }


}
