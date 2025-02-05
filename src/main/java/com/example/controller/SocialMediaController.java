package com.example.controller;

import com.example.entity.*;
import com.example.service.*;
import com.example.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class SocialMediaController {

    private final MessageService messageService;
    private final AccountRepository accountRepository;

    public SocialMediaController(MessageService messageService, AccountRepository accountRepository) {
        this.messageService = messageService;
        this.accountRepository = accountRepository;
    }

    // 1. Register a new user
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Account account) {
        // 1. Ensure username is not blank
        if (account.getUsername() == null || account.getUsername().trim().isEmpty())
            return ResponseEntity.status(400).body("Username cannot be blank.");

        // 2. Ensure password meets minimum length requirement
        if (account.getPassword() == null || account.getPassword().length() < 4)
            return ResponseEntity.status(400).body("Password must be at least 4 characters long.");

        // 3. Check if the username is already taken
        if (accountRepository.findByUsername(account.getUsername()).isPresent())
            return ResponseEntity.status(409).body("Username is already taken."); // 409 Conflict
    
        // 4. Save the new user and return response
        Account savedAccount = accountRepository.save(account);
        return ResponseEntity.ok(savedAccount);
    }

    // 2. Login a user
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account account) {
        // 1. Ensure username and password are provided
        if (account.getUsername() == null || account.getUsername().trim().isEmpty() ||
            account.getPassword() == null || account.getPassword().trim().isEmpty())
            return ResponseEntity.status(400).body("Username and password cannot be blank.");

        // 2. Find user by username
        Optional<Account> existingAccount = accountRepository.findByUsername(account.getUsername());
        
        // 3. Validate account existence and password match
        if (existingAccount.isEmpty() || !existingAccount.get().getPassword().equals(account.getPassword()))
            return ResponseEntity.status(401).body("Invalid username or password.");

        // 4. Return successful login response
        return ResponseEntity.ok(existingAccount.get());
    }

    // 3. Post a new message
    @PostMapping("/messages")
    public ResponseEntity<?> createMessage(@RequestBody Message message) {
        Optional<Message> createdMessage = messageService.createMessage(message);
    
        return createdMessage.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(400).build()); // Return 400 if validation fails
    }

    // 4. Get all messages
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    // 5. Get a message by ID
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<?> getMessageById(@PathVariable int messageId) {
        Optional<Message> message = messageService.getMessageById(messageId);
        return message.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok().build()); // Empty response if not found
    }

    // 6. Delete a message by ID
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Integer> deleteMessage(@PathVariable int messageId) {
        int rowsDeleted = messageService.deleteMessage(messageId) ? 1 : 0;
        if(rowsDeleted == 0)
            return ResponseEntity.ok().build();
        
        return ResponseEntity.ok(rowsDeleted);
    }

    // 7. Update a message by ID
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<?> updateMessage(@PathVariable int messageId, @RequestBody Message message) {
        // Ensure messageText is present in the request
        if (message.getMessageText() == null || message.getMessageText().trim().isEmpty())
            return ResponseEntity.status(400).body("Message text cannot be blank.");
        // Ensure messageText does not exceed 255 characters
        if (message.getMessageText().length() > 255)
            return ResponseEntity.status(400).body("Message text cannot exceed 255 characters.");
    
        int rowsUpdated = messageService.updateMessage(messageId, message.getMessageText());
    
        if (rowsUpdated == 0)
            return ResponseEntity.status(400).body("Message not found or update failed.");
    
        return ResponseEntity.ok(rowsUpdated);
    }

    // 8. Get all messages posted by a specific user
    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByUser(@PathVariable int accountId) {
        return ResponseEntity.ok(messageService.getMessagesByAccountId(accountId));
    }
}