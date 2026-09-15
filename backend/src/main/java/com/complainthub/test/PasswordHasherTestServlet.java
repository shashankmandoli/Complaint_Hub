package com.complainthub.test;

import com.complainthub.util.PasswordHasher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/test/password-hasher")
public class PasswordHasherTestServlet extends HttpServlet {

    private final PasswordHasher passwordHasher = new PasswordHasher();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        String password = "TestPassword123!";

        String hash1 = passwordHasher.hash(password);
        String hash2 = passwordHasher.hash(password);

        boolean correctPassword = passwordHasher.verify(password, hash1);
        boolean wrongPassword =
                passwordHasher.verify("WrongPassword123!", hash1);

        response.setContentType("text/plain");

        response.getWriter().println("Hash 1: " + hash1);
        response.getWriter().println("Hash 2: " + hash2);
        response.getWriter().println();
        response.getWriter().println(
                "Hash 1 != Hash 2: " + !hash1.equals(hash2)
        );
        response.getWriter().println(
                "Correct password: " + correctPassword
        );
        response.getWriter().println(
                "Wrong password: " + wrongPassword
        );
    }
}