package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Registers a new user if the username is unique and password is valid.
     * @param account The account to register.
     * @return The saved account or null if registration fails.
     */
    public Account registerUser(Account account) {
        // Validation: Username must not be blank, password must be at least 4 characters
        if (account.getUsername() == null || account.getUsername().isBlank() ||
            account.getPassword() == null || account.getPassword().length() < 4) {
            return null; // Indicating failure
        }

        // Check if username is already taken
        if (accountRepository.findByUsername(account.getUsername()).isPresent()) {
            return null; // Username already exists
        }

        // Save and return new account
        return accountRepository.save(account);
    }

    /**
     * Authenticates a user based on username and password.
     * @param username The username.
     * @param password The password.
     * @return The authenticated account or null if login fails.
     */
    public Account login(String username, String password) {
        Optional<Account> optionalAccount = accountRepository.findByUsername(username);

        // Validate credentials
        if (optionalAccount.isPresent() && optionalAccount.get().getPassword().equals(password)) {
            return optionalAccount.get();
        }

        return null; // Login failed
    }
}