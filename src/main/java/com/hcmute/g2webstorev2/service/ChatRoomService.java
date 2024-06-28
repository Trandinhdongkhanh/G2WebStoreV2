package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomService {
    Optional<String> getChatRoomId(String senderId, String recipientId, boolean createNewRoomIfNotExists);
    public List<ChatRoom> getChatRooms();
}
