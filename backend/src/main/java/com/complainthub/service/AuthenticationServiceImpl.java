package com.complainthub.service;

import com.complainthub.dao.UserDao;
import com.complainthub.dao.UserDaoImpl;
import com.complainthub.entity.User;
import com.complainthub.util.PasswordHasher;
import com.complainthub.util.ValidationUtil;

public class AuthenticationServiceImpl implements AuthenticationService{
    private final UserDao userDao;
    private final PasswordHasher passwordHasher;

    public AuthenticationServiceImpl(){
        this.userDao = new UserDaoImpl();
        this.passwordHasher = new PasswordHasher();
    }

    @Override
    public User authenticate(String email, String password) {
        ValidationUtil.validateRequired(email, "Email");
        ValidationUtil.validateRequired(password, "Password");

        email = email.trim().toLowerCase();

        User user = userDao.findByEmail(email);
        if(user == null){
            throw new IllegalArgumentException("Invalid email or password.");
        }

        boolean passwordMatch = passwordHasher.verify(password, user.getPassword());
        if(!passwordMatch){
            throw new IllegalArgumentException("Invalid user or password.");
        }

        return user;
    }
}
