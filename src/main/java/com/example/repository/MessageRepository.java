package com.example.repository;

import com.example.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    // Find all messages by a specific user
    List<Message> findByPostedBy(int accountId);

    // Update message text by messageId
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.messageText = :messageText WHERE m.messageId = :messageId")
    int updateMessageText(@Param("messageId") int messageId, @Param("messageText") String messageText);
}
