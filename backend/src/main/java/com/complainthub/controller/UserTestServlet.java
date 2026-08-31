package com.complainthub.controller;

import com.complainthub.dao.UserDao;
import com.complainthub.dao.UserDaoImpl;
import com.complainthub.entity.User;
import com.complainthub.entity.enums.UserRole;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/test/user")
public class UserTestServlet extends HttpServlet {

    private final UserDao userDao =
            new UserDaoImpl();


    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        resp.setContentType("text/plain");

        String action = req.getParameter("action");

        if (action == null) {

            resp.getWriter().println(
                    "Please provide an action."
            );

            return;
        }

        try {

            switch (action) {

                case "save" ->
                        saveUser(req, resp);

                case "findById" ->
                        findById(req, resp);

                case "findByEmail" ->
                        findByEmail(req, resp);

                case "findAll" ->
                        findAll(resp);

                case "findByRole" ->
                        findByRole(req, resp);

                case "update" ->
                        updateUser(req, resp);

                default ->
                        resp.getWriter().println(
                                "Invalid action."
                        );
            }

        } catch (Exception e) {

            resp.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            resp.getWriter().println(
                    "Error: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // SAVE USER
    // =====================================================

    private void saveUser(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        String name =
                req.getParameter("name");

        String email =
                req.getParameter("email");

        String password =
                req.getParameter("password");

        String roleParam =
                req.getParameter("role");


        if (name == null ||
                email == null ||
                password == null ||
                roleParam == null) {

            resp.getWriter().println(
                    "Missing required parameters."
            );

            return;
        }


        User existingUser =
                userDao.findByEmail(email);

        if (existingUser != null) {

            resp.getWriter().println(
                    "User with this email already exists."
            );

            return;
        }


        UserRole role;

        try {

            role = UserRole.valueOf(
                    roleParam.toUpperCase()
            );

        } catch (IllegalArgumentException e) {

            resp.getWriter().println(
                    "Invalid role. Use USER, AGENT, or ADMIN."
            );

            return;
        }


        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);


        User savedUser =
                userDao.save(user);


        resp.getWriter().println(
                "User saved successfully."
        );

        resp.getWriter().println(
                "User ID: "
                        + savedUser.getId()
        );
    }


    // =====================================================
    // FIND USER BY ID
    // =====================================================

    private void findById(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long id = Long.parseLong(
                req.getParameter("id")
        );


        User user =
                userDao.findById(id);


        if (user == null) {

            resp.getWriter().println(
                    "User not found."
            );

            return;
        }


        printUser(resp, user);
    }


    // =====================================================
    // FIND USER BY EMAIL
    // =====================================================

    private void findByEmail(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        String email =
                req.getParameter("email");


        if (email == null ||
                email.isBlank()) {

            resp.getWriter().println(
                    "Email is required."
            );

            return;
        }


        User user =
                userDao.findByEmail(email);


        if (user == null) {

            resp.getWriter().println(
                    "User not found."
            );

            return;
        }


        printUser(resp, user);
    }


    // =====================================================
    // FIND ALL USERS
    // =====================================================

    private void findAll(
            HttpServletResponse resp
    ) throws IOException {

        List<User> users =
                userDao.findAll();


        if (users.isEmpty()) {

            resp.getWriter().println(
                    "No users found."
            );

            return;
        }


        for (User user : users) {

            printUser(resp, user);

            resp.getWriter().println(
                    "-----------------------------"
            );
        }
    }


    // =====================================================
    // FIND USERS BY ROLE
    // =====================================================

    private void findByRole(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        String roleParam =
                req.getParameter("role");


        if (roleParam == null ||
                roleParam.isBlank()) {

            resp.getWriter().println(
                    "Role is required."
            );

            return;
        }


        UserRole role;

        try {

            role = UserRole.valueOf(
                    roleParam.toUpperCase()
            );

        } catch (IllegalArgumentException e) {

            resp.getWriter().println(
                    "Invalid role. Use USER, AGENT, or ADMIN."
            );

            return;
        }


        List<User> users =
                userDao.findByRole(role);


        if (users.isEmpty()) {

            resp.getWriter().println(
                    "No users found with this role."
            );

            return;
        }


        for (User user : users) {

            printUser(resp, user);

            resp.getWriter().println(
                    "-----------------------------"
            );
        }
    }


    // =====================================================
    // UPDATE USER
    // =====================================================

    private void updateUser(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        long id = Long.parseLong(
                req.getParameter("id")
        );


        User user =
                userDao.findById(id);


        if (user == null) {

            resp.getWriter().println(
                    "User not found."
            );

            return;
        }


        String name =
                req.getParameter("name");

        String email =
                req.getParameter("email");

        String password =
                req.getParameter("password");

        String roleParam =
                req.getParameter("role");


        if (name != null &&
                !name.isBlank()) {

            user.setName(name);
        }


        if (email != null &&
                !email.isBlank()) {

            User existingUser =
                    userDao.findByEmail(email);


            if (existingUser != null &&
                    existingUser.getId() != user.getId()) {

                resp.getWriter().println(
                        "Another user already uses this email."
                );

                return;
            }

            user.setEmail(email);
        }


        if (password != null &&
                !password.isBlank()) {

            user.setPassword(password);
        }


        if (roleParam != null &&
                !roleParam.isBlank()) {

            try {

                UserRole role =
                        UserRole.valueOf(
                                roleParam.toUpperCase()
                        );

                user.setRole(role);

            } catch (IllegalArgumentException e) {

                resp.getWriter().println(
                        "Invalid role. Use USER, AGENT, or ADMIN."
                );

                return;
            }
        }


        User updatedUser =
                userDao.update(user);


        resp.getWriter().println(
                "User updated successfully."
        );

        printUser(resp, updatedUser);
    }


    // =====================================================
    // PRINT USER
    // =====================================================

    private void printUser(
            HttpServletResponse resp,
            User user
    ) throws IOException {

        resp.getWriter().println(
                "ID: " + user.getId()
        );

        resp.getWriter().println(
                "Name: " + user.getName()
        );

        resp.getWriter().println(
                "Email: " + user.getEmail()
        );

        resp.getWriter().println(
                "Role: " + user.getRole()
        );

        resp.getWriter().println(
                "Created At: "
                        + user.getCreatedAt()
        );

        resp.getWriter().println(
                "Updated At: "
                        + user.getUpdatedAt()
        );
    }
}