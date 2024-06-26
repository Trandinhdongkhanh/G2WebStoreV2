package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepo extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findByChatId(String chatId);
}
