package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.entity.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    ChatMessage save(ChatMessage chatMessage);
    List<ChatMessage> findChatMessages(String senderId, String recipientId);
}
