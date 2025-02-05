package com.example.service;

import com.example.entity.Message;
import com.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    // Create New Message
    public Optional<Message> createMessage(Message message) {
        // 1. Validate message text is not blank
        if (message.getMessageText() == null || message.getMessageText().trim().isEmpty()) {
            return Optional.empty();
        }
    
        // 2. Validate message text length (255 characters max)
        if (message.getMessageText().length() > 255) {
            return Optional.empty();
        }
    
        // 3. Validate that postedBy refers to an existing user
        if (!accountRepository.existsById(message.getPostedBy())) {
            return Optional.empty();
        }
    
        // Save and return the new message
        return Optional.of(messageRepository.save(message));
    }
    

    // Get All Messages
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    // Get Message by ID
    public Optional<Message> getMessageById(Integer messageId) {
        return messageRepository.findById(messageId);
    }

    // Get Messages by Account ID
    public List<Message> getMessagesByAccountId(Integer accountId) {
        return messageRepository.findByPostedBy(accountId);
    }

    // Update Message
    public int updateMessage(int messageId, String newMessageText) {
        // 1. Validate new message text is not blank
        if (newMessageText == null || newMessageText.trim().isEmpty()) {
            return 0; // Failure
        }
    
        // 2. Validate new message text length (255 characters max)
        if (newMessageText.length() > 255) {
            return 0; // Failure
        }
    
        // 3. Check if the message exists
        Optional<Message> existingMessage = messageRepository.findById(messageId);
        if (existingMessage.isEmpty()) {
            return 0; // Failure
        }
    
        // 4. Update the message text
        return messageRepository.updateMessageText(messageId, newMessageText); // Success
    }
    

    // Delete Message by ID
    public boolean deleteMessage(Integer messageId) {
        Optional<Message> messageOptional = messageRepository.findById(messageId);
        if (messageOptional.isPresent()) {
            messageRepository.delete(messageOptional.get());
            return true;
        }
        return false;
    }
}