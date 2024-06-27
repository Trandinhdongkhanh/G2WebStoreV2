package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.entity.ChatMessage;
import com.hcmute.g2webstorev2.repository.ChatMessageRepo;
import com.hcmute.g2webstorev2.service.ChatMessageService;
import com.hcmute.g2webstorev2.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepo chatMessageRepo;
    private final ChatRoomService chatRoomService;
    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        var chatId = chatRoomService
                .getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true)
                .orElseThrow(); // You can create your own dedicated exception
        chatMessage.setChatId(chatId);
        chatMessageRepo.save(chatMessage);
        return chatMessage;
    }

    @Override
    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        return chatId.map(chatMessageRepo::findByChatId).orElse(new ArrayList<>());
    }
}
