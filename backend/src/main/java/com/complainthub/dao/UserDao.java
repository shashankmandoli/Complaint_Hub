package com.complainthub.dao;

import com.complainthub.entity.User;
import com.complainthub.entity.enums.UserRole;

import java.util.List;

public interface UserDao {
    User save(User user);

    User findById(long id);

    User findByEmail(String email);

    List<User> findAll();

    List<User> findByRole(UserRole role);

    User update(User user);
}
