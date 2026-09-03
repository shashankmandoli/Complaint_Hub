package com.complainthub.test;

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
import java.util.List;

@WebServlet("/api/test/user-service")
public class UserServiceTestServlet extends HttpServlet {

    private final UserService userService =
            new UserServiceImpl();


    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if (action == null || action.isBlank()) {

            response.getWriter().println(
                    "Action parameter is required."
            );

            return;
        }

        try {

            switch (action) {

                case "create":
                    createUser(request, response);
                    break;

                case "findById":
                    findUserById(request, response);
                    break;

                case "findByEmail":
                    findUserByEmail(request, response);
                    break;

                case "findAll":
                    findAllUsers(response);
                    break;

                case "update":
                    updateUser(request, response);
                    break;

                default:
                    response.getWriter().println(
                            "Invalid action."
                    );
            }

        } catch (IllegalArgumentException e) {

            response.getWriter().println(
                    "Validation Error: "
                            + e.getMessage()
            );

        } catch (Exception e) {

            response.getWriter().println(
                    "Error: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // CREATE USER
    // =====================================================

    private void createUser(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String name =
                request.getParameter("name");

        String email =
                request.getParameter("email");

        String password =
                request.getParameter("password");

        String roleParameter =
                request.getParameter("role");


        if (roleParameter == null ||
                roleParameter.isBlank()) {

            throw new IllegalArgumentException(
                    "User Role is required."
            );
        }


        UserRole role;

        try {

            role = UserRole.valueOf(
                    roleParameter.trim().toUpperCase()
            );

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException(
                    "Invalid role. Use USER, AGENT, or ADMIN."
            );
        }


        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);


        User savedUser =
                userService.createUser(user);


        response.getWriter().println(
                "User created successfully."
        );

        printUser(
                savedUser,
                response
        );
    }


    // =====================================================
    // FIND USER BY ID
    // =====================================================

    private void findUserById(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        long id =
                parseId(request.getParameter("id"));


        User user =
                userService.getUserById(id);


        if (user == null) {

            response.getWriter().println(
                    "User not found."
            );

            return;
        }


        printUser(
                user,
                response
        );
    }


    // =====================================================
    // FIND USER BY EMAIL
    // =====================================================

    private void findUserByEmail(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String email =
                request.getParameter("email");


        User user =
                userService.getUserByEmail(email);


        if (user == null) {

            response.getWriter().println(
                    "User not found."
            );

            return;
        }


        printUser(
                user,
                response
        );
    }


    // =====================================================
    // FIND ALL USERS
    // =====================================================

    private void findAllUsers(
            HttpServletResponse response
    ) throws IOException {

        List<User> users =
                userService.getAllUsers();


        if (users.isEmpty()) {

            response.getWriter().println(
                    "No users found."
            );

            return;
        }


        for (User user : users) {

            printUser(
                    user,
                    response
            );

            response.getWriter().println(
                    "-------------------------"
            );
        }
    }


    // =====================================================
    // UPDATE USER
    // =====================================================

    private void updateUser(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        long id =
                parseId(request.getParameter("id"));


        User existingUser =
                userService.getUserById(id);


        if (existingUser == null) {

            response.getWriter().println(
                    "User not found."
            );

            return;
        }


        String name =
                request.getParameter("name");

        String email =
                request.getParameter("email");

        String password =
                request.getParameter("password");

        String roleParameter =
                request.getParameter("role");


        if (name != null) {
            existingUser.setName(name);
        }

        if (email != null) {
            existingUser.setEmail(email);
        }

        if (password != null) {
            existingUser.setPassword(password);
        }

        if (roleParameter != null &&
                !roleParameter.isBlank()) {

            try {

                existingUser.setRole(
                        UserRole.valueOf(
                                roleParameter
                                        .trim()
                                        .toUpperCase()
                        )
                );

            } catch (IllegalArgumentException e) {

                throw new IllegalArgumentException(
                        "Invalid role. Use USER, AGENT, or ADMIN."
                );
            }
        }


        User updatedUser =
                userService.updateUser(
                        existingUser
                );


        response.getWriter().println(
                "User updated successfully."
        );

        printUser(
                updatedUser,
                response
        );
    }


    // =====================================================
    // HELPER METHODS
    // =====================================================

    private long parseId(String value) {

        if (value == null ||
                value.isBlank()) {

            throw new IllegalArgumentException(
                    "User ID is required."
            );
        }

        try {

            return Long.parseLong(value);

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "User ID must be a valid number."
            );
        }
    }


    private void printUser(
            User user,
            HttpServletResponse response
    ) throws IOException {

        response.getWriter().println(
                "ID: " + user.getId()
        );

        response.getWriter().println(
                "Name: " + user.getName()
        );

        response.getWriter().println(
                "Email: " + user.getEmail()
        );

        response.getWriter().println(
                "Role: " + user.getRole()
        );

        response.getWriter().println(
                "Created At: " + user.getCreatedAt()
        );

        response.getWriter().println(
                "Updated At: " + user.getUpdatedAt()
        );
    }
}