package com.complainthub.service;

import com.complainthub.dao.UserDao;
import com.complainthub.dao.UserDaoImpl;
import com.complainthub.entity.User;
import com.complainthub.util.ValidationUtil;

import java.util.List;
import java.util.Locale;

public class UserServiceImpl implements UserService {
    private final UserDaoImpl userDao;

    public UserServiceImpl(){
        this.userDao = new UserDaoImpl();
    }

    @Override
    public User createUser(User user) {
        validateUser(user);

        user.setName(user.getName().trim());
        user.setEmail(user.getEmail().trim().toLowerCase());
        // Unique Email
        User existingUser = userDao.findByEmail(user.getEmail());
        if(existingUser != null)
            throw new IllegalArgumentException("A user with this email already exists.");

        userDao.save(user);
        return user;
    }

    @Override
    public User getUserById(long id) {
        ValidationUtil.validateId(id, "User Id");
        return userDao.findById(id);
    }

    @Override
    public User getUserByEmail(String email) {
        ValidationUtil.validateEmail(email);
        email = email.trim().toLowerCase();
        return userDao.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public User updateUser(User user) {
        validateUser(user);
        ValidationUtil.validateId(user.getId(), "User Id");

        User existingUser = userDao.findById(user.getId());
        if(existingUser == null)
            throw new IllegalArgumentException("User not found.");

        String email = user.getEmail().trim().toLowerCase();
        User userWithEmail = userDao.findByEmail(email);
        if(userWithEmail != null && userWithEmail.getId() != user.getId())
            throw new IllegalArgumentException("A user with this email already exists.");

        user.setName(user.getName().trim());
        user.setEmail(user.getEmail().trim());

        userDao.update(user);
        return user;
    }

    // -------- Validation --------
    private void validateUser(User user) {
        if(user == null)
            throw new IllegalArgumentException("User cannot be null.");

        ValidationUtil.validateRequired(user.getName(), "User Name");
        ValidationUtil.validateMaxLength(user.getName().trim(), 50, "User Name");
        ValidationUtil.validateEmail(user.getEmail());
        ValidationUtil.validateMaxLength(user.getEmail().trim(), 100, "User Email");
        ValidationUtil.validateRequired(user.getPassword(), "User Password");
        ValidationUtil.validateMaxLength(user.getPassword(), 255, "User Password");

        if (user.getRole() == null) {
            throw new IllegalArgumentException(
                    "User Role is required."
            );
        }
    }
}
