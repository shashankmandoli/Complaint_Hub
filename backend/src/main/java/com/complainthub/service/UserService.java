package com.complainthub.service;

import com.complainthub.entity.User;

import java.util.List;

public interface UserService {
    User createUser(User user);
    User getUserById(long id);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    User updateUser(User user);
}
