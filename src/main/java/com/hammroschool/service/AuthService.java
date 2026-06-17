package com.hammroschool.service;

import java.util.List;
import java.util.Optional;

import com.hammroschool.model.auth.UserAccount;
import com.hammroschool.model.auth.UserRole;

public interface AuthService {
    Optional<UserAccount> authenticate(String username, String password, UserRole role);

    boolean createAccount(String username, String password, UserRole role);

    List<UserAccount> getAccounts();

    /**
     * Updates the password for an existing account.
     *
     * @return {@code true} if the account was found and the password updated,
     *         {@code false} if no account with that username exists.
     */
    boolean updatePassword(String username, String newPassword);
}