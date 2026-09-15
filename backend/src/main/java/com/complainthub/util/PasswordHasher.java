package com.complainthub.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    private static final int BCRYPT_ROUNDS = 12;

    public String hash(String password){
        if(password == null || password.isBlank()){
            throw new IllegalArgumentException("Password cannot be blank or null.");
        }
        return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    public boolean verify(String password, String storedHash){
        if(password == null || storedHash == null){
            return false;
        }
        return BCrypt.checkpw(password, storedHash);
    }
}
