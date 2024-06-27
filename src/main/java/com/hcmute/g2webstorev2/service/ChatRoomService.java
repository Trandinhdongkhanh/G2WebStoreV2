package com.hcmute.g2webstorev2.service;

import java.util.Optional;

public interface ChatRoomService {
    Optional<String> getChatRoomId(String senderId, String recipientId, boolean createNewRoomIfNotExists);
}
