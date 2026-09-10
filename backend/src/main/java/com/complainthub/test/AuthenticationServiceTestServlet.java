package com.complainthub.test;

import com.complainthub.entity.User;
import com.complainthub.service.AuthenticationService;
import com.complainthub.service.AuthenticationServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/test/authentication")
public class AuthenticationServiceTestServlet extends HttpServlet {

    private final AuthenticationService authenticationService =
            new AuthenticationServiceImpl();

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

            response.getWriter().println();
            response.getWriter().println(
                    "Available actions:"
            );
            response.getWriter().println(
                    "authenticate"
            );

            return;
        }

        try {

            switch (action) {

                case "authenticate":
                    authenticateUser(request, response);
                    break;

                default:
                    response.getWriter().println(
                            "Invalid action."
                    );
            }

        } catch (IllegalArgumentException e) {

            response.getWriter().println(
                    "Authentication Error: "
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
    // AUTHENTICATE USER
    // =====================================================

    private void authenticateUser(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String email =
                request.getParameter("email");

        String password =
                request.getParameter("password");


        User authenticatedUser =
                authenticationService.authenticate(
                        email,
                        password
                );


        response.getWriter().println(
                "Authentication successful."
        );

        response.getWriter().println();

        response.getWriter().println(
                "User ID: "
                        + authenticatedUser.getId()
        );

        response.getWriter().println(
                "Name: "
                        + authenticatedUser.getName()
        );

        response.getWriter().println(
                "Email: "
                        + authenticatedUser.getEmail()
        );

        response.getWriter().println(
                "Role: "
                        + authenticatedUser.getRole()
        );
    }
}