package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.entity.ChatMessage;
import com.hcmute.g2webstorev2.entity.ChatRoom;
import com.hcmute.g2webstorev2.entity.Seller;
import com.hcmute.g2webstorev2.repository.ChatRoomRepo;
import com.hcmute.g2webstorev2.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepo chatRoomRepo;
    @Override
    public Optional<String> getChatRoomId(String senderId, String recipientId, boolean createNewRoomIfNotExists) {
        return chatRoomRepo
                .findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if(createNewRoomIfNotExists) {
                        var chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId);
                    }

                    return  Optional.empty();
                });
    }

    @Override
    public List<ChatRoom> getChatRooms() {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<ChatRoom> chatRooms = chatRoomRepo.findByRecipientId(seller.getEmail());
        List<ChatMessage> chatMessages = new LinkedList<>();
        chatRooms.forEach(chatRoom -> {
            int lastMsg = chatRoom.getChatMessages().size() - 1;
            chatMessages.add(chatRoom.getChatMessages().get(lastMsg));
        });
        chatMessages.sort(Comparator.comparing(ChatMessage::getTimestamp).reversed());
        List<ChatRoom> orderedChatRooms = new LinkedList<>();
        chatMessages.forEach(chatMessage -> orderedChatRooms.add(chatMessage.getChatRoom()));
        return orderedChatRooms;
    }

    private String createChatId(String senderId, String recipientId) {
        String chatId = String.format("%s_%s", senderId, recipientId); //john_smith
        LocalDateTime now = LocalDateTime.now();
        ChatRoom senderRecipient = ChatRoom
                .builder()
                .id(senderId + "_" + recipientId + "_" + now)
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();

        ChatRoom recipientSender = ChatRoom
                .builder()
                .id(recipientId + "_" + senderId + "_" + now)
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();

        chatRoomRepo.save(senderRecipient);
        chatRoomRepo.save(recipientSender);

        return chatId;
    }
}
