package com.complainthub.service;

import com.complainthub.entity.User;

public interface AuthenticationService {
    User authenticate(String email, String password);
}
