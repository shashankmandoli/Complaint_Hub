package com.complainthub.test;

import com.complainthub.util.AuthenticationConstants;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/api/test/session")
public class SessionTestServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        HttpSession session =
                request.getSession(false);

        if (session == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.getWriter().println(
                    "No authenticated session found."
            );

            return;
        }

        Object userId =
                session.getAttribute(
                        AuthenticationConstants.USER_ID
                );

        Object userRole =
                session.getAttribute(
                        AuthenticationConstants.USER_ROLE
                );

        if (userId == null || userRole == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.getWriter().println(
                    "Session exists, but authentication information is missing."
            );

            return;
        }

        response.setStatus(
                HttpServletResponse.SC_OK
        );

        response.getWriter().println(
                "Authenticated session found."
        );

        response.getWriter().println();

        response.getWriter().println(
                "User ID: " + userId
        );

        response.getWriter().println(
                "User Role: " + userRole
        );
    }
}