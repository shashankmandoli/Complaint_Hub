package com.complainthub.controller;

import com.complainthub.entity.User;
import com.complainthub.entity.enums.UserRole;
import com.complainthub.service.UserService;
import com.complainthub.service.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/auth/register")
public class RegistrationServlet extends HttpServlet {
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws ServletException, IOException {
        res.setContentType("text/plain");
        res.setCharacterEncoding("UTF-8");

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(UserRole.USER);

            User createdUser = userService.createUser(user);

            res.setStatus(
                    HttpServletResponse.SC_CREATED
            );

            res.getWriter().println(
                    "Registration successful."
            );

            res.getWriter().println(
                    "User ID: " + createdUser.getId()
            );

            res.getWriter().println(
                    "Role: " + createdUser.getRole()
            );

        } catch (IllegalArgumentException e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().println(
                    "Registration failed: "
                            + e.getMessage()
            );
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().println("Unable to register user.");
        }
    }
}