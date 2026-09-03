package com.complainthub.util;

import java.util.Objects;

public class ValidationUtil {
    private ValidationUtil(){
    }

    public static void validateId(long id, String fieldName){
        if(id <= 0)
            throw new IllegalArgumentException(fieldName + " must be a valid +ve number.");
    }

    public static void  validateRequired(String value, String fieldName){
        if(value == null || value.isBlank())
            throw new IllegalArgumentException(fieldName + " is required.");
    }

    public static void validateMaxLength(String value, int maxLength, String fieldName){
        if(value == null || value.length() > maxLength)
            throw new IllegalArgumentException(fieldName + " cannot exceed " + maxLength + " characters.");
    }

    public static void validateEmail(String email){
        validateRequired(email, "Email");
        String emailRegex = "^[A-Za-z0-9+_.-]+@[]A-Za-z0-9.-]+$";

        if(!email.matches(emailRegex))
            throw new IllegalArgumentException("Email format is invalid!");
    }
}
