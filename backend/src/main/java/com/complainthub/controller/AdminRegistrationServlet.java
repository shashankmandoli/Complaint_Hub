package com.complainthub.controller;

import com.complainthub.entity.User;
import com.complainthub.entity.enums.UserRole;
import com.complainthub.service.UserService;
import com.complainthub.service.UserServiceImpl;
import com.complainthub.util.AuthenticationConstants;
import com.complainthub.util.AuthorizationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/admin/users/register")
public class AdminRegistrationServlet extends HttpServlet {
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws ServletException, IOException {
        res.setContentType("text/plain");
        res.setCharacterEncoding("UTF-8");

        try {
            AuthorizationUtil.requireRole(
                    req,
                    UserRole.ADMIN
            );
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            String password = req.getParameter("password");

            String roleParameter = req.getParameter("role");
            if (roleParameter == null || roleParameter.isBlank()) {
                throw new IllegalArgumentException("Role is required.");
            }

            UserRole requestedRole;
            try {
                requestedRole =
                        UserRole.valueOf(
                                roleParameter
                                        .trim()
                                        .toUpperCase()
                        );

            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid user role.");
            }

            if (requestedRole != UserRole.ADMIN && requestedRole != UserRole.AGENT) {
                throw new IllegalArgumentException("Only ADMIN or AGENT accounts can be created through this endpoint.");
            }

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(requestedRole);

            User createdUser = userService.createUser(user);

            res.setStatus(HttpServletResponse.SC_CREATED);
            res.getWriter().println(
                    "User registered successfully."
            );

            res.getWriter().println(
                    "User ID: " + createdUser.getId()
            );

            res.getWriter().println(
                    "Role: " + createdUser.getRole()
            );

        } catch (SecurityException e) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.getWriter().println(
                    "Access denied: "
                            + e.getMessage()
            );
        } catch (IllegalArgumentException e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().println("Registration failed: " + e.getMessage());
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().println("Unable to register user.");
        }
    }
}