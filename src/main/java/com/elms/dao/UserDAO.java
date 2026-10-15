package com.elms.dao;

import com.elms.model.User;

import java.sql.Connection;
import java.util.Optional;

public interface UserDAO {
    Optional<User> findById(int id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    int insert(User user, Connection conn);
    boolean updatePassword(int userId, String passwordHash);
    boolean updateStatus(int userId, boolean isActive);
}
