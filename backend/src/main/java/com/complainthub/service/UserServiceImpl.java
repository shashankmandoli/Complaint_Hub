package com.complainthub.service;

import com.complainthub.dao.UserDaoImpl;
import com.complainthub.entity.User;
import com.complainthub.util.PasswordHasher;
import com.complainthub.util.ValidationUtil;

import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDaoImpl userDao;
    private final PasswordHasher passwordHasher;

    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
        this.passwordHasher = new PasswordHasher();
    }

    @Override
    public User createUser(User user) {
        validateUserForCreation(user);

        user.setName(user.getName().trim());
        user.setEmail(user.getEmail().trim().toLowerCase());

        User existingUser = userDao.findByEmail(user.getEmail());

        if (existingUser != null) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }

        user.setPassword(passwordHasher.hash(user.getPassword()));

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
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        ValidationUtil.validateId(user.getId(),"User Id");

        User existingUser = userDao.findById(user.getId());

        if (existingUser == null) {throw new IllegalArgumentException("User not found.");
        }

        // Validate non-password user fields
        validateUserFields(user);

        String email = user.getEmail().trim().toLowerCase();
        User userWithEmail = userDao.findByEmail(email);

        if (userWithEmail != null && userWithEmail.getId() != user.getId()) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }

        user.setName(user.getName().trim());

        user.setEmail(email);

        if (user.getPassword() == null ||
                user.getPassword().isBlank() ||
                user.getPassword().equals(existingUser.getPassword())) {
            user.setPassword(existingUser.getPassword());
        } else {
            ValidationUtil.validateMaxLength(user.getPassword(), 255, "User Password");
            user.setPassword(passwordHasher.hash(user.getPassword()));
        }

        userDao.update(user);
        return user;
    }

    // -------- Validate Methods --------
    private void validateUserForCreation(User user) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        validateUserFields(user);
        ValidationUtil.validateRequired(user.getPassword(), "User Password");
        ValidationUtil.validateMaxLength(user.getPassword(), 255, "User Password");
    }

    private void validateUserFields(User user) {

        ValidationUtil.validateRequired(user.getName(), "User Name");
        ValidationUtil.validateMaxLength(user.getName().trim(), 50, "User Name");
        ValidationUtil.validateEmail(user.getEmail());
        ValidationUtil.validateMaxLength(user.getEmail().trim(), 100, "User Email");

        if (user.getRole() == null) {
            throw new IllegalArgumentException(
                    "User Role is required."
            );
        }
    }
}